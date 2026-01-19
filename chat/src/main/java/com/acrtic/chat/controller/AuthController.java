package com.acrtic.chat.controller;

import com.acrtic.chat.model.UserEntity;
import com.acrtic.chat.repository.UserRepository;
import com.acrtic.chat.utility.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> body) {
        UserEntity user = new UserEntity();
        user.username = body.get("username");
        user.password = encoder.encode(body.get("password"));

        userRepository.save(user);
        return "User registered";
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> body) {
        var user = userRepository.findByUsername(body.get("username"))
                .orElseThrow();

        if (!encoder.matches(body.get("password"), user.password)) {
            throw new RuntimeException("Invalid password");
        }

        return JwtUtil.generateToken(user.username);
    }
}

