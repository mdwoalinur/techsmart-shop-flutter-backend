package com.trademaster.ims.mobile.catalog.service;

import com.trademaster.ims.mobile.catalog.dto.*;
import com.trademaster.ims.mobile.catalog.mapper.MobileCatalogMapper;
import com.trademaster.ims.mobile.common.exception.MobileResourceNotFoundException;
import com.trademaster.ims.mobile.common.exception.MobileValidationException;
import com.trademaster.ims.mobile.common.response.MobileFieldError;
import com.trademaster.ims.model.*;
import com.trademaster.ims.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MobileCatalogService {
    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_SEARCH_LENGTH = 100;
    private static final Map<String, String> SORT_FIELDS = Map.of(
            "name", "productName", "price", "sellingPrice", "createdAt", "createdAt", "updatedAt", "updatedAt");

    private final ProductRepository products;
    private final CategoryRepository categories;
    private final ProductVariationRepository variations;
    private final InventoryRepository inventory;
    private final UnitRepository units;
    private final MobileCatalogMapper mapper;

    public MobileCatalogService(ProductRepository products, CategoryRepository categories,
            ProductVariationRepository variations, InventoryRepository inventory,
            UnitRepository units, MobileCatalogMapper mapper) {
        this.products = products; this.categories = categories; this.variations = variations;
        this.inventory = inventory; this.units = units; this.mapper = mapper;
    }

    public List<MobileCategorySummaryResponse> categories(Long parentId, boolean rootOnly) {
        List<Category> result = parentId != null
                ? categories.findByParentCategoryIdAndStatusTrueOrderByCategoryNameAsc(parentId)
                : rootOnly ? categories.findByParentCategoryIdIsNullAndStatusTrueOrderByCategoryNameAsc()
                : categories.findByStatusTrueOrderByCategoryNameAsc();
        return result.stream().map(mapper::category).toList();
    }

    public MobileCategoryDetailResponse category(long id) {
        Category category = activeCategory(id);
        return mapper.categoryDetail(category,
                categories.findByParentCategoryIdAndStatusTrueOrderByCategoryNameAsc(id));
    }

    public Page<MobileProductSummaryResponse> products(MobileCatalogQuery query, HttpServletRequest request) {
        MobileCatalogQuery validated = validate(query);
        if (validated.categoryId() != null) activeCategory(validated.categoryId());
        Pageable pageable = PageRequest.of(validated.page(), validated.size(), sort(validated));
        Page<Product> page = products.findAll(MobileProductSpecifications.matching(validated), pageable);
        return mapPage(page, request);
    }

    public MobileProductDetailResponse product(long id, HttpServletRequest request) {
        Product product = products.findByIdAndStatus(id, Product.ProductStatus.ACTIVE)
                .orElseThrow(() -> new MobileResourceNotFoundException("Product is unavailable."));
        Category category = product.getCategoryId() == null ? null : categories.findByCategoryIdAndStatusTrue(product.getCategoryId()).orElse(null);
        Unit unit = product.getBaseUnitId() == null ? null : units.findById(product.getBaseUnitId()).filter(u -> Boolean.TRUE.equals(u.getStatus())).orElse(null);
        long stock = stockTotals(List.of(id)).getOrDefault(id, 0L);
        return mapper.detail(product, category, unit,
                variations.findByProductIdAndStatus(id, true), stock, request);
    }

    private Page<MobileProductSummaryResponse> mapPage(Page<Product> page, HttpServletRequest request) {
        List<Long> productIds = page.getContent().stream().map(Product::getId).toList();
        Map<Long, Category> categoryMap = byId(categories.findAllById(page.getContent().stream()
                .map(Product::getCategoryId).filter(Objects::nonNull).distinct().toList()), Category::getCategoryId);
        Map<Long, Unit> unitMap = byId(units.findAllById(page.getContent().stream()
                .map(Product::getBaseUnitId).filter(Objects::nonNull).distinct().toList()), Unit::getUnitId);
        Map<Long, Long> stocks = stockTotals(productIds);
        List<MobileProductSummaryResponse> content = page.getContent().stream()
                .map(p -> mapper.summary(p, categoryMap, unitMap, stocks, request)).toList();
        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }

    private MobileCatalogQuery validate(MobileCatalogQuery query) {
        List<MobileFieldError> errors = new ArrayList<>();
        if (query.page() < 0) errors.add(new MobileFieldError("page", "Page must be zero or greater."));
        if (query.size() < 1 || query.size() > MAX_PAGE_SIZE) errors.add(new MobileFieldError("size", "Size must be between 1 and 100."));
        if (negative(query.minPrice())) errors.add(new MobileFieldError("minPrice", "Minimum price must not be negative."));
        if (negative(query.maxPrice())) errors.add(new MobileFieldError("maxPrice", "Maximum price must not be negative."));
        if (query.minPrice() != null && query.maxPrice() != null && query.minPrice().compareTo(query.maxPrice()) > 0)
            errors.add(new MobileFieldError("minPrice", "Minimum price must not exceed maximum price."));
        if (!SORT_FIELDS.containsKey(query.sort())) errors.add(new MobileFieldError("sort", "Unsupported sort field."));
        if (!"asc".equalsIgnoreCase(query.direction()) && !"desc".equalsIgnoreCase(query.direction()))
            errors.add(new MobileFieldError("direction", "Direction must be asc or desc."));
        String search = query.search() == null ? null : query.search().trim();
        if (query.search() != null && search.isEmpty()) errors.add(new MobileFieldError("q", "Search text must not be blank."));
        if (search != null && search.length() > MAX_SEARCH_LENGTH) errors.add(new MobileFieldError("q", "Search text must not exceed 100 characters."));
        if (!errors.isEmpty()) throw new MobileValidationException(errors);
        return new MobileCatalogQuery(query.page(), query.size(), query.sort(), query.direction(),
                query.categoryId(), query.minPrice(), query.maxPrice(), query.inStock(), search);
    }

    private Sort sort(MobileCatalogQuery query) {
        Sort.Direction direction = "desc".equalsIgnoreCase(query.direction()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, SORT_FIELDS.get(query.sort()));
    }

    private Category activeCategory(long id) {
        return categories.findByCategoryIdAndStatusTrue(id)
                .orElseThrow(() -> new MobileResourceNotFoundException("Category is unavailable."));
    }

    private Map<Long, Long> stockTotals(List<Long> ids) {
        if (ids.isEmpty()) return Map.of();
        return inventory.aggregateAvailableByProductIds(ids).stream().collect(Collectors.toMap(
                InventoryRepository.ProductStockTotal::getProductId,
                row -> Math.max(0L, Optional.ofNullable(row.getAvailableQuantity()).orElse(0L))));
    }

    private boolean negative(BigDecimal value) { return value != null && value.signum() < 0; }
    private <T> Map<Long, T> byId(Iterable<T> values, Function<T, Long> id) {
        Map<Long, T> result = new HashMap<>(); values.forEach(v -> result.put(id.apply(v), v)); return result;
    }
}
