package hu.f1calendar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Kezdőlap - F1 Versenynaptár");
        return "index";
    }
}

