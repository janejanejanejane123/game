package com.ruoyi.common.enums;

/**
 * @description:
 * @author: nn
 * @create: 2022-07-24 13:33
 **/
public interface BaseEnum {
    Short getType();
    String getValue();
    String getDesc();
    BaseEnum getTypeByEnum(Short type);
}
