package com.ruoyi.common.core.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/13,14:56
 * @return:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {

    private String code;

    private String message;


    public static ErrorMessage error(String code,String message){
        return new ErrorMessage(code,message);
    }

}
