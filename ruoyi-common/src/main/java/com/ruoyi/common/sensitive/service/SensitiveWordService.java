package com.ruoyi.common.sensitive.service;

import com.google.common.base.Strings;
import com.ruoyi.common.constant.CacheConstant;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.sensitive.SensitiveWord;
import com.ruoyi.common.sensitive.SensitiveWordResult;
import com.ruoyi.common.sensitive.SensitiveWordSeeker;
import com.ruoyi.common.sensitive.cache.AbstractWordCache;
import com.ruoyi.common.sensitive.cache.JvmWordsCache;
import com.ruoyi.common.sensitive.cache.RedisWordsCache;
import com.ruoyi.common.sensitive.event.CacheChangedEvent;
import com.ruoyi.common.sensitive.event.WordsCacheContext;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.*;

/**
 * @description: 敏感词库服务接口业务实现
 **/
@Slf4j
public class SensitiveWordService {
    private SensitiveWordRepository sensitiveWordRepository;
    private WordsCacheContext wordsCacheContext;
    private AbstractWordCache abstractWordCache;
    private RedisWordsCache redisWordsCache;

    /**
     * @Description:
     * @param isInitJvmCache
     * @return
     */
    public SensitiveWordService(boolean isInitJvmCache, RedisTemplate redisTemplate, SnowflakeIdUtils snowflakeIdFactory, SensitiveWordRepository sensitiveWordRepository) {
        this.sensitiveWordRepository = sensitiveWordRepository;
        RedisWordsCache redisWordsCache = RedisWordsCache.getInstance();
        redisWordsCache.setDataSource(this);
        redisWordsCache.setDataSource(redisTemplate);
        redisWordsCache.setDataSource(snowflakeIdFactory);
        this.wordsCacheContext = WordsCacheContext.getInstance(isInitJvmCache);
        this.redisWordsCache = redisWordsCache;
        if(isInitJvmCache){
            JvmWordsCache jvmWordsCache = JvmWordsCache.getInstance();
            jvmWordsCache.setDataSource(redisWordsCache);
            jvmWordsCache.setDataSource(this);
            jvmWordsCache.setDataSource(redisWordsCache);
            abstractWordCache = jvmWordsCache;
        }else{
            abstractWordCache = redisWordsCache;
        }
    }

    /**
     * @Description: 校验敏感
     * @param useType 使用类型
     * @param alertStr 敏感字提示语
     * @param message 要过虑的敏感字
     * @return {@link String}
     */
    public String checkSensitiveMsg( byte useType,String alertStr, String message) throws Exception {
        if(StringUtils.isBlank(message)){
            return message;
        }
        SensitiveWordSeeker sensitiveWordSeeker = abstractWordCache.get(CacheConstant.SENSITIVE_WORD_IDENTIFIER);
        if(sensitiveWordSeeker == null){
            return message;
        }
        String replaceMsg = sensitiveWordSeeker.replaceWords(false, useType, message);
        List<SensitiveWordResult> sensitiveWordResults = sensitiveWordSeeker.findWords(true, useType, replaceMsg);
        if(CollectionUtils.isNotEmpty(sensitiveWordResults)){
            for(SensitiveWordResult sensitiveWordResult : sensitiveWordResults){
                if(sensitiveWordResult.getWord().getFilterType() == 1){
                    throw new ServiceException(String.format(alertStr,sensitiveWordResult.getWord().getWord()),28342);
                }
            }
            return replaceMsg;
        }else{
            return replaceMsg;
        }
    }

