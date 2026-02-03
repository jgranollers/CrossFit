package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CompetitionController {

    @GetMapping("/")
    public String index() {
        // Esto le dice a Spring que busque "index.html" en la carpeta templates
        return "index";
    }
}