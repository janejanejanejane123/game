package com.ruoyi.game.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.*;

/**
 * 第三方用户对象 user_third_auth
 *
 * @author ruoyi
 * @date 2023-04-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@TableName("user_third_auth")
public class UserThirdAuth extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId
    private Long authId;

    /**
     * 第三方用户唯一标识
     */
    @Excel(name = "第三方用户唯一标识")
    private Long openid;

    /**
     * 第三方平台标识（wechat、QQ等）
     */
    @Excel(name = "第三方平台标识", readConverterExp = "w=echat、QQ等")
    private String apiKey;

    /**
     * 第三方获取的access_token（校验使用）
     */
    @Excel(name = "第三方获取的access_token", readConverterExp = "校=验使用")
    private String accessToken;
    /**
     * desKey
     */
    private String desKey;
}
