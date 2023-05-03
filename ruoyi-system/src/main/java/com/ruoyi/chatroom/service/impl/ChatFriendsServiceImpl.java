package com.ruoyi.chatroom.service.impl;

import com.ruoyi.chatroom.db.domain.ChatFriends;
import com.ruoyi.chatroom.db.domain.ChatUserInfo;
import com.ruoyi.chatroom.db.repository.ChatFriendsRepository;
import com.ruoyi.chatroom.db.repository.ChatUserInfoRepository;
import com.ruoyi.chatroom.db.vo.ChatFriendsVo;
import com.ruoyi.chatroom.service.ChatFriendsService;
import com.ruoyi.chatroom.service.ChatUserInfoService;
import com.ruoyi.chatroom.service.IChatServeCustomerService;
import com.ruoyi.common.annotation.Overtime;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.clazz.CglibBeanCopierUtils;
import com.ruoyi.common.utils.clazz.ClassUtil;
import com.ruoyi.member.domain.UserMailBox;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @description: 好友业务实现类.
 * @author: nn
 * @create: 2022-07-31 11:42
 **/
@Service
@Lazy
public class ChatFriendsServiceImpl implements ChatFriendsService {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

//    @Resource
//    private TransitProcess transitProcess;

    @Resource
    private ChatroomFacade chatroomFacade;

    @Resource
    private RedisTemplate redisTemplate;

//    @Resource
//    private SnowflakeIdFactory snowflakeIdFactory;

    @Resource
    private ChatUserInfoRepository chatUserInfoRepository;

    @Resource
    @Lazy
    private ChatUserInfoService chatUserInfoService;

    @Resource
    private ChatFriendsRepository chatFriendsRepository;

    @Resource
    private IChatServeCustomerService chatServeCustomerService;

    /**
     * @Description: 我的好友列表(分页).
     * @param params
     * @param myUserId  我的userId
     * @return
     */
    @Override
    public TableDataInfo myFriendList(Map<String, Object> params, Long myUserId) {

        long pageNum = ClassUtil.formatObject(params.get("pageNum"),long.class);
        int pageSize = ClassUtil.formatObject(params.get("pageSize"),int.class);

        //思路：
        //1.分页查询出我的好友.
        //2.查询出我的好友的信息.
        //3.返回Vo.

        //1.分页查询出我的好友.
        Query query = new Query(Criteria.where("myUserId").is(myUserId))
                .skip((pageNum-1)* pageSize)
                .limit(pageSize)
                .with(Sort.by(Sort.Order.desc("createTime")));
        List<ChatFriends> chatFriendList = chatFriendsRepository.queryChatFriendsList(query);
        List<ChatFriendsVo> chatFriendsVoList = new ArrayList<>();

        //1.得到所有客服的UserId.
        List<Long> serveCustomerUserIdList = chatServeCustomerService.serveCustomerUserIdList();

        //当有好友的情况.
        if(chatFriendList.size() > 0){
            //2.查询出我的好友的信息.
            List<Long> userIdList = new ArrayList<>();
            for(ChatFriends friendUserId : chatFriendList){
                userIdList.add(friendUserId.getFriendUserId());
            }
            Query queryFriend = new Query();
            queryFriend.addCriteria(Criteria.where("userId").in(userIdList));
            List<ChatUserInfo> friendList = chatUserInfoRepository.queryChatUserInfoList(queryFriend);

            Boolean isOnline  = null;

            //3.返回Vo.
            for(ChatFriends chatFriend : chatFriendList){
                //赋值对象
                ChatFriendsVo chatFriendsVo  = new ChatFriendsVo();
                CglibBeanCopierUtils.copyProperties(chatFriendsVo,chatFriend);
                for(ChatUserInfo chatUserInfo : friendList){
                    if(chatUserInfo.getUserId().equals(chatFriend.getFriendUserId())){
                        chatFriendsVo.setFriendHead(chatUserInfo.getSmallHead());
                        chatFriendsVo.setFriendNikeName(chatUserInfo.getNikeName());

                        //如果自己是客服，好友就返回userName(客服端页面可以通过会员账号搜索).
                        if(serveCustomerUserIdList.contains(myUserId)){
//                            chatFriendsVo.setFriendUserName(chatUserInfo.getUserName());
                        }

                        //判断好友是否在线.
                        isOnline = redisTemplate.opsForHash().hasKey(Constants.ONLINE_WEB_USER_KEY, "0_" + chatUserInfo.getUserId());
                        if(!isOnline){
                            chatFriendsVo.setOnLine((byte) 0);
                        }else {
                            chatFriendsVo.setOnLine((byte) 1);
                        }
                    }
                }
                chatFriendsVoList.add(chatFriendsVo);
            }
        }

        return new TableDataInfo();
    }

