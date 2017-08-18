package com.juhua.hangfen.eedsrd.tools;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;


/**
 * Created by JiaJin Kuai on 2016/9/21.
 */
public class CryptoTools {
    private static final byte[] DESkey = { 10, 20, 30, 40, 50, 60, 70, 80 };// 设置密钥，略去

    private static final byte[] DESIV =  { 11, 22, 33, 44, 55, 66, 77, 85 };
    ;// 设置向量，略去

    public static String Verify;
    static AlgorithmParameterSpec iv = null;// 加密算法的参数接口，IvParameterSpec是它的一个实现
    private static Key key = null;

    public CryptoTools() throws Exception {
        DESKeySpec keySpec = new DESKeySpec(DESkey);// 设置密钥参数
        iv = new IvParameterSpec(DESIV);// 设置向量
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
        key = keyFactory.generateSecret(keySpec);// 得到密钥对象

    }

    public String encode(String data) throws Exception {
        Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");// 得到加密对象Cipher
        enCipher.init(Cipher.ENCRYPT_MODE, key, iv);// 设置工作模式为加密模式，给出密钥和向量
        byte[] pasByte = enCipher.doFinal(data.getBytes("utf-8"));
        System.out.println("shujujiexi555:"+  Arrays.toString(pasByte));
        BASE64Encoder base64Encoder = new BASE64Encoder();
        return base64Encoder.encode(pasByte);
    }
    public String decode(String data) throws Exception {
        Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        deCipher.init(Cipher.DECRYPT_MODE, key, iv);
        BASE64Decoder base64Decoder = new BASE64Decoder();

        byte[] pasByte = deCipher.doFinal(base64Decoder.decodeBuffer(data));

        return new String(pasByte, "UTF-8");
    }
    public static void main(String[] args) throws Exception {
        Date day=new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        CryptoTools tools = new CryptoTools();
        System.out.println("加密:" + tools.encode("2014ZjrdVerify" + sdf.format(day)));
        System.out.println("数据:" + "2014ZjrdVerify" + sdf.format(day));
        String a = new String(DESkey);
        String b = new String(DESIV);
        String aaa = "\n\u0014\u001E(2<FP";
        String bbb = "\u000B\u0016!,7BMU";
        System.out.println("DESKey:"+  Arrays.toString(a.getBytes()));
        System.out.println("DESKey:"+  Arrays.toString(b.getBytes()));
        System.out.println("DESKey:"+  Arrays.toString(aaa.getBytes()));
        System.out.println("DESKey:"+  Arrays.toString(bbb.getBytes()));
        System.out.println("DESKey:"+  a);
        System.out.println("DESKey:"+  b);
        String ccc = "u8g8uoMUtgwgD9JEZXtMFpbzD2ZU0231";
        byte[] adad = {};
        System.out.println("shujujiexi:"+  Arrays.toString(ccc.getBytes()));
        Verify = tools.encode("2014ZjrdVerify" + sdf.format(day));
    }
    public String getencode(String verify){
        Date day=new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        try {
            CryptoTools tools = new CryptoTools();
            verify = tools.encode("2014ZjrdVerify" + sdf.format(day));
        }catch (Exception e1) {
            e1.printStackTrace();
        }

        return verify;
    }
    public String returnVerify() throws Exception{
        String verify = "";
        Date day=new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        CryptoTools tools = new CryptoTools();
        System.out.println("加密:" + tools.encode("2014ZjrdVerify" + sdf.format(day)));
        verify = tools.encode("2014ZjrdVerify" + sdf.format(day));
        return verify;

    }
    public String returnEncode(String spData) throws Exception{
        String encodeData = "";
        CryptoTools tools = new CryptoTools();
        encodeData = tools.encode(spData);
        return encodeData;
    }

    public String returnDecode(String encodeData) throws Exception{
        String decodeData = "";
        CryptoTools tools = new CryptoTools();
        decodeData = tools.decode(encodeData);
        return decodeData;
    }

}
