package com.trademaster.ims.mobile.catalog.controller;

import com.trademaster.ims.mobile.catalog.dto.*;
import com.trademaster.ims.mobile.catalog.service.MobileCatalogService;
import com.trademaster.ims.mobile.common.response.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/mobile/v1")
public class MobileCatalogController {
    private final MobileCatalogService catalog;

    public MobileCatalogController(MobileCatalogService catalog) { this.catalog = catalog; }

    @GetMapping("/categories")
    public MobileApiResponse<List<MobileCategorySummaryResponse>> categories(
            @RequestParam(required = false) Long parentId,
            @RequestParam(defaultValue = "false") boolean rootOnly) {
        return MobileApiResponse.success(catalog.categories(parentId, rootOnly));
    }

    @GetMapping("/categories/{id}")
    public MobileApiResponse<MobileCategoryDetailResponse> category(@PathVariable long id) {
        return MobileApiResponse.success(catalog.category(id));
    }

    @GetMapping("/categories/{id}/products")
    public MobileApiResponse<MobilePageResponse<MobileProductSummaryResponse>> categoryProducts(
            @PathVariable long id, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock, HttpServletRequest request) {
        return page(new MobileCatalogQuery(page, size, sort, direction, id, minPrice, maxPrice, inStock, null), request);
    }

    @GetMapping("/products")
    public MobileApiResponse<MobilePageResponse<MobileProductSummaryResponse>> products(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort, @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) Long categoryId, @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock, HttpServletRequest request) {
        return page(new MobileCatalogQuery(page, size, sort, direction, categoryId, minPrice, maxPrice, inStock, null), request);
    }

    @GetMapping("/products/search")
    public MobileApiResponse<MobilePageResponse<MobileProductSummaryResponse>> search(
            @RequestParam String q, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) Long categoryId, @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock, HttpServletRequest request) {
        return page(new MobileCatalogQuery(page, size, sort, direction, categoryId, minPrice, maxPrice, inStock, q), request);
    }

    @GetMapping("/products/{id}")
    public MobileApiResponse<MobileProductDetailResponse> product(@PathVariable long id, HttpServletRequest request) {
        return MobileApiResponse.success(catalog.product(id, request));
    }

    private MobileApiResponse<MobilePageResponse<MobileProductSummaryResponse>> page(
            MobileCatalogQuery query, HttpServletRequest request) {
        return MobileApiResponse.success(MobilePageResponse.from(catalog.products(query, request)));
    }
}
