package com.ruoyi.chatroom.db.vo;

import lombok.Data;

/**
 * @author st  //作者
 * @ClassName : SoundVo  //类名
 * @Description: 声音Vo
 * @date 2022-08-05 15:25  //时间
 */
@Data
public class SoundVo {
    /*图片ID*/
    private Long soundId;
    /*文件分组*/
    private String group;
    /*文件路径*/
    private String filePath;
}
