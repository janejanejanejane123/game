package com.ruoyi.common.core.auth;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/8/20,12:42
 * @return:
 **/
public class ApiAuthKey {

    private String keyStr;

    public static final ApiAuthKey REAL_NAME=new ApiAuthKey("real_name");
    public static final ApiAuthKey MERCHANT_ID=new ApiAuthKey("merchant_id");
    public static final ApiAuthKey MERCHANT_NUMBER=new ApiAuthKey("merchant_number");

    private ApiAuthKey(String keyStr){
        this.keyStr=keyStr;
    }







    @Override
    public int hashCode() {
        return keyStr.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ApiAuthKey)){
            return false;
        }


        return keyStr.equals(((ApiAuthKey) obj).keyStr);
    }
}
