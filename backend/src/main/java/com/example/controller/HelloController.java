 package com.example.controller;

 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RestController;

 @RestController
 @RequestMapping("/api")
 public class HelloController {

     @GetMapping("/hello")
     public String hello() {
         System.out.println("....yey!!");
         return "Hello from Spring Boot!";
     }
 }