package com.ruoyi.common.sensitive;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 关键词
 */
@Data
public class SensitiveWord implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6050328795034034286L;

    private Long id;
    /**
     * 关键词内容
     */
    private String word;
    /**关键字类型(1色情,2政治,3暴恐,4民生,5反动,6贪腐,7其他)*/
    private byte type;
    /**适用类型*/
    private byte useType;
    /**
     * （单字符）词的前缀,支持正则
     */
    private String pre;

    /**
     * （单字符）词的后缀，支持正则
     */
    private String sufix;

    /**
     * 过滤类型(0:替换敏感词,1:屏蔽敏感词,2:停顿词)
     */
    private byte filterType;

    /**
     * 替换字符
     * 表 : t_sensitive_str
     * 对应字段 : replace_str
     */
    private String replaceStr;
    /**
     * 关键词长度
     */
    private int wordLength = 0;

    /**
     * 过滤长度
     */
    transient private int filterLength = 0;

    /**
     * 前缀长度
     */
    private int prefixLength = 0;

    /**
     * 后缀长度
     */
    private int suffixLength = 0;

    public SensitiveWord() {

    }

    public SensitiveWord(String word) {
        this.word = word;
        this.wordLength = word.length();
    }
    /**
     * @param word
     */
    public SensitiveWord(Long id, String word) {
        this.id = id;
        this.word = word;
        this.wordLength = word.length();
    }

    /**
     * @param id
     * @param word
     * @param pre
     */
    public SensitiveWord(Long id, String word, byte type, byte useType, byte filterType, String pre) {
        this(id,word);
        this.type = type;
        this.filterType = filterType;
        this.pre = pre;
        this.useType = useType;
    }

    public SensitiveWord(Long id, String word, byte type, byte useType, byte filterType, String pre, String sufix) {
        this(id,word,type,useType,filterType,pre);
        this.sufix = sufix;
    }

    public SensitiveWord(Long id, String word, byte type, byte useType, byte filterType, String pre, String sufix, String replaceStr) {
        this(id,word,type,useType,filterType,pre,sufix);
        this.replaceStr = replaceStr;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((word == null) ? 0 : word.hashCode());
        result = prime * result + wordLength;
        result = prime * result + useType;
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SensitiveWord other = (SensitiveWord) obj;
        if(id != null && other.id != null && id.longValue() == other.id.longValue()){
            return true;
        }
        return hashCode() == obj.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "KeyWord [id="+id+",word=" + word + ",wordLength=" + wordLength + "]";
    }

    public void incrementFilterLength() {
        filterLength ++;
    }
}
