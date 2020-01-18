package com.teamzero.javaweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "test")
public class TestController {

    @GetMapping(value = "")
    public String showSignUp(){
        return "test";
    }
}
