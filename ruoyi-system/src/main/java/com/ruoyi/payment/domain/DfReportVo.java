package com.ruoyi.payment.domain;

import java.util.List;

public class DfReportVo extends DfReport{

    private List<Long> uids;

    public List<Long> getUids() {
        return uids;
    }

    public void setUids(List<Long> uids) {
        this.uids = uids;
    }
}
