package com.imooc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@MapperScan(basePackages = "com.imooc.mapper")
@EnableDiscoveryClient  //开启服务注册与发现
public class ServiceCompanyApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(ServiceCompanyApplication.class, args);
    }
}
