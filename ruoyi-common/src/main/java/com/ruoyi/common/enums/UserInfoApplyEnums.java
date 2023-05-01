package com.ruoyi.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/28,15:39
 * @return:
 **/
public enum  UserInfoApplyEnums {
    /**
     * 人工审核配置
     */

    VX_QR_CODE("微信收款码申请",  (short)1 , 1,"qrCodeVerify","member:apply:edit","apply:qrCode"),
    ZFB_QR_CODE("支付宝收款码申请",  (short)2,1,"qrCodeVerify","member:apply:edit","apply:qrCode"),
    QQ_QR_CODE("QQ收款码申请",  (short)3,1,"qrCodeVerify","member:apply:edit","apply:qrCode"),
    ZFB_ACOUNT("支付宝账号",  (short)4,1,"qrCodeVerify","member:apply:edit","apply:qrCode"),



    REAL_NAME_VERIFY("实名认证申请",  (short)6,1,"realNameVerify","member:registered:edit","apply:realName"),
    SALE_COIN_VERIFY("卖蛋申请",  (short)7,1,"saleCoinVerify","system:order:edit","apply:saleCoin"),
    PROXY_APPLY("代理申请",  (short)8,1,"proxyApply","member:proxy:edit","apply:proxy"),
    TRANSFER_VERIFY("转账申请",  (short)9,1,"transfer","transfer:edit","transfer:edit"),
//    PHOTO("用户头像申请",  (short)3,1),
//    NICKNAME("用户昵称申请",  (short)4,1),
//    REGISTERED("注册审核",(short)5,0, false);
    ;
    private static final Map<String,UserInfoApplyEnums> COLL =new HashMap<>();
    static {
        UserInfoApplyEnums[] values = values();
        for (UserInfoApplyEnums value : values) {
            COLL.put(value.topic,value);
        }
    }

    public static UserInfoApplyEnums find(String topic){
        return COLL.get(topic);
    }

    UserInfoApplyEnums(String desc,short type,int multi){
        this(desc,type,multi,true);
    }

    UserInfoApplyEnums(String desc,short type,int multi,boolean login){
        this.desc=desc;
        this.type=type;
        this.multi=multi;
        this.login=login;
    }


    UserInfoApplyEnums(String desc,short type,int multi,String topic,String perm,String redisKey){
        this.desc=desc;
        this.type=type;
        this.multi=multi;
        this.topic=topic;
        this.perm=perm;
        this.redisKey=redisKey;
        this.login=true;
    }

    public String getDesc() {
        return desc;
    }

    public short getType() {
        return type;
    }

    public int getMulti() {
        return multi;
    }

    private String desc;

    private short type;

    public boolean isLogin() {
        return login;
    }

    public String getTopic() {
        return topic;
    }

    public String getPerm() {
        return perm;
    }

    private int multi;

    private boolean login;

    private String topic;

    private String perm;

    public String getRedisKey() {
        return redisKey;
    }

    private String redisKey;


}
