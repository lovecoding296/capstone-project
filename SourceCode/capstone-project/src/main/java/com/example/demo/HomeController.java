package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }
   
    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }
    
    @GetMapping("/signup")
    public String signup(Model model) {
        return "signup";
    }
    
    @GetMapping("/tour-guides")
    public String tourGuides(Model model, @RequestParam(defaultValue = "all") String city) {
    	model.addAttribute("city", city);
        return "tour-guides";
    }
    
    
    @GetMapping("/become-a-guide")
    public String becomeAGuide(Model model) {
        return "become-a-guide";
    }
}
