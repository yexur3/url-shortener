package com.illiasapa.url_shortener.Controller;

import com.illiasapa.url_shortener.Dto.ClickAnalyticResponse;
import com.illiasapa.url_shortener.Dto.CreateUrlRequest;
import com.illiasapa.url_shortener.Dto.CreateUrlRequestDto;
import com.illiasapa.url_shortener.Entity.ClickEvent;
import com.illiasapa.url_shortener.Service.ShortUrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class ShortUrlController {

    public final ShortUrlService shortUrlService;

    public ShortUrlController(ShortUrlService shortUrlService){
        this.shortUrlService = shortUrlService;
    }

    @PostMapping("/api/urls")
    public CreateUrlRequestDto createShortUrl(@RequestBody CreateUrlRequest request){
        return shortUrlService.createShortUrl(request.getOriginalUrl());
    }

    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode, HttpServletResponse response, HttpServletRequest request) throws IOException {
        String originalUrl = shortUrlService.getOriginalUrl(shortCode, request);
        response.sendRedirect(originalUrl);
    }

    @GetMapping("/api/urls/{shortCode}/analytics")
    public List<ClickAnalyticResponse> getAnalytics(@PathVariable String shortCode){
        return shortUrlService.getAnalytics(shortCode);
    }
}
