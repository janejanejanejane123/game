package com.ruoyi.common.utils.file;

import com.ruoyi.common.exception.UtilException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.uuid.UUID;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @description： minio工具类
 * @author：weirx
 * @date：2021/8/25 10:03
 * @version：3.0
 */
@Component
public class MinioUtil {

    private static final Logger log = LoggerFactory.getLogger(MinioUtil.class);

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String access;

    @Value("${minio.secretKey}")
    private String secret;

    @Resource
    private MinioClient minioClient;

    @Resource
    SnowflakeIdUtils snowflakeIdUtils;

    @Resource
    RedisTemplate redisTemplate;


    /**
     * description: 上传文件
     *
     * @param file
     * @return: java.lang.String
     */
    public String upload(MultipartFile file,Boolean needVerify) {
        return upfileToMinio(file,needVerify);
    }

    /**
     * description: 批量上传文件
     *
     * @param multipartFile
     * @return: java.lang.String
     * @time: 2021/8/25 10:44
     */
    public List<String> upload(MultipartFile[] multipartFile,Boolean needVerify) {
        List<String> names = new ArrayList<>(multipartFile.length);
        for (MultipartFile file : multipartFile) {
            String name = upfileToMinio(file,needVerify);
            names.add(name);
        }
        return names;
    }

    /**
     * 只开放部分图片和音视频格式
     * @param file
     * @param needVerify
     * @return
     */
    private String upfileToMinio(MultipartFile file,Boolean needVerify) {
        String file_suffix = FileUploadUtils.getExtension(file);
        if(!Arrays.asList(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION).contains(file_suffix.toLowerCase())){
            log.error("不支持此格式{}文件上传!",file_suffix);
            throw new UtilException("不支持此格式" + file_suffix + "文件上传");
        }
        String file_type = FileTypeUtil.getFileType(file);
        if (StringUtils.isEmpty(file_type)) {
            log.error("上传文件实际格式为空{}!",file.getName());
            throw new UtilException("上传文件格式有误");
        }
        String fileName = DateUtils.parseDateToStr("yyyyMMddHHmmss", new Date()) + "_" + UUID.getRandomStr(4);
        InputStream in = null;
        try {
            if(Arrays.asList(MimeTypeUtils.VIDEO_EXTENSION).contains(file_type)){
                File source = new File("source." + file_type);
                File dest = new File(UUID.getRandomStr(6) + ".mp4");
                inputStreamToFile(file,source);
                try {
                    AudioAttributes audio = new AudioAttributes();
                    audio.setCodec("libmp3lame");
                    audio.setBitRate(new Integer(800000));//设置比特率
                    audio.setChannels(new Integer(1));//设置音频通道数
                    audio.setSamplingRate(new Integer(44100));//设置采样率
                    VideoAttributes video = new VideoAttributes();
//                    video.setCodec("mpeg4");
                    video.setCodec("libx264");
                    video.setBitRate(new Integer(3200000));
                    video.setFrameRate(new Integer(15));
                    EncodingAttributes attrs = new EncodingAttributes();
                    attrs.setOutputFormat("mp4");
                    attrs.setAudioAttributes(audio);
                    attrs.setVideoAttributes(video);
                    Encoder encoder = new Encoder();
                    encoder.encode(new MultimediaObject(source), dest, attrs);
                    in = new FileInputStream(dest);
                    fileName +=  ".mp4";
                } catch (Exception e) {
                    log.error("转换视频文件错误:",e);
                }
                finally {
                    source.delete();
                    dest.delete();
                }
            }else{
                in = file.getInputStream();
                fileName +=  "." + file_suffix;
            }

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(in, in.available(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
        } catch (Exception e) {
            log.error("上传文件异常:",e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("上传文件关闭流异常:",e);
                }
            }
        }
        String path = "/" + bucketName + "/" + fileName;
        if(needVerify){
            Long key = snowflakeIdUtils.nextId();
            redisTemplate.opsForValue().set(MimeTypeUtils.UPFILE_VERIFYID_KEY + key,path);
            return key + "";
        }
        return path;
    }

    /**
     * 删除
     * @param fileName
     * @return
     * @throws Exception
     */
    public void removeFile(String fileName){
        try {
            //创建MinioClient对象
            minioClient.removeObject( RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        }catch (Exception e){
            log.error("删除文件{}失败==={}",fileName,e.getStackTrace());
        }

    }

    public static void inputStreamToFile(MultipartFile source, File dest) {
        try {
            InputStream ins = source.getInputStream();
            OutputStream os = new FileOutputStream(dest);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
