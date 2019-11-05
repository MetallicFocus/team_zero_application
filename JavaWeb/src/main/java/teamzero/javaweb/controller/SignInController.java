package teamzero.javaweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import teamzero.javaweb.repository.UserRepository;

@Controller
public class SignInController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = {"/", "index"})
    public String index(){
        return "sign_in";
    }


    public String signIn(){
        return "sign_in";
    }
}
