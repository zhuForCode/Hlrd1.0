package com.juhua.hangfen.shaoxingrd.tools;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
/**
 * Created by congj on 2018/1/3.
 */

public class AesCrypto {
    public static void main(String[] args) throws Exception {
       // String content = "{\"serialNumber\":\"330211JD04000017092001003JKLBNTH\",\"status\":36}";
     //   System.out.println("加密前：" + content);

        String key = "EIFEICCSPVKDVRHWDUCX";
        System.out.println("加密密钥和解密密钥：" + (int)'1' + " " + (int)'2' + " " + (int)'3' );

        String encrypt = "VlfxPC1Urf0hZ+7aExB0SMma/FLJrhxTtpAAZk8MZulgM69LCAvrQgtjHOLmj7kUyHzaL1slz7u3e3FIttSMcY1RG4FB0SR6h2IIxd6OzBY=";
        System.out.println("密文：" + encrypt);

        String decrypt = aesDecrypt(encrypt, key);
        System.out.println("解密后：" + decrypt);

        String password = "EIFEICCSPVKDVRHWDUCX";//AES的密钥
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(password.getBytes());
        kgen.init(128, secureRandom);

        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();//AES加密实际的Key值
        System.out.println(org.kobjects.base64.Base64.encode(enCodeFormat));
        //System.out.println(new String(Base64.getEncoder().encodeToString(enCodeFormat)));
    }

    /**
     * 将byte[]转为各种进制的字符串
     * @param bytes byte[]
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix){
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }

    /**
     * base 64 encode
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    public static String base64Encode(byte[] bytes){
        return new BASE64Encoder().encode(bytes);
    }

    /**
     * base 64 decode
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     * @throws Exception
     */
    public static byte[] base64Decode(String base64Code) throws Exception{
        return QStringUtil.isEmpty(base64Code) ? null : new BASE64Decoder().decodeBuffer(base64Code);
    }

    /**
     * 获取byte[]的md5值
     * @param bytes byte[]
     * @return md5
     * @throws Exception
     */
    public static byte[] md5(byte[] bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bytes);

        return md.digest();
    }

    /**
     * 获取字符串md5值
     * @param msg
     * @return md5
     * @throws Exception
     */
    public static byte[] md5(String msg) throws Exception {
        return QStringUtil.isEmpty(msg) ? null : md5(msg.getBytes());
    }

    /**
     * 结合base64实现md5加密
     * @param msg 待加密字符串
     * @return 获取md5后转为base64
     * @throws Exception
     */
    public static String md5Encrypt(String msg) throws Exception{
        return QStringUtil.isEmpty(msg) ? null : base64Encode(md5(msg));
    }

    /**
     * AES加密
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     * @throws Exception
     */
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        //加密解密时都加上,否则不同机器加密结果不一致
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random=SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(encryptKey.getBytes());
        kgen.init(128, random);

        //一般写法
        //KeyGenerator kgen = KeyGenerator.getInstance("AES");
        //kgen.init(128, new SecureRandom(encryptKey.getBytes()));

        Cipher cipher = Cipher.getInstance("AES");
        System.out.println("加密密钥和解密密钥：" + (int)'1' + " " + (int)'2' + " " + (int)'3' );
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));

        return cipher.doFinal(content.getBytes("utf-8"));
    }

    /**
     * AES加密为base 64 code
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     * @throws Exception
     */
    public static String aesEncrypt(String content, String encryptKey) throws Exception {
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }

    /**
     * AES解密
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey 解密密钥
     * @return 解密后的String
     * @throws Exception
     */
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        //加密解密时都加上,否则不同机器加密结果不一致
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random=SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(decryptKey.getBytes());
        kgen.init(128, random);

        //一般写法
        //KeyGenerator kgen = KeyGenerator.getInstance("AES");
        //kgen.init(128, new SecureRandom(decryptKey.getBytes()));

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes);
    }

    /**
     * 将base 64 code AES解密
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     * @throws Exception
     */
    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
        return QStringUtil.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }

}

class QStringUtil {
    public static boolean isEmpty(String str){
        if(str==null){
            return true;
        }else{
            return false;
        }

    }
}
