package com.ruoyi.common.sensitive.cache;


import com.ruoyi.common.sensitive.SensitiveWord;
import com.ruoyi.common.sensitive.SensitiveWordSeeker;
import com.ruoyi.common.sensitive.service.SensitiveWordService;
import com.ruoyi.common.utils.clazz.ClassUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description: Jvm 本地敏感词缓存，默认1分钟去redis拉取一次,每次拉取按pullTime间隔时间进行key对比，当nextId不一样才拉取数据，防止数据过大堵死redis
 * @return
 */
@Slf4j
public final class JvmWordsCache extends AbstractWordCache {

	private WordsCache wordsCache;
	private SensitiveWordService service;
	public static Map<String,WordSeekerWrapper> localCache = new ConcurrentHashMap<>();
	private final ReentrantLock lock = new ReentrantLock();
	private static class SingleFactory {
		private static final JvmWordsCache INSTANCE = new JvmWordsCache();
	}

	public static final JvmWordsCache getInstance() {
		return SingleFactory.INSTANCE;
	}
	
	private JvmWordsCache() {
		super("JVM Sensitive Word thesaurus cache");
	}

	@Override
	public void setDataSource(Object dataSource) {
		super.setDataSource(dataSource);

		if (dataSource instanceof WordsCache) {
			this.wordsCache = (WordsCache) dataSource;
		}else if (dataSource instanceof SensitiveWordService) {
            this.service = (SensitiveWordService) dataSource;
        }else {
			throw new IllegalArgumentException("Unknown data source type" + getListenerName());
		}
	}

	@Override
	public boolean init()  {
		super.init();
		if (localCache.isEmpty()) {
			log.debug("{}: jvm cache Initialization", getListenerName());
			refreshAll();
			return false;
		} else {
			log.debug("{}: jvm cache has been initialized, no need to repeat execution", getListenerName());
		}

		return true;
	}

	public boolean put(String identifier, SensitiveWord words)  {
		super.put(identifier, words);
		Set<SensitiveWord> sensitiveWords = new HashSet<>(1);
		sensitiveWords.add(words);
		return put(identifier, sensitiveWords);
	}

	public boolean put(String identifier, Set<SensitiveWord> words)  {
		super.put(identifier, words);
		SensitiveWordSeeker sensitiveWordSeeker = get(identifier);
		if(sensitiveWordSeeker == null){
			return false;
		}else{
			sensitiveWordSeeker.addWord(words);
		}
		return true;
	}

	public SensitiveWordSeeker get(String identifier)  {
		super.get(identifier);
		WordSeekerWrapper wordSeekerWrapper = localCache.get(identifier);
		long nowTime = System.currentTimeMillis();
		if(wordSeekerWrapper == null){
			SensitiveWordSeeker sensitiveWordSeeker = wordsCache.get(identifier);
			if(sensitiveWordSeeker == null) {
				return null;
			}else{
				String tempId = wordsCache.getNextId(identifier);
				if(StringUtils.isBlank(tempId) || tempId.indexOf(":") == -1){
					sensitiveWordSeeker = wordsCache.refresh(identifier);
					tempId = wordsCache.getNextId(identifier);
				}
				long nextId = ClassUtil.formatObject(tempId.split(":")[0],Long.class);
				WordSeekerWrapper temp = new WordSeekerWrapper(nextId,nowTime,sensitiveWordSeeker);

				if(localCache.putIfAbsent(identifier, temp) == null ){
					wordSeekerWrapper = temp;
					wordSeekerWrapper.setPullDateTime(nowTime);
				}

				return sensitiveWordSeeker;
			}
		}else {
			if ((nowTime - wordSeekerWrapper.getPullDateTime()) > 10 * 1000) {//10秒后去系统校对
				if(lock.tryLock()) {//判断是否被锁住，锁住证明有人已经使用，为了防止缓存过刷
					try {
						String tempId = wordsCache.getNextId(identifier);
						if (StringUtils.isBlank(tempId) || tempId.indexOf(":") == -1) {
							SensitiveWordSeeker sensitiveWordSeeker = wordsCache.refresh(identifier);
							tempId = wordsCache.getNextId(identifier);
							long nextId = ClassUtil.formatObject(tempId.split(":")[0], Long.class);
							wordSeekerWrapper.setNextId(nextId);
							wordSeekerWrapper.setSensitiveWordSeeker(sensitiveWordSeeker);
							wordSeekerWrapper.setPullDateTime(nowTime);
							return sensitiveWordSeeker;
						}
						long nextId = wordSeekerWrapper.getNextId();
						if (ClassUtil.formatObject(tempId.split(":")[0], Long.class).equals(nextId)) {
							return wordSeekerWrapper.getSensitiveWordSeeker();
						} else {
							SensitiveWordSeeker sensitiveWordSeeker = wordsCache.get(identifier);
							wordSeekerWrapper.setNextId(nextId);
							wordSeekerWrapper.setSensitiveWordSeeker(sensitiveWordSeeker);
							wordSeekerWrapper.setPullDateTime(nowTime);
							return sensitiveWordSeeker;
						}
					} finally {
						lock.unlock();
					}
				}else{
					return wordSeekerWrapper.getSensitiveWordSeeker();
				}
			}else{
				return wordSeekerWrapper.getSensitiveWordSeeker();
			}
		}
	}

