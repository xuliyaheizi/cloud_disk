package com.zhulin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhulin.bean.User;
import com.zhulin.mapper.UserMapper;
import com.zhulin.service.UserService;
import com.zhulin.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @Author:ZHULIN
 * @Date: 2022/7/5
 * @Description:
 */
@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    /**
     * spring security提供的一个密码加密的类
     */
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserVO insert(UserVO user) {
        User u = new User();
        u.setUname(user.getUname());
        u.setUpwd(passwordEncoder.encode(user.getUpwd()));
        u.setRole("ROLE_ADMIN");
        int insert = userMapper.insert(u);
        if (insert != 1) {
            return null;
        }
        User one = userMapper.selectOne(new QueryWrapper<User>().eq("uname", user.getUname()));
        user.setUid(one.getUid());
        return user;
    }

    @Override
    public boolean isUnameValid(String uname) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("uname", uname));
        if (user == null) {
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("uname", username));
        if (user == null) {
            return null;
        }

        //创建一个权限的集合
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        //如果有多个权限，则要使用循环
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        org.springframework.security.core.userdetails.User u =
                new org.springframework.security.core.userdetails.User(user.getUname(),
                        user.getUpwd(), authorities);
        return u;
    }
}
