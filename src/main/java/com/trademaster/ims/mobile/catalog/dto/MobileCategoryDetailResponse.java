package com.trademaster.ims.mobile.catalog.dto;

import java.util.List;

public record MobileCategoryDetailResponse(Long id, String name, String description,
        Long parentId, boolean root, boolean active, List<MobileCategorySummaryResponse> children) {}