    /**
     * @Description: 我的好友总记录数.
     * @param myUserId 我的userId
     * @return
     */
    @Override
    @Overtime(value = 3600)
    @Cacheable(key = "'t_chat_friends:myFriendTotalCount' + ':'+ #myUserId", value = "redisCache4Spring", sync = true)
    public int myFriendTotalCount(Long myUserId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("myUserId").is(myUserId);
        query.addCriteria(criteria);
        int totalCount = chatFriendsRepository.selectChatFriendsCount(query).intValue();
        return totalCount;
    }

    /**
     * @Description: 设置好友消息声音.
     * @param userIdentifier   好友用户标识
     * @param muteOrSound  静音mute  响声sound
     * @param myUserId  我的userId
     * @return
     */
    @Override
    public Long setMuteOrSound(String userIdentifier,String muteOrSound, Long myUserId) {

        //思路：
        //1.获取设置人的信息(我的).
        //2.获取被设置人的信息(好友).
        //3.设置.

        //1.获取设置人的信息(我的).//走缓存
        ChatUserInfo myInfo = chatUserInfoService.getChatUserInfoByUserId(myUserId);
        if (myInfo == null) {
            throw new ServiceException("设置人信息不存在!");
        }

        //2.获取被设置人的信息(好友). //走缓存
        ChatUserInfo friendInfo = chatUserInfoService.getChatUserInfoByUserIdentifier(userIdentifier);
        if (friendInfo == null) {
            throw new ServiceException("被设置人信息不存在!");
        }

        //3.设置.
        Query query = new Query();
        query.addCriteria(Criteria.where("myUserId").is(myUserId).and("friendUserId").is(friendInfo.getUserId()));
        Update update = new Update();
        update.set("muteOrSound",muteOrSound);
        long count = chatFriendsRepository.updateChatFriend(query,update);

        return count;
    }

    /**
     * @Description: 修改好友备注.
     * @param userIdentifier  好友用户标识
     * @param remark  好友备注
     * @param myUserId 我的userId
     * @return
     */
    @Override
    @Caching(evict={
            @CacheEvict(key = "'t_chat_user_info:getChatUserInfoVoByUserIdentifier'+ ':'+ #userIdentifier + ':'+ #myUserId", value = "redisCache4Spring")
    })
    public Long updateFriendRemark(String userIdentifier,String remark,Long myUserId) {

        //思路：
        //1.获取设置人的信息(我的).
        //2.获取被设置人的信息(好友).
        //3.修改好友备注.
        //4.修改我的好友版本信息

        //1.获取设置人的信息(我的).//走缓存
        ChatUserInfo myInfo = chatUserInfoService.getChatUserInfoByUserId(myUserId);
        if (myInfo == null) {
            throw new ServiceException("我的信息不存在!");
        }

        //2.获取被设置人的信息(好友).//走缓存
        ChatUserInfo friendInfo = chatUserInfoService.getChatUserInfoByUserIdentifier(userIdentifier);
        if (friendInfo == null) {
            throw new ServiceException("好友信息不存在!");
        }

        //3.修改好友备注.
        Query query = new Query();
        query.addCriteria(Criteria.where("myUserId").is(myUserId).and("friendUserId").is(friendInfo.getUserId()));
        Update update = new Update();
        update.set("remark",remark);

        long count = chatFriendsRepository.updateChatFriend(query,update);
        return count;
    }

    /**
     * @Description: 8.删除好友.
     * @param userIdentifier  好友用户标识
     * @param myUserId 我的userId
     * @return
     */
    @Override
    @Caching(evict={
            @CacheEvict(key = "'t_chat_friends:myFriendTotalCount:'+ #myUserId", value = "redisCache4Spring"),
            @CacheEvict(key = "'t_chat_user_info:getChatUserInfoByUserIdentifier:'+ #userIdentifier", value = "redisCache4Spring"),
            @CacheEvict(key = "'t_chat_user_info:getChatUserInfoVoByUserIdentifier:'+ #userIdentifier + ':'+ #myUserId", value = "redisCache4Spring")
    })
    @Transactional(transactionManager = "mongoTransactionManager")
    public Long deleteFriend(String userIdentifier,Long myUserId) {

        //思路：
        //1.获取删除人的信息(我的).
        //2.获取被删除人的信息(好友).
        //3.删除好友.
        //4.删除好友申请/邀请记录(逻辑删除)
        //5.修改版本号
        //6.删除相关缓存.

        //1.获取删除人的信息(我的).//走缓存
        ChatUserInfo myInfo = chatUserInfoService.getChatUserInfoByUserId(myUserId);
        if (myInfo == null) {
            throw new ServiceException("删除人信息不存在!");
        }

        //2.获取被删除人的信息(好友).//走缓存
        ChatUserInfo friendInfo = chatUserInfoService.getChatUserInfoByUserIdentifier(userIdentifier);
        if (friendInfo == null) {
            throw new ServiceException("被删除人信息不存在!");
        }

        //客服不能删除.
        boolean flag = chatServeCustomerService.isServeCustomer(userIdentifier,friendInfo.getUserId());
        if(flag){
            throw new ServiceException("客服不能删除!");
        }

        //3.删除好友.
        Query query = new Query();
        query.addCriteria(Criteria.where("myUserId").is(myUserId).and("friendUserId").is(friendInfo.getUserId()));
        long count = chatFriendsRepository.deleteChatFriend(query);

        return count;
    }


