package com.bickynani.trimmer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100) private String fullName;
    @Column(nullable = false, unique = true, length = 190) private String email;
    @Column(nullable = false) private String passwordHash;
    @Column(nullable = false) private LocalDateTime createdAt = LocalDateTime.now();

    public User() {}
    public User(String fullName, String email, String passwordHash) {
        this.fullName = fullName; this.email = email; this.passwordHash = passwordHash;
    }
    public Long getId(){return id;}
    public String getFullName(){return fullName;}
    public String getEmail(){return email;}
    public String getPasswordHash(){return passwordHash;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}