    public boolean add(String identifier, SensitiveWord entity, Object parameter) {
        if(log.isDebugEnabled()) {
            log.debug("Add [Sensitive Thesaurus] data parameter：identifier:{},entity{},parameter:{}", identifier,entity,parameter);
        }
        validate(entity);

        boolean flag = false;
        try {
            if (entity.getId() == null) {
                throw new NullPointerException("sensitive word id is empty!!!");
            }
            checkUnique(identifier,entity);

            flag = sensitiveWordRepository.add(identifier,entity,parameter) > 0;

            if (flag) {
                wordsCacheContext.dispatchChanged(identifier, new CacheChangedEvent(entity, CacheChangedEvent.Action.PUT));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        if(log.isDebugEnabled()) {
            log.debug("Whether adding [Sensitive Thesaurus] data was successful：identifier:{},entity{},parameter:{},flag:{}", identifier,entity,parameter,flag);
        }
        return flag;
    }

    private void checkUnique(String identifier, SensitiveWord entity) {
        @SuppressWarnings("unchecked")
        SensitiveWord card = sensitiveWordRepository.get(identifier, entity);

        checkState(card == null, "The sensitive identifier:%s, word:%s already exists, cannot be added repeatedly!!!", identifier,entity.getWord());
    }

    public void validate(SensitiveWord entity) {
        checkNotNull(entity, "Sensitive thesaurus object cannot be empty");

        checkArgument(!Strings.isNullOrEmpty(entity.getWord()), "Sensitive words cannot be empty!!!");
        checkNotNull(entity.getType(), "Sensitive word type cannot be empty!!!");
    }

    public boolean batch(String identifier, Set<SensitiveWord> entities, Object parameter) {
        if(log.isDebugEnabled()) {
            log.debug("Add [Sensitive Thesaurus] data parameters in batches:identifier:{},entities:{},parameter:{}", identifier,entities,parameter);
        }
        boolean flag = false;
        try {
            if (entities != null && !entities.isEmpty()) {
                Iterator<SensitiveWord> iter = entities.iterator();
                while (iter.hasNext()) {
                    SensitiveWord entity = iter.next();

                    validate(entity);
                    if (entity.getId() == null) {
                        throw new NullPointerException("sensitive word id is empty!!!");
                    }
                    checkUnique(identifier, entity);
                }

                int count = sensitiveWordRepository.batch(identifier,entities,parameter);
                if(log.isDebugEnabled()) {
                    log.debug("Batch storage of sensitive thesaurus data：{}", count);
                }
                if (count != entities.size()) {
                    throw new RuntimeException("Batch storage of sensitive thesaurus data is incomplete");
                }

                wordsCacheContext.dispatchChanged(identifier, new CacheChangedEvent(entities, CacheChangedEvent.Action.PUT_LIST));
            }

            flag = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        if(log.isDebugEnabled()) {
            log.debug("Whether to add [Sensitive Thesaurus] data in batches：identifier:{},entities:{},parameter:{},flag:{}", identifier,entities,parameter,flag);
        }
        return flag;
    }

    public boolean edit(String identifier, SensitiveWord entity, Object parameter) {
        if(log.isDebugEnabled()) {
            log.debug("Modify [Sensitive Thesaurus] data parameters：identifier:{}, entity:{}, parameter:{}", identifier,entity,parameter);
        }
        boolean flag;
        try {
            flag = sensitiveWordRepository.edit(identifier, entity, parameter) > 0;

            if (flag) {
                wordsCacheContext.dispatchChanged(identifier, new CacheChangedEvent(entity, CacheChangedEvent.Action.UPDATE));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        if(log.isDebugEnabled()) {
            log.debug("Whether to modify the [Sensitive Thesaurus] data：identifier:{}, entity:{}, parameter:{}, flag:{}", identifier,entity,parameter,flag);
        }
        return flag;
    }

    public boolean remove(String identifier, SensitiveWord entity, Object parameter) {
        if(log.isDebugEnabled()) {
            log.debug("Delete the [Sensitive Thesaurus] data parameter：identifier:{}, entity:{}, parameter:{}", identifier, entity, parameter);
        }
        boolean flag = sensitiveWordRepository.remove(identifier,entity, parameter) > 0;
        if (flag) {
            wordsCacheContext.dispatchChanged(identifier, new CacheChangedEvent(entity, CacheChangedEvent.Action.REMOVE));
        }
        if(log.isDebugEnabled()) {
            log.debug("Whether the deletion of [Sensitive Thesaurus] data was successful：identifier:{}, entity:{}, parameter:{}, flag:{}", identifier, entity, parameter, flag);
        }
        return flag;
    }

    public boolean updateStatus(String identifier, SensitiveWord entity, Object parameter) {
        if(log.isDebugEnabled()) {
            log.debug("Modify [Sensitive Thesaurus] data parameters：identifier:{}, entity:{}, parameter:{}", identifier,entity,parameter);
        }
        boolean flag;
        try {
            flag = sensitiveWordRepository.updateStatus(identifier, entity, parameter) > 0;

            if (flag) {
                wordsCacheContext.dispatchChanged(identifier, new CacheChangedEvent(entity, CacheChangedEvent.Action.UPDATE));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        if(log.isDebugEnabled()) {
            log.debug("Whether to modify the [Sensitive Thesaurus] data：identifier:{}, entity:{}, parameter:{}, flag:{}", identifier,entity,parameter,flag);
        }
        return flag;
    }

    public Future<List<SensitiveWord>> list(String identifier, long pageSize, long begin) {
        if(log.isDebugEnabled()) {
            log.debug("Dynamic query [sensitive thesaurus] data parameters identifier:{}, pageSize：{}, begin: {}", identifier, pageSize, begin);
        }
        return sensitiveWordRepository.query(identifier,pageSize,begin);
    }

    public List<String> getAllIdentifier() {
        return sensitiveWordRepository.getAllIdentifier();
    }

    public int count(String identifier) {
        return sensitiveWordRepository.count(identifier);
    }

    public AbstractWordCache getAbstractWordCache() {
        return abstractWordCache;
    }

    public RedisWordsCache getRedisWordsCache() {
        return redisWordsCache;
    }
}
