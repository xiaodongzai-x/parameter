package com.parameter.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName ByteArray.java
 * @Description TODO
 * @createTime 2021��11��30�� 17:51:00
 */
public class ByteArray {
    /**
     * @param bt
     * @return
     * @description ��byte ����ѹ��
     */
    public static byte[] compress(byte[] bt) {
        //��byte���ݶ����ļ���
        ByteArrayOutputStream bos = null;
        GZIPOutputStream gzipos = null;
        try {
            bos = new ByteArrayOutputStream();
            gzipos = new GZIPOutputStream(bos);
            gzipos.write(bt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(gzipos);
            closeStream(bos);
        }
        return bos.toByteArray();
    }

    /**
     * @param bt
     * @return
     * @description ��ѹ��byte����
     */
    public static byte[] unCompress(byte[] bt) {
        ByteArrayOutputStream byteAos = null;
        ByteArrayInputStream byteArrayIn = null;
        GZIPInputStream gzipIn = null;
        try {
            byteArrayIn = new ByteArrayInputStream(bt);
            gzipIn = new GZIPInputStream(byteArrayIn);
            byteAos = new ByteArrayOutputStream();
            byte[] b = new byte[4096];
            int temp = -1;
            while ((temp = gzipIn.read(b)) > 0) {
                byteAos.write(b, 0, temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeStream(byteAos);
            closeStream(gzipIn);
            closeStream(byteArrayIn);
        }
        return byteAos.toByteArray();
    }

    /**
     * @param oStream
     * @description �ر�������
     */
    public static void closeStream(Closeable oStream) {
        if (null != oStream) {
            try {
                oStream.close();
            } catch (IOException e) {
                oStream = null;//��ֵΪnull,�ȴ���������
                e.printStackTrace();
            }
        }
    }
}

