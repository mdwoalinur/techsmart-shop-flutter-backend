package com.trademaster.ims.mobile.catalog.dto;

public record MobileCategorySummaryResponse(Long id, String name, String description,
        Long parentId, boolean root, boolean active) {}
