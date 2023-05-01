package com.ruoyi.common.utils.number.randomX;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.clazz.ClassUtil;
import com.ruoyi.common.utils.number.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @description: 量子力学随机数
 **/
public class QuantumRandom extends RandomX{
    private Stack<Byte> stack = new Stack();
    private static AnuQrngRandom anuQrngRandom;
    private RandomHotBits randomHotBits;
    private SecureRandom randomTemp;
    private final static Logger logger = LoggerFactory.getLogger(QuantumRandom.class);
    private volatile long lastPullTime = System.currentTimeMillis();
    private int maxStackSize = 1024;
    private int minPullSize = 512;
    private int pollQuantumRandomInterval = 5000;
    private static long retryTime = 8*60*1000;
    private static volatile boolean isStart;
    private ScheduledExecutorService scheduledExecutorService;
    public QuantumRandom(SecureRandom randomTemp){
        this.randomTemp = randomTemp;
        anuQrngRandom = new AnuQrngRandom();
        String apiKey = System.getenv("HotBitsApiKey");
        if(StringUtils.isNotBlank(apiKey) ) {
            randomHotBits = new RandomHotBits(apiKey);
        }
        pollQuantumRandomInterval += randomTemp.nextInt(16888);//减少启动时间间隔问题
        retryTime += pollQuantumRandomInterval;
        logger.info("[QuantumRandom.pollQuantumRandomInterval] value is {}" + pollQuantumRandomInterval);
        boolean isAutoPull = ClassUtil.formatObject(System.getenv("IS_AUTO_PULL_QUANTUM_RANDOM"),true,Boolean.class);
        if(isAutoPull){
            if(!isStart){
                scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "QuantumRandomScheduledThread");
                    }
                });
                this.isStart = true;
                startScheduledTask();
            }
        }
    }

    public void shutdown() {
        if(isStart && scheduledExecutorService != null){
            isStart = false;
            scheduledExecutorService.shutdown();
            logger.info("isAutoPull scheduledExecutorService close");
        }
    }

    @Override
    public byte nextByte()  {
        try {
            logger.info("nextByte quantum random size is {}",stack.size());
            synchronized (stack) {
                if (stack.empty()) {
                    fillBuffer();
                }
                return stack.pop();
            }
        } catch (IOException e) {
            throw new ServiceException("Cannot obtain quantum random number");
        }
    }
    private static volatile long errorDateCase1 = -1;
    private static volatile long errorDateCase2 = -1;
    private static volatile long errorDateCase3 = -1;
    private static volatile long errorDateCase4 = -1;

    private synchronized void fillBuffer() throws IOException{

        // 错误后8分钟后再请求
        long diff = System.currentTimeMillis() - errorDateCase1;

        if(randomHotBits != null) {
            diff = System.currentTimeMillis() - errorDateCase4;
            if (errorDateCase4 == -1 || diff > retryTime) {
                try {
                    Byte[] data = randomHotBits.getBytesSafe(randomHotBits.getApiUrl());
                    if(data.length >= 128) {
                        putArrayToStack(data);
                        logger.info("------->pull randomHotBits byte success,pull size is {}<------", data.length);
                    }
                } catch (IOException e) {
                    errorDateCase4 = System.currentTimeMillis();
                    logger.error("get RandomHotBits data error!!!", e);
                }
            }
        }
    }

    private void putArrayToStack(Byte[] data){
        for (int i = 0; i < data.length; i++) {
            synchronized (stack) {
                if(stack.empty()){
                    stack.push(data[i]);
                }else{
                    stack.add(RandomUtil.nextInt(stack.size()),data[i]);
                }
            }
        }
    }

    public int getQuantumRandom(int bound){
        if(RandomUtil.nextBoolean()) {
            int result = getRandomXData(bound);
            logger.info("[QuantumRandom.generateCakes] use quantum random case C,bound:{}",bound);
            errorDateCase1 = -1;
            return result;
        }else if(RandomUtil.nextBoolean() ) {
            int result = Math.abs(nextShort());
            errorDateCase1 = -1;
            logger.info("[QuantumRandom.generateCakes] use quantum random case D,bound:{}",bound);
            return result % bound;
        }else{
            int result = Math.abs(nextInt()) % bound;
            logger.info("[QuantumRandom.generateCakes] use quantum random case E,bound:{}",bound);
            errorDateCase1 = -1;
            return result;
        }
    }

    public int getRandomXData(int bound){
        byte b0 = nextByte();
        byte b1 = nextByte();
        byte b2 = nextByte();
        int result = 0;
        if(RandomUtil.nextBoolean()) {
            byte b3 = nextByte();
            result = (((b3) << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | ((b0 & 0xff)));
        }else {
            result = (((b2) << 24) | ((b1 & 0xff) << 16) | ((b1 & 0xff) << 8) | ((b0 & 0xff)));
        }
        return Math.abs(result) % bound;
    }

    private void startScheduledTask() {
        int nextPullTime = 1888 + randomTemp.nextInt(3888);
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                try {
                    if(stack.size() < minPullSize) {
                        while (stack.size() < minPullSize && isStart) {
                            logger.info("scheduledTask while pull before QuantumRandom size is {}",stack.size());
                            fillBuffer();
                            lastPullTime = System.currentTimeMillis();
                            Thread.sleep(2888 + randomTemp.nextInt(3888));//防止请求过于快
                        }
                    }else if(stack.size() <= maxStackSize && (System.currentTimeMillis() - lastPullTime) > nextPullTime){//防止并发拉取
                        logger.info("scheduledTask supplement ,pull before QuantumRandom size is {}",stack.size());
                        fillBuffer();
                        lastPullTime = System.currentTimeMillis();
                    }
                } catch (Exception e) {
                    logger.error("[QuantumRandom.startScheduledTask] fillBuffer", e);
                }
                logger.info("scheduledTask QuantumRandom size is {}",stack.size());
            }
        }, 10, this.pollQuantumRandomInterval, TimeUnit.MILLISECONDS);
    }

    public int getDataSize(){
        return stack.size();
    }
}
