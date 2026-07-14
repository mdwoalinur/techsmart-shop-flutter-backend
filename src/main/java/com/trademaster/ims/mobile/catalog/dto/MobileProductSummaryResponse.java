package com.trademaster.ims.mobile.catalog.dto;

import java.math.BigDecimal;

public record MobileProductSummaryResponse(Long id, String productCode, String sku, String name,
        String description, BigDecimal sellingPrice, BigDecimal taxRate, String imageUrl,
        MobileCategorySummaryResponse category, MobileUnitSummaryResponse unit,
        MobileStockAvailabilityResponse stock) {}
