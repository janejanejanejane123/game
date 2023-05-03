package com.ruoyi.chat.config.properties;

import com.farsunset.cim.constant.WebsocketProtocol;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "cim.websocket")
public class CIMWebsocketProperties {

    private boolean enable;

    private Integer port;
    private String path;
    private WebsocketProtocol protocol;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public WebsocketProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(WebsocketProtocol protocol) {
        this.protocol = protocol;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
