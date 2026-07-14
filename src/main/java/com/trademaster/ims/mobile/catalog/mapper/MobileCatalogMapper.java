package com.trademaster.ims.mobile.catalog.mapper;

import com.trademaster.ims.mobile.catalog.dto.*;
import com.trademaster.ims.mobile.catalog.service.MobileImageUrlService;
import com.trademaster.ims.model.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class MobileCatalogMapper {
    private final MobileImageUrlService imageUrls;

    public MobileCatalogMapper(MobileImageUrlService imageUrls) { this.imageUrls = imageUrls; }

    public MobileCategorySummaryResponse category(Category category) {
        if (category == null) return null;
        return new MobileCategorySummaryResponse(category.getCategoryId(), category.getCategoryName(),
                category.getDescription(), category.getParentCategoryId(), category.getParentCategoryId() == null,
                Boolean.TRUE.equals(category.getStatus()));
    }

    public MobileCategoryDetailResponse categoryDetail(Category category, List<Category> children) {
        return new MobileCategoryDetailResponse(category.getCategoryId(), category.getCategoryName(),
                category.getDescription(), category.getParentCategoryId(), category.getParentCategoryId() == null,
                true, children.stream().map(this::category).toList());
    }

    public MobileProductSummaryResponse summary(Product product, Map<Long, Category> categories,
            Map<Long, Unit> units, Map<Long, Long> stock, HttpServletRequest request) {
        return new MobileProductSummaryResponse(product.getId(), product.getProductCode(), product.getSku(),
                product.getProductName(), product.getDescription(), product.getSellingPrice(), product.getTaxRate(),
                imageUrls.resolve(product.getImageUrl(), request), category(categories.get(product.getCategoryId())),
                unit(units.get(product.getBaseUnitId())), availability(product, stock.getOrDefault(product.getId(), 0L)));
    }

    public MobileProductDetailResponse detail(Product product, Category category, Unit unit,
            List<ProductVariation> variations, long available, HttpServletRequest request) {
        MobileStockAvailabilityResponse stock = availability(product, available);
        List<MobileProductVariationResponse> safeVariations = variations.stream()
                .map(v -> variation(v, product.getSellingPrice(), stock, request)).toList();
        return new MobileProductDetailResponse(product.getId(), product.getProductCode(), product.getSku(),
                product.getProductName(), product.getDescription(), product.getSellingPrice(), product.getTaxRate(),
                imageUrls.resolve(product.getImageUrl(), request), category(category), unit(unit), stock, safeVariations);
    }

    private MobileProductVariationResponse variation(ProductVariation variation, BigDecimal basePrice,
            MobileStockAvailabilityResponse stock, HttpServletRequest request) {
        BigDecimal additional = variation.getAdditionalPrice() == null ? BigDecimal.ZERO : variation.getAdditionalPrice();
        BigDecimal effective = (basePrice == null ? BigDecimal.ZERO : basePrice).add(additional);
        return new MobileProductVariationResponse(variation.getVariationId(), variation.getVariationName(),
                variation.getSku(), effective, additional, imageUrls.resolve(variation.getImageUrl(), request), stock);
    }

    private MobileUnitSummaryResponse unit(Unit unit) {
        return unit == null ? null : new MobileUnitSummaryResponse(unit.getUnitId(), unit.getUnitName(), unit.getUnitCode());
    }

    private MobileStockAvailabilityResponse availability(Product product, long available) {
        if (available <= 0) return new MobileStockAvailabilityResponse(false, "Out of Stock");
        int reorder = product.getReorderLevel() == null ? 0 : product.getReorderLevel();
        return new MobileStockAvailabilityResponse(true, available <= reorder ? "Low Stock" : "In Stock");
    }
}
