package com.trademaster.ims.mobile.catalog.dto;

import java.math.BigDecimal;
import java.util.List;

public record MobileProductDetailResponse(Long id, String productCode, String sku, String name,
        String description, BigDecimal sellingPrice, BigDecimal taxRate, String imageUrl,
        MobileCategorySummaryResponse category, MobileUnitSummaryResponse unit,
        MobileStockAvailabilityResponse stock, List<MobileProductVariationResponse> variations, java.math.BigDecimal averageRating, long reviewCount) {}
