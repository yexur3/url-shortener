package com.illiasapa.url_shortener.Dto;

import lombok.Data;

import java.util.List;

@Data
public class AnalyticsFull {
    private List<ClickAnalyticResponse> list;
    private long clickCount;
}
