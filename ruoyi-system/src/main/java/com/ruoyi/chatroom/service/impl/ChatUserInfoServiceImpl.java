package com.ruoyi.chatroom.service.impl;

import com.ruoyi.chatroom.db.domain.ChatFriends;
import com.ruoyi.chatroom.db.domain.ChatUserInfo;
import com.ruoyi.chatroom.db.repository.ChatFriendsRepository;
import com.ruoyi.chatroom.db.repository.ChatUserInfoRepository;
import com.ruoyi.chatroom.db.vo.ChatUserInfoVo;
import com.ruoyi.chatroom.service.ChatFriendOfflineService;
import com.ruoyi.chatroom.service.ChatFriendsService;
import com.ruoyi.chatroom.service.ChatUserInfoService;
import com.ruoyi.chatroom.service.IChatServeCustomerService;
import com.ruoyi.common.annotation.Overtime;
import com.ruoyi.common.constant.ChatConstant;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.redis.RedisLock;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SpringContextUtil;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.clazz.CglibBeanCopierUtils;
import com.ruoyi.common.vo.MemberMessageVo;
import com.ruoyi.member.util.ShortUrlUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/18,15:51
 * @return:
 **/
@Service
public class ChatUserInfoServiceImpl implements ChatUserInfoService {

    private static final Logger logger = LoggerFactory.getLogger(ChatUserInfoServiceImpl.class);

    @Resource
    private ChatUserInfoRepository chatUserInfoRepository;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ShortUrlUtil shortUrlUtil;

    @Resource
    private ChatroomFacade chatroomFacade;

    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;

    @Resource
    private ChatFriendsRepository chatFriendsRepository;

    @Resource
    @Lazy
    private ChatFriendsService chatFriendsService;

    @Resource
    @Lazy
    private ChatFriendOfflineService chatFriendOfflineService;

    @Resource
    @Lazy
    private IChatServeCustomerService chatServeCustomerService;

    /**
     * @Description: 根据userId查询信息.
     * @param
     * @return
     */
    @Override
    @Overtime(value = 3600)
    @Cacheable(key = "'t_chat_user_info:getChatUserInfoByUserId:'+ #userId", value = "redisCache4Spring", sync = true)
    public ChatUserInfo getChatUserInfoByUserId(Long userId) {
        return chatUserInfoRepository.getChatUserInfoByUserId(userId);
    }

