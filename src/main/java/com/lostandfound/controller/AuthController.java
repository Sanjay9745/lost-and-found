package com.lostandfound.controller;

import com.lostandfound.dto.LoginRequest;
import com.lostandfound.dto.LoginResponse;
import com.lostandfound.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = adminService.login(request);
        
        if (response.getToken() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String username) {
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = adminService.validateToken(token, username);
            
            if (isValid) {
                return ResponseEntity.ok("Token is valid");
            }
        }
        
        return ResponseEntity.status(401).body("Invalid token");
    }
}
