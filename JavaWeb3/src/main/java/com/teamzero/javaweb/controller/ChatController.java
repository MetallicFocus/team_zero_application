package com.teamzero.javaweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "chat")
public class ChatController {

    @RequestMapping(value = "")
    public String initialize() {
        return "";
    }

    //test main
    @GetMapping(value = "/getmain")
    public String test() {
        return "/main";
    }

    @PostMapping(value = "/sign_in")
    public String init(String id) {
        System.out.println(id);
        return "redirect:/chat/getmain";
    }

    //test
    @PostMapping(value = "/main")
    public String chatPage(String id) {
        System.out.println(id);
        return "/main";
    }

    //test test
    @RequestMapping(value = "/test")
    public String test2() {
        return "/test";
    }
}
