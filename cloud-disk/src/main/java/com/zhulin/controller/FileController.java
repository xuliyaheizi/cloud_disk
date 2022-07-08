package com.zhulin.controller;

import com.zhulin.service.HdfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author:ZHULIN
 * @Date: 2022/7/5
 * @Description:
 */
@Controller
@RequestMapping("/hdfs/file")
public class FileController {
    @Autowired
    private HdfsService hdfsService;

    @GetMapping("/downLoadFile")
    public ResponseEntity<InputStreamResource> downLoadFile(@RequestParam("path") String path, @RequestParam(
            "fileName") String fileName) {
        System.out.println(path);
        ResponseEntity<InputStreamResource> result = null;
        try {
            result = hdfsService.downLoadFile(path, fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @GetMapping("/downLoadDirectoryFile")
    public ResponseEntity<byte[]> downLoadDirectoryFile(@RequestParam("path") String path, @RequestParam(
            "fileName") String fileName) {
        ResponseEntity<byte[]> result = null;
        try {
            result = hdfsService.downLoadDirectory(path, fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
