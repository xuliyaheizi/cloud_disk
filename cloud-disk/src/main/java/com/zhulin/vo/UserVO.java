package com.zhulin.vo;

import lombok.Data;

/**
 * @Author:ZHULIN
 * @Date: 2022/6/9
 * @Description:
 */
@Data
public class UserVO {
    private Integer uid;
    private String uname;
    private String upwd;
    private String role;
    /** 界面上才有验证码 */
    private String imageCode;
}
