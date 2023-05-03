package com.ruoyi.web.controller.chatroom;

import com.ruoyi.chatroom.service.ChatRoomRecordService;
import com.ruoyi.chatroom.service.ReportService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.UserTypeEnum;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 *  举报管理
 * @author nn
 * @date 2022/07/20
 */
@RestController
@RequestMapping("/chatroom/report")
public class ReportController extends BaseController {

    @Resource
    private ReportService reportService;

    @Resource
    private ChatRoomRecordService chatRoomRecordService;

    @Resource
    @Lazy
    private HttpServletRequest request; //这里可以获取到request

    /**
     * 查询举报列表.
     * @param param
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('chatroom:report:list')")
    public TableDataInfo list(@RequestParam Map<String,Object> param){
        TableDataInfo tableDataInfo = reportService.queryPage(param);
        return tableDataInfo;
    }

    /**
     * 消息详情列表.
     * @param param
     * @return
     */
    @GetMapping("/messageList")
    @PreAuthorize("@ss.hasPermi('chatroom:report:messageList')")
    public TableDataInfo messageList(@RequestParam Map<String,Object> param){
        TableDataInfo tableDataInfo = reportService.messageList(param);
        return tableDataInfo;
    }

    /**
     * 删除
     * @param params
     * @return
     */
    @PostMapping("/delete")
    @PreAuthorize("@ss.hasPermi('chatroom:report:delete')")
    public  AjaxResult delete(@RequestParam Map<String,Object> params){
        return reportService.remove(params);
    }

    /**
     * 误报
     * @param params
     * @return
     */
    @PostMapping("/recover")
    @PreAuthorize("@ss.hasPermi('chatroom:report:recover')")
    public  AjaxResult recover(@RequestParam Map<String,Object> params){
        return reportService.recover(params);
    }

    /**
     * 撤回消息-后台撤回
     * @param messageId  消息Id
     * @return
     */
    @PostMapping("/retractMessage")
    @PreAuthorize("@ss.hasPermi('chatroom:report:retractMessage')")
    public AjaxResult retractMessage(@NotNull Long messageId){
        String loginIp = IpUtils.getIpAddr(request);
        LoginUser loginUser =  getLoginUser();
        chatRoomRecordService.retractMessage(messageId,(byte)1,loginIp,loginUser, UserTypeEnum.MANAGE);
        return new AjaxResult();
    }

}
