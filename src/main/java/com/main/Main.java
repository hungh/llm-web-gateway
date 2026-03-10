package com.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@SpringBootApplication
@ComponentScan(basePackages = "com")
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    @RequestMapping("/")
    public String home() {
        return "Service is running: OK";
    }
    
    @RequestMapping("/prompt")
    public ResponseEntity<String> prompt(@RequestParam("prompt") String prompt) {
        logger.info("Received prompt: {}", prompt);
        return ResponseEntity.ok("The prompt has been received and is being processed!");
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    
}
