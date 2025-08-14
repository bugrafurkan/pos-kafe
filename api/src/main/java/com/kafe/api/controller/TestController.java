package com.kafe.api.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

  @GetMapping
  public String test() {
    return "Test controller is working!";
  }
}
