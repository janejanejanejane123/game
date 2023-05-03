package com.ruoyi.chatroom.service.impl;


import com.alibaba.fastjson.JSON;
import com.ruoyi.chatroom.db.domain.ChatFriendRecord;
import com.ruoyi.chatroom.db.domain.ChatRoomRecord;
import com.ruoyi.chatroom.db.repository.ChatFriendRecodRepository;
import com.ruoyi.chatroom.db.repository.ChatRoomRecordRepository;
import com.ruoyi.chatroom.db.vo.ForwardMessageVo;
import com.ruoyi.chatroom.db.vo.ImageMessageVo;
import com.ruoyi.chatroom.db.vo.SoundVo;
import com.ruoyi.chatroom.domain.ChatEmoji;
import com.ruoyi.chatroom.domain.ChatSettings;
import com.ruoyi.chatroom.service.ChatFriendRecordService;
import com.ruoyi.chatroom.service.IChatEmojiService;
import com.ruoyi.chatroom.service.IChatSettingsService;
import com.ruoyi.common.constant.OriginalMsgType;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.redis.RedisLock;
import com.ruoyi.common.core.redis.RedisScriptStrings;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.enums.ChatMessageType;
import com.ruoyi.common.enums.ChatRoomConfigEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.sensitive.service.SensitiveWordService;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.chat.GeneralUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.common.utils.file.MinioUtil;
import com.ruoyi.common.utils.security.MD5Util;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Future;

/**
 * @description: 聊天室门面
 **/
@Component
@Lazy
public class ChatroomFacade {
    @Resource
    private SensitiveWordService sensitiveWordService;
    @Resource
    private ChatRoomRecordRepository chatRoomRecordRepository;
    @Resource
    private ChatFriendRecodRepository chatFriendRecodRepository;
    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;
    @Resource
    private IChatSettingsService chatSettingsService;
    @Resource
    private MinioUtil minioUtil;
    @Resource
    private RedisCache redisCache;
    @Resource
    private IChatEmojiService chatEmojiService;
    /**
     * 检查聊天室是否维护(注意:此处的true or false是开关的 所以 true 是启用 不是关闭).
     *
     * @return
     */
    public void checkChatRoomMaintenance() {
        //聊天室开关.
        ChatSettings chatRoomSwitch = chatSettingsService.getSettingByKey(ChatRoomConfigEnum.CHAT_SWITCH.getKey());
        String chatRoom = chatRoomSwitch.getConfigValue();
        Assert.test(!Convert.toBool(chatRoom), "chat.room.off");
        //聊天室大厅开关.
        ChatSettings chatRoomLobbySwitch = chatSettingsService.getSettingByKey(ChatRoomConfigEnum.CHAT_HALL_SWITCH.getKey());
        String chatRoomLobby = chatRoomLobbySwitch.getConfigValue();
        Assert.test(!Convert.toBool(chatRoomLobby), "chat.lobby.off");
    }


    @Async("taskExecutor")
    public Future<ChatRoomRecord> getChatRoomRecord(Long messageId, Long userId) {
        return new AsyncResult<ChatRoomRecord>(chatRoomRecordRepository.getChatRoomRecord(messageId, userId));
    }


    public Object processingMsgType(Object msg, ChatMessageType chatMessageType,String forwardSourceId) throws Exception {

        Assert.test(chatMessageType == null,"system.error");
        Long userId = GeneralUtils.getCurrentLoginUser().getUserId();

        switch (chatMessageType){
            case IMAGE:
                msg = getImageMessageVo(userId,msg, chatMessageType);
                break;
            case CUSTOMER_SERVICE_IMAGE:
                msg = getImageMessageVo(userId,msg, chatMessageType);
                break;
            case CUSTOMER_SERVICE_SOUND:
                msg = getSoundVo(msg);
                break;
            case STRING:
                if(msg != null) {
                    return sensitiveWordService.checkSensitiveMsg((byte) 1, "您的发言包含敏感关键词(%s)!!!", msg.toString());
                }
                break;
            case CHAT_EMOTICON:
                msg = getImageMessageVo(userId,msg, chatMessageType);
                break;
            case FORWARD_MESSAGE:
                msg = getForwardMessageVo( userId, msg, forwardSourceId);
                break;
            default:
                Assert.error("system.error");
        }
        return msg;

    }


    /**
     * @param userId    用户ID
     * @param userName 用户名称
     * @return {@link String}
     * @Description: 获取用户标识，方便每次不用去查数据库或者缓存
     */
    public String getUserIdentifier(Long userId, String userName) {
        SimpleHash hash = new SimpleHash("SHA-512", "∴⅛∵·⅜∶½⅓∞⅔⅛∷" + MD5Util.getMd5(userId + "≌s↖df←△@fdg~|`ξ" + userName) + "∈" + userName, userName + "∑:↑<ψ" + userId + ">∏↓⅞", 3);
        String md5Str = MD5Util.getMd5("︼❝" + hash.toHex() + "々☀♫♬♩♭♪☆∷");
        md5Str = md5Str.substring(0, 16) + md5Str.hashCode() + "♧♡♂♀" + md5Str.substring(16);
        hash = new SimpleHash("SHA-512", md5Str + "；*）#‖壹@±" + md5Str.hashCode(), "捌AES:想⊙破⌒⊥解，吃≤你≥的∧屎∨去√吧，还∫∮有$100层θ密ζ码η等κ你㎝呢，哈㎥哈㎡哈" + md5Str + "/*÷【{《→" + md5Str.hashCode() + ">}]asd@#%我⊇操≡你Ⓟ妈ⓔ逼ASKHI", 3);
        md5Str = MD5Util.getMd5(" ͜✿҉ ͜ღ҉ ℘ ೄ ζั͡ ต" + hash.toHex() + "｡◕‿◕｡");
        StringBuilder sb = new StringBuilder(50);
        sb.append(md5Str.hashCode() >> 8).append(":").append(md5Str.substring(0, 8)).append("-").append(md5Str.substring(8, 12)).append("-").append(md5Str.substring(12, 16))
                .append("-").append(md5Str.substring(16, 20)).append("-").append(md5Str.substring(20, 32)).append(":").append(md5Str.hashCode() << 8);
        return sb.toString();
    }


