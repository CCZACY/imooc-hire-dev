package com.imooc.test;

import com.google.gson.Gson;
import com.imooc.pojo.Stu;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKey;

@SpringBootTest
public class JWTTest {

    // 定义JWT密钥 一般都是由开发者或者公司自定义的规范
    public static final String USER_KEY = "imooc_123456789_123456789_123456789";

    @Test
    public void createJWT() {

        // 1.对密钥进行BASE64加密编码
        String base64 = new BASE64Encoder().encode(USER_KEY.getBytes());

        // 2.对base64生成一个密钥对象
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());

        // 3.通过JWT生成一个token字符串
        Stu stu = new Stu(1001, "imooc中文网", 18);
        System.out.println("stu:"+ stu.toString());
        String stuJson = new Gson().toJson(stu);
        System.out.println("stuJson:" + stuJson);
        String myJWT = Jwts.builder()
                .setSubject(stuJson) // 设置用户自定义数据
                .signWith(secretKey) // 使用哪个密钥对象进行JWT的生成
                .compact(); // 压缩并且生成JWT
        System.out.println("myJWT:" + myJWT);

    }

    @Test
    public void checkJWT() {

        String jwt = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ7XCJpZFwiOjEwMDEsXCJuYW1lXCI6XCJpbW9vY-S4reaWh-e9kVwiLFwiYWdlXCI6MTh9In0._CdEVjs0qV7Lmcr-UtIyTomMZargChVeCmb4rXRAWJosvqhQeJsjZjaivNJnF-WU";

        // 1.对密钥进行BASE64加密编码
        String base64 = new BASE64Encoder().encode(USER_KEY.getBytes());

        // 2.对base64生成一个密钥对象
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());

        // 3.校验JWT 构造jwt解析器
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();
        // 开始解析jwt 并通过getBody()获取内容
        Jws<Claims> jws = jwtParser.parseClaimsJws(jwt);
        Claims body = jws.getBody();
        // 通过body 获取用户自定义数据subject
        String subject = body.getSubject();

        Stu stu = new Gson().fromJson(subject, Stu.class);

        System.out.println("stu:" + stu.toString());


    }

}
