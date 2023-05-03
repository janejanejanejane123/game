package com.ruoyi.chat.component.handler.annotation;

import com.ruoyi.common.constant.ChatRoomConstants;

import java.lang.annotation.*;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/23,15:49
 * @return:
 **/

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Action {

    String group() default ChatRoomConstants.CHAT_ROOM_NAME;

}
