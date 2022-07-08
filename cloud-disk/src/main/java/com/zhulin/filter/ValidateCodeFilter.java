package com.zhulin.filter;

import com.zhulin.auth.DemoAuthenticationFailureHandler;
import com.zhulin.exception.ValidateCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @Author:ZHULIN
 * @Date: 2022/6/10
 * @Description: 定义一个验证码的拦截器
 */
public class ValidateCodeFilter extends OncePerRequestFilter {
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 失败处理器
     */
    private DemoAuthenticationFailureHandler demoAuthenticationFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //判断是否是登录请求
        if ("/doLogin".equals(request.getRequestURI()) && "post".equalsIgnoreCase(request.getMethod())) {
            try {
                validate(request);
            } catch (ValidateCodeException e) {
                demoAuthenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 校验验证码
     * @param request
     * @throws ValidateCodeException
     */
    private void validate(HttpServletRequest request) throws ValidateCodeException {
        //获取Session中验证码
        HttpSession session = request.getSession();
        String validateCode = session.getAttribute("validateCode").toString();
        String imageCode = request.getParameter("code");
        if (imageCode == null || "".equalsIgnoreCase(imageCode)) {
            throw new ValidateCodeException("验证码的值不能为空");
        }
        if (!validateCode.equalsIgnoreCase(imageCode)) {
            throw new ValidateCodeException("验证码不匹配");
        }
    }

    public DemoAuthenticationFailureHandler getDemoAuthenticationFailureHandler() {
        return demoAuthenticationFailureHandler;
    }

    public void setDemoAuthenticationFailureHandler(DemoAuthenticationFailureHandler demoAuthenticationFailureHandler) {
        this.demoAuthenticationFailureHandler = demoAuthenticationFailureHandler;
    }
}
