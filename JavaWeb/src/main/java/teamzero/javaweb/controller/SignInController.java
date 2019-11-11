package teamzero.javaweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "sign_in")
public class SignInController {

    @GetMapping(value = "")
    public String showSignIn() {
        return "sign_in_vue";
    }

    //Todo: id verification
    @RequestMapping(value = "processing", method = RequestMethod.POST)
    public String signIn(HttpSession session,
                         @RequestParam("name") String name,
                         @RequestParam("pwd") String password,
                         Model model){
        if (true) { //connect with server api for sign in
            System.out.println(name + " " + password);
            model.addAttribute("name", name);
            model.addAttribute("pwd", password);
            return "/test";
        } else {
            return "redirect:/sign_in";
        }

    }
}
