package com.ruoyi.common.sensitive.util;



import com.ruoyi.common.sensitive.SensitiveWord;
import com.ruoyi.common.utils.clazz.CglibBeanCopierUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 关键词尾节点
 */
public class EndTagUtil implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8278503553932163596L;

    /**
     * 尾节点key
     */
    public static final String TREE_END_TAG = "end";

    public static Map<String, Map> buind(Map<String, Map> tree, SensitiveWord sensitiveWord) {
        Map<Byte, Map> endTag = tree.get(TREE_END_TAG);
        if(endTag == null){
            Map<Byte, Map> temp = new ConcurrentHashMap<>(2);
            if(tree.putIfAbsent(TREE_END_TAG,temp) == null){
                endTag = temp;
            }
        }
        if(endTag.get(sensitiveWord.getUseType()) == null) {
            endTag.putIfAbsent(sensitiveWord.getUseType(), CglibBeanCopierUtils.beanToStringMap(sensitiveWord));
        }
        return tree;
    }

}