    /**
     * @Description: 1：查询我信息.
     * @param loginMember  当前登录用户信息
     * @return
     */
    @Override
    @Transactional(transactionManager = "mongoTransactionManager")
    public ChatUserInfo myChatUserInfo(LoginMember loginMember) {

        Long myUserId = loginMember.getUserId();
        String myUserIdentifier = chatroomFacade.getUserIdentifier(loginMember.getUserId(),loginMember.getUsername());
        //走缓存.
        ChatUserInfo myInfo = SpringContextUtil.getBean(this.getClass()).getChatUserInfoByUserId(myUserId);

        //不存在就新增我的信息.
        if (myInfo == null) {
            if (RedisLock.lock("myChatUserInfo:"+myUserId,2)) {
                myInfo = new ChatUserInfo();
                myInfo.setId(snowflakeIdUtils.nextId());
                myInfo.setUserId(myUserId);
                myInfo.setUserName(loginMember.getUsername());
                myInfo.setUserIdentifier(chatroomFacade.getUserIdentifier(myUserId,loginMember.getUsername()));
                myInfo.setLargeHead(loginMember.getPhoto());
                myInfo.setSmallHead(loginMember.getPhoto());
                myInfo.setNikeName(loginMember.getNickName());
                myInfo.setShortUrl(loginMember.getShortUrl());
                myInfo.setCreateTime(new Date());
                myInfo.setRemark("第一次进入大厅不存在就新增我的信息");
                myInfo.setRunId(6L);
                myInfo.setMerchantId(loginMember.getMerchantId()==null? Constants.MERCHANT_ID_DEFAULT:loginMember.getMerchantId());
                chatUserInfoRepository.saveChatUserInfo(myInfo);
            }
        } else {
            //后面加的东西在这里做个处理.
            if(myInfo.getShortUrl() == null || "".equals(myInfo.getShortUrl())){
                String shortUrl = loginMember.getShortUrl();
                myInfo.setShortUrl(shortUrl);

                Query query = new Query();
                Criteria criteria = Criteria.where("userId").is(myUserId);
                query.addCriteria(criteria);
                Update update = new Update();
                update.set("updateTime",new Date());
                update.set("shortUrl",shortUrl);
                chatUserInfoRepository.updateChatUserInfo(query,update);
            }
            return myInfo;
        }

        //1,每个会员随机只加一个客服为好友，
        //2,如果有客服了，就跳过.
        //3,如果有好友了，判断一下，是否有加过客服，没有就随机加一个客服.
        //4.如果没有好友，就随机取一个客服加为好友.

        boolean result = chatServeCustomerService.isServeCustomer(myUserIdentifier,myUserId);
        //如果自己是客服就算了.
        if(!result){
            //查询我所有的好友.
            Query queryChatFriendList = new Query(Criteria.where("siteCode").is("1")
                    .orOperator(
                            Criteria.where("myUserId").is(myUserId),
                            Criteria.where("friendUserId").is(myUserId)
                    ));
            List<ChatFriends> chatFriendList = chatFriendsRepository.queryChatFriendsList(queryChatFriendList);

            //得到所有客服的UserId.
            List<Long> serveCustomerUserIdList = chatServeCustomerService.serveCustomerUserIdList();

            Random random = new Random();
            int randNumber = 0;
            List<ChatUserInfo> chatUserInfoList = null;
            if(serveCustomerUserIdList != null && serveCustomerUserIdList.size() > 0){
                //查询所有客服信息.
                Query queryInfo = new Query();
                queryInfo.addCriteria(Criteria.where("userId").in(serveCustomerUserIdList));
                chatUserInfoList = chatUserInfoRepository.queryChatUserInfoList(queryInfo);

                //随机数(取值范围客服数量).
                randNumber = random.nextInt(chatUserInfoList.size());
            }

            //有好友，判断是否有加客服.
            if(chatFriendList != null && chatFriendList.size() > 0){
                for(ChatFriends chatFriends :chatFriendList){
                    //如果有加客服。
                    if(serveCustomerUserIdList.contains(chatFriends.getMyUserId()) || serveCustomerUserIdList.contains(chatFriends.getFriendUserId())){
                        return myInfo;
                    }
                }
                //插入好友 双向.
                if(chatUserInfoList != null && chatUserInfoList.size() > 0){
                    //随机选取一个客服.
                    ChatUserInfo friendInfo = chatUserInfoList.get(randNumber);
                    // 1:他是我好友
                    Query queryMyCF = new Query();
                    queryMyCF.addCriteria(Criteria.where("myUserId").is(myUserId).and("friendUserId").is(friendInfo.getUserId()));
                    long countMy = chatFriendsRepository.selectChatFriendsCount(queryMyCF);
                    if (countMy == 0) {
                        ChatFriends chatFriends1 = new ChatFriends();
                        chatFriends1.setId(snowflakeIdUtils.nextId());
                        chatFriends1.setMyUserId(myUserId);
                        chatFriends1.setMyUserIdentifier(myUserIdentifier);
                        chatFriends1.setFriendUserId(friendInfo.getUserId());
                        chatFriends1.setFriendUserIdentifier(friendInfo.getUserIdentifier());
                        chatFriends1.setCreateTime(new Date());
                        chatFriends1.setMuteOrSound(ChatConstant.SOUND);
                        chatFriendsRepository.saveChatFriend(chatFriends1);
                    }

                    //2：我是他好友.
                    Query queryFriendCF = new Query();
                    queryFriendCF.addCriteria(Criteria.where("myUserId").is(friendInfo.getUserId()).and("friendUserId").is(myUserId));
                    long countFriend =  chatFriendsRepository.selectChatFriendsCount(queryFriendCF);
                    if (countFriend == 0) {
                        ChatFriends chatFriends2 = new ChatFriends();
                        chatFriends2.setId(snowflakeIdUtils.nextId());
                        chatFriends2.setMyUserId(friendInfo.getUserId());
                        chatFriends2.setMyUserIdentifier(friendInfo.getUserIdentifier());
                        chatFriends2.setFriendUserId(myUserId);
                        chatFriends2.setFriendUserIdentifier(myUserIdentifier);
                        chatFriends2.setCreateTime(new Date());
                        chatFriends2.setMuteOrSound(ChatConstant.SOUND);
                        chatFriendsRepository.saveChatFriend(chatFriends2);
                    }
                }
            }else {
                //插入好友 双向 他在我的(客服)分组  我在他的(我的好友)分组.
                if(chatUserInfoList != null && chatUserInfoList.size() > 0){
                    //随机选取一个客服.
                    ChatUserInfo friendInfo = chatUserInfoList.get(randNumber);
                    // 1:他是我好友(我的客服分组)
                    Query queryMyCF = new Query();
                    queryMyCF.addCriteria(Criteria.where("myUserId").is(myUserId).and("friendUserId").is(friendInfo.getUserId()));
                    long countMy = chatFriendsRepository.selectChatFriendsCount(queryMyCF);
                    if (countMy == 0) {
                        ChatFriends chatFriends1 = new ChatFriends();
                        chatFriends1.setId(snowflakeIdUtils.nextId());
                        chatFriends1.setMyUserId(myUserId);
                        chatFriends1.setMyUserIdentifier(myUserIdentifier);
                        chatFriends1.setFriendUserId(friendInfo.getUserId());
                        chatFriends1.setFriendUserIdentifier(friendInfo.getUserIdentifier());
                        chatFriends1.setCreateTime(new Date());
                        chatFriends1.setMuteOrSound(ChatConstant.SOUND);
                        chatFriendsRepository.saveChatFriend(chatFriends1);
                    }

                    //2：我是他好友.
                    Query queryFriendCF = new Query();
                    queryFriendCF.addCriteria(Criteria.where("myUserId").is(friendInfo.getUserId()).and("friendUserId").is(myUserId));
                    long countFriend =  chatFriendsRepository.selectChatFriendsCount(queryFriendCF);
                    if (countFriend == 0) {
                        ChatFriends chatFriends2 = new ChatFriends();
                        chatFriends2.setId(snowflakeIdUtils.nextId());
                        chatFriends2.setMyUserId(friendInfo.getUserId());
                        chatFriends2.setMyUserIdentifier(friendInfo.getUserIdentifier());
                        chatFriends2.setFriendUserId(myUserId);
                        chatFriends2.setFriendUserIdentifier(myUserIdentifier);
                        chatFriends2.setCreateTime(new Date());
                        chatFriends2.setMuteOrSound(ChatConstant.SOUND);
                        chatFriendsRepository.saveChatFriend(chatFriends2);
                    }

                }
            }
        }

        return myInfo;
    }

