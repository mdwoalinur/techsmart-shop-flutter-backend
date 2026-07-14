package com.trademaster.ims.mobile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trademaster.ims.mobile.catalog.dto.MobileProductDetailResponse;
import com.trademaster.ims.mobile.catalog.mapper.MobileCatalogMapper;
import com.trademaster.ims.mobile.catalog.service.MobileImageUrlService;
import com.trademaster.ims.model.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MobileCatalogMapperTest {
    @Test void mapsSafeDetailVariationPriceStockAndImages() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("https"); when(request.getServerName()).thenReturn("shop.example");
        when(request.getServerPort()).thenReturn(443);
        MobileCatalogMapper mapper = new MobileCatalogMapper(new MobileImageUrlService(""));
        Product product = new Product(); product.setId(1L); product.setProductName("Laptop");
        product.setSellingPrice(new BigDecimal("100.00")); product.setBuyingPrice(new BigDecimal("40.00"));
        product.setImageUrl("/uploads/products/laptop.png"); product.setReorderLevel(3);
        ProductVariation variation = new ProductVariation(); variation.setVariationId(2L); variation.setProductId(1L);
        variation.setVariationName("16 GB"); variation.setSku("L-16"); variation.setBuyingPrice(new BigDecimal("50"));
        variation.setAdditionalPrice(new BigDecimal("10.00")); variation.setStatus(true);
        MobileProductDetailResponse result = mapper.detail(product, null, null, List.of(variation), 2, request);
        assertEquals(new BigDecimal("110.00"), result.variations().get(0).effectivePrice());
        assertEquals("Low Stock", result.stock().stockLabel());
        assertEquals("https://shop.example:443/uploads/products/laptop.png", result.imageUrl());
        String json = new ObjectMapper().writeValueAsString(result);
        assertFalse(json.contains("buyingPrice")); assertFalse(json.contains("availableQuantity"));
    }

    @Test void preservesAbsoluteAndRejectsPrivateImagePaths() {
        MobileImageUrlService images = new MobileImageUrlService("");
        HttpServletRequest request = mock(HttpServletRequest.class);
        assertEquals("https://cdn.example/p.png", images.resolve("https://cdn.example/p.png", request));
        assertNull(images.resolve("/api/files/receipts/private.png", request));
    }
}
