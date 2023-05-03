package com.ruoyi.common.sensitive;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 敏感词管理器
 */
public class SensitiveWordSeekerManage {

    /**
     * 敏感词模块. key为模块名，value为对应的敏感词搜索器
     */
    protected Map<String, SensitiveWordSeeker> seekers = new ConcurrentHashMap<String, SensitiveWordSeeker>();

    /**
     * 初始化
     */
    public SensitiveWordSeekerManage() {
    }

    /**
     * 
     * @param seekers
     */
    public SensitiveWordSeekerManage(Map<String, SensitiveWordSeeker> seekers) {
        this.seekers.putAll(seekers);
    }

    /**
     * 获取一个敏感词搜索器
     * 
     * @param wordType
     * @return
     */
    public SensitiveWordSeeker getKWSeeker(String wordType) {
        return seekers.get(wordType);
    }

    /**
     * 加入一个敏感词搜索器
     * 
     * @param wordType
     * @param sensitiveWordSeeker
     * @return
     */
    public void putKWSeeker(String wordType, SensitiveWordSeeker sensitiveWordSeeker) {
        seekers.put(wordType, sensitiveWordSeeker);
    }

    /**
     * 移除一个敏感词搜索器
     * 
     * @param wordType
     * @return
     */
    public void removeKWSeeker(String wordType) {
        seekers.remove(wordType);
    }

    /**
     * 清除空搜索器
     */
    public void clear() {
        seekers.clear();
    }

}
