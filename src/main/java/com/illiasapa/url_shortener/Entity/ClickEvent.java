package com.illiasapa.url_shortener.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;


@Entity
@Data
public class ClickEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long shortUrlId;

    private Instant clickedAt;

    private String ipAddress;

    private String referrer;

    private String userAgent;
}
