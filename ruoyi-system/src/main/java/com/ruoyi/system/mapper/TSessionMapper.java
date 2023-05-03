package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TSession;
import org.apache.ibatis.annotations.Param;

/**
 * systemMapper接口
 * 
 * @author ry
 * @date 2022-05-24
 */
public interface TSessionMapper 
{
    /**
     * 查询system
     * 
     * @param id system主键
     * @return system
     */
    public TSession selectTSessionById(Long id);

    /**
     * 查询system列表
     * 
     * @param tSession system
     * @return system集合
     */
    public List<TSession> selectTSessionList(TSession tSession);

    /**
     * 新增system
     * 
     * @param tSession system
     * @return 结果
     */
    public int insertTSession(TSession tSession);

    /**
     * 修改system
     * 
     * @param tSession system
     * @return 结果
     */
    public int updateTSession(TSession tSession);

    /**
     * 删除system
     * 
     * @param uid system主键
     * @return 结果
     */
    public int deleteTSessionByUid(String uid);

    /**
     * 批量删除system
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTSessionByIds(Long[] ids);

    void deleteByUIdAndNid(@Param("uid") String uid, @Param("nid")String nid);

    void deleteByHost(String host);

    List<TSession> findAll();

    void updateState(@Param("uid")String uid,  @Param("state")int state);
}
