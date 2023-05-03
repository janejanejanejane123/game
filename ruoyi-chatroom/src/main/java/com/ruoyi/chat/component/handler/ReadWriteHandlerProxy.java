package com.ruoyi.chat.component.handler;

import com.alibaba.fastjson.JSON;
import com.farsunset.cim.handler.CIMRequestHandler;
import com.farsunset.cim.model.SentBody;
import com.ruoyi.chat.component.disruptor.subevent.SubEventDispatcher;
import com.ruoyi.chat.component.handler.annotation.AppAsync;
import com.ruoyi.common.core.domain.model.chat.enums.MessageTypeEnum;
import com.ruoyi.chat.component.handler.annotation.AppController;
import com.ruoyi.chat.component.handler.annotation.AppRequestMapping;
import com.ruoyi.chat.component.handler.annotation.CIMHandler;
import com.ruoyi.chat.netty.AppResponse;
import com.ruoyi.common.core.domain.model.chat.MesEvent;
import com.ruoyi.chatroom.db.domain.ChatUserInfo;
import com.ruoyi.chatroom.service.ChatUserInfoService;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.filter.HTMLFilter;
import com.ruoyi.common.utils.JSONUtils;
import com.ruoyi.common.utils.SpringContextUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.chat.ChatContext;
import com.ruoyi.common.utils.chat.Dispatcher;
import com.ruoyi.common.utils.clazz.ClassUtil;
import com.ruoyi.framework.web.service.TokenService;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ParameterNameDiscoverer;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;


/**
 * 客户实现通话
 */

@CIMHandler(key = "read_write_handler")
@Slf4j
public class ReadWriteHandlerProxy implements CIMRequestHandler, ApplicationContextAware {

    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("TOKEN");
    public static AttributeKey<String> USER_TYPE = AttributeKey.valueOf("USER_TYPE");


    private final static HTMLFilter htmlFilter = new HTMLFilter();




    @Resource(name = "parameterNameDiscoverer")
    private ParameterNameDiscoverer parameterNameDiscoverer;
    @Resource
    private TokenService tokenService;

    @Resource
    private ChatUserInfoService chatUserInfoService;
    @Resource
    private Dispatcher messageDispatcher;

    private Map<String, MethodHandler> handlers=new HashMap<>();


    @Override
    public void process(Channel channel, SentBody sentBody) {
        if (!Constants.USER_TYPE_PLAYER.equals(channel.attr(USER_TYPE).get())){
            throw new ServiceException("403 player only");
        };

        String url= parseUrl(sentBody);
        if (!handlers.containsKey(url)){
            throw new ServiceException("404 error");
        }

        MethodHandler mh = handlers.get(url);
        if (mh.isAsync()){
            invokeAsync(mh,channel,sentBody.getData());
        }else {
            invoke(mh,channel,sentBody.getData());
        }
    }



    private void invoke(MethodHandler mh,Channel channel,Map<String,String> params){
        try {
            ChatContext.MES_EVENT_CHAIN.set(new LinkedList<>());
            ChatContext.CHANNEL_CONTEXT.set(channel);

            String token = channel.attr(TOKEN).get();
            LoginUser loginUser = tokenService.getLoginUser(token);

            if (!mh.checkPerm((LoginMember) loginUser)){
                throw new ServiceException("403 access error");
            };

            Map<Class, Object> extraParams = new HashMap<>();
            extraParams.put(Channel.class,channel);
            extraParams.put(LoginUser.class,loginUser);
            extraParams.put(LoginMember.class,loginUser);

            Object[] methodArg = getMethodArg(params, mh.getMethod(), extraParams);
            ChatContext.LOGIN_USER_CONTEXT.set(loginUser);
            validateParams(methodArg);
            Object invoke = mh.invoke(methodArg);
            if (invoke instanceof MesEvent){
                messageDispatcher.dispatch((MesEvent)invoke);
            }else if (invoke instanceof AppResponse){
                AppResponse appResponse = (AppResponse) invoke;

                MessageTypeEnum messageType = appResponse.getMessageType();
                if (messageType==null){
                    return;
                }
                MesEvent mesEvent = new MesEvent();
                mesEvent.setSendIdentifier(appResponse.getSender());
                mesEvent.setType(appResponse.getMessageType());
                mesEvent.setContent(appResponse.getData());
                mesEvent.setRecipient(appResponse.getReceiver());
                mesEvent.setNodeServer(appResponse.getRunServer());
                mesEvent.setSendTime(new Date());
                mesEvent.setTopic(appResponse.getTopic());
                switch (messageType){
                    case PRIVATE:
                        messageDispatcher.sendPrivate(mesEvent);
                        break;
                    case CHATROOM:
                        messageDispatcher.sendBroadcasting(mesEvent);
                        break;
                    case ACK:
                        messageDispatcher.sendAck(mesEvent);
                        break;
                    case SELF:
                        messageDispatcher.sendMsgToSelf(mesEvent);
                        break;
                    case ALL:
                        messageDispatcher.sendAll(mesEvent);
                        break;
                    default:
                        throw new ServiceException("错误的消息类型");
                }





            }else {
                if (mh.getReturnType()!=void.class&&mh.getReturnType()!=Void.class){
                    messageDispatcher.data2Self(invoke);
                }
            }
            messageDispatcher.dispatch(ChatContext.MES_EVENT_CHAIN.get());
        }catch (Exception e){
            messageDispatcher.errorMessage(e);
        }finally {
            ChatContext.LOGIN_USER_CONTEXT.remove();
            ChatContext.CHANNEL_CONTEXT.remove();
            ChatContext.MES_EVENT_CHAIN.remove();
        }
    }


