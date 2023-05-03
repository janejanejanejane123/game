package com.ruoyi.web.controller.tool;

import com.alibaba.fastjson.JSONArray;
import com.ruoyi.common.constant.CacheConstant;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sign.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/1,15:46
 * @return:
 **/

@Component
public class RSAKeyGenerator {


    public static final Logger logger= LoggerFactory.getLogger(RSAKeyGenerator.class);
    private final Timer timer = new Timer("RedisCreateRsaKeysService", true);
    private final String KeyId;
    private static volatile long TIME=0L;
    private final long period=58_000;
    private RedisCache redisCache;
    private final ReentrantLock lock=new ReentrantLock();


    public RSAKeyGenerator(RedisCache redisCache){
        this.redisCache=redisCache;
        this.KeyId= UUID.randomUUID().toString();
        init();
    }

    private void init(){
        startCheckAndGenerateKeys();
    }


    public Map<String,String> getCurrentPublicKey(){
        Map<String, String> map = new HashMap<>(4);
        String uuid = this.getCurrentKeyUuid();
        JSONArray keyPair = redisCache.getCacheObject(CacheConstant.RSA_KEY_STORE + uuid);
        map.put("pointer",uuid);
        map.put("pbk",keyPair.getString(1));
        return map;
    }


    private String getCurrentKeyUuid() {
        //获取uuid;
        String uuid = redisCache.getCacheObject(CacheConstant.RSA_KEY + this.KeyId);
        //redis 或网路故障后,如果定时器未能及时更新,会导致缓存中没有uuid,此时尝试手动添加缓存
        if (StringUtils.isBlank(uuid)){
            //尝试手动生成;
            lock.lock();
            try {
                String currentUuid =  redisCache.getCacheObject(CacheConstant.RSA_KEY + this.KeyId);
                if (StringUtils.isBlank(currentUuid)){
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String date = format.format(new Date());
                    String newId = tryCreateKeyPair();
                    long l = System.currentTimeMillis();
                    logger.info("app thread create key success, date:{},spend_time:{} ms ",date,System.currentTimeMillis()-l);
                    return newId;
                }else {
                    return currentUuid;
                }
            } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                logger.error("app thread create keyPair errors caused by:{}",e.getMessage());
                throw new ServiceException("系统错误");
            } finally {
                lock.unlock();
            }
        }
        return uuid;
    }


    private String tryCreateKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException {
        String uuid=UUID.randomUUID().toString();
        String[] pair =genKeyPair(1024);
        List<String> list = Arrays.asList(pair);
        redisCache.setCacheObject(CacheConstant.RSA_KEY_STORE+uuid,list,300, TimeUnit.SECONDS);
        redisCache.setCacheObject(CacheConstant.RSA_KEY+this.KeyId,uuid,60,TimeUnit.SECONDS);

        return uuid;
    }

    private String[] genKeyPair(int keySize) throws NoSuchProviderException, NoSuchAlgorithmException {
        String[] strings = new String[2];
        byte[][] keyPairBytes = new byte[2][];

        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", "SunRsaSign");
        gen.initialize(keySize, new SecureRandom());
        KeyPair pair = gen.generateKeyPair();

        keyPairBytes[0] = pair.getPrivate().getEncoded();
        keyPairBytes[1] = pair.getPublic().getEncoded();

        strings[0] = Base64.encode(keyPairBytes[0]);
        strings[1] = Base64.encode(keyPairBytes[1]);


        return strings;
    }

    private void startCheckAndGenerateKeys(){
        logger.info("timer started");
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long l = System.currentTimeMillis();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String date = format.format(new Date());
                if (l-TIME>period-1_000){
                    try {
                        tryCreateKeyPair();
                        TIME=l;
                        logger.info("timer thread create key success, date:{},spend_time:{} ms ",date,System.currentTimeMillis()-l);
                    } catch (Throwable e) {
                        logger.error("timer thread create key fail, date:{},caused_by:{}",date,e.getMessage());
                    }
                }
            }
            //间隔58秒去更新一次;
        }, 0, period);
    }

}
