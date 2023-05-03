package com.ruoyi.member.service.impl;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.member.domain.UserMailBox;
import com.ruoyi.member.mapper.UserMailBoxMapper;
import com.ruoyi.member.service.IUserMailBoxService;
import com.ruoyi.system.service.IMemberService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * memberService业务层处理
 * 
 * @author ry
 * @date 2022-06-06
 */
@Service
public class UserMailBoxServiceImpl implements IUserMailBoxService
{
    @Resource
    private UserMailBoxMapper userMailBoxMapper;

    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;

    @Resource
    private IMemberService memberService;

    @Resource
    private ISysUserService sysUserService;

    /**
     * 查询member
     * 
     * @param id member主键
     * @return member
     */
    @Override
    public UserMailBox selectUserMailBoxById(Long id)
    {
        return userMailBoxMapper.selectUserMailBoxById(id);
    }

    /**
     * 查询member列表
     * 
     * @param userMailBox member
     * @return member
     */
    @Override
    public List<UserMailBox> selectUserMailBoxList(UserMailBox userMailBox)
    {
        return userMailBoxMapper.selectUserMailBoxList(userMailBox);
    }

    @Override
    public List<UserMailBox> selectMyMailBoxList(Long uid)
    {
        return userMailBoxMapper.selectMyMailBoxList(uid);
    }

    @Override
    public int changeReadStatus(Long id)
    {
        return userMailBoxMapper.changeReadStatus(id);
    }

    /**
     * 新增member
     * 
     * @param userMailBox member
     * @return 结果
     */
    @Override
    public int insertUserMailBox(UserMailBox userMailBox)
    {
        List<UserMailBox> list = new ArrayList<>();
        userMailBox.setCreatTime(new Date());
        userMailBox.setState(0);
        Integer userType = userMailBox.getUserType();
//        if(userMailBox.getSendTime().getTime() <= System.curntTimeMillis()){
//            throw new ServiceException("发送时间须大于当前时间!");
//        }
        String userNames = userMailBox.getUserNames();
        if(StringUtils.isEmpty(userNames)){
            if(userType == 0){
                List<TUser> tUsers = memberService.queryAllUsers();
                tUsers.forEach(item->{
                    UserMailBox temp = new UserMailBox();
                    BeanUtils.copyBeanProp(temp,userMailBox);
                    temp.setId(snowflakeIdUtils.nextId());
                    temp.setUserIds(item.getUid() + "");
                    temp.setUserNames(item.getUsername());
                    list.add(temp);
                });
            }
            if(userType == 1){
                List<SysUser> sysUsers = sysUserService.selectAllUsers();
                sysUsers.forEach(item->{
                    UserMailBox temp = new UserMailBox();
                    BeanUtils.copyBeanProp(temp,userMailBox);
                    temp.setId(snowflakeIdUtils.nextId());
                    temp.setUserIds(item.getUserId() + "");
                    temp.setUserNames(item.getUserName());
                    list.add(temp);
                });
            }
        }else{
            if(userType == 0){
                TUser tUser = memberService.queryMemberByUsername(userNames);
                if(tUser == null){
                    throw new ServiceException("查无此人,请检查站内信前台收件人的名字是否正确!");
                }
                userMailBox.setId(snowflakeIdUtils.nextId());
                userMailBox.setUserIds(tUser.getUid() + "");
                list.add(userMailBox);
            }
            if(userType == 1){
                SysUser sysUser = sysUserService.selectUserByUserName(userNames);
                if(sysUser == null){
                    throw new ServiceException("查无此人,请检查站内信后台收件人的名字是否正确!");
                }
                userMailBox.setId(snowflakeIdUtils.nextId());
                userMailBox.setUserIds(sysUser.getUserId() + "");
                list.add(userMailBox);
            }
        }
        return userMailBoxMapper.insertBatchUserMailBox(list);
    }

    /**
     * 修改member
     * 
     * @param userMailBox member
     * @return 结果
     */
    @Override
    public int updateUserMailBox(UserMailBox userMailBox)
    {
        return userMailBoxMapper.updateUserMailBox(userMailBox);
    }

    /**
     * 批量删除member
     * 
     * @param ids 需要删除的member主键
     * @return 结果
     */
    @Override
    public int deleteUserMailBoxByIds(Long[] ids)
    {
        return userMailBoxMapper.deleteUserMailBoxByIds(ids);
    }

    /**
     * 删除member信息
     * 
     * @param id member主键
     * @return 结果
     */
    @Override
    public int deleteUserMailBoxById(Long id)
    {
        return userMailBoxMapper.deleteUserMailBoxById(id);
    }
}
