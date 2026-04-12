package com.sahil.chatapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Base64;

 @SpringBootApplication
public class Main {
    static void main(String [] args) {
         SpringApplication.run(Main.class);
//        Base64.Decoder decoder = Base64.getDecoder();
//        String token = new String(decoder.decode("eyJzdWIiOiJQcmljZSIsInVzZXJJZCI6Miwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTc3NTA0NzI3NSwiZXhwIjoxNzc1OTExMjc1fQ"));
//        System.out.println(token);
    }
}
