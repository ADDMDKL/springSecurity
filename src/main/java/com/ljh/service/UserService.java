package com.ljh.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljh.entity.User;
import com.ljh.utils.ResponseResult;

public interface UserService {


    ResponseResult login(User user);

    ResponseResult logout();
}
