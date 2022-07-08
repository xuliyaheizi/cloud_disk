package com.zhulin.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhulin.auth.DemoAuthenticationFailureHandler;
import com.zhulin.constants.Constants;
import com.zhulin.filter.ValidateCodeFilter;
import com.zhulin.service.impl.UserServiceImpl;
import com.zhulin.vo.JsonModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author:ZHULIN
 * @Date: 2022/6/10
 * @Description: Security配置文件
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserServiceImpl userService;

    /** 写类名 */
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /** 登录失败的处理 */
    @Autowired
    private DemoAuthenticationFailureHandler demoAuthenticationFailureHandler;

    /**
     * 配置拦截模式（判断哪些资源需要拦截）
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //自定义过滤器
        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        //错误处理组件
        validateCodeFilter.setDemoAuthenticationFailureHandler(demoAuthenticationFailureHandler);

        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class).formLogin()
                //定义登录页面，访问一个需要登录之后才能访问的接口，会自动跳转到该页面
                //默认跳转的是spring security自带的登录界面
                .loginPage("http://localhost/")
                //默认是 /login  但是当配置了 .loginPage("/login.html"),默认值就变成了 /login.html
                .loginProcessingUrl("/doLogin")
                //设置登录成功页
                //.defaultSuccessUrl("/back/index.html")
                //定义登录时，用户名的key，默认为 username
                .usernameParameter("uname")
                //定义登陆时，用户密码的key，默认为password
                .passwordParameter("pwd")
                //登录成功的处理器
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                        Authentication authentication) throws IOException,
                            ServletException {
                        String uname = request.getParameter("uname");
                        HttpSession session = request.getSession();
                        session.setAttribute(Constants.LOGINUSER, uname);
                        response.setContentType("application/json; charset=UTF-8");
                        PrintWriter out = response.getWriter();
                        JsonModel jm = new JsonModel<>(200, "登录成功");
                        out.write(objectMapper.writeValueAsString(jm));
                        out.flush();
                    }
                })
                //登录失败的处理器
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                                        AuthenticationException exception) throws IOException,
                            ServletException {
                        exception.printStackTrace();
                        response.setContentType("application/json; charset=UTF-8");
                        PrintWriter out = response.getWriter();
                        out.write("fail");
                        out.flush();
                    }
                })
                //和表单登录相关的接口统统都直接通过
                .permitAll()
                .and()
                .logout()
                //退出url
                .logoutUrl("/logout.action")
                //退出成功处理器
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                        response.setContentType("application/json; charset=UTF-8");
                        PrintWriter out = response.getWriter();
                        JsonModel jm = new JsonModel<>(200, null, "logout success");
                        out.write(objectMapper.writeValueAsString(jm));
                        out.flush();
                    }
                })
                .permitAll()
                .and()
                .httpBasic()
                .and()
                .authorizeRequests() //开启登录配置
                .antMatchers("/verifyCodeServlet", "/doLogin")
                .permitAll()
                .antMatchers("http://localhost")
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .csrf()
                //关闭CSRF跨域
                .disable()
                .headers()
                .frameOptions()
                .disable();
    }

    /**
     * 配置用户信息，主要用法
     * 1.通过Java的方式，配置用户名、密码
     * 2.在这里完成获得数据库中的用户信息
     * 3.密码一定要加密（加密的方式一定要和注册的加密方式一样）
     * 4.登录认证
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //springSecurity 提供BCryptPasswordEncoder类，实现了String的passwordEncoder接口使用BCrypt强哈希方法来加密密码
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }

    /**
     * 地址过滤，即该地址不走spring security过滤链
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        //设置拦截忽略文件，可以对静态资源放行
        web.ignoring().antMatchers();
    }
}
