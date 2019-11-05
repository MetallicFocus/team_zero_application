package teamzero.javaweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
@Controller
public class SignUpController {

    @GetMapping(value = "show_sign_up")
    public String showSignUp(){
        return "sign_up";
    }

    @RequestMapping(value = "sign_up", method = RequestMethod.POST)
    public String signUp(){
        /* Todo: sign up function */
        return "";
    }
}
