package teamzero.javaweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import teamzero.javaweb.service.UserService;

@Controller
public class SignInController {

    @Autowired
    private UserService userService;

    @GetMapping(value = {"/", "index"})
    public String index(){
        return "sign_in";
    }

    @RequestMapping(value = "sign_in", method = RequestMethod.POST)
    public String signIn(@RequestParam("identifier") String identifier,
                         @RequestParam("pwd") String pwd){
        userService.signIn(identifier, pwd);
        return "sign_in";
    }
}
