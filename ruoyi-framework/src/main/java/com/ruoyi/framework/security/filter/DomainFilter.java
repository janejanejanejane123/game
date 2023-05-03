//package com.ruoyi.framework.security.filter;
//
//import com.alibaba.fastjson.JSON;
//import com.ruoyi.common.bussiness.RequestContext;
//import com.ruoyi.common.core.domain.AjaxResult;
//import com.ruoyi.common.exception.ServiceException;
//import com.ruoyi.common.utils.Assert;
//import com.ruoyi.common.utils.SecurityUtils;
//import com.ruoyi.common.utils.SpringContextUtil;
//import com.ruoyi.common.utils.chat.GeneralUtils;
//import com.ruoyi.common.utils.http.HttpUtils;
//import com.ruoyi.common.utils.ip.old.OldIpUtils;
//import com.ruoyi.framework.web.service.TokenService;
//import com.ruoyi.settings.domain.DomainManage;
//import com.ruoyi.settings.domain.SysMaintenance;
//import com.ruoyi.settings.service.ISysMaintenanceService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.annotation.Resource;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.function.Predicate;
//
///**
// * @param
// * @Author: sddd
// * @Date: 2022/8/21,12:23
// * @return:
// **/
//@Slf4j
//public class DomainFilter extends OncePerRequestFilter {
//
//
//    private DomainGetter domainGetter;
//    private static final ThreadLocal<DomainManage> THREAD_LOCAL=new ThreadLocal<>();
//
//
//
//    public DomainFilter(DomainGetter domainGetter){
//        Assert.isNull(domainGetter,"域名获取类不得为空");
//        this.domainGetter=domainGetter;
//    }
//
//
//
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        try {
//            if (domainGetter!=null){
//                String websiteHost = OldIpUtils.getWebsiteHost(request);
//                DomainManage domainByHost = domainGetter.getDomainByHost(websiteHost);
//
//                if (null==domainByHost||!websiteHost.equals(domainByHost.getDomain())){
//                    String s = JSON.toJSONString(domainByHost);
//                    logger.info("domain error {"+s+"}");
//                    AjaxResult ajaxResult = new AjaxResult(85301, "domain error");
//                    HttpUtils.writeAjaxMsg(request,response,null, JSON.toJSONString(ajaxResult));
//                    return;
//                }else if (1==domainByHost.getStatus()) {
//                    String s = JSON.toJSONString(domainByHost);
//                    logger.info("domain close {" + s + "}");
//                    AjaxResult ajaxResult = new AjaxResult(85302, "domain close");
//                    HttpUtils.writeAjaxMsg(request, response, null, JSON.toJSONString(ajaxResult));
//                    return;
//                    //如果是前台域名;
//                }else if (1==domainByHost.getType()){
//                    ISysMaintenanceService maintenanceService = SpringContextUtil.getBean(ISysMaintenanceService.class);
//                    Assert.test(maintenanceService==null,"system.error");
//                    String status = maintenanceService.getSysMaintenance();
//                    if (!"0".equals(status)){
//                        AjaxResult ajaxResult = new AjaxResult(85303, "系统维护中......");
//                        HttpUtils.writeAjaxMsg(request,response,null, JSON.toJSONString(ajaxResult));
//                        return;
//                    }
//                }
//                THREAD_LOCAL.set(domainByHost);
//            }
//            filterChain.doFilter(request,response);
//        }finally {
//            THREAD_LOCAL.remove();
//        }
//    }
//
//
//    public static String getSiteCode(){
//        DomainManage domainManage = THREAD_LOCAL.get();
//        if (domainManage==null){
//            return null;
//        }
//        return domainManage.getSiteCode();
//    }
//
//}
