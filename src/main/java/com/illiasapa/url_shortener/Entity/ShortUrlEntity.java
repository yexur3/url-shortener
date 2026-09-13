package com.illiasapa.url_shortener.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
public class ShortUrlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String shortCode;

    @NotNull
    private String originalUrl;

    private Instant timestamp;

    private long clickCount;

}
