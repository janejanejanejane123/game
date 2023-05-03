package com.ruoyi.common.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.ruoyi.common.annotation.Decrypt;
import com.ruoyi.common.annotation.DecryptLong;
import com.ruoyi.common.annotation.ReferType;
import com.ruoyi.common.utils.AESUtils;
import com.ruoyi.common.utils.RSAUtil;
import com.ruoyi.common.utils.clazz.ClassFieldMetaInfoWithDecrypter;
import com.ruoyi.common.vo.EncStringParamsVo;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;

/**
 * 请求数据接收处理类<br>
 * 
 * 对加了@Decrypt的方法的数据进行解密操作<br>
 * 
 * 只对@RequestBody参数有效
 * 
 * @author yinjihuan
 * 
 * @about http://cxytiandi.com/about
 *
 */
@ControllerAdvice
public class EncryptRequestBodyAdvice implements RequestBodyAdvice {

	private Logger logger = LoggerFactory.getLogger(EncryptRequestBodyAdvice.class);
	
	@Value("${rsa.privateKey}")
	private String privateKey;

	@Value("${rsa.charset}")
	private String charset;
	
	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		return body;
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
		if(parameter.getMethod().isAnnotationPresent(Decrypt.class)){
			try {
				return new DecryptHttpInputMessage(inputMessage, privateKey, charset);
			} catch (Exception e) {
				logger.error("数据解密失败", e);
			}
		}else if (parameter.getMethod().isAnnotationPresent(DecryptLong.class)){
			try {
				return new AesAndRsaHttpInputMessage(inputMessage, privateKey, charset);
			}catch (Exception e){
				logger.error("数据解密失败 2",e);
			}

		}
		return inputMessage;
	}

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,

			Class<? extends HttpMessageConverter<?>> converterType) {
		if (body instanceof EncStringParamsVo){
			if (parameter.hasParameterAnnotation(ReferType.class)){
				ReferType referType = parameter.getParameterAnnotation(ReferType.class);
				assert referType!=null;
				EncStringParamsVo paramsVo = (EncStringParamsVo) body;
				Class<?> value = referType.value();
				String encrypt = paramsVo.getBody();
				try {
					String decrypt = RSAUtil.decrypt(encrypt, privateKey);
					paramsVo.setBody(JSON.parseObject(decrypt,value));
				} catch (Exception e) {
					return paramsVo;
				}
			}
		}else {
			ClassFieldMetaInfoWithDecrypter.isDetectorPresent(body,parameter);
		}
		return body;
	}
}

class AesAndRsaHttpInputMessage implements HttpInputMessage{

	private HttpHeaders headers;
	private InputStream body;

    public AesAndRsaHttpInputMessage(HttpInputMessage inputMessage,String privateKey,String charset)throws Exception{
		this.headers=inputMessage.getHeaders();
		String content = IOUtils.toString(inputMessage.getBody(), charset);
		JSONObject object = JSON.parseObject(content);
		String a = object.getString("a");
		String i = object.getString("i");

		String trace = object.getString("trace");
		String aesKey = RSAUtil.decrypt(a, privateKey);
		String iv = RSAUtil.decrypt(i, privateKey);
		byte[] decrypt = AESUtils.decrypt(trace, aesKey.getBytes(), iv);
		this.body = IOUtils.toInputStream(new String(decrypt,charset), charset);
	}

	@Override
	public InputStream getBody() throws IOException {
		return body;
	}

	@Override
	public HttpHeaders getHeaders() {
		return headers;
	}
}




class DecryptHttpInputMessage implements HttpInputMessage {
	private Logger logger = LoggerFactory.getLogger(EncryptRequestBodyAdvice.class);
    private HttpHeaders headers;
    private InputStream body;

    public DecryptHttpInputMessage(HttpInputMessage inputMessage, String key, String charset) throws Exception {
        this.headers = inputMessage.getHeaders();
        String content = IOUtils.toString(inputMessage.getBody(), charset);
		long startTime = System.currentTimeMillis();
        // JSON 数据格式的不进行解密操作
        String decryptBody = "";
        if (content.startsWith("{")) {
        	decryptBody = content;
		} else {
			decryptBody = RSAUtil.decrypt(content, key);
		}
        long endTime = System.currentTimeMillis();
		logger.debug("Decrypt Time:" + (endTime - startTime));
        this.body = IOUtils.toInputStream(decryptBody, charset);
    }

    @Override
    public InputStream getBody() throws IOException {
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}
