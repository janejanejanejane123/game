package com.ruoyi.chatroom.db.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection="t_chat_session")
public class ChatSession {

    @Id
    private Long id;

    private String userIdentifier;

    private String customerIdentifier;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date date;
}