    @Override
    public ChatUserInfo getChatUserInfo(String userName, String nikeName) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if(StringUtils.isNotBlank(userName)){
            criteria.and("userName").is(userName);
        }
        if(StringUtils.isNotBlank(nikeName)){
            criteria.and("nikeName").is(nikeName);
        }
        query.addCriteria(criteria);
        ChatUserInfo chatUserInfo = chatUserInfoRepository.getChatUserInfo(query);
        return chatUserInfo;
    }

    /**
     * 根据用户Id查询信息
     * @param userIds  userIds.
     * @return
     */
    @Override
    public List<ChatUserInfo> queryChatUserInfoListByUserIds(List<Long> userIds) {
        Query queryInfo = new Query();
        queryInfo.addCriteria(Criteria.where("userId").in(userIds));
        List<ChatUserInfo> chatUserInfoList = chatUserInfoRepository.queryChatUserInfoList(queryInfo);
        return chatUserInfoList;
    }


    /**
     * @Description: 根据userIdentifier查询信息.
     * @param userIdentifier  好友用户标识
     * @param myUserId 我的userId
     * @return
     */
    @Override
    public ChatUserInfoVo getChatUserInfoVoByUserIdentifier(String userIdentifier,Long myUserId) {

        return null;
    }

    /**
     * @Description: 根据用户标识查询信息.
     * @param
     * @return
     */
    @Override
    public ChatUserInfo getChatUserInfoByUserIdentifier(String userIdentifier) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userIdentifier").is(userIdentifier));
        ChatUserInfo chatUserInfo =  chatUserInfoRepository.getChatUserInfo(query);
        return chatUserInfo;
    }

    /**
     * A：会员修改信息 发消息修改.
     * @param memberMessageVo
     * @return
     */
    @Override

    public Long updateChatUserInfo(MemberMessageVo memberMessageVo, String userIdentifier) {

        Query query = new Query();
        Criteria criteria = Criteria.where("userId").is(memberMessageVo.getUserId());
        query.addCriteria(criteria);
        ChatUserInfo chatUserInfo =  chatUserInfoRepository.getChatUserInfo(query);

        if(chatUserInfo != null){
            Update update = new Update();
            update.set("updateTime",new Date());
            /**修改类型(1:注册 2: 头像 3:昵称)*/
            byte type = memberMessageVo.getType();
            if(type == 1){

            }else if(type == 2){
                //说明，不上传头像，是没有小头像的.
                if(memberMessageVo.getSmallHead() == null){
                    update.set("smallHead",memberMessageVo.getUserHead());
                }else {
                    update.set("smallHead",memberMessageVo.getSmallHead());
                }
                update.set("largeHead",memberMessageVo.getUserHead());
            }else if(type == 3){
                update.set("nikeName",memberMessageVo.getUserNikeName());
            }
            long count = chatUserInfoRepository.updateChatUserInfo(query,update);
            return count;
        }

        return 0L;
    }

    /**
     * B：注册会员信息 发消息新增.
     * @param memberMessageVo
     * @return
     */
    @Override
    public ChatUserInfo addChatUserInfo(MemberMessageVo memberMessageVo) {
        Long userId = memberMessageVo.getUserId();
        String userName = memberMessageVo.getUserName();
        //走缓存.
        ChatUserInfo chatUserInfo =  SpringContextUtil.getBean(this.getClass()).getChatUserInfoByUserId(userId);
        if (chatUserInfo != null) {
            return chatUserInfo;
        }

        chatUserInfo = new ChatUserInfo();
        chatUserInfo.setId(snowflakeIdUtils.nextId());
        chatUserInfo.setUserId(userId);
        chatUserInfo.setMerchantId(memberMessageVo.getMerchantId());
        chatUserInfo.setUserName(userName);
        chatUserInfo.setUserIdentifier(chatroomFacade.getUserIdentifier(userId,userName));
        //说明，不上传头像，是没有小头像的.
        if(memberMessageVo.getSmallHead() == null){
            chatUserInfo.setSmallHead(memberMessageVo.getUserHead());
        }else {
            chatUserInfo.setSmallHead(memberMessageVo.getSmallHead());
        }
        chatUserInfo.setLargeHead(memberMessageVo.getUserHead());
        chatUserInfo.setNikeName(memberMessageVo.getUserNikeName());
        chatUserInfo.setShortUrl(memberMessageVo.getShortUrl());
        chatUserInfo.setCreateTime(new Date());
        chatUserInfo.setRemark("注册新增");

        return chatUserInfoRepository.saveChatUserInfo(chatUserInfo);
    }

    @Override
    @Async("taskExecutor")
    public Future<ChatUserInfo> getChatUserInFuturefoByUserId(Long userId) {
        ChatUserInfo chatUserInfo = SpringContextUtil.getBean(this.getClass()).getChatUserInfoByUserId(userId);
        return new AsyncResult<>(chatUserInfo);
    }

    /**
     * 修改版本信息.
     * @param
     * @return
     */
    @Override
    public Long updateChatUserInfoVersion(ChatUserInfo chatUserInfo) {

        return 0L;
    }

    @Override
    public AjaxResult deleteChatUserInfo(byte accountType) {
        Query query = new Query();
        Criteria criteria = Criteria.where("accountType").is(accountType);
        query.addCriteria(criteria);
        chatUserInfoRepository.deleteChatUserInfo(query);
        return new AjaxResult();
    }

    @Override
    public ChatUserInfoVo getChatUserInfoByCustomerService(String parameter, Long myUserId) {

        //思路：
        //1.查询我的信息.
        //2.查询好友信息.
        //3.判断是否已经申请加好友.
        //4.判断是否已经邀请加好友.
        //5.判断是否已经加为好友.

        //1.查询我的信息.
        Query queryMy = new Query();
        queryMy.addCriteria(Criteria.where("userId").is(myUserId)
                .orOperator(
                        Criteria.where("userName").is(parameter),
                        Criteria.where("nikeName").is(parameter))
        );
        ChatUserInfo chatUserInfoMy = chatUserInfoRepository.getChatUserInfo(queryMy);
        if(chatUserInfoMy != null){
            throw new ServiceException("搜索自己？");
        }

        //2.查询好友信息.
        Query queryFriend = new Query();
        queryFriend.addCriteria(Criteria.where("siteCode").is("1")
                .orOperator(
                        Criteria.where("userName").is(parameter),
                        Criteria.where("nikeName").is(parameter))
        );
        ChatUserInfo chatUserInfoFriend = chatUserInfoRepository.getChatUserInfo(queryFriend);
        if(chatUserInfoFriend == null){
            throw new ServiceException("请输入正确的会员账号或昵称!");
        }
        //复制对象.
        ChatUserInfoVo chatUserInfoVo = new ChatUserInfoVo();
        CglibBeanCopierUtils.copyProperties(chatUserInfoVo,chatUserInfoFriend);

        //3.判断是否已经加为好友.
        boolean flag = chatFriendsService.hasMyFriends(myUserId,chatUserInfoFriend.getUserId());
        if(flag){
            //已经是好友.
            chatUserInfoVo.setMyStatus((short)4);
            return chatUserInfoVo;
        }
        chatUserInfoVo.setMyStatus((short)0);
        return chatUserInfoVo;
    }

}
