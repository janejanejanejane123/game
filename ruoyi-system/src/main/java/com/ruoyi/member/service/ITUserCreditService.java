package com.ruoyi.member.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.member.TUserCredit;
import com.ruoyi.member.vo.CreditVo;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 【请填写功能名称】Service接口
 *
 * @author ruoyi
 * @date 2022-03-27
 */
public interface ITUserCreditService {

    /**
     * 添加收款信息
     * @param creditVo vo
     * @return aj
     */
    AjaxResult addCredit(CreditVo creditVo);

    AjaxResult deleteCredit(CreditVo creditVo);

    AjaxResult list();

    AjaxResult applyCredit(CreditVo creditVo);

    TUserCredit SelectById(Long id);

    List<TUserCredit> listed(TUserCredit credit);

    AjaxResult delete(Long id);


    //==========================================================后台===================
}
