package com.ruoyi.common.enums;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ValidateUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/29,16:09
 * @return:
 **/
public enum  EmailEnums {
    //绑定邮箱
    BINDEMAIL((short)1,"绑定邮箱", (uid)->"bindEmail:"+ uid),
    RESETPASSWORD((short)2,"重置密码", (uid)->"resetPassword:"+ uid),
    ADDCARD((short)3,"添加银行卡", (uid)->"addCard:"+ uid),
    DELETECREDIT((short)4,"删除付款方式", (uid)->"deleteCard:"+ uid),
    SELLCOIN((short)5,"游戏交易", (uid)->"sellCoin:"+ uid),
    OPENORCLOSEEMAIL((short)6,"开启或关闭邮箱验证",(uid)->"openOrCloseEmailVeri:"+uid);

    private static final Map<Short,EmailEnums> SHORT_EMAIL_ENUMS_MAP=new HashMap<>();

    static {
        EmailEnums[] values = EmailEnums.values();
        for (EmailEnums value : values) {
            SHORT_EMAIL_ENUMS_MAP.put(value.type,value);
        }
    }

    private short type;

    private String msg;
    private Function<Object,String> callback;

    EmailEnums(short type, String msg, Function<Object,String> callback) {
        this.type=type;
        this.msg=msg;
        this.callback=callback;
    }

    public static EmailEnums getEmailType(short type){
        return SHORT_EMAIL_ENUMS_MAP.get(type);
    }

    public short getType(){
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public String redisKey(Long uid){
        return this.callback.apply(uid);
    }
};








