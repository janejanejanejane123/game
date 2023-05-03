package  com.ruoyi.member.mapper;

import java.util.List;
import  com.ruoyi.member.domain.UserRank;

/**
 * 会员星级Mapper接口
 * 
 * @author ry
 * @date 2022-09-09
 */
public interface UserRankMapper 
{
    /**
     * 查询会员星级
     * 
     * @param uid 会员星级主键
     * @return 会员星级
     */
    public UserRank selectUserRankByUid(Long uid);

    /**
     * 查询会员星级列表
     * 
     * @param userRank 会员星级
     * @return 会员星级集合
     */
    public List<UserRank> selectUserRankList(UserRank userRank);

    /**
     * 新增会员星级
     * 
     * @param userRank 会员星级
     * @return 结果
     */
    public int insertUserRank(UserRank userRank);

    /**
     * 修改会员星级
     * 
     * @param userRank 会员星级
     * @return 结果
     */
    public int updateUserRank(UserRank userRank);

    /**
     * 删除会员星级
     * 
     * @param uid 会员星级主键
     * @return 结果
     */
    public int deleteUserRankByUid(Long uid);

    /**
     * 批量删除会员星级
     * 
     * @param uids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserRankByUids(Long[] uids);
}
