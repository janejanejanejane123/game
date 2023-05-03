package com.ruoyi.web.controller.common;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.file.MinioUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;

/**
 * 上传
 * 
 * @author nn
 */
@RestController
@RequestMapping("/upload/")
public class UploadController
{
    @Resource
    private MinioUtil minioUtil;

    @PostMapping("/uploadPicture")
    public AjaxResult uploadPicture(@RequestParam(value = "file") MultipartFile file) throws Exception {
        String path = minioUtil.upload(file, true);
        return AjaxResult.success().put("path",path);
    }

}
