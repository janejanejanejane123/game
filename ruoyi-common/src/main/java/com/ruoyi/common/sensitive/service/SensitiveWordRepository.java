package com.ruoyi.common.sensitive.service;

import com.ruoyi.common.sensitive.SensitiveWord;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * @description: 敏感词库接口定义
 **/
public interface SensitiveWordRepository {

    /**
     * @Description: 添加敏感字
     * @param identifier 标识符
     * @param entity 敏感字
     * @param parameter 参数
     * @return int
     */
    int add(String identifier, SensitiveWord entity, Object parameter);

    /**
     * @Description: 批量添加敏感字
     * @param identifier 标识符
     * @param entity 敏感字
     * @param parameter 参数
     * @return int
     */
    int batch(String identifier, Set<SensitiveWord> entity, Object parameter);
    /**
     * @Description: 修改敏感字
     * @param identifier 标识符
     * @param entity 敏感字
     * @param parameter 参数
     * @return int
     */
    int edit(String identifier, SensitiveWord entity, Object parameter);

    /**
     * @Description: 移除敏感字
     * @param identifier 标识符
     * @param entity 敏感字
     * @param parameter 参数
     * @return int
     */
    int remove(String identifier, SensitiveWord entity, Object parameter);

    /**
     * @Description: 修改敏感字状态
     * @param identifier 标识符
     * @param entity 敏感字
     * @param parameter 参数
     * @return int
     * @author 南南
     * @date 2020/5/9 11:44
     */
    int updateStatus(String identifier, SensitiveWord entity, Object parameter);

    /**
     * @Description: 获取敏感字数量，根据站点
     * @param identifier 标识符
     * @return int
     */
    int count(String identifier);

    /**
     * @Description: 异步分页查询敏感字
     * @param identifier 标识符
     * @param pageSize 页大小
     * @param begin 从哪里开始
     * @return java.util.concurrent.Future<java.util.List<cloud.data.sensitive.SensitiveWord>>
     */
    Future<List<SensitiveWord>> query(String identifier, long pageSize, long begin);

    /**
     * @Description: 获取所有标识符
     * @param
     * @return java.util.List<java.lang.String>
     */
    List<String> getAllIdentifier();

    /**
     * @Description: 根据标识符 获取指定敏感字
     * @param identifier 标识符
     * @param word 敏感字
     * @return cloud.data.sensitive.SensitiveWord
     */
    SensitiveWord get(String identifier, SensitiveWord word);
}
