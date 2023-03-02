package com.alexmisko.service;

import com.alexmisko.pojo.User;
import com.alexmisko.vo.LoginUserInfo;

public interface JwtService{
    String generateToken(User user) throws Exception;
    String signIn(User user) throws Exception;
    String signUp(User user) throws Exception;

    LoginUserInfo test(String token) throws Exception;
}
