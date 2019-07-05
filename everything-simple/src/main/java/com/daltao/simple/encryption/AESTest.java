package com.daltao.simple.encryption;

import com.google.common.base.Charsets;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AESTest {
    public static void main(String[] args) throws Exception {
        String key = "de538c0c97788b8d8f0e88fe98e71ffb";

        System.out.println(key.length());

        System.out.println(AES.Encrypt("13590158643", key));
        System.out.println(AES.Decrypt("SCT//Vu/8/NhS4+1aKds6A==", key));
        //System.out.println(decrypt("SCT//Vu/8/NhS4+1aKds6A==".getBytes("utf-8"), key));
    }



    /**
     *
     * @author Administrator
     *
     */
    public static class AES {

        // 加密
        public static String Encrypt(String sSrc, String sKey) throws Exception {
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
//            if (sKey.length() != 16) {
//                System.out.print("Key长度不是16位");
//                return null;
//            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

            return new BASE64Encoder().encode(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
        }

        // 解密
        public static String Decrypt(String sSrc, String sKey) throws Exception {
            try {
                // 判断Key是否正确
                if (sKey == null) {
                    System.out.print("Key为空null");
                    return null;
                }
//                // 判断Key是否为16位
//                if (sKey.length() != 16) {
//                    System.out.print("Key长度不是16位");
//                    return null;
//                }
                byte[] raw = sKey.getBytes("utf-8");
                SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
                byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);//先用base64解密
                try {
                    byte[] original = cipher.doFinal(encrypted1);
                    String originalString = new String(original,"utf-8");
                    return originalString;
                } catch (Exception e) {
                    System.out.println(e.toString());
                    return null;
                }
            } catch (Exception ex) {
                System.out.println(ex.toString());
                return null;
            }
        }

        public static void main(String[] args) throws Exception {
            /*
             * 此处使用AES-128-ECB加密模式，key需要为16位。
             */
            String cKey = "1234567890123456";
            // 需要加密的字串
            String cSrc = "www.gowhere.so";
            System.out.println(cSrc);
            // 加密
            String enString = AES.Encrypt(cSrc, cKey);
            System.out.println("加密后的字串是：" + enString);

            // 解密
            String DeString = AES.Decrypt(enString, cKey);
            System.out.println("解密后的字串是：" + DeString);
        }
    }


    public static byte[] toByte(String key)
    {
        byte[] bytes = new byte[key.length() / 2];
        for(int i = 0; i < key.length(); i += 2)
        {
            bytes[i / 2] = (byte)Integer.parseInt(key.substring(i, i + 2), 16);
        }
        return bytes;
    }

    static char[] chars = "0123456789abcdef".toCharArray();

    public static String toRadix16(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            int v = ((int)b) & 0xff;
            builder.append(chars[v >>> 4]).append(chars[v & 0xf]);
        }
        return builder.toString();
    }

    public static byte[] encrypt(String content, String password) {
        KeyGenerator kgen = null;
        try {
            kgen = KeyGenerator.getInstance("AES");
            kgen.init(192, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] byteContent = content.getBytes("utf-8");
            byte[] result = cipher.doFinal(byteContent);
            return result;//加密
        } catch (NoSuchAlgorithmException | InvalidKeyException
                | NoSuchPaddingException | BadPaddingException
                | UnsupportedEncodingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    public static byte[] decrypt(byte[] content, String password) {
        KeyGenerator kgen = null;
        try {
            kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 解密
        } catch (NoSuchAlgorithmException | BadPaddingException
                | IllegalBlockSizeException | NoSuchPaddingException
                | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;

    }

}
