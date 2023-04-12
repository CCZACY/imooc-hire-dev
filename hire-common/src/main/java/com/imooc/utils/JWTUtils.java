package com.imooc.utils;

import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JWTUtils {

    @Autowired
    private JWTProperties jwtProperties;

    public static final String AT = "@";

    public String createJWTWithPrefix(String body, Long expireTimes, String prefix) {
        if (expireTimes == null) {
            GraceException.display(ResponseStatusEnum.SYSTEM_NO_EXPIRE_ERROR);
        }
        return prefix + AT + createJWT(body, expireTimes);
    }

    public String createJWTWithPrefix(String body, String prefix) {
        return prefix + AT + createJWT(body);
    }

    public String createJWT(String body) {
        return dealJWT(body, null);
    }

    public String createJWT(String body, Long expireTimes) {
        if (expireTimes == null) {
            GraceException.display(ResponseStatusEnum.SYSTEM_NO_EXPIRE_ERROR);
        }
        return dealJWT(body, expireTimes);
    }

    public String dealJWT(String body, Long expireTimes) {

        // 1.对密钥进行BASE64加密编码
        String base64 = new BASE64Encoder().encode(jwtProperties.getKey().getBytes());

        // 2.对base64生成一个密钥对象
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());

        // 3.通过JWT生成一个token字符串
        String myJWT = "";
        if (expireTimes == null) {
            myJWT = generatorJWT(body, secretKey);
        } else {
            myJWT = generatorJWTExpire(body, secretKey, expireTimes);
        }

//        Stu stu = new Stu(1001, "imooc中文网", 18);
//        System.out.println("stu:"+ stu.toString());
//        String stuJson = new Gson().toJson(stu);
//        System.out.println("stuJson:" + stuJson);
//        String myJWT = Jwts.builder()
//                .setSubject(stuJson) // 设置用户自定义数据
//                .signWith(secretKey) // 使用哪个密钥对象进行JWT的生成
//                .compact(); // 压缩并且生成JWT
        System.out.println("myJWT:" + myJWT);
        log.info("myJWT:" + myJWT);
        return myJWT;

    }

    public String generatorJWT(String body, SecretKey secretKey) {

        String myJWT = Jwts.builder()
                .setSubject(body) // 设置用户自定义数据
                .signWith(secretKey) // 使用哪个密钥对象进行JWT的生成
                .compact(); // 压缩并且生成JWT
        return myJWT;
    }

    public String generatorJWTExpire(String body, SecretKey secretKey, Long expireTimes) {

        // 定义过期时间
        Date expireDate = new Date(System.currentTimeMillis() + expireTimes);
        String myJWT = Jwts.builder()
                .setSubject(body) // 设置用户自定义数据
                .signWith(secretKey) // 使用哪个密钥对象进行JWT的生成
                .setExpiration(expireDate) // 定义JWTToken过期时间
                .compact(); // 压缩并且生成JWT
        return myJWT;
    }
}
