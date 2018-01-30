package cn.relyy.controller;

import cn.relyy.annotation.ReAutoWired;
import cn.relyy.annotation.ReController;
import cn.relyy.annotation.ReRequestMapping;
import cn.relyy.annotation.ReRequestParam;
import cn.relyy.service.IUserService;

/**
 * $DISCRIPTION
 *
 * @author cairuirui
 * @create 2018-01-30
 */
@ReController
@ReRequestMapping("user")
public class UserController {
    @ReAutoWired
    private IUserService userService;

    @ReRequestMapping("/checkuserInfo")
    public String checkuserInfo(@ReRequestParam("name") String name,
                                @ReRequestParam("pwd") String pwd){



        return String.valueOf(userService.checkUserInfo(name,pwd));
    }
}
