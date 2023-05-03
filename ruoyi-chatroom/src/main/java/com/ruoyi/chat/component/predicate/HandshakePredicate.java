package com.ruoyi.chat.component.predicate;

import com.farsunset.cim.handshake.HandshakeEvent;
import com.ruoyi.chatroom.service.ChatFriendOfflineService;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserTypeEnum;
import com.ruoyi.framework.web.service.TokenService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.function.Predicate;


/**
 * WS链接握手鉴权验证
 */
@Component
@Log4j2
public class HandshakePredicate implements Predicate<HandshakeEvent> {

    @Resource
    TokenService tokenService;


    /**
     *
     * @param event
     * @return true验证通过 false验证失败
     */
    @Override
    public boolean test(HandshakeEvent event) {

        /*
            可通过header或者uri传递参数
         */
        String token = event.getParameter("token");
        if(StringUtils.isEmpty(token)){
            return false;
        }
        LoginUser loginUser = tokenService.getLoginUser(token);
        if(loginUser == null){
            return false;
        }
        log.info("用户："+loginUser.getUsername()+" 登录成功！ ");
        return true;
    }
}
