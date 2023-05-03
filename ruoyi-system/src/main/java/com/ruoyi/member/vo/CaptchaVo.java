package com.ruoyi.member.vo;

import com.ruoyi.common.annotation.DecryptField;
import com.ruoyi.common.annotation.Detector;
import lombok.Data;
import lombok.ToString;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/9,13:38
 * @return:
 **/
@Data
@ToString
@Detector
public class CaptchaVo {
    /**
     * 旋转角度;
     */
    @DecryptField
    private String a;
    /**
     * 轨迹;
     */
    @DecryptField
    private String t;
    /**
     * 签名
     */
    @DecryptField
    private String m;
    /**
     * aes key 秘钥;
     */
    @DecryptField
    private String e;

}
