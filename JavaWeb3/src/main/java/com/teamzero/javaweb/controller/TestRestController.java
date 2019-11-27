package com.teamzero.javaweb.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/test")
public class TestRestController {

    @PostMapping(value = "/post")
    public String testPost() {
        return "Post successfully!";
    }

    @GetMapping(value = "/get")
    public String testGet() {
        return "Get successfully!";
    }
}
