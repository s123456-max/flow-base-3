package com.alexmisko.controller;

import com.alexmisko.service.UserInfoService;
import com.alexmisko.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserInfoController {
    @Autowired
    UserInfoService userInfoService;

    /**
     * 查询某个用户信息
     */
    @GetMapping("/userInfo/{id}")
    public Result getUserInfo(@PathVariable Long id){
        return Result.success(userInfoService.getById(id));
    }
}
