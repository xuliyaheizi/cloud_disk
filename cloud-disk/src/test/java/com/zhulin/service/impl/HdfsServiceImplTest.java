package com.zhulin.service.impl;

import com.zhulin.service.HdfsService;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @Author:ZHULIN
 * @Date: 2022/7/5
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class HdfsServiceImplTest {
    @Autowired
    private HdfsService hdfsService;

    @Test
    void getConfiguration() {
    }

    @Test
    void getFileSystem() {
    }

    @Test
    void getConfigurationInfoAsMap() {
    }

    @Test
    void listStatus() {
    }

    @Test
    void existFile() {
    }

    @Test
    void mkdir() {
    }

    @Test
    void readPathInfo() {
    }

    @Test
    void createFile() {
    }

    @Test
    void readFile() {
        StringBuffer readFile = hdfsService.readFile("/danbook.sql");
        System.out.println(readFile);
    }

    @Test
    void testListStatus() {
    }

    @Test
    void getFileInfo() throws IOException {
        FileSystem fs = hdfsService.getFileSystem();
        RemoteIterator<LocatedFileStatus> fileStatuses = fs.listFiles(new Path("/xsxjzp.jpg"), true);
        while (fileStatuses.hasNext()) {
            LocatedFileStatus next = fileStatuses.next();
            System.out.println(next);
        }

    }

    @Test
    void listFile() {
    }

    @Test
    void renameFile() {
    }

    @Test
    void deleteFile() {
    }

    @Test
    void downLoadFile() {
    }

    @Test
    void downLoadDirectory() {
    }

    @Test
    void copyFile() {
    }

    @Test
    void openFileToBytes() {
    }

    @Test
    void getFileBlockLocations() {
    }

    @Test
    void upLoadFile() {
    }
}