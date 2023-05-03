package com.ruoyi.chat.netty;

public enum RequestType {
    HTTP((byte)0, "http请求"),
    WEBSOCKET((byte)1, "websocket请求"),
    SYSTEM_CLUSTER((byte)2, "系统集群");

    private byte tyep;
    private String desc;

    private RequestType(byte tyep, String desc) {
        this.tyep = tyep;
        this.desc = desc;
    }

    public byte getTyep() {
        return this.tyep;
    }

    public void setTyep(byte tyep) {
        this.tyep = tyep;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
