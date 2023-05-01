
package com.ruoyi.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

/**
 *  * @ClassName MapUtil
 *  * @Description this is a about map tools clazz
 *  * @Author 
 *  * @Date 2018年08月31日 10:46
 *  * @Version 1.0.0
 *  
 **/
public class MapSortUtil {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(MapSortUtil.class);

    /**
     * 功能描述:
     * this is a method about map sort by keys
     * @param data
     * @return: java.util.Map<java.lang.String , java.lang.String>
     **/
    public static Map<String,String> sortByKeys(Map<String,String>data)throws Exception{
        try {
            // let's sort this map by values first
            Map<String, String> sorted = data
                    .entrySet()
                    .stream()
                    .sorted(comparingByKey())
                    .collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
            return sorted;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Map is sort by keys is exception:"+e.getMessage());
            throw new Exception("Map is sort by keys is exception");
        }
    }

    /**
     * 功能描述:
     * this is a method about map sort by values
     * @param data
     * @return: java.util.Map<java.lang.String , java.lang.String>
     **/
    public static Map<String,String> sortByValues(Map<String,String> data) throws Exception{
        try {
            // now let's sort the map in decreasing order of value
            Map<String, String> sorted = data
                    .entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(comparingByValue()))
                    .collect(
                            toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                    LinkedHashMap::new));

            return sorted;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Map is sort by values is exception:"+e.getMessage());
            throw new Exception("Map is sort by values is exception!");
        }
    }

    /**
     * 根据Map获取排序拼接后的字符串
     *
     * @param map
     * @return
     */
    public static String getStringByMap(Map<String, String> map) {
        SortedMap<String, Object> smap = new TreeMap<String, Object>(map);
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> m : smap.entrySet()) {
            sb.append(m.getKey()).append("=").append(m.getValue()).append("&");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    public static String getStringByStringMap(Map<String, String> map) {
        SortedMap<String, Object> smap = new TreeMap<String, Object>(map);
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> m : smap.entrySet()) {
            sb.append(m.getKey()).append("=").append(m.getValue()).append("&");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }
}
