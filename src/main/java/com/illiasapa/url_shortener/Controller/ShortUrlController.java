package com.illiasapa.url_shortener.Controller;

import com.illiasapa.url_shortener.Dto.CreateUrlRequest;
import com.illiasapa.url_shortener.Service.ShortUrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class ShortUrlController {

    public final ShortUrlService shortUrlService;

    public ShortUrlController(ShortUrlService shortUrlService){
        this.shortUrlService = shortUrlService;
    }

    @PostMapping("/api/urls")
    public String createShortUrl(@RequestBody CreateUrlRequest request){
        return shortUrlService.createShortUrl(request.getOriginalUrl());
    }

    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        String originalUrl = shortUrlService.getOriginalUrl(shortCode);
        response.sendRedirect(originalUrl);
    }
}
