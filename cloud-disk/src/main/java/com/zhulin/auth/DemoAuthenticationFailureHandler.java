package com.zhulin.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author:ZHULIN
 * @Date: 2022/6/10
 * @Description: 验证失败的处理器
 */
@Component
public class DemoAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private Logger logger = LoggerFactory.getLogger(DemoAuthenticationFailureHandler.class);

    /**
     * Json框架
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 处理登录失败的请求
     * @param request
     * @param response
     * @param exception
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        logger.info("登录失败");
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        //将异常输出成json
        String jsonResult = objectMapper.writeValueAsString(exception.getMessage());
        out.write(jsonResult);
        out.flush();
    }
}
