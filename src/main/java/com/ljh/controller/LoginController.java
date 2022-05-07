package com.ljh.controller;

import com.ljh.entity.LoginUser;
import com.ljh.entity.User;
import com.ljh.service.UserService;
import com.ljh.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user){
        return userService.login(user);
    }

    @RequestMapping("/user/logout")
    public ResponseResult logout(){
        return userService.logout();
    }

}
