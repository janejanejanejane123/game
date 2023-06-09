package com.ruoyi.common.sensitive.processor;


import com.ruoyi.common.sensitive.SensitiveWord;
import com.ruoyi.common.sensitive.SensitiveWordResult;
import com.ruoyi.common.sensitive.util.AnalysisUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 对文本中的关键词进行提取。主要根据关键词对文本中的关键词进行提取！
 */
public class WordFinder implements Processor {

    /**
     * 将文本中的关键词提取出来。
     */
    public List<SensitiveWordResult> process(Map<String, Map> wordsTree, Map<String, SensitiveWord> stopwdMap, Byte useType, String text,
                                             AbstractFragment fragment, int minLen) {
        // 词的前面一个字
        String pre = null;
        // 词匹配的开始位置
        int startPosition = 0;
        // 返回结果
        List<SensitiveWordResult> rs = new ArrayList<SensitiveWordResult>();

        while (true) {
            try {
                if (wordsTree == null || wordsTree.isEmpty() || StringUtils.isEmpty(text)) {
                    return rs;
                }
                if (text.length() < minLen) {
                    return rs;
                }
                String chr = text.substring(0, 1);
                text = text.substring(1);
                Map<String, Map> nextWord = wordsTree.get(chr);

                // 没有对应的下一个字，表示这不是关键词的开头，进行下一个循环
                if (nextWord == null ) {
                    pre = chr;
                    continue;
                }
                SensitiveWord kw = AnalysisUtil.getSensitiveWord(useType, pre, nextWord,stopwdMap, text);
                if (kw == null) {
                    // 没有匹配到完整关键字，下一个循环
                    pre = chr;
                    continue;
                }
                // 同一个word多次出现记录在一起
                SensitiveWordResult result = new SensitiveWordResult(startPosition, kw);
                int index = rs.indexOf(result);
                if (index > -1) {
                    rs.get(index).addPosition(startPosition, kw.getWord());
                } else {
                    rs.add(result);
                }
                // 从text中去除当前已经匹配的内容，进行下一个循环匹配
                text = text.substring(kw.getWordLength() - 1);
                pre = kw.getWord().substring(kw.getWordLength() - 1, kw.getWordLength());
                continue;
            } finally {
                if (pre != null) {
                    startPosition = startPosition + pre.length();
                }
            }

        }
    }
}
