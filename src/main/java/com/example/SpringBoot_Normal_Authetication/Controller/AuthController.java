package com.example.SpringBoot_Normal_Authetication.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.SpringBoot_Normal_Authetication.Service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String email) {
        try {
            return ResponseEntity.ok(authService.register(email));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        String token = authService.login(email, password);
        if (email == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        return ResponseEntity.ok("Login successful. Your token: " + token);
       
    }

    @GetMapping("/secure")
    public ResponseEntity<String> secureAccess(
            @RequestHeader("X-Auth-Email") String email,
            @RequestHeader("X-Auth-Token") String token) {
        if (!authService.isAuthenticated(email, token)) {
            return ResponseEntity.status(403).body("Access Denied: Invalid token.");
        }
        return ResponseEntity.ok("Welcome! You have access to secure content.");
    }
}
