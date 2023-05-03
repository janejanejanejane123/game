package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TSession;

/**
 * systemService接口
 * 
 * @author ry
 * @date 2022-05-24
 */
public interface ITSessionService 
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
     * 批量删除system
     * 
     * @param ids 需要删除的system主键集合
     * @return 结果
     */
    public int deleteTSessionByIds(Long[] ids);

    /**
     * 删除system信息
     * 
     * @param uid system主键
     * @return 结果
     */
    public int deleteTSessionByUid(String uid);

    void add(TSession session);

    void delete(String uid,String nid);

    /**
     * 删除本机的连接记录
     */
    void deleteLocalhost(String host);

    void updateState(String uid,int state);

    List<TSession> findAll();
}
