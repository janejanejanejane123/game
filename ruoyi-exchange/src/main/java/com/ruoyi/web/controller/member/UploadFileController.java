package com.ruoyi.web.controller.member;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.file.MinioUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/5/21,14:12
 * @return:
 **/
@RestController
@RequestMapping("/upload")
public class UploadFileController {

    @Resource
    private MinioUtil minioUtil;


    @PostMapping("/file")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult handelFileUpload(@RequestParam(value = "file") MultipartFile file) throws Exception {
        String upload = minioUtil.upload(file, true);
        return AjaxResult.success().put("id",upload);
    }
}
