package com.ruoyi.common.sensitive;



import com.ruoyi.common.sensitive.processor.*;
import com.ruoyi.common.sensitive.util.AnalysisUtil;
import com.ruoyi.common.sensitive.util.EmojiUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 关键词搜索器
 * @return
 */
public class SensitiveWordSeeker {

    /**
     * 所有的关键词
     */
    private Set<SensitiveWord> sensitiveWords;

    /**
     * 关键词树
     */
    private Map<String, Map> wordsTree = new ConcurrentHashMap<String, Map>();
    private Map<String, SensitiveWord> stopwdMap = new ConcurrentHashMap<>(); // 停顿词
    /**
     * 最短的关键词长度。用于对短于这个长度的文本不处理的判断，以节省一定的效率
     */
    private int wordLeastLen = 0;

    /**
     * 
     * @param sensitiveWords 关键词列表
     */
    public SensitiveWordSeeker(Set<SensitiveWord> sensitiveWords) {
        this.sensitiveWords = sensitiveWords;
        reloadKWSeeker();
    }

    /**
     * 初始化
     */
    public void reloadKWSeeker() {
        wordLeastLen = new DataInit().generalTree(getSensitiveWords(), wordsTree, stopwdMap);
    }

    /**
     * 添加一个或多个新的关键词。
     * 
     * @param newWord
     */
    public void addWord(Set<SensitiveWord> newWord) {
        if (newWord != null && newWord.size() > 0) {
            for (SensitiveWord kw : newWord) {
                if (StringUtils.isNotEmpty(kw.getWord())) {
                    getSensitiveWords().add(kw);
                }
            }
            reloadKWSeeker();
        }
    }

    /**
     * 清除所有的关键词
     */
    public void clear() {
        getSensitiveWords().clear();
        reloadKWSeeker();
    }

    /**
     * 是否包含指定的词
     * 
     * @param word 指定的关键词
     * @return true:包含,false:不包含
     */
    public boolean contains(String word) {
        if (sensitiveWords.isEmpty() || StringUtils.isEmpty(word)) {
            return false;
        }
        return getSensitiveWords().contains(word);
    }

    /**
     * 含有敏感的个数
     * 
     * @return 返回具体的数量
     */
    public int size() {
        return sensitiveWords == null ? 0 : sensitiveWords.size();
    }

    /**
     * @Description: 从词库中移除指定的关键词
     * @param sensitiveWord 指定需要被移除的关键词
     * @return boolean
     */
    public boolean remove(SensitiveWord sensitiveWord) {
        if(CollectionUtils.isNotEmpty(sensitiveWords)){
            for(SensitiveWord temp : sensitiveWords){
                if(temp.equals(sensitiveWord)){
                    return sensitiveWords.remove(temp);
                }
            }
            reloadKWSeeker();
        }
        return false;
    }


    /**
     * 从词库中移除指定的关键词
     * 
     * @param word 指定需要被移除的关键词
     * @return true:移除成功,false:移除失败
     */
    public boolean remove(String word) {
        if (getSensitiveWords().isEmpty() || StringUtils.isEmpty(word)) {
            return false;
        }
        boolean isRemove = sensitiveWords.remove(new SensitiveWord(word));
        if(isRemove) {
            reloadKWSeeker();
        }
        return isRemove;
    }

    // *************************操作接口****************************//

    /**
     * 使用指定的处理器进行处理！
     * 
     * @param proc 处理器
     * @param text 目标文本
     * @param fragment 命中词的处理器
     * @return 返回处理结果
     */
    public Object process(boolean filterEmoji, Byte useType, Processor proc, String text, AbstractFragment fragment) {
        if(filterEmoji) {
            text = EmojiUtil.filterEmoji(text);
        }
        return proc.process(wordsTree, stopwdMap, useType, text, fragment, wordLeastLen);
    }

    /**
     * 高亮处理器
     * 
     * @param text
     * @param fragment
     * @return
     */
    public String highlightWords(boolean filterEmoji,Byte useType, String text, AbstractFragment fragment) {
        return (String) process(filterEmoji,useType,new Highlight(), text, fragment);
    }