    private void invokeAsync(MethodHandler mh,Channel channel,Map<String,String> params){
        SubEventDispatcher subEventDispatcher = SpringContextUtil.getBean(SubEventDispatcher.class);
        if (subEventDispatcher==null){
            invoke(mh,channel,params);
        }else {
            subEventDispatcher.onData(()->{
                invoke(mh,channel,params);
            });
        }
    }




    private void validateParams(Object[] methodArg) {

    }

    private String parseUrl(SentBody sentBody) {
        return sentBody.getData().get("url");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        handlers.clear();
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(AppController.class);
        beansWithAnnotation.forEach((k,v)->{
            Class<?> aClass = v.getClass();
            String baseUrl="";
            AppRequestMapping baseMapping = aClass.getAnnotation(AppRequestMapping.class);
            if (baseMapping!=null){
                baseUrl+=baseMapping.value();
            }
            boolean defaultAsync = aClass.isAnnotationPresent(AppAsync.class);
            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                int m = declaredMethod.getModifiers();
                //跳过静态方法,非公共,抽象方法
                if (!Modifier.isPublic(m)||Modifier.isStatic(m)||Modifier.isAbstract(m)){
                    continue;
                }
                AppRequestMapping methodMapping = declaredMethod.getAnnotation(AppRequestMapping.class);

                if (methodMapping!=null){
                    StringBuilder methodMappingStringBuilder = new StringBuilder(methodMapping.value());
                    if (baseUrl.endsWith("/")&& methodMappingStringBuilder.toString().startsWith("/")){
                        methodMappingStringBuilder = new StringBuilder(methodMappingStringBuilder.substring(1));
                    }else if (!baseUrl.endsWith("/")&&!methodMappingStringBuilder.toString().startsWith("/")){
                        methodMappingStringBuilder.insert(0, "/");
                    }
                    String methodMappingValue = methodMappingStringBuilder.toString();
                    String methodMappingUrl=baseUrl+methodMappingValue;
                    if (handlers.containsKey(methodMappingUrl)){
                        throw new ServiceException("重复的method url:"+methodMappingUrl);
                    }
                    handlers.put(methodMappingUrl,new MethodHandler(declaredMethod,v,defaultAsync));
                }
            }
        });
    }


    private Object[] getMethodArg(Map<String, String> requestArgMap, Method method, Map<Class, Object> classParameter) {
        // 获取方法的参数
        assert classParameter!=null;
        String[] names = parameterNameDiscoverer.getParameterNames(method);
        Parameter[] methodArgArr = method.getParameters();
        Object[] methodArg = new Object[methodArgArr.length];
        // 方法上有参数
        // 方法上有参数
        if (names!=null&&names.length>0){
            for (int i = 0; i < methodArgArr.length; i++) {
                // 参数名
                String methodArgName = names[i];
                // 参数类型
                Class<?> methodArgType = methodArgArr[i].getType();
                // 判断请求参数是否在方法参数中
                if (ChatUserInfo.class==methodArgType){
                    LoginUser o = (LoginUser)classParameter.get(LoginUser.class);
                    //查询当前的ChatUserInfo;
                    methodArg[i] = chatUserInfoService.getChatUserInfoByUserId(o.getUserId());
                }else if (classParameter.containsKey(methodArgType)) {
                    methodArg[i] = classParameter.get(methodArgType);
                } else if (requestArgMap != null && requestArgMap.containsKey(methodArgName)) {
                    String requestValue = requestArgMap.get(methodArgName);
                    boolean baseType = ClassUtil.isBaseType(requestValue);
                    if (baseType) {
                        methodArg[i] = ClassUtil.formatObject(requestValue, methodArgType);
                    } else if (StringUtils.isBlank(requestValue)) {
                        methodArg[i] = null;
                    } else if (methodArgType == requestValue.getClass() && String.class != requestValue.getClass()) {
                        methodArg[i] = requestValue;
                    } else if (String.class == requestValue.getClass() || requestValue.getClass().isArray() && !methodArgType.isArray()) {
                        String dataStr = null;
                        if(String.class == requestValue.getClass()){
                            dataStr = requestValue.toString();
                        }else {
                            List<String> dataList = ClassUtil.objToList(requestValue,false,String.class);
                            if(CollectionUtils.isNotEmpty(dataList)){
                                dataStr = dataList.get(0);
                            }
                        }
                        dataStr = htmlFilter.filter(dataStr);
                        if (JSONUtils.isJsonStr(dataStr)) {
                            methodArg[i] = JSON.parseObject(dataStr, methodArgType);
                        } else {
                            methodArg[i] = ClassUtil.formatObject(dataStr, methodArgType);
                        }
                    } else {
                        String dataStr = htmlFilter.filter(JSON.toJSONString(requestValue));
                        methodArg[i] = JSON.parseObject(dataStr, methodArgType);
                    }
                }
            }
        }


        return methodArg;
    }
}
