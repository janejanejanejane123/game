package com.ruoyi.game.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.*;

/**
 * 用户验证关联对象 user_auth_rel
 *
 * @author ruoyi
 * @date 2023-04-28
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@TableName("user_auth_rel")
public class UserAuthRel extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId
    private Long id;

    /**
     * 用户id
     */
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 验证表id
     */
    @Excel(name = "验证表id")
    private Long authId;

    /**
     * 验证表id
     */
    @Excel(name = "验证表id")
    private String authType;
}
