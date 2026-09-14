package com.illiasapa.url_shortener.Dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ClickAnalyticResponse {
    private Instant clickedAt;

    private String ipAddress;

    private String referrer;

    private String userAgent;
}
