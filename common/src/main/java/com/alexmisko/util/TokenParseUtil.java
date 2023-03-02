package com.alexmisko.util;

import com.alexmisko.constant.CommonConstant;
import com.alexmisko.vo.LoginUserInfo;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import sun.misc.BASE64Decoder;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;

public class TokenParseUtil {
    public static LoginUserInfo getLoginUserInfo(String token) throws Exception{
        if(token == null){
            return null;
        }
        Jws<Claims> claimJws = parseToken(token, getPublicKey());
        Claims body = claimJws.getBody();
        if(body.getExpiration().before(Calendar.getInstance().getTime())){
            return null;
        }
        return JSON.parseObject(body.get(CommonConstant.JWT_USER_INFO_KEY).toString(), LoginUserInfo.class);
    }

    private static Jws<Claims> parseToken(String token, PublicKey publicKey){
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
    }

    private static PublicKey getPublicKey() throws Exception{
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
                new BASE64Decoder().decodeBuffer(CommonConstant.PUBLIC_KEY));
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }
}
