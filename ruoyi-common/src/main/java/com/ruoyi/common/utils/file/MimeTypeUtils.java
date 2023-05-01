package com.ruoyi.common.utils.file;

/**
 * 媒体类型工具类
 * 
 * @author ruoyi
 */
public class MimeTypeUtils
{
    public static final String UPFILE_VERIFYID_KEY = "upfile:verifyId:";

    public static final String IMAGE_PNG = "image/png";

    public static final String IMAGE_JPG = "image/jpg";

    public static final String IMAGE_JPEG = "image/jpeg";

    public static final String IMAGE_BMP = "image/bmp";

    public static final String IMAGE_GIF = "image/gif";

    public static final String IMAGE_ICO = "image/ico";

    public static final String[] IMAGE_EXTENSION = { "jpg","png","gif","ico","heic","jpeg" };

    public static final String[] FLASH_EXTENSION = { "swf", "flv" };

    public static final String[] MEDIA_EXTENSION = { "swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg",
            "asf", "rm", "rmvb" };

    public static final String[] VIDEO_EXTENSION = { "avi", "rm", "rmvb","wmv","mov","mkv","webm"};

    public static final String[] DEFAULT_ALLOWED_EXTENSION = {
            // 图片 "bmp", "gif", "jpg", "jpeg", "png","ico",
            "jpg","png","gif","ico","heic","jpeg",
            // word文档 "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",
            // 压缩文件 "rar", "zip", "gz", "bz2",
            // 音视频格式
            "mp3","wav","avi","mp4", "rm", "rmvb","wmv","mov","mkv","webm"
            // pdf "pdf"
     };

    public static String getExtension(String prefix)
    {
        switch (prefix)
        {
            case IMAGE_PNG:
                return "png";
            case IMAGE_JPEG:
            case IMAGE_JPG:
                return "jpg";
            case IMAGE_BMP:
                return "bmp";
            case IMAGE_GIF:
                return "gif";
            case IMAGE_ICO:
                return "ico";
            default:
                return "";
        }
    }
}
