package com.zhulin.service;

import com.zhulin.vo.UserVO;

/**
 * @Author:ZHULIN
 * @Date: 2022/7/5
 * @Description:
 */
public interface UserService {
    /**
     * 添加用户
     * @param user
     * @return
     */
    UserVO insert(UserVO user);

    /**
     * 判断用户名是否有效
     * @param uname
     * @return
     */
    boolean isUnameValid(String uname);
}
