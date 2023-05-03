package com.ruoyi.common.sensitive.util;


import com.ruoyi.common.sensitive.SensitiveWord;
import com.ruoyi.common.utils.clazz.CglibBeanCopierUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 关键词分析工具类。
 */
public class AnalysisUtil {

    /**
     * 构建关键词
     * 
     *
     * @param useType
     * @param endTag 属性信息
     * @return
     */
    public static SensitiveWord getKeyWord(Byte useType, Map<String, Object> endTag) {
        Map useTypeMap = (Map) endTag.get(useType);
        if(useTypeMap == null){
            return null;
        }
        SensitiveWord tmp = new SensitiveWord();
        CglibBeanCopierUtils.mapToBean(useTypeMap,tmp);
        return tmp;
    }

    /**
     * 将指定的词构造到一棵树中。
     * 
     * @param tree 构造出来的树
     * @param word 指定的词
     * @param SensitiveWord 对应的词
     * @return
     */
    public static Map<String, Map> makeTreeByWord(Map<String, Map> tree, String word,
            SensitiveWord SensitiveWord) {
        if (StringUtils.isEmpty(word)) {
            EndTagUtil.buind(tree,SensitiveWord);
            return tree;
        }
        String next = word.substring(0, 1);
        Map<String, Map> nextTree = tree.get(next);
        if (nextTree == null) {
            Map<String, Map> temp = new ConcurrentHashMap<>();
            if(tree.putIfAbsent(next,temp) == null){
                nextTree = temp;
            }
        }
        // 递归构造树结构
        tree.put(next, makeTreeByWord(nextTree, word.substring(1), SensitiveWord));
        return tree;
    }

    /**
     * 根据精确、模糊等匹配方式返回相应的实际关键词。
     * 
     * @param kw 敏感词
     * @param pre 前缀
     * @param sufix 后缀
     * @return
     */
    private static SensitiveWord checkPattern(SensitiveWord kw, String pre, String sufix) {
        if(kw == null){
            return null;
        }else if (StringUtils.isNotBlank(kw.getPre()) && StringUtils.isNotBlank(kw.getSufix())) {
            if (null == pre || null == sufix) {
                return null;
            }
            if (!pre.matches(kw.getPre()) || !sufix.matches(kw.getSufix())) {
                return null;
            }
        } else if (StringUtils.isNotBlank(kw.getPre()) && StringUtils.isBlank(kw.getSufix())) {
            if (null == pre) {
                return null;
            }
            if (!pre.matches(kw.getPre())) {
                return null;
            }
        } else if (StringUtils.isBlank(kw.getPre()) && StringUtils.isNotBlank(kw.getSufix())) {
            if (null == sufix) {
                return null;
            }
            if (!sufix.matches(kw.getSufix())) {
                return null;
            }
        }
        return kw;
    }

    /**
     * 查询文本开头的词是否在词库树中，如果在，则返回对应的词，如果不在，则返回null。
     * 
     *
     * @param useType
     * @param pre 词的前一个字，如果为空，则表示前面没有内容
     * @param nextWordsTree 下一层树
     * @param stopwdMap
     * @param text 剩余的文本内容
     * @return 返回找到的词
     */
    public static SensitiveWord getSensitiveWord(Byte useType, String pre,
                                                 Map<String, Map> nextWordsTree, Map<String, SensitiveWord> stopwdMap, String text) {
        if (nextWordsTree == null || nextWordsTree.isEmpty()) {
            return null;
        }

        Map<String, Object> endTag = nextWordsTree.get(EndTagUtil.TREE_END_TAG);
        // 原始文本已到末尾
        if (StringUtils.isEmpty(text)) {
            // 如果有结束符，则表示匹配成功，没有，则返回null
            if (endTag != null) {
                return checkPattern(getKeyWord(useType, endTag), pre, null);
            } else {
                return null;
            }
        }
        String next = text.substring(0, 1);
        SensitiveWord stopWord = stopwdMap.get(next);
        if((stopWord != null || !EmojiUtil.isNotEmojiCharacter(next.charAt(0))) && endTag == null){//如果是停顿词,或者表情包
            SensitiveWord sensitiveWord = getSensitiveWord(useType,  pre, nextWordsTree, stopwdMap, text.substring(1));
            if(sensitiveWord != null) {
                sensitiveWord.incrementFilterLength();
            }
            return sensitiveWord;
        }
        String suffix = text.substring(0, 1);
        Map<String, Map> nextTree = nextWordsTree.get(next);
        // 没有遇到endTag，继续匹配
        if (endTag == null) {
            if (nextTree != null && nextTree.size() > 0) {
                // 没有结束标志，则表示关键词没有结束，继续往下走。
                return getSensitiveWord(useType,  pre, nextTree, stopwdMap, text.substring(1));
            }

            // 如果没有下一个匹配的字，表示匹配结束！
            return null;
        }

        // 有下一个匹配的词则继续匹配，一直取到最大的匹配关键字
        SensitiveWord tmp = null;
        if (nextTree != null && nextTree.size() > 0) {
            // 如果大于0，则表示还有更长的词，继续往下找
            tmp = getSensitiveWord(useType, pre, nextTree, stopwdMap, text.substring(1));
            if (tmp == null) {
                // 没有更长的词，则就返回这个词。在返回之前，先判断它是模糊的，还是精确的
                tmp = getKeyWord(useType, endTag);
            }
        }else{
            tmp = getKeyWord(useType, endTag);
        }
        // 没有往下的词了，返回该关键词。
        return checkPattern(tmp, pre, suffix);

    }

}
