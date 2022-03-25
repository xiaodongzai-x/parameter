package com.parameter.tools;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName MD5.java
 * @Description TODO
 * @createTime 2021��11��30�� 17:23:00
 */
public class MD5 {
    /**
     * byte MD5����
     *
     * @param bytes
     * @return
     */
    public static String getMD5Bytes(byte[] bytes) {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest = md5.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //16�Ǳ�ʾת��Ϊ16������
        String md5Str = new BigInteger(1, digest).toString(16);
        StringBuffer stringBuffer = new StringBuffer();
        int m = 32-md5Str.length();
        if(m==0){
            return md5Str.toUpperCase();
        }else {
            for (int i=0;i<m;i++){
                stringBuffer.append("0");
            }
            stringBuffer.append(md5Str);
            return stringBuffer.toString().toUpperCase();
        }
    }
}
