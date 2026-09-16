package com.illiasapa.url_shortener.Service;

import com.illiasapa.url_shortener.Entity.ClickEvent;
import com.illiasapa.url_shortener.Entity.ShortUrlEntity;
import com.illiasapa.url_shortener.Repository.ClickEventRepository;
import com.illiasapa.url_shortener.Repository.ShortUrlRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ClickLoggingService {

    private final ShortUrlRepository shortUrlRepository;
    private final ClickEventRepository clickEventRepository;

    public ClickLoggingService(ShortUrlRepository shortUrlRepository, ClickEventRepository clickEventRepository){
        this.shortUrlRepository = shortUrlRepository;
        this.clickEventRepository = clickEventRepository;
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

}
