//以下是用户模块的代码示例，包含了用户注册和登录功能的实现：
//演示了用户模块的基本功能，包括用户实体类、服务接口、服务实现类和控制器。您可以根据自己的需求进行扩展，例如添加密码重置、用户信息修改等功能。
package com.example.canteenapp.model;                                //用户实体类 

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data                                                               //Lombok 注解，自动生成 getter、setter、toString 等方法。
@Document(collection = "users")                                     //指定该实体类映射到 MongoDB 中名为 "users" 的集合。
public class User {

    @Id                                                             //标识该字段为主键。
    private String id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;                                          // 可选，头像 URL
    private String email;                                           // 可选，邮箱地址
}


package com.example.canteenapp.service;                             //用户服务接口:定义了用户注册 (register) 和登录 (login) 的接口方法。

import com.example.canteenapp.model.User;

public interface UserService {

    User register(User user);

    User login(String username, String password);
}


package com.example.canteenapp.service.impl;                       //用户服务实现类 

import com.example.canteenapp.exception.ResourceNotFoundException;
import com.example.canteenapp.model.User;
import com.example.canteenapp.repository.UserRepository;
import com.example.canteenapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service                                                          //标识该类为服务类，会被 Spring 容器管理。
public class UserServiceImpl implements UserService {

    @Autowired                                                    //自动注入 UserRepository 和 PasswordEncoder 实例。
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(User user) {
        // 对密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return user;
    }
}
//register 方法: 对用户密码进行加密后，调用 UserRepository 的 save 方法将用户数据保存到数据库。
//login 方法:根据用户名查询用户，如果用户不存在则抛出异常。
//           使用 PasswordEncoder 验证密码是否正确。


package com.example.canteenapp.controller;                              //用户控制器 

import com.example.canteenapp.model.User;
import com.example.canteenapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController                                                        //标识该类为 RESTful 风格的控制器。
@RequestMapping("/api/users")                                          //设置该控制器的根路径为 /api/users。
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")                                         //处理用户注册请求，请求路径为 /api/users/register，请求方式为 POST。
    public ResponseEntity<User> register(@RequestBody User user) {
        User registeredUser = userService.register(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")                                           //处理用户登录请求，请求路径为 /api/users/login，请求方式为 POST。
    public ResponseEntity<User> login(@RequestBody User user) {
        User loggedInUser = userService.login(user.getUsername(), user.getPassword());
        return new ResponseEntity<>(loggedInUser, HttpStatus.OK);
    }
}
