package com.github.spring_security_form_auth.controller;

import com.github.spring_security_form_auth.entity.UserAuthEntity;
import com.github.spring_security_form_auth.service.UserAuthEntityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserAuthEntityService userAuthEntityService;

    public UserAuthController(PasswordEncoder passwordEncoder, UserAuthEntityService userAuthEntityService) {
        this.passwordEncoder = passwordEncoder;
        this.userAuthEntityService = userAuthEntityService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserAuthEntity userAuthEntity) {
        userAuthEntity.setPassword(passwordEncoder.encode(userAuthEntity.getPassword()));
        userAuthEntityService.save(userAuthEntity);
        return ResponseEntity.ok("User saved successfully!");
    }

    // Default Page
    @GetMapping("/")
    public ResponseEntity<String> defaultController() {
        return ResponseEntity.ok("Welcome");
    }
}
