package com.ruoyi.common.vo;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/13,17:51
 * @return:
 **/

public class EncStringParamsVo {

    private Object body;


    @SuppressWarnings("unchecked")
    public <T> T getBody() {
        return (T) body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
