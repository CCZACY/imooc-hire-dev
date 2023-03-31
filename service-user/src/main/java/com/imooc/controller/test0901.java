package com.imooc.controller;

import com.imooc.pojo.Stu;

public class test0901 {

    public static void main(String[] args) {
        Stu stu = new Stu();
        stu.setId(1);
        stu.setName("测试");
        stu.setAge(18);
        System.out.println(stu);
        System.out.println(stu.toString());
    }
}
