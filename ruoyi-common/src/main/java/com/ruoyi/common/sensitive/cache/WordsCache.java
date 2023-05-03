package com.ruoyi.common.sensitive.cache;


import com.ruoyi.common.sensitive.SensitiveWord;
import com.ruoyi.common.sensitive.SensitiveWordSeeker;

import java.util.Set;

/**
 * @Description: 敏感词库缓存
 * @return
 */
public interface WordsCache {

	/**
	 * @Description: 设置数据源
	 * @param dataSource
	 * @return void
	 */
	void setDataSource(Object dataSource);

	/**
	 * @Description: 初始化缓存
	 * @return boolean
	 */
	boolean init() ;

	/**
	 * @Description: 设置缓存数据
	 * @param words
	 * @return boolean
	 */
	boolean put(String identifier, SensitiveWord words) ;

	/**
	 * @Description: 设置缓存数据列表
	 * @param words
	 * @return boolean
	 */
	boolean put(String identifier, Set<SensitiveWord> words) ;
	/**
	 * @Description: 获取缓存列表
	 * @return List<SensitiveWord>
	 */
	SensitiveWordSeeker get(String identifier) ;

	/**
	 * @Description: 移除缓存
	 * @param words 敏感词
	 * @return
	 */
	boolean remove(String identifier, SensitiveWord words) ;

	/**
	 * @Description: 移除缓存
	 * @param wordsSet 敏感词集合
	 * @return
	 */
	boolean remove(String identifier, Set<SensitiveWord> wordsSet);
	/**
	 * @Description: 刷新缓存数据
	 * @return
	 */
	boolean refreshAll() ;
	/**
	 * @Description: 刷新指定缓存数据
	 * @return
	 */
	SensitiveWordSeeker refresh(String identifier) ;
	/**
	 * @Description: 更新指定缓存
	 * @param word 铭感字符
	 * @return
	 */
	boolean update(String identifier, SensitiveWord word) ;

	/**
	 * @Description: 获取标识符
	 * @param identifier
	 * @return
	 */
	String getNextId(String identifier);
}
