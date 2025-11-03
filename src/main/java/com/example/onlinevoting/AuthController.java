package com.example.onlinevoting;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";  // looks for login.html
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";  // looks for register.html
    }
}
