package com.zhulin.controller;

import com.zhulin.service.HdfsService;
import com.zhulin.vo.JsonModel;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author:ZHULIN
 * @Date: 2022/7/4
 * @Description: Hdfs控制类
 */
@RestController
@RequestMapping("/hdfs")
public class HdfsController {
    @Autowired
    private HdfsService hdfsService;
    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/showList")
    public JsonModel<List<Map<String, String>>> showList(@RequestParam String path) {
        //判断路径是否为空，为空则默认是根目录 /
        if (StringUtils.isEmpty(path) || path == null) {
            path = "/";
        }
        //判断请求参数格式，开头是否是以 / 开头
        if (!path.substring(0, 1).equals("/")) {
            return new JsonModel<>(400, "请求参数错误");
        }
        //判断请求路径是否存在
        if (!hdfsService.existFile(path)) {
            return new JsonModel<>(400, "请求路径不存在");
        }
        List<Map<String, String>> maps = hdfsService.listStatus(path);
        return new JsonModel<>(200, "请求成功", maps);
    }

    @PostMapping("/delete")
    public JsonModel deleteFile(@RequestBody String path) {
        if (StringUtils.isEmpty(path) || path == null) {
            return new JsonModel(400, "请求参数错误");
        }
        if (!hdfsService.existFile(path)) {
            return new JsonModel(400, "请求路径不存在");
        }
        //获取文件信息
        Map<String, String> fileInfo = hdfsService.getFileInfo(path);
        boolean deleteFile = hdfsService.deleteFile(path);
        if (!deleteFile) {
            return new JsonModel(400, "执行错误");
        }
        return new JsonModel(200, "删除成功");
    }

    @PostMapping("/mkdir")
    public JsonModel mkdir(@RequestBody String path) {
        if (StringUtils.isEmpty(path) || path == null) {
            return new JsonModel(400, "请求参数错误");
        }
        if (hdfsService.existFile(path)) {
            return new JsonModel(400, "新建路径已存在");
        }
        boolean mkdir = hdfsService.mkdir(path);
        if (!mkdir) {
            return new JsonModel(400, "创建目录失败");
        }
        return new JsonModel(200, "成功创建目录");
    }

    @PostMapping("/rename")
    public JsonModel<Map<String, String>> rename(@RequestParam("srcPath") String srcPath,
                                                 @RequestParam("newPath") String newPath) {
        if (StringUtils.isEmpty(srcPath) || StringUtils.isEmpty(newPath)) {
            return new JsonModel<>(400, "请求参数错误");
        }

        if (hdfsService.existFile(newPath)) {
            return new JsonModel<>(400, "该路径已存在");
        }
        boolean renameFile = hdfsService.renameFile(srcPath, newPath);
        if (!renameFile) {
            return new JsonModel<>(400, "重命名失败");
        }
        Map<String, String> fileInfo = hdfsService.getFileInfo(newPath);
        return new JsonModel<>(200, "重命名成功", fileInfo);
    }

    @PostMapping("/upload")
    public JsonModel upload(@RequestParam MultipartFile file, @RequestParam String curPath) {
        if (file.isEmpty()) {
            return new JsonModel(400, "请求参数错误");
        }
        try {
            RBloomFilter<Object> cloudDisk = redissonClient.getBloomFilter("cloudDisk");
            //初始化布隆过滤器
            cloudDisk.tryInit(500000L, 0.03);
            //获取文件的md5吗
            String md5Hex = DigestUtils.md5Hex(file.getInputStream());
            //判断布隆过滤器中是否存在
            if (cloudDisk.contains(md5Hex)) {
                //TODO:判断指纹码（数据库--->布隆过滤器）是否存在，存在，则只要存数据库，没有则真正上传
                return new JsonModel(400, "文件已存在，请勿重复上传");
            }
            //过滤器中不存在
            cloudDisk.add(md5Hex);
            hdfsService.createFile(curPath, file);
            //回传当前路径的文件信息
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonModel(400, "创建文件异常!" + e.getMessage());
        }
        return new JsonModel(200, "请求成功");
    }

    @GetMapping("/read")
    public JsonModel readFile(@RequestParam String path) {
        if (StringUtils.isEmpty(path) || path == null) {
            return new JsonModel(400, "请求参数错误");
        }

        if (!hdfsService.existFile(path)) {
            return new JsonModel(400, "请求路径不存在!请重试");
        }
        StringBuffer readFile = hdfsService.readFile(path);
        return new JsonModel(200, "请求成功", readFile);
    }
}
