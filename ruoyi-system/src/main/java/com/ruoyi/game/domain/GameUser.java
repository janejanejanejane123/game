package com.ruoyi.game.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@TableName("game_user")
public class GameUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String account;

}
