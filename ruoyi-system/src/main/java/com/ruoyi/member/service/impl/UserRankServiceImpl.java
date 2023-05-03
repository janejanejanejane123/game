package com.ruoyi.member.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.ruoyi.member.mapper.UserRankMapper;
import com.ruoyi.member.domain.UserRank;
import com.ruoyi.member.service.IUserRankService;

import javax.annotation.Resource;

/**
 * 会员星级Service业务层处理
 * 
 * @author ry
 * @date 2022-09-09
 */
@Service
public class UserRankServiceImpl implements IUserRankService 
{
    @Resource
    private UserRankMapper userRankMapper;

    /**
     * 查询会员星级
     * 
     * @param uid 会员星级主键
     * @return 会员星级
     */
    @Override
    public UserRank selectUserRankByUid(Long uid)
    {
        return userRankMapper.selectUserRankByUid(uid);
    }

    /**
     * 查询会员星级列表
     * 
     * @param userRank 会员星级
     * @return 会员星级
     */
    @Override
    public List<UserRank> selectUserRankList(UserRank userRank)
    {
        return userRankMapper.selectUserRankList(userRank);
    }

    /**
     * 新增会员星级
     * 
     * @param userRank 会员星级
     * @return 结果
     */
    @Override
    public int insertUserRank(UserRank userRank)
    {
        return userRankMapper.insertUserRank(userRank);
    }

    /**
     * 修改会员星级
     * 
     * @param userRank 会员星级
     * @return 结果
     */
    @Override
    public int updateUserRank(UserRank userRank)
    {
        return userRankMapper.updateUserRank(userRank);
    }

    /**
     * 批量删除会员星级
     * 
     * @param uids 需要删除的会员星级主键
     * @return 结果
     */
    @Override
    public int deleteUserRankByUids(Long[] uids)
    {
        return userRankMapper.deleteUserRankByUids(uids);
    }

    /**
     * 删除会员星级信息
     * 
     * @param uid 会员星级主键
     * @return 结果
     */
    @Override
    public int deleteUserRankByUid(Long uid)
    {
        return userRankMapper.deleteUserRankByUid(uid);
    }
}
