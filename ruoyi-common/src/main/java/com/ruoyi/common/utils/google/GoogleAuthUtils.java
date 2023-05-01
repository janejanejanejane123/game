package com.ruoyi.common.utils.google;

/**
 *
 * 谷歌身份认证工具类.
 * 
 * @author nn
 * @date  2019-02-18 11:40:28
 *
 */
public class GoogleAuthUtils {

	public static void main(String args[]) {
		System.out.println(   GoogleAuthenticator.generateSecretKey());
	}

	/**
	 * 用这个qrcode生成二维码，用google身份验证器扫描二维码就能添加成功.
	 * @param user
	 * @param secret
	 * @return
	 */
	public static String genSecret(String user,String secret) {

		String qrcode = GoogleAuthenticator.getQRBarcode(user, secret);

		return qrcode;
	}

	/**
	 * 对app的随机生成的code,输入并验证.
	 */
	public static boolean verify(String secret,Long code) {
		long t = System.currentTimeMillis();
		GoogleAuthenticator ga = new GoogleAuthenticator();
		ga.setWindowSize(2);
		return ga.check_code(secret, code, t);
	}


}