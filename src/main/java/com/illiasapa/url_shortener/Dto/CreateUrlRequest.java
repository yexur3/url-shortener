package com.illiasapa.url_shortener.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUrlRequest {
    @NotBlank
    private String originalUrl;
}
