package com.illiasapa.url_shortener.Service;

import com.illiasapa.url_shortener.Dto.ClickAnalyticResponse;
import com.illiasapa.url_shortener.Dto.CreateUrlRequestDto;
import com.illiasapa.url_shortener.Entity.ClickEvent;
import com.illiasapa.url_shortener.Entity.ShortUrlEntity;
import com.illiasapa.url_shortener.Repository.ClickEventRepository;
import com.illiasapa.url_shortener.Repository.ShortUrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ShortUrlService {

    @Value("${app.base-url}")
    private String baseUrl;

    public final ShortUrlRepository shortUrlRepository;
    public final ClickEventRepository clickEventRepository;
    public final Base62Service base62Service;

    public ShortUrlService(
            ShortUrlRepository shortUrlRepository,
            Base62Service base62Service,
            ClickEventRepository clickEventRepository
    ){
        this.shortUrlRepository = shortUrlRepository;
        this.base62Service = base62Service;
        this.clickEventRepository = clickEventRepository;
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
        ShortUrlEntity entity = shortUrlRepository.findByShortCode(shortCode);

        if(entity == null){
            throw new NoSuchElementException("Short URL not found: " + shortCode);
        }

        ClickEvent clickEvent = new ClickEvent();

        entity.setClickCount(entity.getClickCount() + 1);
        shortUrlRepository.save(entity);


        clickEvent.setShortUrlId(entity.getId());
        clickEvent.setClickedAt(Instant.now());
        clickEvent.setIpAddress(request.getRemoteAddr());
        clickEvent.setReferrer(request.getHeader("Referer"));
        clickEvent.setUserAgent(request.getHeader("User-Agent"));

        clickEventRepository.save(clickEvent);

        return entity.getOriginalUrl();
    }

    public List<ClickAnalyticResponse> getAnalytics(String shortCode){

        ShortUrlEntity entity = shortUrlRepository.findByShortCode(shortCode);

        if(entity == null){
            throw new NoSuchElementException("There no element with this shortCode");
        }

        List<ClickEvent> list = clickEventRepository.findByShortUrlId(entity.getId());

        if(list.isEmpty()) {
            return null;
        }

        List<ClickAnalyticResponse> analyticResponses = new ArrayList<>(list.size());

        for(var item : list){
            ClickAnalyticResponse response = new ClickAnalyticResponse();

            response.setClickedAt(item.getClickedAt());
            response.setIpAddress(item.getIpAddress());
            response.setReferrer(item.getReferrer());
            response.setUserAgent(item.getUserAgent());

            analyticResponses.add(response);
        }

        return analyticResponses;
    }
}
