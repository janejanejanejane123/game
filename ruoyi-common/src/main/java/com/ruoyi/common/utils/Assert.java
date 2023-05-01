/**
 * Copyright 2018 草榴总部 http://www.newage.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ruoyi.common.utils;

import com.ruoyi.common.exception.ServiceException;
import java.util.Collection;
import java.util.Map;

/**
 * 数据校验
 * @author
 * @email sunlightcs@gmail.com
 * @date 2018-13-23 15:50
 */
public abstract class Assert {



    public static void error(String msgCode,Object...args){
        throw new ServiceException(MessageUtils.message(msgCode,args));
    }

    public static void test(boolean expression,String msgCode){
        if (expression){
            throw new ServiceException(MessageUtils.message(msgCode));
        }
    }

    public static void test(boolean expression,String msgCode,Object...args){
        if (expression){
            throw new ServiceException(MessageUtils.message(msgCode,args));
        }
    }

    public static void test(boolean expression,String msgCode,Integer errCode){
        if (expression){
            throw new ServiceException(MessageUtils.message(msgCode),errCode);
        }
    }


    public static void isBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new ServiceException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object == null) {
            throw new ServiceException(message);
        }
    }

    /**
     * 断言对象不为空
     *
     * @param object
     * @param message
     */
    public static void isNotNull(Object object, String message) {
        if (object != null) {
            throw new ServiceException(message);
        }
    }
    /**
     * 断言字符串不为空
     *
     * @param str
     * @param message
     */
    public static void isNotBlank(String str, String message) {
        if (StringUtils.isNotBlank(str)) {
            throw new ServiceException(message);
        }
    }

    /**
     * 断言表达式为true
     *
     * @param expression
     * @param message
     */
    public static void isTrue(boolean expression, String message) {
        if (expression) {
            throw new ServiceException(message);
        }
    }

    /**
     * 断言表达式为false
     *
     * @param expression
     * @param message
     */
    public static void isFalse(boolean expression, String message) {
        isTrue(!expression, message);
    }




    /**
     * 断言集合为空
     *
     * @param coll
     * @param message
     */
    public static void isEmptys(Collection coll, String message) {
        if (coll == null || coll.isEmpty()) {
            throw new ServiceException(message);
        }
    }

    /**
     * 断言集合为空
     *
     * @param coll
     * @param message
     */
    public static void isNotEmptys(Collection coll, String message) {
        if (coll != null && !coll.isEmpty()) {
            throw new ServiceException(message);
        }
    }

    /**
     * 断言集合为空
     *
     * @param coll
     * @param message
     */
    public static void isMapEmptys(Map coll, String message) {
        if (coll == null || coll.isEmpty()) {
            throw new ServiceException(message);
        }
    }
}
