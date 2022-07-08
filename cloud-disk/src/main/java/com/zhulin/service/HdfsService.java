package com.zhulin.service;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Author:ZHULIN
 * @Date: 2022/7/4
 * @Description: Hadoop-Hdfs相关接口
 */
public interface HdfsService {
    /**
     * 获取HDFS配置信息
     * @return
     */
    Configuration getConfiguration();

    /**
     * 获取HDFS文件系统对象
     * @return
     */
    FileSystem getFileSystem();

    /**
     * 获取配置文件，以map形式返回
     * @return
     */
    Map<String, String> getConfigurationInfoAsMap();

    /**
     * 读取Hdfs文件内容
     * @param path
     * @return
     */
    List<Map<String, String>> listStatus(String path);

    /**
     * 判断Hdfs文件是否存在
     * @param path
     * @return
     */
    boolean existFile(String path);

    /**
     * 创建Hdfs新目录
     * @param path
     * @return
     */
    boolean mkdir(String path);

    /**
     * 读取HDFS目录信息
     * @param path
     * @return
     */
    List<Map<String, Object>> readPathInfo(String path);

    /**
     * HDFS上传文件
     * @param path
     * @param file
     */
    void createFile(String path, MultipartFile file);

    /**
     * 读取HDFS文件内容
     * @param path
     * @return
     */
    StringBuffer readFile(String path);

    /**
     * 搜索整个磁盘中所有的某种类型的文件
     * @param type
     * @return
     */
    List<Map<String, String>> listStatus(int type);

    /**
     * 获取某个文件的信息
     * @param path
     * @return
     */
    Map<String, String> getFileInfo(String path);

    /**
     * 读取HDFS文件列表
     * @param path
     * @return
     */
    List<Map<String, String>> listFile(String path);

    /**
     * HDFS重命名文件
     * @param oldName
     * @param newName
     * @return
     */
    boolean renameFile(String oldName, String newName);

    /**
     * 删除HDFS文件
     * @param path
     * @return
     */
    boolean deleteFile(String path);

    /**
     * 下载HDFS文件
     * @param path
     * @param fileName
     * @return
     */
    ResponseEntity<InputStreamResource> downLoadFile(String path, String fileName);

    /**
     * 压缩打包目录
     * @param path
     * @param fileName
     * @return
     */
    ResponseEntity<byte[]> downLoadDirectory(String path, String fileName);

    /**
     * HDFS文件复制
     * @param sourcePath
     * @param targetPath
     * @return
     */
    boolean copyFile(String sourcePath, String targetPath);

    /**
     * 打开HDFS上的文件并返回byte数组
     * @param path
     * @return
     */
    byte[] openFileToBytes(String path);

    /**
     * 获取某个文件在HDFS的集群位置
     * @param path
     * @return
     */
    BlockLocation[] getFileBlockLocations(String path);

    /**
     * 上传HDFS文件
     * @param path
     * @param upLoadPath
     */
    void upLoadFile(String path, String upLoadPath);
}
