package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from Spring Boot!");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", "success");
        return response;
    }

    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Received your data!");
        response.put("receivedData", data);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", "success");
        return response;
    }

    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "API连接测试成功！");
        response.put("backend", "Spring Boot 3.5.7");
        response.put("frontend", "Vue 3");
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }
}

