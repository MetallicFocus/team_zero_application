package com.teamzero.javaweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "sign_in")
public class SignInController {

    @GetMapping(value = "")
    public String showSignIn() {
        return "sign_in";
    }
}
