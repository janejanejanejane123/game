package com.ruoyi.chatroom.common;

/**
 * 请求id容器
 * @author nn
 *
 */
public abstract class ReqContext {
	private final static ThreadLocal<String> reqId = new ThreadLocal<String>();

	public static void setReqId(String uuid) {
		reqId.set(uuid);
	}

	public static String getReqId() {
		return reqId.get();
	}

	public static void removeReqId() {
		reqId.remove();
	}
}