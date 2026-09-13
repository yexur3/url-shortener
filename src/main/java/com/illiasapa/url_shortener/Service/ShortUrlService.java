package com.illiasapa.url_shortener.Service;

import com.illiasapa.url_shortener.Entity.ShortUrlEntity;
import com.illiasapa.url_shortener.Repository.ShortUrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.NoSuchElementException;

@Service
public class ShortUrlService {

    @Value("${app.base-url}")
    private String baseUrl;

    public final ShortUrlRepository shortUrlRepository;
    public final Base62Service base62Service;

    public ShortUrlService(ShortUrlRepository shortUrlRepository, Base62Service base62Service){
        this.shortUrlRepository = shortUrlRepository;
        this.base62Service = base62Service;
    }

    public String createShortUrl(String originalUrl){
        ShortUrlEntity entity = new ShortUrlEntity();
        entity.setOriginalUrl(originalUrl);
        entity.setTimestamp(Instant.now());
        entity.setClickCount(0);

        entity = shortUrlRepository.save(entity);

        String code = base62Service.encode(entity.getId());

        entity.setShortCode(code);

        shortUrlRepository.save(entity);

        return baseUrl + "/" + code;
    }

    public String getOriginalUrl(String shortCode){
        ShortUrlEntity entity = shortUrlRepository.findByShortCode(shortCode);

        if(entity == null){
            throw new NoSuchElementException("Short URL not found: " + shortCode);
        }

        entity.setClickCount(entity.getClickCount() + 1);
        shortUrlRepository.save(entity);

        return entity.getOriginalUrl();
    }
}
