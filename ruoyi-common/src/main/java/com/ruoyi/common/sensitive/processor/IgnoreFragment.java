package com.ruoyi.common.sensitive.processor;


import com.ruoyi.common.sensitive.SensitiveWord;

/**
 * @Description: 替换内容的片段处理方式
 * @return
 */
public class IgnoreFragment extends AbstractFragment {

    private String ignore = "";

    public IgnoreFragment() {
    }

    public IgnoreFragment(String ignore) {
        this.ignore = ignore;
    }

    @Override
    public String format(SensitiveWord word) {
        return ignore;
    }

}
