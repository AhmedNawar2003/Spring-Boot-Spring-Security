package com.springboot.Login_Register.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MY_APP_USER")
public class MyAppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;

    private  String username;

    private String email;

    private String password;

    private String verificationToken;

    private Boolean isVerified;

    @Column(name = "reset_token")
    private  String resetToken;
}
