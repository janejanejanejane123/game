package com.ruoyi.common.sensitive.processor;



import com.ruoyi.common.sensitive.SensitiveWord;

import java.util.Map;

/**
 * @Description: 处理接口
 * @return
 */
public interface Processor {
    /**
     * 处理操作
     * 
     * @param wordsTree 词表树
     * @param stopwdMap 停顿词
     * @param useType 使用类型，穿空的话就不对使用类型进行判断
     * @param text 目标文本
     * @param fragment 每个命中的词处理器
     * @param minLen 词树中最短的词的长度
     * @return 返回处理结果
     */
    @SuppressWarnings("rawtypes")
    Object process(Map<String, Map> wordsTree, Map<String, SensitiveWord> stopwdMap, Byte useType, String text, AbstractFragment fragment,
                   int minLen);
}
