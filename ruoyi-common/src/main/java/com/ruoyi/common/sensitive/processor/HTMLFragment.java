package com.ruoyi.common.sensitive.processor;


import com.ruoyi.common.sensitive.SensitiveWord;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: 高亮的方式。
 * @return
 */
public class HTMLFragment extends AbstractFragment {

    /**
     * 开口标记
     */
    private String open;

    /**
     * 封闭标记
     */
    private String close;

    /**
     * 初始化开闭标签
     * 
     * @param open 开始标签，如< b >、< font >等。
     * @param close 结束标签，如< /b >、< /font >等。
     */
    public HTMLFragment(String open, String close) {
        this.open = StringUtils.trimToEmpty(open);
        this.close = StringUtils.trimToEmpty(close);
    }

    @Override
    public String format(SensitiveWord word) {
        return open + word.getWord() + close;
    }
}
