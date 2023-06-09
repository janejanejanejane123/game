package com.ruoyi.game.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.*;

/**
 * 【请填写功能名称】对象 api_detail
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@TableName("api_detail")
public class ApiDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    @TableId
    private Long id;

    /** api_key */
    @Excel(name = "api_key")
    private String apiKey;
}
