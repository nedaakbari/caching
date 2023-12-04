package com.example.caching.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 150, nullable = false)
    private String username;
    @Lob
    @Column(length = 256, nullable = false)
    private byte[] password;
    @Lob
    @Column(length = 8, nullable = false)
    private byte[] salt;

    private boolean deleted;
}
