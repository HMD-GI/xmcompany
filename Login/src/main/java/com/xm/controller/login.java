package com.xm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value ="/xm" )
public class login {
    @GetMapping(value = "/login")
    public String login(){
        return "login";
    }
}
