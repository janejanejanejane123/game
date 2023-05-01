package com.ruoyi.game.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户对象 t_user
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@TableName("tuser")
public class TUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId
    private Long uid;

    /** 账号 */
    @Excel(name = "账号")
    private String username;

    /** 密码 */
    @Excel(name = "密码")
    private String password;

    /** 真实姓名 */
    @Excel(name = "真实姓名")
    private String realname;

    /** 头像 */
    @Excel(name = "头像")
    private String photo;

    /** 提款密码 */
    @Excel(name = "提款密码")
    private String payPassword;

    /** 昵称 */
    @Excel(name = "昵称")
    private String nickname;

    /** 会员等级 */
    @Excel(name = "会员等级")
    private Long userLevel;

    /** 盐 */
    @Excel(name = "盐")
    private String salt;

    /** 状态 0 禁用 1启用 */
    @Excel(name = "状态 0 禁用 1启用")
    private Long disabled;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String email;

    /** 电话 */
    @Excel(name = "电话")
    private String telephone;

    /** 0 未认证 1 已认证 */
    @Excel(name = "0 未认证 1 已认证")
    private Long verifiedRealName;

    /** 上级id */
    @Excel(name = "上级id")
    private Long pid;

    /** 上级id数组 */
    @Excel(name = "上级id数组")
    private String pidArray;

    /** 0 普通会员 1 代理  */
    @Excel(name = "0 普通会员 1 代理 ")
    private Long type;

    /** 推广码 */
    @Excel(name = "推广码")
    private Long uniqueCode;

    /** 推广url码 */
    @Excel(name = "推广url码")
    private String url;

    /** 最近一次登录IP */
    @Excel(name = "最近一次登录IP")
    private String lastLoginIp;

    /** 最近登录城市 */
    @Excel(name = "最近登录城市")
    private String lastLoginCity;

    /** 商户ID */
    @Excel(name = "商户ID")
    private Long merchantId;
}
