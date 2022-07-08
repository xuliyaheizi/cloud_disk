package com.zhulin.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Author:ZHULIN
 * @Date: 2022/6/10
 * @Description: 用于抛出验证码错误的异常，集成AuthenticationException可被SpringSecurity捕获到
 */
public class ValidateCodeException extends AuthenticationException {
    public ValidateCodeException(String msg) {
        super(msg);
    }
}
