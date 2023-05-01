//package com.ruoyi.web.filter;
//
//import com.alibaba.fastjson.JSON;
//import com.ruoyi.common.core.domain.AjaxResult;
//import com.ruoyi.constant.AuthConstants;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import java.io.*;
//
///**
// * 自定义Filter
// * 对请求的header 过滤token
// * <p>
// * 过滤器Filter可以拿到原始的HTTP请求和响应的信息，
// * 但是拿不到你真正处理请求方法的信息，也就是方法的信息
// *
// * @Component 注解让拦截器注入Bean，从而让拦截器生效
// * @WebFilter 配置拦截规则
// * <p>
// * 拦截顺序：filter—>Interceptor-->ControllerAdvice-->@Aspect -->Controller
// */
//@Slf4j
//@Component
//@WebFilter(urlPatterns = {"/**"}, filterName = "tokenFilter")
//public class TokenFilter implements Filter {
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        log.info("TokenFilter init {}", filterConfig.getFilterName());
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest req = (HttpServletRequest) request;
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("application/json; charset=utf-8");
//        String token = req.getHeader(AuthConstants.Token);
//        AjaxResult res = new AjaxResult();
//        String path = req.getRequestURI();
//        if (path.contains("/channelHandle")) {
//            chain.doFilter(request, response);
//            return;
//        }
//        if (null == token || token.isEmpty()) {
//            res = AjaxResult.error("403", "token没有认证通过!原因为：客户端请求参数中无token信息");
//        } else {
//            String gameToken = stringRedisTemplate.opsForValue().get("game.token");
//            if (gameToken == null) {
//                res = AjaxResult.error("403", "token没有认证通过!原因为：客户端请求中认证的token信息无效，请查看申请流程中的正确token信息");
//            }
//            String gameTokenString = gameToken.split(" ")[1];
//            if (null == gameToken.split(" ")[1]) {
//                res = AjaxResult.error("401", "该token目前已处于停用状态，请联系邮件系统管理员确认!");
//            }
//            if (!gameTokenString.equals(token)) {
//                res = AjaxResult.error("401", "该token目前已处于停用状态，请联系邮件系统管理员确认!");
//            } else {
//                res = AjaxResult.success();
//            }
//        }
//        if (!res.isSuccess()) {
//            PrintWriter writer = null;
//            OutputStreamWriter osw = null;
//            try {
//                osw = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
//                writer = new PrintWriter(osw, true);
//                String jsonStr = JSON.toJSONString(res);
//                writer.write(jsonStr);
//                writer.flush();
//                writer.close();
//                osw.close();
//            } catch (UnsupportedEncodingException e) {
//                log.error("过滤器返回信息失败:" + e.getMessage(), e);
//            } catch (IOException e) {
//                log.error("过滤器返回信息失败:" + e.getMessage(), e);
//            } finally {
//                if (null != writer) {
//                    writer.close();
//                }
//                if (null != osw) {
//                    osw.close();
//                }
//            }
//            return;
//        }
//        //UserUtils.setLoginUser();
//        //log.info("token filter过滤ok!");
//        //chain.doFilter(request, response);
//    }
//
//    @Override
//    public void destroy() {
//        log.info("TokenFilter destroy");
//    }
//}