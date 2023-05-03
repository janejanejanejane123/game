package com.ruoyi.framework.security.filter;

import com.ruoyi.settings.domain.DomainManage;

import java.time.Year;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/8/22,16:13
 * @return:
 **/
public class DefaultDomainGetter implements DomainGetter {



    private final Short type;

    private DefaultDomainGetter(Short type){
        this.type=type;
    }

    public static DefaultDomainGetter of(Short type){
        return new DefaultDomainGetter(type);
    }


    @Override
    public DomainManage getDomainByHost(String host) {
        DomainManage domainManage = new DomainManage();
        domainManage.setStatus(1);
        domainManage.setId(1L);
        domainManage.setDomain(host);
        domainManage.setType(type);
        return domainManage;
    }
}
