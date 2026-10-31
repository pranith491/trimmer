package com.bickynani.trimmer.controller;

import com.bickynani.trimmer.dto.AuthDtos.*;
import com.bickynani.trimmer.model.User;
import com.bickynani.trimmer.repository.UserRepository;
import com.bickynani.trimmer.security.ApiException;
import com.bickynani.trimmer.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users=users; this.encoder=encoder; this.jwt=jwt;
    }
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        String email=req.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) throw new ApiException(409, "An account with this email already exists.");
        User user=users.save(new User(req.fullName().trim(), email, encoder.encode(req.password())));
        return response(user);
    }
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        User user=users.findByEmailIgnoreCase(req.email().trim()).orElseThrow(() -> new ApiException(401, "Email or password is incorrect."));
        if (!encoder.matches(req.password(), user.getPasswordHash())) throw new ApiException(401, "Email or password is incorrect.");
        return response(user);
    }
    private AuthResponse response(User user) {
        return new AuthResponse(jwt.createToken(user.getId(), user.getEmail()), new UserView(user.getId(), user.getFullName(), user.getEmail()));
    }
}
