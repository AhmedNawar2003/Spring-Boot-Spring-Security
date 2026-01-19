package com.springboot.Login_Register.controller;

import com.springboot.Login_Register.model.MyAppUser;
import com.springboot.Login_Register.model.MyAppUserRepository;
import com.springboot.Login_Register.service.EmailService;
import com.springboot.Login_Register.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/req")
public class RegistrationController {

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<String> createUser(@RequestBody MyAppUser user) {

        MyAppUser existingUser = myAppUserRepository.findByEmail(user.getEmail());

        // Case 1: User already exists & verified
        if (existingUser != null && Boolean.TRUE.equals(existingUser.getIsVerified())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("User already exists and is verified.");
        }

        // Case 2: User exists but NOT verified → resend verification
        if (existingUser != null) {
            String verificationToken =
                    JwtTokenUtil.generateToken(existingUser.getEmail());

            existingUser.setVerificationToken(verificationToken);
            myAppUserRepository.save(existingUser);

            emailService.sendVerificationEmail(
                    existingUser.getEmail(),
                    verificationToken
            );

            return ResponseEntity.ok(
                    "Verification email resent. Please check your inbox."
            );
        }

        // Case 3: New user registration
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsVerified(false);

        String verificationToken =
                JwtTokenUtil.generateToken(user.getEmail());
        user.setVerificationToken(verificationToken);

        myAppUserRepository.save(user);

        emailService.sendVerificationEmail(
                user.getEmail(),
                verificationToken
        );

        return ResponseEntity.ok(
                "Registration successful! Please verify your email."
        );
    }
}
