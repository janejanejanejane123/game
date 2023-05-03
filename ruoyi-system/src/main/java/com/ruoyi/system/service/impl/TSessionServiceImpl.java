package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.TSession;
import com.ruoyi.system.mapper.TSessionMapper;
import com.ruoyi.system.service.ITSessionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * systemService业务层处理
 * 
 * @author ry
 * @date 2022-05-24
 */
@Service
public class TSessionServiceImpl implements ITSessionService 
{
    @Resource
    private TSessionMapper tSessionMapper;

    /**
     * 查询system
     * 
     * @param id system主键
     * @return system
     */
    @Override
    public TSession selectTSessionById(Long id)
    {
        return tSessionMapper.selectTSessionById(id);
    }

    /**
     * 查询system列表
     * 
     * @param tSession system
     * @return system
     */
    @Override
    public List<TSession> selectTSessionList(TSession tSession)
    {
        return tSessionMapper.selectTSessionList(tSession);
    }

    /**
     * 新增system
     * 
     * @param tSession system
     * @return 结果
     */
    @Override
    public int insertTSession(TSession tSession)
    {
        return tSessionMapper.insertTSession(tSession);
    }

    /**
     * 修改system
     * 
     * @param tSession system
     * @return 结果
     */
    @Override
    public int updateTSession(TSession tSession)
    {
        return tSessionMapper.updateTSession(tSession);
    }

    /**
     * 批量删除system
     * 
     * @param ids 需要删除的system主键
     * @return 结果
     */
    @Override
    public int deleteTSessionByIds(Long[] ids)
    {
        return tSessionMapper.deleteTSessionByIds(ids);
    }

    /**
     * 删除system信息
     * 
     * @param uid system主键
     * @return 结果
     */
    @Override
    public int deleteTSessionByUid(String uid)
    {
        return tSessionMapper.deleteTSessionByUid(uid);
    }

    @Override
    public void add(TSession session) {
        session.setState(1);
        session.setBindTime(System.currentTimeMillis());
        tSessionMapper.insertTSession(session);
    }

    @Override
    public void delete(String uid, String nid) {
        tSessionMapper.deleteByUIdAndNid(uid,nid);
    }

    @Override
    public void deleteLocalhost(String host) {
        tSessionMapper.deleteByHost(host);
    }

    @Override
    public void updateState(String uid,int state) {
        tSessionMapper.updateState(uid,state);
    }

    @Override
    public List<TSession> findAll() {
        return tSessionMapper.findAll();
    }
}
