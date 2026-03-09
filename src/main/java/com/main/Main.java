package com.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.ComponentScan;

@RestController
@SpringBootApplication
@ComponentScan(basePackages = "com")
public class Main {
    @RequestMapping("/")
    public String home() {
        return "Service is running: OK";
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    
}
