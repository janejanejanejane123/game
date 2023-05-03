package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * system对象 t_session
 * 
 * @author ry
 * @date 2022-05-24
 */
public class TSession extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    public static final transient int STATE_ACTIVE = 0;
    public static final transient int STATE_APNS = 1;
    public static final transient int STATE_INACTIVE = 2;

    public static final transient String CHANNEL_IOS = "ios";
    public static final transient String CHANNEL_ANDROID = "android";
    public static final transient String CHANNEL_WINDOWS = "windows";
    public static final transient String CHANNEL_MAC = "mac";
    public static final transient String CHANNEL_WEB = "web";

    /** 主键ID */
    private Long id;

    /** 终端应用版本 */
    @Excel(name = "终端应用版本")
    private String appVersion;

    /** 登录时间 */
    @Excel(name = "登录时间")
    private Long bindTime;

    /** 终端设备类型 */
    @Excel(name = "终端设备类型")
    private String channel;

    /** 客户端ID (设备号码+应用包名) */
    @Excel(name = "客户端ID (设备号码+应用包名)")
    private String deviceId;

    /** 终端设备型号 */
    @Excel(name = "终端设备型号")
    private String deviceName;

    /** session绑定的服务器IP */
    @Excel(name = "session绑定的服务器IP")
    private String host;

    /** 终端语言 */
    @Excel(name = "终端语言")
    private String language;

    /** 维度 */
    @Excel(name = "维度")
    private Integer latitude;

    /** 用户名 */
    @Excel(name = "用户名")
    private String location;

    /** 经度 */
    @Excel(name = "经度")
    private Integer longitude;

    /** session在本台服务器上的ID */
    @Excel(name = "session在本台服务器上的ID")
    private String nid;

    /** 终端系统版本 */
    @Excel(name = "终端系统版本")
    private String osVersion;

    /** 状态 */
    @Excel(name = "状态")
    private Integer state;

    /** ession绑定的用户id */
    @Excel(name = "ession绑定的用户id")
    private String uid;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setAppVersion(String appVersion) 
    {
        this.appVersion = appVersion;
    }

    public String getAppVersion() 
    {
        return appVersion;
    }
    public void setBindTime(Long bindTime) 
    {
        this.bindTime = bindTime;
    }

    public Long getBindTime() 
    {
        return bindTime;
    }
    public void setChannel(String channel) 
    {
        this.channel = channel;
    }

    public String getChannel() 
    {
        return channel;
    }
    public void setDeviceId(String deviceId) 
    {
        this.deviceId = deviceId;
    }

    public String getDeviceId() 
    {
        return deviceId;
    }
    public void setDeviceName(String deviceName) 
    {
        this.deviceName = deviceName;
    }

    public String getDeviceName() 
    {
        return deviceName;
    }
    public void setHost(String host) 
    {
        this.host = host;
    }

    public String getHost() 
    {
        return host;
    }
    public void setLanguage(String language) 
    {
        this.language = language;
    }

    public String getLanguage() 
    {
        return language;
    }
    public void setLatitude(Integer latitude) 
    {
        this.latitude = latitude;
    }

    public Integer getLatitude() 
    {
        return latitude;
    }
    public void setLocation(String location) 
    {
        this.location = location;
    }

    public String getLocation() 
    {
        return location;
    }
    public void setLongitude(Integer longitude) 
    {
        this.longitude = longitude;
    }

    public Integer getLongitude() 
    {
        return longitude;
    }
    public void setNid(String nid) 
    {
        this.nid = nid;
    }

    public String getNid() 
    {
        return nid;
    }
    public void setOsVersion(String osVersion) 
    {
        this.osVersion = osVersion;
    }

    public String getOsVersion() 
    {
        return osVersion;
    }
    public void setState(Integer state) 
    {
        this.state = state;
    }

    public Integer getState() 
    {
        return state;
    }
    public void setUid(String uid) 
    {
        this.uid = uid;
    }

    public String getUid() 
    {
        return uid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("appVersion", getAppVersion())
            .append("bindTime", getBindTime())
            .append("channel", getChannel())
            .append("deviceId", getDeviceId())
            .append("deviceName", getDeviceName())
            .append("host", getHost())
            .append("language", getLanguage())
            .append("latitude", getLatitude())
            .append("location", getLocation())
            .append("longitude", getLongitude())
            .append("nid", getNid())
            .append("osVersion", getOsVersion())
            .append("state", getState())
            .append("uid", getUid())
            .toString();
    }
}
