package com.imooc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@MapperScan(basePackages = "com.imooc.mapper")
public class ServiceUserApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(ServiceUserApplication.class, args);
    }
}
