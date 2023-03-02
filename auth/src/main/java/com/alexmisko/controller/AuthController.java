package com.alexmisko.controller;

import com.alexmisko.pojo.User;
import com.alexmisko.service.JwtService;
import com.alexmisko.vo.JwtToken;
import com.alexmisko.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    JwtService jwtService;
    @PostMapping("signIn")
    public JwtToken signIn(@RequestBody User user) throws Exception{
        log.info("sign in with param [{}] [{}]", user.getUsername(), user.getPassword());
        String token = jwtService.signIn(user);
        return new JwtToken(token);
    }

    @PostMapping("signUp")
    public JwtToken signUp(@RequestBody User user) throws Exception{
        log.info("sign up with param [{}] [{}]", user.getUsername(), user.getPassword());
        String token = jwtService.signUp(user);
        return new JwtToken(token);
    }

    @GetMapping("test")
    public LoginUserInfo test(String token) throws Exception{
        return jwtService.test(token);
    }
}
