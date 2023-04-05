package com.imooc.controller;

import com.imooc.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class HelloController {

    @Autowired
    private SMSUtils smsUtils;

    @GetMapping("test")
    public Object hello() {
        return "Hello UserService~~~";
    }

    @GetMapping("sms")
    public String sms() throws Exception {
        smsUtils.sendSMS("15231244867", "123456");
        return "SMS Sending~~~";
    }
}
