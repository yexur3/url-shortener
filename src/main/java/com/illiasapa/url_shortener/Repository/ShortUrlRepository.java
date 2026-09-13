package com.illiasapa.url_shortener.Repository;

import com.illiasapa.url_shortener.Entity.ShortUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrlEntity, Long> {
        ShortUrlEntity findByShortCode(String shortCode);
}
