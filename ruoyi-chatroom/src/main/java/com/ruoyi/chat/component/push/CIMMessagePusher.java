package com.ruoyi.chat.component.push;


import com.farsunset.cim.model.Message;

/*
 * 消息发送实接口
 * 
 */
public interface CIMMessagePusher {

	/*
	 * 向用户发送消息
	 * 
	 * @param msg
	 */
	void push(Message msg);

}
