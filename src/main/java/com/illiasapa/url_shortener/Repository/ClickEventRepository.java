package com.illiasapa.url_shortener.Repository;

import com.illiasapa.url_shortener.Entity.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    List<ClickEvent> findByShortUrlId(long shortUrlId);
}
