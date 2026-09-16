package com.illiasapa.url_shortener.Service;

import com.illiasapa.url_shortener.Dto.AnalyticsFull;
import com.illiasapa.url_shortener.Dto.ClickAnalyticResponse;
import com.illiasapa.url_shortener.Dto.CreateUrlRequestDto;
import com.illiasapa.url_shortener.Entity.ClickEvent;
import com.illiasapa.url_shortener.Entity.ShortUrlEntity;
import com.illiasapa.url_shortener.Repository.ClickEventRepository;
import com.illiasapa.url_shortener.Repository.ShortUrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ShortUrlService {

    @Value("${app.base-url}")
    private String baseUrl;

    private final ShortUrlRepository shortUrlRepository;
    private final ClickEventRepository clickEventRepository;
    private final Base62Service base62Service;
    private final RedisTemplate<String, String> redisTemplate;

    public ShortUrlService(
            ShortUrlRepository shortUrlRepository,
            Base62Service base62Service,
            ClickEventRepository clickEventRepository,
            RedisTemplate<String, String> redisTemplate
    ){
        this.shortUrlRepository = shortUrlRepository;
        this.base62Service = base62Service;
        this.clickEventRepository = clickEventRepository;
        this.redisTemplate = redisTemplate;
    }

    public CreateUrlRequestDto createShortUrl(String originalUrl){
        ShortUrlEntity entity = new ShortUrlEntity();
        CreateUrlRequestDto dto = new CreateUrlRequestDto();
        entity.setOriginalUrl(originalUrl);
        entity.setTimestamp(Instant.now());
        entity.setClickCount(0);

        entity = shortUrlRepository.save(entity);

        String code = base62Service.encode(entity.getId());

        entity.setShortCode(code);

        shortUrlRepository.save(entity);

        dto.setShortUrl(baseUrl + "/" + code);

        return dto;
    }

    public String getOriginalUrl(String shortCode, HttpServletRequest request){
        String cache = redisTemplate.opsForValue().get(shortCode);

        if(cache != null){
            logClickAsync(shortCode, request.getRemoteAddr(), request.getHeader("Referer"), request.getHeader("User-Agent"));
            return cache;
        }

        ShortUrlEntity entity = shortUrlRepository.findByShortCode(shortCode);
        if(entity == null){
            throw new NoSuchElementException("Short URL not found: " + shortCode);
        }

        redisTemplate.opsForValue().set(shortCode, entity.getOriginalUrl());

        logClickAsync(entity.getShortCode(), request.getRemoteAddr(), request.getHeader("Referer"), request.getHeader("User-Agent"));

        return entity.getOriginalUrl();
    }

    @Async
    public void logClickAsync(String shortCode, String ip, String referrer, String userAgent){
        ShortUrlEntity shortUrlEntity = shortUrlRepository.findByShortCode(shortCode);
        shortUrlEntity.setClickCount(shortUrlEntity.getClickCount() + 1);
        shortUrlRepository.save(shortUrlEntity);

        ClickEvent clickEvent = new ClickEvent();
        clickEvent.setShortUrlId(shortUrlEntity.getId());
        clickEvent.setClickedAt(Instant.now());
        clickEvent.setIpAddress(ip);
        clickEvent.setReferrer(referrer);
        clickEvent.setUserAgent(userAgent);
        clickEventRepository.save(clickEvent);
    }

    public AnalyticsFull getAnalytics(String shortCode){

        ShortUrlEntity entity = shortUrlRepository.findByShortCode(shortCode);

        if(entity == null){
            throw new NoSuchElementException("There no element with this shortCode");
        }

        List<ClickEvent> list = clickEventRepository.findByShortUrlId(entity.getId());
        AnalyticsFull analyticsFull = new AnalyticsFull();


        List<ClickAnalyticResponse> analyticResponses = new ArrayList<>(list.size());

        for(var item : list){
            ClickAnalyticResponse response = new ClickAnalyticResponse();

            response.setClickedAt(item.getClickedAt());
            response.setIpAddress(item.getIpAddress());
            response.setReferrer(item.getReferrer());
            response.setUserAgent(item.getUserAgent());

            analyticResponses.add(response);
        }

        analyticsFull.setList(analyticResponses);
        analyticsFull.setClickCount(entity.getClickCount());

        return analyticsFull;
    }
}
