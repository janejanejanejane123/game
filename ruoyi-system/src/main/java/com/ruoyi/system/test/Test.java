package com.ruoyi.system.test;

import com.ruoyi.common.utils.RSAUtil;
import com.ruoyi.common.utils.http.HttpUtil;
import com.ruoyi.common.utils.http.HttpUtils;

public class Test {

    public static void main(String[] args) {
        Block block = new Block("952304081247113216","QWAZ");
        block.setHash();


        String hash = block.str2HexStr("434576ygregfedg");
        String s = block.toString();
        System.out.println(hash);
        System.out.println(s);

        String a= "{\"amount\":19.00,\"merNo\":\"a001\",\"sysOrder\":\"A00120220613135247135247lb2m\",\"payTime\":\"1655099603187\",\"orderid\":\"EPAY202206131352471352476616\",\"sign\":\"Wic0JX3RgildKvvY139uAnAESVdo1Oa+EHY6IqgmTMBBRH5+kSJwBLBUqcNFHImrls0T2MHf7rUUfKZ/3ciqUBOGmoIZBhD5DQJRfm2qXkQffYRLJ42ucAcPfmChTdvJykREbravDqnqOeAdlJzc21+mVQR438IA3l3ILxNtOz4=\"}";

        String s1 = null;
        try {
            s1 = HttpUtil.toPostJson(a,"http://wwww.000.com:1688/cloud-pay-service-server/notify/cm/1/EPAY202206131352471352476616");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(s1);

        //   testSha();

    }


   static public void testSha(){
        String pub="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCoj/RPYiPXNmnystH3O/Lt6fTWzHdeIEk4f0cOIsbnPRKjgXycxE1v/G88Kyha6at8LfgGkwgZ9L6RaaQXskGc+CdARdANvq8Tdm9CpjsvntYGNcD0BkSwTNVG1htN5AZJQxMTTHHVcFwXonR/dQYBl+kT3vmyeaXPYPLnIh0x2QIDAQAB";
        String pri = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKiP9E9iI9c2afKy0fc78u3p9NbMd14gSTh/Rw4ixuc9EqOBfJzETW/8bzwrKFrpq3wt+AaTCBn0vpFppBeyQZz4J0BF0A2+rxN2b0KmOy+e1gY1wPQGRLBM1UbWG03kBklDExNMcdVwXBeidH91BgGX6RPe+bJ5pc9g8uciHTHZAgMBAAECgYEAgcIfXiS8VmWq9Yu2mk3p2j/pnJjVov3Tn8xK1JTs7QFUoc2CMePjgLugnX7Tl/idbACvF9CDwgdn0+SwzxUH8B4n8Fl/pBJw93EsywnqGbcmwvQSSfnsDoC3nwPwwvdvZbH7dkRldp9JWqxYGZBfkN7dYkQWKQy1feqCWM+NzKkCQQDo0E9xQfcICvjgLS618oLIx2JI6byBlV5kfaWQZfqhun6xaNwCWtBMyec3KMQk7MtdAqSH9dL+cyYiNXPSYDSfAkEAuVmGrZrBpZ3P8VyIy/8jytxdAyGJCAIkGk20uCfov21eEqNs3Wte3p8EaoKtUMM3RfDKfNt5vIrZjX4I7wdOhwJAKcTl294H4guF50Jz9PTTfg43lflJdr5DtyfLwoJpWwbIoC+9+TYt1PkMYJsRxcYuHjvRJQt3lWs3Vj/czcuw5QJAcd1q3FnXHuxNI8SqEVBjZ6tqxJKkgTP3g+U64Ws7E+aXRTH6nifo9zCPYPN4j8XtvdzIOF4IcmiGb+L+kAFYHwJAMREMX08uTLEiZEBvoJMtMd4i31PCQpQ9lIx7WF7H0FQMGg0uoeSoHn33Jt2xHVXFjMovB9jEx/qqSrKgB4S9xg==";
        try {
            String s = RSAUtil.signBySHA1WithRSA("1111111", pri);

            boolean b = RSAUtil.rsaVerify(pub, s, "1111111");

            System.out.println(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
