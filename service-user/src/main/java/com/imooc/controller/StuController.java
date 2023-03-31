package com.imooc.controller;


import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.Stu;
import com.imooc.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 风间影月
 * @since 2023-03-31
 */
@RestController
@RequestMapping("stu")
public class StuController {

    @Autowired
    private StuService service;

    @GetMapping("test001")
    public GraceJSONResult stu001() {
        Stu stu = new Stu();
        stu.setName("测试2");
        stu.setAge(19);
        service.save(stu);
        return GraceJSONResult.ok();
    }
    @GetMapping("test002")
    public String stu002() {
        Stu stu = new Stu();
        stu.setName("测试3");
        stu.setAge(20);
        service.save(stu);
        return "ok!";
    }

}

