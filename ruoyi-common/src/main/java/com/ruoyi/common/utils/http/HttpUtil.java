package com.ruoyi.common.utils.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  * @ClassName HttpUtils
 *  * @Description HttpClient请求工具类
 *  * @Date 2018年08月26日 17:56
 *  * @Version 1.0.0
 *  
 **/
public class HttpUtil {

    //日志
    public static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * 功能描述:
     * 适用于有些http请求不需要传contentType的
     * @Date: 2018年08月31日 18:39:40
     * @param data
     * @param url
     * @param contentType
     * @return: java.lang.String
     **/
    public static String post(Map<String,String> data,String url,String contentType) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            if(data != null){
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for(Map.Entry<String, String> entry : data.entrySet()){
                    NameValuePair v = new BasicNameValuePair(entry.getKey(),entry.getValue());
                    nvps.add(v);
                }
                httppost.setEntity(new UrlEncodedFormEntity(nvps,Consts.UTF_8));
                if(StringUtils.isNotBlank(contentType)){
                    httppost.setHeader("Content-Type", "application/json");
                }
                httppost.setHeader("dsign",data.get("signature"));
            }
            return formatResponse(httpclient.execute(httppost));
        } catch (Exception e) {
            //e.printStackTrace();
            logger.info("HTTPCLIENT-toPostJson请求异常:"+e.getMessage(),e);
            throw new Exception(e.getMessage());
        }finally {
            formatHttpClient(httpclient);
        }
    }

    public static String post(Map<String,String> data,String url,String contentType,Map headMap) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            if(data != null){
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for(Map.Entry<String, String> entry : data.entrySet()){
                    NameValuePair v = new BasicNameValuePair(entry.getKey(),entry.getValue());
                    nvps.add(v);
                }
                httppost.setEntity(new UrlEncodedFormEntity(nvps,Consts.UTF_8));
                if(StringUtils.isNotBlank(contentType)){
                    httppost.setHeader("Content-Type", "application/json");
                }
                httppost.setHeader("dsign",data.get("signature"));
                if(headMap != null &&  !headMap.isEmpty()){
                    headMap.forEach((key,value)->httppost.setHeader(key+"",value+""));
                }
                httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            }
            return formatResponse(httpclient.execute(httppost));
        } catch (Exception e) {
            //e.printStackTrace();
            logger.info("HTTPCLIENT-toPostJson请求异常:"+e.getMessage(),e);
            throw new Exception(e.getMessage());
        }finally {
            formatHttpClient(httpclient);
        }
    }

    public static String post(String data,String url,String contentType) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            // 设置包体参数。
            StringEntity se = new StringEntity(data);
            se.setContentEncoding("UTF-8");
            if(StringUtils.isNotBlank(contentType)){
                se.setContentType("application/json");
            }
            httppost.setHeader("accept","*/*");
            httppost.setHeader("connection","Keep-Alive");
            httppost.setHeader("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            httppost.setHeader("security_header_key", "headerKey");
            httppost.setEntity(se);
            return formatResponse(httpclient.execute(httppost));
        } catch (Exception e) {
            //e.printStackTrace();
            logger.info("HTTPCLIENT-toPostJson请求异常:"+e.getMessage());
            throw new Exception(e.getMessage());
        }finally {
            formatHttpClient(httpclient);
        }
    }

    /**
     * 功能描述:
     * content-type:application/json
     * @Date: 2018年08月31日 17:19:54
     * @param data
     * @param payUrl
     * @return: java.lang.String
     **/
    public static String toPostJson(Map<String,String> data,String url) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            if(data != null){
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for(Map.Entry<String, String> entry : data.entrySet()){
                    NameValuePair v = new BasicNameValuePair(entry.getKey(),entry.getValue());
                    nvps.add(v);
                }
                httppost.setEntity(new UrlEncodedFormEntity(nvps,Consts.UTF_8));
                httppost.setHeader("Content-Type", "application/json");
            }
            return formatResponse(httpclient.execute(httppost));
        } catch (Exception e) {
            //e.printStackTrace();
            logger.info("HTTPCLIENT-toPostJson请求异常:"+e.getMessage());
            throw new Exception(e.getMessage());
        }finally {
            formatHttpClient(httpclient);
        }
    }

    /**
     * 功能描述:
     * content-type:application/json
     * @Date: 2018年08月31日 17:31:52
     * @param data
     * @param url
     * @return: java.lang.String
     **/
    public static String toPostJson(String data,String url) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            // 设置包体参数。
            StringEntity se = new StringEntity(data);
            se.setContentEncoding("UTF-8");
            se.setContentType("application/json");
            httppost.setEntity(se);
            return formatResponse(httpclient.execute(httppost));
        } catch (Exception e) {
            //e.printStackTrace();
            logger.info("HTTPCLIENT-toPostJson请求异常:"+e.getMessage());
            throw new Exception(e.getMessage());
        }finally {
            formatHttpClient(httpclient);
        }
    }
    /**
     * 功能描述:支持请求头
     * content-type:application/json
     * @Date: 2018年08月31日 17:31:52
     * @param data
     * @param url
     * @return: java.lang.String
     **/
    public static String toPostJson(String data,String url,Map<String,String> headMap) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            // 设置包体参数。
            StringEntity se = new StringEntity(data);
            se.setContentEncoding("UTF-8");
            se.setContentType("application/json");
            httppost.setEntity(se);
            //设置请求头
            if(headMap != null){
                for(Map.Entry<String,String> entry:headMap.entrySet()){
                    httppost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            httppost.setHeader("Content-Type", "application/json");
            return formatResponse(httpclient.execute(httppost));
        } catch (Exception e) {
            //e.printStackTrace();
            logger.info("HTTPCLIENT-toPostJson请求异常:"+e.getMessage());
            throw new Exception(e.getMessage());
        }finally {
            formatHttpClient(httpclient);
        }
    }

    /**
     * 功能描述:
     * content-type:application/x-www-form-urlencoded
     * @Date: 2018年08月31日 17:34:20
     * @param data
     * @param url
     * @return: java.lang.String
     **/
    public static String toPostForm(Map<String,String> data,String url) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            if(data != null){
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for(Map.Entry<String, String> entry : data.entrySet()){
                    NameValuePair v = new BasicNameValuePair(entry.getKey(),entry.getValue());
                    nvps.add(v);
                }
                httppost.setEntity(new UrlEncodedFormEntity(nvps,Consts.UTF_8));
                httppost.setHeader("Content-Type", "application/json");
            }
            return formatResponse(httpclient.execute(httppost));
        } catch (Exception e) {
            //e.printStackTrace();
            logger.info("HTTPCLIENT-toPostJson请求异常:"+e.getMessage());
            throw new Exception(e.getMessage());
        }finally {
            formatHttpClient(httpclient);
        }
    }

    /**
     * 功能描述:
     * content-type:application/x-www-form-urlencoded
     * @Date: 2018年08月31日 17:34:20
     * @param data
     * @param url
     * @return: java.lang.String
     **/
    public static String toPostForm(String data,String url) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpPost httppost = new HttpPost(url);
            // 设置包体参数。
            StringEntity se = new StringEntity(data);
            se.setContentEncoding("UTF-8");
            se.setContentType("application/x-www-form-urlencoded");
            httppost.setEntity(se);
            return formatResponse(httpclient.execute(httppost));
        } catch (Exception e) {
            //e.printStackTrace();
            logger.info("HTTPCLIENT-toPostJson请求异常:"+e.getMessage());
            throw new Exception(e.getMessage());
        }finally {
            formatHttpClient(httpclient);
        }
    }

    /**
     * 功能描述:
     * get请求
     * @Date: 2018年09月10日 18:47:56
     * @param reqParams
     * @param url
     * @return: java.lang.String
     **/
    public static String get(Map<String,String> reqParams,String url) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            //创建HTTPCLIENT链接对象
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            StringBuffer sb = new StringBuffer(url);
            if(reqParams !=null && !reqParams.isEmpty()){
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(reqParams.size());
                for (String key :reqParams.keySet()){
                    pairs.add(new BasicNameValuePair(key, reqParams.get(key).toString()));
                }
                sb.append("?").append(EntityUtils.toString(new UrlEncodedFormEntity(pairs), "UTF-8"));
            }
            logger.info("请求地址:"+sb.toString());
            HttpGet httpGet = new HttpGet(sb.toString());
            return formatResponse(httpclient.execute(httpGet));
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("发起HTTP-GET请求异常:"+e.getMessage());
            throw new Exception("发起HTTP-GET请求异常!");
        }
    }

    /**
     * 功能描述:
     * get请求
     * @Date: 2018年09月10日 18:55:28
     * @param reqParams
     * @param url
     * @return: java.lang.String
     **/
    public static String get(String reqParams,String url) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            //创建HTTPCLIENT链接对象
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            StringBuffer sb = new StringBuffer(url);
            sb.append("?").append(reqParams);
            HttpGet httpGet = new HttpGet(sb.toString());
            return formatResponse(httpclient.execute(httpGet));
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("发起HTTP-GET请求异常:"+e.getMessage());
            throw new Exception("发起HTTP-GET请求异常!");
        }
    }

    /**
     * 功能描述:
     * GET请求
     * @Date: 2018年09月10日 18:57:26
     * @param reqParams
     * @return: java.lang.String
     **/
    public static String get(String reqParams) throws Exception{
        CloseableHttpClient httpclient = null;
        try {
            //创建HTTPCLIENT链接对象
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            HttpGet httpGet = new HttpGet(reqParams);
            return formatResponse(httpclient.execute(httpGet));
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("发起HTTP-GET请求异常:"+e.getMessage());
            throw new Exception("发起HTTP-GET请求异常!");
        }
    }

    /**
     * 功能描述:
     * 处理请求结果
     * @Date: 2018年08月31日 17:22:17
     * @param
     * @return: java.lang.String
     **/
    private static String formatResponse(CloseableHttpResponse response) throws Exception{
        try {
           // if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),Consts.UTF_8));
                StringBuffer sb = new StringBuffer();
                String content = null;
                while((content = reader.readLine()) != null){
                    sb.append(content);
                }
                return sb.toString();
          //  }
           // return null;
        } catch (Exception e) {
            //e.printStackTrace();
            logger.info("处理HTTPCLIENT请求结果异常:"+e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            response.close();
        }
    }

    /**
     * 功能描述:
     * 处理HTTPCLIENT连接
     * @Date: 2018年08月31日 17:38:02
     * @param httpclient
     * @return: void
     **/
    private static void formatHttpClient(CloseableHttpClient httpclient) throws Exception{
        try {
            if(httpclient != null){
                httpclient.close();
            }
        } catch (IOException e) {
            //e.printStackTrace();
            logger.info("关闭HTTPCLIENT连接异常:"+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 功能描述:
     * 生成表单
     * @Date: 2018年08月31日 17:16:49
     * @param params
     * @param payUrl
     * @return: java.lang.String
     **/
    public static String generatorForm(Map<String, String> params,String payUrl) {
        String FormString = "<body onLoad=\"document.actform.submit()\">正在处理请稍候.....................<form  id=\"actform\" name=\"actform\" method=\"post\" action=\""
                + payUrl + "\">";
        for (String key : params.keySet()) {
            if (StringUtils.isNotBlank(params.get(key)))
                FormString += "<input name=\"" + key + "\" type=\"hidden\" value='" + params.get(key) + "'>\r\n";
        }
        FormString += "</form></body>";
        return FormString;
    }

    /**
     * 功能描述:
     * 创建连接
     * @Date: 2018年08月31日 17:17:55
     * @param
     * @return: org.apache.http.impl.conn.PoolingHttpClientConnectionManager
     **/
    private static PoolingHttpClientConnectionManager createConnectionManager() throws Exception {
        TrustManager tm = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
        };
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[] { tm }, null);

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(context,
                NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
        return connectionManager;
    }



    public static String doPost(String url,Map<String,String> map,String charset){
        HttpPost httpPost = null;
        String result = null;
        try{
            HttpClient httpClient=new DefaultHttpClient();
            httpPost = new HttpPost(url);
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
            }
            if(list.size() > 0){
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);
                httpPost.setEntity(entity);
            }

            HttpResponse response = httpClient.execute(httpPost);

            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,charset);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    public static String jsonEncode(Object map) throws
            JsonProcessingException {
        String s = new ObjectMapper().writeValueAsString(map);
        s = s.replace("/", "\\/");
        s = chineseCharacterToUnicode(s);
        return s;
    }

    private static String chineseCharacterToUnicode(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            int chr1 = (char) str.charAt(i);
            if (chr1 >= 19968 && chr1 <= 171941) {//汉字范围 \u4e00-\u9fa5 (中文)
                result += "\\u" + Integer.toHexString(chr1);
            } else {
                result += str.charAt(i);
            }
        }
        return result;
    }
}
