package com.mafia.mafiademo.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ExampleController {

    @GetMapping
    public String hello() {
        return "Hello";
    }

}
