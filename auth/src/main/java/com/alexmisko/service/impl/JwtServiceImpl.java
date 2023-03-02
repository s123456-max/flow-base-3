package com.alexmisko.service.impl;

import com.alexmisko.constant.AuthConstant;
import com.alexmisko.constant.CommonConstant;
import com.alexmisko.dao.UserMapper;
import com.alexmisko.pojo.User;
import com.alexmisko.service.JwtService;
import com.alexmisko.util.TokenParseUtil;
import com.alexmisko.vo.LoginUserInfo;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class JwtServiceImpl implements JwtService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 生成token
     */
    @Override
    public String generateToken(User user) throws Exception{
        LoginUserInfo loginUserInfo = new LoginUserInfo(user.getId(), user.getUsername());
        ZonedDateTime zdt = LocalDate.now().plus(AuthConstant.DEFAULT_EXPIRE_DAY, ChronoUnit.DAYS).atStartOfDay(ZoneId.systemDefault());
        Date expireDate = Date.from(zdt.toInstant());
        return Jwts.builder().claim(CommonConstant.JWT_USER_INFO_KEY, JSON.toJSONString(loginUserInfo))
                .setId(UUID.randomUUID().toString())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.RS256, getPrivateKey())
                .compact();
    }

    /**
     * 登录
     */
    @Override
    public String signIn(User user) throws Exception {
        String username = user.getUsername();
        String password = user.getPassword();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username).eq("password", password);
        User user_query = userMapper.selectOne(queryWrapper);
        if(user_query == null){
            log.error("cannot find user [{}] [{}]", username, password);
            return null;
        }
        return generateToken(user);
    }

    /**
     * 注册
     */
    @Override
    public String signUp(User user) throws Exception {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User user_query = userMapper.selectOne(queryWrapper);
        if(user_query != null){
            log.error("username is registered: [{}]", user_query);
            return null;
        }
        userMapper.insert(user);
        log.info("register user success: [{}] [{}]", user.getUsername(), user.getId());
        return generateToken(user);
    }

    @Override
    public LoginUserInfo test(String token)throws Exception{
        return TokenParseUtil.getLoginUserInfo(token);
    }

    private PrivateKey getPrivateKey() throws Exception{
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                new BASE64Decoder().decodeBuffer(AuthConstant.PRIVATE_KEY));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(priPKCS8);
    }
}
