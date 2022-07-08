package com.zhulin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @Author:ZHULIN
 * @Date: 2022/7/4
 * @Description: 云盘项目启动类
 */
@SpringBootApplication
@ServletComponentScan
@MapperScan("com.zhulin.mapper")
public class CloudDiskMain {
    public static void main(String[] args) {
        SpringApplication.run(CloudDiskMain.class, args);
    }
}