    public boolean isForbiddenToTalk(Long userId) {
        return false;
    }


    private SoundVo getSoundVo(Object msg) {
        SoundVo soundVo = JSON.parseObject(JSON.toJSONString(msg), SoundVo.class);


        if(soundVo.getSoundId() == null || soundVo.getSoundId() < 0){
            throw new ServiceException(MessageUtils.message("chat.params.error"));
        }

        String path=getFilePath(String.valueOf(soundVo.getSoundId()));
        soundVo.setFilePath(path);
        return soundVo;

    }

    private ImageMessageVo getImageMessageVo(Long userId,Object msg,ChatMessageType chatMessageType) {
        ImageMessageVo imageMessageVo = null;
        try {
            imageMessageVo = JSON.parseObject(JSON.toJSONString(msg), ImageMessageVo.class);
        } catch (Exception e) {
            throw new ServiceException(MessageUtils.message("system.error"));
        }
        if (imageMessageVo.getImageId() == null || imageMessageVo.getImageId() < 0) {
            throw new ServiceException(MessageUtils.message("chat.params.error"));
        }
        String imagePath=null;

        if (chatMessageType == ChatMessageType.IMAGE) {
            imagePath = getFilePath(String.valueOf(imageMessageVo.getImageId()));
        } else if (chatMessageType == ChatMessageType.CHAT_EMOTICON) {
            ChatEmoji chatEmoji = chatEmojiService.selectChatEmojiByEmojiId(imageMessageVo.getImageId());
            imagePath=chatEmoji.getFilePath();
        } else if (chatMessageType == ChatMessageType.CUSTOMER_SERVICE_IMAGE) {

            //feinData = uploadFileFeign.getCustomerServiceFileAndTypeById(siteCode, imageMessageVo.getImageId());
        }
        if (imagePath == null) {
            throw new ServiceException("文件不存在");
        }

        imageMessageVo.setThumbnailPath(imagePath);
        imageMessageVo.setFilePath(imagePath);
        return imageMessageVo;
    }


    private ForwardMessageVo getForwardMessageVo( Long userId, Object msg, String forwardSourceId) throws Exception {
        ForwardMessageVo imageMessageVo = JSON.parseObject(JSON.toJSONString(msg), ForwardMessageVo.class);


        if(imageMessageVo.getOriginalMessageId() == null || imageMessageVo.getOriginalType() == null || forwardSourceId == null ){
            throw new ServiceException(MessageUtils.message("chat.params.error"));
        }
        ChatMessageType chatMessageType = ChatMessageType.getMessageTypeByType(imageMessageVo.getMsgType());
        if(chatMessageType == ChatMessageType.FORWARD_MESSAGE){
            throw new ServiceException(MessageUtils.message("chat.params.error"));
        }
        OriginalMsgType originalMsgType = OriginalMsgType.getOriginalMsgType(imageMessageVo.getOriginalType());
        if(originalMsgType == null){
            throw new ServiceException(MessageUtils.message("chat.params.error"));
        }
        Byte msgType = null;
        Object msgBody = null;
        switch (originalMsgType){
            case ORIGINAL_USER:
                ChatFriendRecord friendChartRecord = chatFriendRecodRepository.getChatFriendRecordById(imageMessageVo.getOriginalMessageId());
                if(friendChartRecord != null){
                    msgType = friendChartRecord.getMsgType();
                    msgBody = friendChartRecord.getMsgBody();
                }
                break;
            case ORIGINAL_GROUP:
                ChatRoomRecord chatRoomRecord = chatRoomRecordRepository.getChatRoomRecord(imageMessageVo.getOriginalMessageId(),null);
                if(chatRoomRecord != null){
                    msgType = chatRoomRecord.getMsgType();
                    msgBody = chatRoomRecord.getMsgBody();
                }
                break;
            default:
                break;
        }
        ChatMessageType chatMessageTypeTemp = ChatMessageType.getMessageTypeByType(msgType);
        if((chatMessageTypeTemp == ChatMessageType.IMAGE || chatMessageTypeTemp == ChatMessageType.STRING || chatMessageTypeTemp == ChatMessageType.CHAT_EMOTICON)){
            imageMessageVo.setMsgType(msgType);
            imageMessageVo.setMsgBody(msgBody);
        }else{
            throw new ServiceException(MessageUtils.message("chat.params.error"));
        }
        imageMessageVo.setForwardSourceId(forwardSourceId);
        return imageMessageVo;
    }


    private String getFilePath(String id){
        return redisCache.getAndDelete( MimeTypeUtils.UPFILE_VERIFYID_KEY +id);
    }

    public void limitedChat(String key){
        RedisLock.limited(key,5,10);
    }

    public void checkMerchantClose(Long merchantId) {

    }

    public void checkCashLock(Long userId) {

    }
}