	public boolean update(String identifier, SensitiveWord word)  {
		super.update(identifier,word);

		if (remove(identifier,word)) {
			return put(identifier,word);
		}

		return false;
	}

	public boolean remove(String identifier, final SensitiveWord word)  {
		super.remove(identifier,word);

		if (word == null) {
			return false;
		}
		SensitiveWordSeeker sensitiveWordSeeker = get(identifier);
		if(sensitiveWordSeeker == null){
			return false;
		}
		return sensitiveWordSeeker.remove(word);
	}

	public boolean refreshAll()  {
		super.refreshAll();
		synchronized (wordsCache) {
            log.debug("{}: Initialize JVM cache from new refreshAll", getListenerName());

            List<String> identifierList = service.getAllIdentifier();
            if (CollectionUtils.isNotEmpty(identifierList)) {
                for (String identifier : identifierList) {
					refresh(identifier);
                }
                log.debug("{}: JVM cache sensitive words number：{}", getListenerName(), localCache.size());
            }
        }
		return true;
	}

	@Override
	public SensitiveWordSeeker refresh(String identifier) {
		localCache.remove(identifier);
		SensitiveWordSeeker sensitiveWordSeeker = wordsCache.get(identifier);
		if(sensitiveWordSeeker != null){
			String tempId = wordsCache.getNextId(identifier);
			//redis本来就是个不稳定的存在，所有数据错误应该重新加载数据，正常情况下不会出现问题，但要保证可用性
			if (StringUtils.isBlank(tempId) || tempId.indexOf(":") == -1) {
				sensitiveWordSeeker = wordsCache.refresh(identifier);
				tempId = wordsCache.getNextId(identifier);
			}
			long nextId = ClassUtil.formatObject(tempId.split(":")[0], Long.class);

			WordSeekerWrapper wordSeekerWrapper = localCache.get(identifier);
			if(wordSeekerWrapper == null){
				wordSeekerWrapper = localCache.putIfAbsent(identifier,new WordSeekerWrapper(nextId,System.currentTimeMillis(),sensitiveWordSeeker));
				if(wordSeekerWrapper != null){
					wordSeekerWrapper.setNextId(nextId);
					wordSeekerWrapper.setSensitiveWordSeeker(sensitiveWordSeeker);
					wordSeekerWrapper.setPullDateTime(System.currentTimeMillis());
				}
			}

		}
		return sensitiveWordSeeker;
	}

	@Data
	@AllArgsConstructor
	class WordSeekerWrapper{
		private long nextId; //下一个id的指标
		private long pullDateTime;//上次拉取校对事件
		private SensitiveWordSeeker sensitiveWordSeeker;//敏感词搜索者
	}

}