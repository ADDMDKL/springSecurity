package com.ljh;

import com.ljh.entity.User;
import com.ljh.mapper.MenuMapper;
import com.ljh.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootTest
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Test
    public void testMenuMapper(){
        List<String> list = menuMapper.selectPermsByUserId((long)1);
        System.out.println(list);
    }

    @Test
    public void TestBCryptPasswordEncoder(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("test");
        System.out.println(encode);
        boolean b = passwordEncoder.matches("123", "$2a$10$gQJ.67Vpu3Pfc9.6UfVsQevdG3yzpxS1pDvBT3KZ3HhO/hIQ37Wtq");
        System.out.println(b);
    }

    @Test
    public void testUserMapper(){
        List<User> users = userMapper.selectList(null);
        System.out.println(users);
    }

}
