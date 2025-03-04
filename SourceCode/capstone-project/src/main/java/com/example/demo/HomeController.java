package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home(Model model) {
        //model.addAttribute("name", "Phi"); // Passing data to Thymeleaf
        return "home";
    }
   
    @GetMapping("/login")
    public String login(Model model) {
        //model.addAttribute("name", "Phi"); // Passing data to Thymeleaf
        return "login";
    }
    
    @GetMapping("/signup")
    public String signup(Model model) {
        //model.addAttribute("name", "Phi"); // Passing data to Thymeleaf
        return "signup";
    }
}
