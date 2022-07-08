package com.zhulin.utils;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author:ZHULIN
 * @Date: 2022/6/11
 * @Description: 工具类
 */
public class DiskUtil {
    private final static long FILESIZE_B = 1024;
    private final static long FILESIZE_KB = 1048576;
    private final static long FILESIZE_GB = 1073741824;

    /**
     * 时间格式化
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:sss");
        Date date = new Date(time);
        return sdf.format(date);
    }

    /**
     * 文件大小单位换算
     * @param fileS
     * @return
     */
    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < FILESIZE_B) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < FILESIZE_KB) {
            fileSizeString = df.format((double) fileS / FILESIZE_B) + "KB";
        } else if (fileS < FILESIZE_GB) {
            fileSizeString = df.format((double) fileS / FILESIZE_KB) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / FILESIZE_GB) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 输出文件
     * @param inputStream
     * @param fileName
     * @return
     */
    public static ResponseEntity<InputStreamResource> downLoadFile(InputStream inputStream, String fileName) {
        try {
            byte[] testBytes = new byte[inputStream.available()];
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Cache-Control", "no-cache,no-store,must-revalidate");
            httpHeaders.add("Content-Disposition", String.format("attachment;fileName=\"%s\"", fileName));
            httpHeaders.add("Pragma", "no-cache");
            httpHeaders.add("Expires", "0");
            httpHeaders.add("Content-Language", "UTF-8");
            //让文件内容以流的形式输出
            return ResponseEntity.ok()
                    .headers(httpHeaders)
                    .contentLength(testBytes.length)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new InputStreamResource(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 目录打包
     * @param bs       压缩打包的字节数组流
     * @param fileName
     * @return
     */
    public static ResponseEntity<byte[]> downLoadDirectoryFile(byte[] bs, String fileName) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Cache-Control", "no-cache,no-store,must-revalidate");
            httpHeaders.add("Content-Disposition", String.format("attachment;fileName=\"%s\"", fileName));
            httpHeaders.add("Pragma", "no-cache");
            httpHeaders.add("Expires", "0");
            httpHeaders.add("Content-Language", "UTF-8");
            return ResponseEntity.ok()
                    .headers(httpHeaders)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .contentLength(bs.length)
                    .body(bs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String genFileName(String fileName) {
        return fileName + ".zip";
    }
}
