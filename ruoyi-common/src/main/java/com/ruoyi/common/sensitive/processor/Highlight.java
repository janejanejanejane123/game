package com.ruoyi.common.sensitive.processor;


import com.ruoyi.common.sensitive.SensitiveWord;
import com.ruoyi.common.sensitive.util.AnalysisUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @Description: 对文本进行高亮处理
 * @return
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Highlight implements Processor {
    /**
     * 将文本中的关键词提取出来。
     * 
     * @param wordsTree 关键词库树
     * @param stopwdMap 停顿词
     * @param useType 使用类型，如果传空值就不对使用类型进行校验
     * @param text 待处理的文本
     * @return 返回提取的关键词或null
     */
    public String process(Map<String, Map> wordsTree, Map<String, SensitiveWord> stopwdMap, Byte useType, String text, AbstractFragment fragment,
                          int minLen) {
        StringBuffer result = new StringBuffer("");
        String pre = null;// 词的前面一个字
        while (true) {
            if (wordsTree == null || wordsTree.isEmpty() || StringUtils.isEmpty(text)) {
                return result.append(text).toString();
            }
            if (text.length() < minLen) {
                return result.append(text).toString();
            }
            String chr = text.substring(0, 1);
            text = text.substring(1);
            Map<String, Map> nextWord = wordsTree.get(chr);
            // 没有对应的下一个字，表示这不是关键词的开头，进行下一个循环
            if (nextWord == null) {
                result.append(chr);
                pre = chr;
                continue;
            }

            SensitiveWord kw = AnalysisUtil.getSensitiveWord(useType, pre, nextWord, stopwdMap, text);
            // 没有匹配到完整关键字，下一个循环
            if (kw == null) {
                result.append(chr);
                pre = chr;
                continue;
            }
            if(kw.getFilterType() == 0) {
                String replaceStr = kw.getReplaceStr();
                if(StringUtils.isNotBlank(replaceStr)){
                    result.append(replaceStr);
                }else {
                    // 处理片段
                    result.append(fragment.format(kw));
                }
            }else{
                result.append(kw.getWord());
            }
            // 从text中去除当前已经匹配的内容，进行下一个循环匹配
            text = text.substring(kw.getWordLength() + kw.getFilterLength()- 1);
            pre = kw.getWord().substring(kw.getWordLength() - 1, kw.getWordLength());
            continue;
        }
    }

}
