package com.ruoyi.pay.domain.vo;

import com.ruoyi.pay.domain.AmountRecord;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PayResponVo extends AmountRecord {

    private String nickName;

    private String navurl;

    private Long effectiveTime;

    private List domainlList = new ArrayList();

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNavurl() {
        return navurl;
    }

    public void setNavurl(String navurl) {
        this.navurl = navurl;
    }

    public Long getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Long effectiveTime) {
        this.effectiveTime = effectiveTime;
    }
}
