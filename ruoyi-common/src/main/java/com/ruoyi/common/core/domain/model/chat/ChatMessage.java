package com.ruoyi.common.core.domain.model.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/10,15:41
 * @return:
 **/
@Data
public class ChatMessage {
    private int type;

    private String content;

}
