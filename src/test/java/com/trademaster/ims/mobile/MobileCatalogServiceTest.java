package com.trademaster.ims.mobile;

import com.trademaster.ims.mobile.catalog.dto.*;
import com.trademaster.ims.mobile.catalog.mapper.MobileCatalogMapper;
import com.trademaster.ims.mobile.catalog.service.*;
import com.trademaster.ims.mobile.common.exception.*;
import com.trademaster.ims.model.*;
import com.trademaster.ims.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MobileCatalogServiceTest {
    @Mock ProductRepository products;
    @Mock CategoryRepository categories;
    @Mock ProductVariationRepository variations;
    @Mock InventoryRepository inventory;
    @Mock UnitRepository units;
    @Mock HttpServletRequest request;
    MobileCatalogService service;

    @BeforeEach void setUp() {
        service = new MobileCatalogService(products, categories, variations, inventory, units,
                new MobileCatalogMapper(new MobileImageUrlService("")));
    }

    @Test void rejectsInvalidPaginationPriceSortAndDirection() {
        assertThrows(MobileValidationException.class, () -> service.products(
                query(-1, 101, "unsafe", "sideways", new BigDecimal("20"), new BigDecimal("10"), null), request));
    }

    @Test void rejectsBlankAndLongSearch() {
        assertThrows(MobileValidationException.class, () -> service.products(
                query(0, 20, "name", "asc", null, null, "   "), request));
        assertThrows(MobileValidationException.class, () -> service.products(
                query(0, 20, "name", "asc", null, null, "x".repeat(101)), request));
    }

    @Test void activeCategoryRequiredForCategoryFilter() {
        when(categories.findByCategoryIdAndStatusTrue(9L)).thenReturn(Optional.empty());
        MobileCatalogQuery query = new MobileCatalogQuery(0, 20, "name", "asc", 9L, null, null, null, null);
        assertThrows(MobileResourceNotFoundException.class, () -> service.products(query, request));
    }

    @Test void categoryListUsesOnlyActiveRepositoryResults() {
        Category category = new Category("Laptops", null, "Portable computers");
        category.setCategoryId(1L);
        when(categories.findByParentCategoryIdIsNullAndStatusTrueOrderByCategoryNameAsc())
                .thenReturn(List.of(category));
        List<MobileCategorySummaryResponse> result = service.categories(null, true);
        assertEquals(1, result.size());
        assertTrue(result.get(0).active());
        assertTrue(result.get(0).root());
    }

    @Test void categoryDetailReturnsActiveChildrenAndNotFoundIsSafe() {
        Category parent = new Category("Laptops", null, "Portable computers"); parent.setCategoryId(1L);
        Category child = new Category("Business", 1L, null); child.setCategoryId(2L);
        when(categories.findByCategoryIdAndStatusTrue(1L)).thenReturn(Optional.of(parent));
        when(categories.findByParentCategoryIdAndStatusTrueOrderByCategoryNameAsc(1L)).thenReturn(List.of(child));
        assertEquals(1, service.category(1L).children().size());
        when(categories.findByCategoryIdAndStatusTrue(99L)).thenReturn(Optional.empty());
        assertThrows(MobileResourceNotFoundException.class, () -> service.category(99L));
    }

    @Test void listUsesDatabaseSpecificationPaginationAndAllowedSort() {
        Product product = product();
        when(products.findAll(ArgumentMatchers.<Specification<Product>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(product), PageRequest.of(0, 20), 1));
        when(categories.findAllById(any())).thenReturn(List.of());
        when(units.findAllById(any())).thenReturn(List.of());
        when(inventory.aggregateAvailableByProductIds(List.of(1L))).thenReturn(List.of());
        Page<MobileProductSummaryResponse> result = service.products(
                query(0, 20, "price", "desc", null, null, null), request);
        assertEquals(1, result.getTotalElements());
        assertFalse(result.getContent().get(0).stock().inStock());
        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(products).findAll(ArgumentMatchers.<Specification<Product>>any(), pageable.capture());
        assertEquals(Sort.Direction.DESC, pageable.getValue().getSort().getOrderFor("sellingPrice").getDirection());
    }

    @Test void detailHidesInactiveAndMissingProducts() {
        when(products.findByIdAndStatus(2L, Product.ProductStatus.ACTIVE)).thenReturn(Optional.empty());
        assertThrows(MobileResourceNotFoundException.class, () -> service.product(2L, request));
    }

    private MobileCatalogQuery query(int page, int size, String sort, String direction,
            BigDecimal min, BigDecimal max, String search) {
        return new MobileCatalogQuery(page, size, sort, direction, null, min, max, null, search);
    }

    private Product product() {
        Product product = new Product(); product.setId(1L); product.setProductName("Laptop");
        product.setSellingPrice(new BigDecimal("100")); product.setStatus(Product.ProductStatus.ACTIVE);
        return product;
    }
}
