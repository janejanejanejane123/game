package com.ruoyi.common.sensitive.cache;


import com.ruoyi.common.core.redis.RedisLock;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.sensitive.SensitiveWord;
import com.ruoyi.common.sensitive.SensitiveWordSeeker;
import com.ruoyi.common.sensitive.service.SensitiveWordService;
import com.ruoyi.common.serializer.ProtostuffSerializer;
import com.ruoyi.common.utils.SystemUtil;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * @Description: Redis 分布式缓存
 */
@Slf4j
public final class RedisWordsCache extends AbstractWordCache {

	private SensitiveWordService service;
	private SnowflakeIdUtils snowflakeIdFactory;
	private ProtostuffSerializer protostuffSerializer;
	private RedisTemplate redisTemplate;
    public final static String SENSITIVE_WORD_FILTER_PREFIX = "SENSITIVE_WORD_FILTER_PREFIX:";
	private RedisWordsCache() {
		super("redis敏感词库缓存!!!");
		protostuffSerializer = new ProtostuffSerializer();
	}

	private static class SingleFactory {
		
		private static final RedisWordsCache INSTANCE = new RedisWordsCache();
	}

	public static final RedisWordsCache getInstance() {
		return SingleFactory.INSTANCE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setDataSource(Object dataSource) {
		super.setDataSource(dataSource);
		
		if (dataSource instanceof SensitiveWordService) {
			this.service = (SensitiveWordService) dataSource;
		} else if(dataSource instanceof RedisTemplate){
			this.redisTemplate = (RedisTemplate) dataSource;
		}else if(dataSource instanceof  SnowflakeIdUtils){
			this.snowflakeIdFactory = (SnowflakeIdUtils) dataSource;
		}else {
			throw new ServiceException("未知数据源类型:" + getListenerName());
		}
	}
	
	@Override
	public boolean init() {
		super.init();
		List<String> identifierList = service.getAllIdentifier();
		if(CollectionUtils.isNotEmpty(identifierList)){
			for(String identifier : identifierList) {
				String redisKey = SENSITIVE_WORD_FILTER_PREFIX + identifier;
				if(!redisTemplate.hasKey(redisKey)) {
					refresh(identifier);
				}else{
					log.debug("{}: redis缓存已初始化，不需要执行!!!", getListenerName());
				}
			}
		}
		return true;
	}
	
	public boolean put(String identifier, SensitiveWord words) {
		Set<SensitiveWord> wordSet = new HashSet<>(1);
		wordSet.add(words);
		return put(identifier, wordSet);
	}

	public boolean put(String identifier, Set<SensitiveWord> words) {
		super.put(identifier, words);
		SensitiveWordSeeker sensitiveWordSeeker = get(identifier);
		if (sensitiveWordSeeker == null) {
			sensitiveWordSeeker = new SensitiveWordSeeker(words);
		} else {
			sensitiveWordSeeker.addWord(words);
		}
		String redisKey = SENSITIVE_WORD_FILTER_PREFIX + identifier;
		try {
			setSensitiveWordSetToRedis(redisKey, sensitiveWordSeeker);
		} catch (IOException e) {
			throw new ServiceException( "将敏感字放入缓存错误!!!");
		}
		return true;
	}

	public SensitiveWordSeeker get(String identifier) {
		super.get(identifier);
		SensitiveWordSeeker sensitiveWordSeeker = null;
		String redisKey = SENSITIVE_WORD_FILTER_PREFIX + identifier;
		byte[] sensitiveArray = (byte[]) redisTemplate.opsForValue().get(redisKey);
		if(sensitiveArray == null) {
			try {
				if (RedisLock.tryLock("sensitiveword:get:"+identifier,60)) {
					sensitiveArray = (byte[]) redisTemplate.opsForValue().get(redisKey);
					if(sensitiveArray == null) {
						int count = service.count(identifier);
						if (count > 0) {

							int pageSize = 500;
							int pageCount = count % pageSize != 0 ? (int) Math.floor((count * 1.0d) / pageSize) + 1 : (int) Math.floor((count * 1.0d) / pageSize);
							Set<SensitiveWord> wordSet = new HashSet<SensitiveWord>(count);
							List<Future<List<SensitiveWord>>> futureList = new ArrayList<>(pageCount);
							for (int i = 0; i < pageCount; i++) {
								futureList.add(service.list(identifier, pageSize, pageSize * i));
							}
							for (Future<List<SensitiveWord>> future : futureList) {
								if (future != null) {
									List<SensitiveWord> sensitiveWordList = future.get();
									RedisLock.delayLock();
									if (CollectionUtils.isNotEmpty(sensitiveWordList)) {
										wordSet.addAll(sensitiveWordList);
									}
								}
							}
							sensitiveWordSeeker = new SensitiveWordSeeker(wordSet);
							setSensitiveWordSetToRedis(redisKey,sensitiveWordSeeker);
						}
					}
				} else {
					throw new ServiceException( "刷新缓存,不要重复!!!");
				}
			} catch (Exception e) {
				throw new ServiceException("刷新缓存错误!");
			} finally {
				RedisLock.unLock();
			}
		}
		try {
			if(sensitiveArray != null && sensitiveWordSeeker == null){
				byte[] tempData = new byte[sensitiveArray.length-1];
				System.arraycopy(sensitiveArray, 1, tempData, 0, tempData.length);
				if(sensitiveArray[0] == 1){
					byte[] compressData = SystemUtil.uncompress(tempData);
					tempData = compressData;
				}
				sensitiveWordSeeker = (SensitiveWordSeeker) protostuffSerializer.deserialize(tempData);
				tempData = null;
			}
		} catch (Exception e) {
			deleteRedisKey(identifier);
			throw new ServiceException("redis数据错误，错误数据已清除，请重试");
		}
		return sensitiveWordSeeker;
	}

	private void deleteRedisKey(String identifier){
		String redisKey = SENSITIVE_WORD_FILTER_PREFIX + identifier;
		redisTemplate.delete(redisKey);
		redisTemplate.delete(redisKey + ":nextId");
	}


	public void setSensitiveWordSetToRedis(String redisKey,SensitiveWordSeeker sensitiveWordSeeker) throws IOException {
		long nextId = snowflakeIdFactory.nextId();
		byte[] data = protostuffSerializer.serialize(sensitiveWordSeeker);
		if (data.length > 2048) {
			byte[] temp = SystemUtil.compress(data, 5);
			data = null;
			byte[] tempData = new byte[temp.length + 1];
			tempData[0] = 1;
			System.arraycopy(temp, 0, tempData, 1, temp.length);
			data = tempData;
		} else {
			byte[] tempData = new byte[data.length + 1];
			tempData[0] = 0;
			System.arraycopy(data, 0, tempData, 1, data.length);
			data = tempData;
		}

		redisTemplate.opsForValue().set(redisKey + ":nextId", protostuffSerializer.serialize(nextId + ":" + System.currentTimeMillis()));
		redisTemplate.opsForValue().set(redisKey, data);
		data = null;
	}

	public boolean update(String identifier, SensitiveWord word) {
		super.update(identifier,word);
		
		if (remove(identifier,word)) {
			return put(identifier, word);
		}
		
		return false;
	}

	public boolean remove(String identifier, final SensitiveWord word) {
		super.remove(identifier, word);
		Set<SensitiveWord> wordSet = new HashSet<>(1);
		wordSet.add(word);
		return remove(identifier,wordSet);
	}

	@Override
	public boolean remove(String identifier, Set<SensitiveWord> wordsSet){
		super.remove(identifier, wordsSet);
		if (CollectionUtils.isEmpty(wordsSet)) {
			return false;
		}
		boolean isRemove = false;
		SensitiveWordSeeker sensitiveWordSeeker = get(identifier);
		for (SensitiveWord sensitiveWord : wordsSet) {
			if(sensitiveWordSeeker.remove(sensitiveWord)){
				isRemove = true;
			}
		}
		try {
			String redisKey = SENSITIVE_WORD_FILTER_PREFIX + identifier;
			setSensitiveWordSetToRedis(redisKey, sensitiveWordSeeker);
		} catch (IOException e) {
			throw new ServiceException("将敏感字放入缓存错误!!!");
		}
		return isRemove;
	}

	public boolean refreshAll() {
		super.refreshAll();
		List<String> identifierList = service.getAllIdentifier();
		if(CollectionUtils.isNotEmpty(identifierList)){
			for(String identifier : identifierList) {
				refresh(identifier);
			}
		}
		log.debug("{}: 从新的刷新中初始化redis缓存", getListenerName());
		return true;
	}

	public SensitiveWordSeeker refresh(String identifier){
		try {
			if(RedisLock.lock("sensitiveword:refresh:"+identifier,60)) {
				deleteRedisKey(identifier);
				return get(identifier);
			}else{
				throw new ServiceException( "刷新缓存,不要重复!!!");
			}
		} finally {
			RedisLock.unLock();
		}
	}

	public String getNextId(String identifier){
		super.getNextId(identifier);
		String redisKey = SENSITIVE_WORD_FILTER_PREFIX + identifier;
		return (String) protostuffSerializer.deserialize((byte[]) redisTemplate.opsForValue().get(redisKey + ":nextId"));
	}
}
