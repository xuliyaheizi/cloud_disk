package com.zhulin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author:ZHULIN
 * @Date: 2022/7/4
 * @Description: 前后端传输Json格式
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonModel<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    public JsonModel(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
