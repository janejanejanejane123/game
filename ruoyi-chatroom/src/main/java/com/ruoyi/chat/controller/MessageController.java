package com.ruoyi.chat.controller;

import com.farsunset.cim.model.Message;
import com.ruoyi.chat.component.push.DefaultMessagePusher;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/api/message")
@Api(produces = "application/json", tags = "消息相关接口" )
public class MessageController  {

	@Resource
	MongoTemplate mongoTemplate;

	@Resource
	private DefaultMessagePusher defaultMessagePusher;

	@ApiOperation(httpMethod = "POST", value = "发送消息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sender", value = "发送者UID", paramType = "query", dataTypeClass = String.class, required = true, example = ""),
			@ApiImplicitParam(name = "receiver", value = "接收者UID", paramType = "query", dataTypeClass = String.class, required = true, example = ""),
			@ApiImplicitParam(name = "action", value = "消息动作", paramType = "query", dataTypeClass = String.class, required = true, example = ""),
			@ApiImplicitParam(name = "title", value = "消息标题", paramType = "query", dataTypeClass = String.class, example = ""),
			@ApiImplicitParam(name = "content", value = "消息内容", paramType = "query", dataTypeClass = String.class,  example = ""),
			@ApiImplicitParam(name = "format", value = "消息格式", paramType = "query", dataTypeClass = String.class,  example = ""),
			@ApiImplicitParam(name = "extra", value = "扩展字段", paramType = "query", dataTypeClass = String.class, example = ""),
	})
	@PostMapping(value = "/send")
	public ResponseEntity<Long> send(@RequestParam String sender ,
									 @RequestParam String receiver ,
									 @RequestParam String action ,
									 @RequestParam(required = false) String title ,
									 @RequestParam(required = false) String content ,
									 @RequestParam(required = false) String format ,
									 @RequestParam(required = false) String extra)  {

		Message message = new Message();
		message.setSender(sender);
		message.setReceiver(receiver);
		message.setAction(action);
		message.setContent(content);
		message.setFormat(format);
		message.setTitle(title);
		message.setExtra(extra);

		message.setId(System.currentTimeMillis());

		defaultMessagePusher.push(message);

		return ResponseEntity.ok(message.getId());
	}
}