    /**
     *  @Description: 14.对离线服务获取确认.
     * @param ids  离线消息Id数组
     * @return
     */
    @Override
    public AjaxResult ackToFriend(Long[] ids,Long userId) throws InterruptedException {
        for(int i = 0; i < ids.length; i++){
//            transitProcess.ack(ids[i],userId);
        }
        return new AjaxResult();
    }

    /**
     * @Description:
     * @param myUserId 用户ID
     * @param friendUserId 朋友ID
     * @return boolean
     */
    @Override
    @Overtime(value = 3600)
    @Cacheable(key = "'t_chat_friends:hasMyFriends'+':'+#myUserId+':'+#friendUserId", value = "redisCache4Spring", sync = true)
    public boolean hasMyFriends(Long myUserId,Long friendUserId){
        Criteria criteria = new Criteria();
        criteria.orOperator(
                Criteria.where("friendUserId").is(friendUserId).and("myUserId").is(myUserId),
                Criteria.where("friendUserId").is(myUserId).and("myUserId").is(friendUserId)
        );
        Query query = new Query();
        query.addCriteria(criteria);
        long count = chatFriendsRepository.selectChatFriendsCount(query);

        return count == 2L ? true : false;
    }

    /**
     * 会员上下线消息回调.
     * @param userId 用户ID
     * @param online  1:在线 0:离线
     * @return
     */
    @Override
    @Async("taskExecutor")
    public AjaxResult onOfflinesCallback(Long userId, byte online) {
        //思路：
        //根据用户Id查询出我的好友(我存在好友的好友列表才算,因为他把我删了，我这边不一定把他删了 取chatFriends.getMyUserId() 就是好友的userId)
        //给每个好友发送上线下线消息.

        List<ChatFriends> friendList = queryMyFriendsList(userId);
        for(ChatFriends chatFriends : friendList){
            //赋值对象
            ChatFriendsVo chatFriendsVo  = new ChatFriendsVo();
            CglibBeanCopierUtils.copyProperties(chatFriendsVo,chatFriends);
            chatFriendsVo.setOnLine(online);
            //判断好友是否在线.
            Boolean isOnline = redisTemplate.opsForHash().hasKey(Constants.ONLINE_WEB_USER_KEY, "0_" + chatFriends.getMyUserId());
            if(isOnline){
                //给好友发送消息通知我上线 下线.
                UserMailBox mailBox = new UserMailBox();
                mailBox.setContent(chatFriendsVo.toString());
                mailBox.setUserIds(chatFriends.getMyUserId().toString());
                mailBox.setUserType(0);
                mailBox.setTopic("onOfflinesMessage");
                MessageBuilder builder = MessageBuilder.withPayload(mailBox);
                SendResult result = rocketMQTemplate.syncSend("ECOIN_MSG_TO_WEB:websocketMsg", builder.build());
                if (!SendStatus.SEND_OK.equals(result.getSendStatus())) {
                    throw new ServiceException("消息发送失败!");
                }
            }
        }

        return new AjaxResult();
    }

    /**
     * 查询我的好友记录集合.
     * @param
     * @return
     */
    @Override
    @Overtime(value = 3600)
    @Cacheable(key = "'t_chat_friends:queryMyFriendsList' + ':'+ #userId", value = "redisCache4Spring", sync = true)
    public List<ChatFriends> queryMyFriendsList(Long userId) {
        //根据用户Id查询出我的好友(我存在好友的好友列表才算,因为他把我删了，我这边不一定把他删了 就是好友的userId)
        Query query = new Query();
        query.addCriteria(Criteria.where("friendUserId").is(userId));
        List<ChatFriends> chatFriendList = chatFriendsRepository.queryMyFriendsList(query);
        return chatFriendList;
    }
}
