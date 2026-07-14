package com.trademaster.ims.mobile.catalog.dto;

import java.math.BigDecimal;

public record MobileProductVariationResponse(Long id, String name, String sku,
        BigDecimal effectivePrice, BigDecimal additionalPrice, String imageUrl,
        MobileStockAvailabilityResponse stock) {}
