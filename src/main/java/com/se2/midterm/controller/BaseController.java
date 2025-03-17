package com.se2.midterm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BaseController {
    @GetMapping("/")
    public String getHomePage() {
        return "index";
    }
}