    /**
     * 将指定的字符串中的关键词提取出来。
     * 
     * @param text 指定的字符串。即：预处理的字符串
     * @return 返回其中所有关键词。如果没有，则返回空列表。
     */
    public List<SensitiveWordResult> findWords(boolean filterEmoji,Byte useType, String text) {
        return (List<SensitiveWordResult>) process(filterEmoji,useType,new WordFinder(), text, null);
    }

    /**
     * 返回将传入的字符串使用HTML的高亮方式处理之后的结果
     * 
     * @param text 传入需要处理的字符串。
     * @return 返回处理后的结果。
     */
    public String htmlHighlightWords(boolean filterEmoji, Byte useType, String text) {
        return (String) process(filterEmoji,useType, new Highlight(), text, new HTMLFragment("<font color='red'>","</font>"));
    }

    /**
     * 返回替换后的处理结果
     * @param filterEmoji 是否过滤emoji 或者 其他非文字类型的字符
     * @param text 传入需要处理的字符串。
     * @return 返回处理后的结果。
     */
    public String replaceWords(boolean filterEmoji, Byte useType, String text) {
        return (String) process(filterEmoji, useType, new Highlight(), text, new IgnoreFragment("***"));
    }

    /**
     * 返回替换后的处理结果
     * @param filterEmoji 是否过滤emoji 或者 其他非文字类型的字符
     * @param text 传入需要处理的字符串。
     * @param ignore 要替换的字符
     * @return 返回处理后的结果。
     */
    public String replaceWords(boolean filterEmoji, Byte useType, String text,String ignore) {
        return (String) process(filterEmoji, useType, new Highlight(), text, new IgnoreFragment(ignore));
    }


    /**
     * 
     * 数据初始化
     * 
     * @author hailin0@yeah.net
     * @createDate 2016年5月22日
     *
     */
    private static class DataInit {

        /**
         * 生成的临时词库树。用于在最后生成的时候一次性替换，尽量减少对正在查询时的干扰
         */
        private Map<String, Map> wordsTreeTmp = new ConcurrentHashMap<>();

        /**
         * 构造、生成词库树。并返回所有敏感词中最短的词的长度。
         * 
         * @param sensitiveWords 词库
         * @param wordsTree 聚合词库的树
         * @return 返回所有敏感词中最短的词的长度。
         */
        public int generalTree(Set<SensitiveWord> sensitiveWords, Map<String, Map> wordsTree, Map<String, SensitiveWord> stopwdMap) {
            if (sensitiveWords == null || sensitiveWords.isEmpty() || wordsTree == null) {
                if(wordsTree != null && !wordsTree.isEmpty()){
                    wordsTree.clear();
                    stopwdMap.clear();
                    wordsTreeTmp.clear();
                }
                return 0;
            }

            wordsTreeTmp.clear();
            int len = 0;
            for (SensitiveWord kw : sensitiveWords) {
                if(kw.getFilterType() != 2) {
                    if (len == 0) {
                        len = kw.getWordLength();
                    } else if (kw.getWordLength() < len) {
                        len = kw.getWordLength();
                    }
                    AnalysisUtil
                            .makeTreeByWord(wordsTreeTmp, StringUtils.trimToEmpty(kw.getWord()), kw);
                }else{
                    stopwdMap.put(kw.getWord(), kw);
                }
            }
            wordsTree.clear();
            wordsTree.putAll(wordsTreeTmp);
            return len;
        }
    }

    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "KWSeeker [sensitiveWords=" + sensitiveWords + ", wordsTree=" + wordsTree
                + ", wordLeastLen=" + wordLeastLen + "]";
    }

    public Set<SensitiveWord> getSensitiveWords() {
        if(sensitiveWords == null){
            sensitiveWords = new HashSet<>();
        }
        return sensitiveWords;
    }

    public void setSensitiveWords(Set<SensitiveWord> sensitiveWords) {
        this.sensitiveWords = sensitiveWords;
    }

    public Map<String, Map> getWordsTree() {
        return wordsTree;
    }

    public void setWordsTree(Map<String, Map> wordsTree) {
        this.wordsTree = wordsTree;
    }

    public int getWordLeastLen() {
        return wordLeastLen;
    }

    public void setWordLeastLen(int wordLeastLen) {
        this.wordLeastLen = wordLeastLen;
    }
}
