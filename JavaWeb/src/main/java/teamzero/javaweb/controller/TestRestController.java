package teamzero.javaweb.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
public class TestRestController {

    @PostMapping(value = "")
    public String index(){
        return "Mapping successfully!";
    }

}
