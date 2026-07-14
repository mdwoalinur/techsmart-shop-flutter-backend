package com.trademaster.ims.mobile.catalog.service;

import com.trademaster.ims.mobile.catalog.dto.MobileCatalogQuery;
import com.trademaster.ims.model.Inventory;
import com.trademaster.ims.model.Product;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class MobileProductSpecifications {
    private MobileProductSpecifications() {}

    static Specification<Product> matching(MobileCatalogQuery filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), Product.ProductStatus.ACTIVE));
            if (filters.categoryId() != null) predicates.add(cb.equal(root.get("categoryId"), filters.categoryId()));
            if (filters.minPrice() != null) predicates.add(cb.greaterThanOrEqualTo(root.get("sellingPrice"), filters.minPrice()));
            if (filters.maxPrice() != null) predicates.add(cb.lessThanOrEqualTo(root.get("sellingPrice"), filters.maxPrice()));
            if (filters.search() != null) {
                String pattern = "%" + escapeLike(filters.search().toLowerCase(Locale.ROOT)) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("productName")), pattern, '\\'),
                        cb.like(cb.lower(root.get("productCode")), pattern, '\\'),
                        cb.like(cb.lower(root.get("sku")), pattern, '\\'),
                        cb.like(cb.lower(root.get("description")), pattern, '\\')));
            }
            if (filters.inStock() != null) {
                Subquery<Long> stock = query.subquery(Long.class);
                Root<Inventory> inventory = stock.from(Inventory.class);
                stock.select(cb.coalesce(cb.sumAsLong(inventory.get("availableQuantity")), 0L))
                        .where(cb.equal(inventory.get("productId"), root.get("id")));
                predicates.add(filters.inStock()
                        ? cb.greaterThan(stock, 0L)
                        : cb.lessThanOrEqualTo(stock, 0L));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
