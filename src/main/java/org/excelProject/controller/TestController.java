package org.excelProject.controller;

import org.excelProject.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {

    @Autowired
    TestService service;
    @RequestMapping(value = "/test")
    public Object test(){
        System.out.println(service);
        System.out.println("controller");
        return "test";
    }
}
