package com.ruoyi.common.core.domain.model.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/13,15:18
 * @return:
 **/
@Data
public class CommonContent {

    private Integer code;

    private Object message;

    private String extra;

    private String senderIdentifier;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date;

}
