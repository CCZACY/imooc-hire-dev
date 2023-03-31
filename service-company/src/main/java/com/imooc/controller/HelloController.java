package com.imooc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("company")
public class HelloController {

    @GetMapping("test")
    public Object hello() {
        return "Hello CompanyService~~~";
    }
}
