package com.ruoyi.framework.security.filter;

import com.ruoyi.settings.domain.DomainManage;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/8/21,15:48
 * @return:
 **/
public interface DomainGetter {


    DomainManage getDomainByHost(String host);


}
