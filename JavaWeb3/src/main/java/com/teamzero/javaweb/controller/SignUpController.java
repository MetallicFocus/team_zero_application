package com.teamzero.javaweb.controller;

import com.teamzero.javaweb.vo.UserInfoVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "sign_up")
public class SignUpController {

    @GetMapping(value = "")
    public String showSignUp(){
        return "sign_up";
    }

    // Todo: register in server
    @RequestMapping(value = "processing", method = RequestMethod.POST)
    public String signUp(HttpSession session,
                         @RequestBody UserInfoVo userInfoVo) {

        return "/sign_in";
    }
}
