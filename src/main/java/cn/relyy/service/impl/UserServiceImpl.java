package cn.relyy.service.impl;

import cn.relyy.annotation.ReService;
import cn.relyy.service.IUserService;

/**
 * $DISCRIPTION
 *
 * @author cairuirui
 * @create 2018-01-30
 */
@ReService("userService")
public class UserServiceImpl implements IUserService {

    @Override
    public boolean checkUserInfo(String name, String pwd) {
        return false;
    }
}
