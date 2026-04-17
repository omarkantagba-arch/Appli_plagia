package com.plagicheck.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Backend is running");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-test")
    public ResponseEntity<Map<String, String>> testUpload(
            @RequestParam(value = "file", required = false) String file,
            @RequestParam(value = "themeId", required = false) String themeId) {
        Map<String, String> response = new HashMap<>();
        response.put("file", file != null ? file : "null");
        response.put("themeId", themeId != null ? themeId : "null");
        return ResponseEntity.ok(response);
    }
}
