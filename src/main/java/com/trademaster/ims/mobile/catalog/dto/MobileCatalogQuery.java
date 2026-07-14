package com.trademaster.ims.mobile.catalog.dto;

import java.math.BigDecimal;

public record MobileCatalogQuery(int page, int size, String sort, String direction,
        Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, String search) {}
