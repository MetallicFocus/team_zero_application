package teamzero.javaweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "chat")
public class ChatController {

    @RequestMapping(value = "")
    public String initialize() {
        return "";
    }
}
