package com.ljh.service.impl;

import com.ljh.entity.LoginUser;
import com.ljh.entity.User;
import com.ljh.service.UserService;
import com.ljh.utils.JwtUtil;
import com.ljh.utils.RedisCache;
import com.ljh.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    //在SecurityConfig中加入的，这里注入的
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult login(User user) {
        /**因为需要Authentication去封装User用户，然后去认证，但是获得Authentication又需要
         * authenticationManager authenticate()获取到，这个方法又有一个参数Authentication类型
         * 由于这是个接口，不能实例化，所以我们去创建一个它的实现类的对象，所以这里有三个步骤
         * 1.Spring容器里是没有authenticationManager这个东西的，所以在配置类SecurityConfig中将
         * authenticationManager放入了Spring容器中，并且在实现类中注入
         * 2.有了authenticationManager但是authenticate()方法还没有参数，所以需要它的实现类
         * UsernamePasswordAuthenticationToken，创建对象，并且构造器带有两个参数，这里就可以封装用户的密码与账户
         */
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        /**AuthenticationManager authenticate进行用户认证3.获取Authentication对象
         * 从这里开始，会有一段过滤器，经过这段过滤器认证后返回的依然是这个对象authenticate，
         * 只不过，这个对象如果封装的用户密码账户是正确的
         * 会补全这个对象封装的用户信息，如果密码账户是错误的就会返回一个空对象
         */
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //如果用户认证没通过，给出对应的提示
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户或密码错误");
        }
        /**如果认证通过了，使用userid生成一个JWT JWT作为ResponseResult返回
         * 因为能到这里的话authenticate就不会是空的，并且里面的用户信息都会查询数据库补全，
         * 所以会在这里获取用户的userId
         */
        //先获取返回的数据，自定义的UserDetails的实现类，然后在实现类里获取userId
        LoginUser loginUser = (LoginUser)authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        //使用JWT的工具类，将userId封装成一个JWT
        String jwt = JwtUtil.createJWT(userId);
        //存入redis里
        redisCache.setCacheObject("login:"+userId,loginUser);
        //把完整的用户信息存入Radis  token作为key，以用户信息封装的JWT为值，放入map集合中
        Map<String,String> map = new HashMap<>();
        map.put("token",jwt);
        //并且返回一个ResponseResult结果类:状态码，状态信息，数据
        return new ResponseResult(200,"登陆成功",map);
    }

    @Override
    public ResponseResult logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long id = loginUser.getUser().getId();
        redisCache.deleteObject("login:"+id);
        return new ResponseResult(200,"注销成功");
    }
}
