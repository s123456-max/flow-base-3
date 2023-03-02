package com.alexmisko.feign;

import com.alexmisko.vo.Result;
import com.alexmisko.vo.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("USER-INFO")
public interface UserInfoFeign {
    @GetMapping("/userInfo/{id}")
    public Result<UserInfo> getUserInfo(@PathVariable Long id);
}
