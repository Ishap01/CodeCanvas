package com.codecanvas.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "JWT Authentication Successful";
    }
}