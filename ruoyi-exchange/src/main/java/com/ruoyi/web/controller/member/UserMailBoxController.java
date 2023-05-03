package com.ruoyi.web.controller.member;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.member.domain.UserMailBox;
import com.ruoyi.member.service.IUserMailBoxService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * userController
 * 
 * @author ruoyi
 * @date 2022-03-29
 */
@RestController
@RequestMapping("/member/mailbox")
public class UserMailBoxController extends BaseController
{
    @Resource
    private IUserMailBoxService userMailBoxService;

    /**
     * 查询user列表
     */
    @PreAuthorize("@ss.playerOnly()")
    @GetMapping("/record")
    public TableDataInfo record()
    {
        startPage();
        List<UserMailBox> list = userMailBoxService.selectMyMailBoxList(SecurityUtils.getUserId());
        List<JSONObject> res = new ArrayList<>();
        list.forEach(item->{
            JSONObject json = new JSONObject();
            json.put("id",item.getId());
            json.put("title",item.getTitle());
            json.put("content",item.getContent());
            json.put("state",item.getState());
            json.put("sendTime",item.getSendTime());
            res.add(json);
        });
        return getDataTable(res);
    }

    /**
     * 更改为已读
     */
    @PreAuthorize("@ss.playerOnly()")
    @GetMapping("/read")
    public AjaxResult export(@RequestParam Long id)
    {
        userMailBoxService.changeReadStatus(id);
        return AjaxResult.success();
    }

    /**
     * 删除站内信
     */
    @PreAuthorize("@ss.playerOnly()")
    @GetMapping(value = "/delete")
    public AjaxResult getInfo(@RequestParam Long id)
    {
        userMailBoxService.deleteUserMailBoxById(id);
        return AjaxResult.success();
    }

}
