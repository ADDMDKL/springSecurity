package com.ljh.filter;

import com.ljh.entity.LoginUser;
import com.ljh.utils.JwtUtil;
import com.ljh.utils.RedisCache;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {


    //负责存入redis并取出redis
    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        //先获取token，解析token里的jwt
        String token = req.getHeader("token");
        //判断token里有没有值，如果没有就是还没有运行到登录接口，直接放行，让它先去登录接口认证
        if (!StringUtils.hasText(token)) {
            //因为这个类本质上是一个过滤器，所以并不返回，只会放行
            filterChain.doFilter(req, res);
            return;
        }
        //解析token里的jwt
        String userId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法");
        }
        //组装在redis里的key值
        String redisKey = "login:" + userId;
        LoginUser loginUser = redisCache.getCacheObject(redisKey);
        if(Objects.isNull(loginUser)){
            throw new RuntimeException("用户未登录");
        }
        /**将用户信息存入SecurityContextHolder
         * SecurityContextHolder存入用方法getContext()获取上下文，再用上下文的setAuthentication()方法
         * 存入用户信息，因为参数是一个接口，所以用它的实现类
         */
        //创建参数的实现类，这个实现类有三个构造器参数，第一个是用户信息，第二个是给与用户登录的权限，第三个是授权
        //授权
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser,null,loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //放行
        filterChain.doFilter(req, res);
    }
}
