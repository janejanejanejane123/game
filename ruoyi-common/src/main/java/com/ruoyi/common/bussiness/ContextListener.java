package com.ruoyi.common.bussiness;

import com.ruoyi.common.utils.DecryptContext;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/6,19:23
 * @return:
 **/
public interface ContextListener<T> {
    /**
     *
     * @param o
     */
    void beforeDecrypt(T o, DecryptContext context);
    /**
     *
     * @param o
     */
    void afterDecryptField(T o,DecryptContext context);
}
