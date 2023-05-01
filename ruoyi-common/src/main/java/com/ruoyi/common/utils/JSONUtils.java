package com.ruoyi.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;

public final class JSONUtils {
    private static final JsonMapper OBJECT_MAPPER = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
            .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();


    public static String toJSONString(Object data) {

        try {
            return OBJECT_MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }

    }

    public static <T> T fromJson(String str, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(str, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T fromJson(byte[] data, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(data, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }


    public static <T> List<T> parseList(String str, Class<T> clazz) {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructCollectionLikeType(List.class,clazz);
        try {
            return OBJECT_MAPPER.readValue(str, javaType);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 判断对象是否为合法JSON Array的字符串
     *
     * @param object
     * @return boolean
     */
    public static boolean mayBeJSONArray(Object object) {
        if (object == null || !String.class.isAssignableFrom(object.getClass())) {
            return false;
        }
        String string = (String) object;
        if (string.isEmpty()) {
            return false;
        }
        char head = string.charAt(0);
        char tail = string.charAt(string.length()-1);
        return head == '['  && tail == ']';
    }

    /**
     * @Description: 判断是否json
     * @param str
     * @return boolean
     */
    public static boolean isJsonStr(String str) {
        boolean result = false;
        if (StringUtils.isNotBlank(str)) {
            str = str.trim();
            if (str.startsWith("{") && str.endsWith("}")) {
                result = true;
            } else if (str.startsWith("[") && str.endsWith("]")) {
                result = true;
            }
        }
        return result;
    }
}
