package com.ruoyi.common.utils.ip.old;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.*;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @version v 1.0
 * @ClassName: IpUtils
 * @date 2018/12/05
 */
public class OldIpUtils {
    private static Logger logger = LoggerFactory.getLogger(OldIpUtils.class);
    private final static String ipPrefix ;
    static {
        String[] ipArr = OldIpUtils.intranetIp().split("\\.");
        ipPrefix = ipArr[0] + "." + ipArr[1] + ".";
    }

    private OldIpUtils() {
    }

    /**
     * 获取当前网络ip
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request){
        String ipAddress = request.getHeader("x-forwarded-for");
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)){
                //根据网卡取本机配置的IP
                InetAddress inet=null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    logger.error("[IpUtils.getIpAddr] getLocalHost error",e);
                }
                ipAddress= inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15
            if(ipAddress.indexOf(",")>0){
                ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    /**
     * 获得MAC地址
     * @param ip
     * @return
     */
    public static String getMACAddress(String ip){
        String str = "";
        String macAddress = "";
        try {
            Process p = Runtime.getRuntime().exec("nbtstat -A " + ip);
            InputStreamReader ir = new InputStreamReader(p.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    if (str.indexOf("MAC Address") > 1) {
                        macAddress = str.substring(str.indexOf("MAC Address") + 14, str.length());
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return macAddress;
    }

    /**
     * 获取内网IP
     *
     * @return
     */
    public static String intranetIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getWebsiteHost(HttpServletRequest request){
        String url = request.getRequestURL().toString();
        String contextPath = request.getServletContext().getContextPath();
        if(url.indexOf(contextPath) != -1){
            if(request.getHeader("referer") == null && request.getHeader("host") != null){
                return request.getHeader("host").split(":")[0];
            }else if(request.getHeader("referer") != null) {
                url = request.getHeader("referer");
            }
        }
        String host = null;
        try {
            host = new URL(url).getHost();
        } catch (MalformedURLException e) {
            logger.error("domain url erro ,access url is:{},request ip is:{}",url,getIpAddr(request));
            throw new ServiceException("3459050","domain url erro");
        }
        return host;
    }

    /**
     * 获取服务启动host
     * @return
     */
    public static String getHost(){
        String internetIp = internetIp();
        return internetIp ==null ? intranetIp() : internetIp;
    }

    public static String getLocalHost(){
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            logger.error("netty initRedisData erro",e);
            throw new ServiceException("23543","netty initRedisData erro",e);
        }
        return inetAddress.getHostName().toString();
    }
    /***
     * 获取外网IP
     * @return
     */
    public static String internetIp() {
        try {

            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            InetAddress inetAddress = null;
            Enumeration<InetAddress> inetAddresses = null;
            while (networks.hasMoreElements()) {
                inetAddresses = networks.nextElement().getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress != null
                            && inetAddress instanceof Inet4Address
                            && !inetAddress.isSiteLocalAddress()
                            && !inetAddress.isLoopbackAddress()
                            && inetAddress.getHostAddress().indexOf(":") == -1) {
                        return inetAddress.getHostAddress();
                    }
                }
            }

            return null;

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @Description: 输出本机名
     * 
     * @Title:
     * @date 2018/12/05
     * @return
     */
    public static String getHostName(){
        try {
            return InetAddress.getLocalHost().getHostName() ;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null ;
    }

    /**
     * @Description: 判断IP是否在指定范围；
     * @param ipSection ip段范围
     * @param ip 当前人ip
     * @return boolean
     * 
     * @date 2019/4/26 11:49
     * @Test IpUtils.ipIsValid("192.168.1.1-192.168.1.10", "192.168.3.54")
     */
    public static boolean ipSegmentValid(String ipSection, String ip) {
        if (StringUtils.isBlank(ipSection))
            return true;
        if (ip == null)
            throw new NullPointerException("client ip cannot be empty！");
        ipSection = ipSection.trim();
        ip = ip.trim();
        final String REGX_IP = "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
        final String REGX_IPB = REGX_IP + "\\-" + REGX_IP;
        if (!ipSection.matches(REGX_IPB) || !ip.matches(REGX_IP))
            return false;
        int idx = ipSection.indexOf('-');
        String[] sips = ipSection.substring(0, idx).split("\\.");
        String[] sipe = ipSection.substring(idx + 1).split("\\.");
        String[] sipt = ip.split("\\.");
        long ips = 0L, ipe = 0L, ipt = 0L;
        for (int i = 0; i < 4; ++i) {
            ips = ips << 8 | Integer.parseInt(sips[i]);
            ipe = ipe << 8 | Integer.parseInt(sipe[i]);
            ipt = ipt << 8 | Integer.parseInt(sipt[i]);
        }
        if (ips > ipe) {
            long t = ips;
            ips = ipe;
            ipe = t;
        }
        return ips <= ipt && ipt <= ipe;
    }

    public static boolean isIP(String addr)
    {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
        {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();
        return ipAddress;
    }


    public static boolean isInternalIP(String str) {
        if(StringUtils.isBlank(str)){
            return false;
        }
        if(str.indexOf(":") != -1){
            str = str.substring(0,str.indexOf(":"));
        }
        if(!isIP(str)){
            return false;
        }
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(str);
        } catch (UnknownHostException e) {
            logger.error("[IpUtils.isInternalIP] getByName error",e);
            return false;
        }
        byte[] ipByte = ip.getAddress();
        return isInternalIP(ipByte);
    }

    /**
     * @Description: 判断内网IP
     * @param ip
     * @return boolean
     * 
     * @date 2019/11/10 13:43
     */
    public static boolean isInternalIP(byte[] ip) {
        if (ip.length != 4) {
            throw new RuntimeException("illegal ipv4 bytes");
        }

        //10.0.0.0~10.255.255.255
        //172.16.0.0~172.31.255.255
        //192.168.0.0~192.168.255.255
        if (ip[0] == (byte) 10) {

            return true;
        } else if (ip[0] == (byte) 172) {
            if (ip[1] >= (byte) 16 && ip[1] <= (byte) 31) {
                return true;
            }
        } else if (ip[0] == (byte) 192) {
            if (ip[1] == (byte) 168) {
                return true;
            }
        }
        if((ip[0]+"."+ip[1]).startsWith(ipPrefix)){
            return true;
        }
        return false;
    }

    public static String ipToIPv4Str(byte[] ip) {
        if (ip.length != 4) {
            return null;
        }
        return new StringBuilder().append(ip[0] & 0xFF).append(".").append(
                ip[1] & 0xFF).append(".").append(ip[2] & 0xFF)
                .append(".").append(ip[3] & 0xFF).toString();
    }

    /**
     * 验证IP是否属于某个IP段
     * ip           所验证的IP号码
     * ipSection    IP段（以'-'分隔）
     */
    public static boolean ipExistsInRange(String ip,String ipSection) {
        ipSection = ipSection.trim();
        ip = ip.trim();
        int idx = ipSection.indexOf('-');
        String beginIP = ipSection.substring(0, idx);
        String endIP = ipSection.substring(idx + 1);
        return getIp2long(beginIP)<=getIp2long(ip) &&getIp2long(ip)<=getIp2long(endIP);
    }

    /**
     * IP地址的long值
     * @param ip
     * @return
     */
    public static long getIp2long(String ip) {
        ip = ip.trim();
        String[] ips = ip.split("\\.");
        long ip2long = 0L;
        for (int i = 0; i < 4; ++i) {
            ip2long = ip2long << 8 | Integer.parseInt(ips[i]);
        }
        return ip2long;
    }

    /**
     * IP地址的long值
     * @param ip
     * @return
     */
    public static long getIp2long2(String ip) {
        ip = ip.trim();
        String[] ips = ip.split("\\.");
        long ip1 = Integer.parseInt(ips[0]);
        long ip2 = Integer.parseInt(ips[1]);
        long ip3 = Integer.parseInt(ips[2]);
        long ip4 = Integer.parseInt(ips[3]);
        long ip2long =1L* ip1 * 256 * 256 * 256 + ip2 * 256 * 256 + ip3 * 256 + ip4;

        return ip2long;
    }

    /**
     * 比较IP大小
     * @param  ip1
     * @param  ip2
     */
    public static int compareIpV4s(String ip1,String ip2) {
        int result = 0;
        int ipValue1 = getIpV4Value(ip1);     // 获取ip1的32bit值
        int ipValue2 = getIpV4Value(ip2); // 获取ip2的32bit值
        if(ipValue1 > ipValue2) {
            result =  -1;
        } else if(ipValue1 <= ipValue2) {
            result = 1;
        }
        return result;
    }

    public static int getIpV4Value(String ipOrMask) {
        byte[] addr = getIpV4Bytes(ipOrMask);
        int address1  = addr[3] & 0xFF;
        address1 |= ((addr[2] << 8) & 0xFF00);
        address1 |= ((addr[1] << 16) & 0xFF0000);
        address1 |= ((addr[0] << 24) & 0xFF000000);
        return address1;
    }

    public static byte[] getIpV4Bytes(String ipOrMask) {
        try{
            String[] addrs = ipOrMask.split("\\.");
            int length = addrs.length;
            byte[] addr = new byte[length];
            for (int index = 0; index < length; index++) {
                addr[index] = (byte) (Integer.parseInt(addrs[index]) & 0xff);
            }
            return addr;
        }catch (Exception e){
        }
        return new byte[4];
    }
}
