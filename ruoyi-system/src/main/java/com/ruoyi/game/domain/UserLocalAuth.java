package com.ruoyi.game.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 本地用户对象 user_local_auth
 *
 * @author ruoyi
 * @date 2023-04-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@TableName("user_local_auth")
public class UserLocalAuth extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 认证id（自增id）
     */
    @TableId
    private Long authId;

    /**
     * 用户key
     */
    @Excel(name = "用户key")
    private String userName;

    /**
     * 密码
     */
    @Excel(name = "密码")
    private String password;

    /**
     * 用户手机号
     */
    @Excel(name = "用户手机号")
    private String mobile;

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    public Long getAuthId() {
        return authId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("authId", getAuthId())
                .append("userName", getUserName())
                .append("password", getPassword())
                .append("mobile", getMobile())
                .toString();
    }
}
