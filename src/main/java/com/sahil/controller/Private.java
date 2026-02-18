package com.sahil.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class Private {
    
    @GetMapping("/private")
    public String getMethodName() {
        return "Yeah, it works!!🙂🙂🙂🙂";
    }
    
}
