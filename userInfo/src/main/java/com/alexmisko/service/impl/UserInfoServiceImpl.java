package com.alexmisko.service.impl;

import com.alexmisko.dao.UserInfoMapper;
import com.alexmisko.pojo.UserInfo;
import com.alexmisko.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

}
