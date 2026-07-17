package com.trademaster.ims.mobile.reviews;

import com.trademaster.ims.mobile.auth.service.CustomerAuthException;
import com.trademaster.ims.mobile.checkout.model.CustomerOrder;
import com.trademaster.ims.mobile.checkout.model.CustomerOrderItem;
import com.trademaster.ims.mobile.checkout.repository.CustomerOrderItemRepository;
import com.trademaster.ims.mobile.checkout.repository.CustomerOrderRepository;
import com.trademaster.ims.mobile.reviews.dto.ReviewDtos.ReviewRequest;
import com.trademaster.ims.mobile.reviews.dto.ReviewDtos.ReviewResponse;
import com.trademaster.ims.mobile.reviews.model.CustomerProductReview;
import com.trademaster.ims.mobile.reviews.repository.CustomerProductReviewRepository;
import com.trademaster.ims.mobile.reviews.service.CustomerReviewService;
import com.trademaster.ims.model.Product;
import com.trademaster.ims.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerReviewServiceTest {
    @Mock CustomerProductReviewRepository reviews;
    @Mock CustomerOrderRepository orders;
    @Mock CustomerOrderItemRepository items;
    @Mock ProductRepository products;
    CustomerReviewService service;

    @BeforeEach void setUp() {
        service = new CustomerReviewService(reviews, orders, items, products);
    }

    @Test void createsReviewOnlyForDeliveredPurchasedOrderItem() {
        Product product = product(42L, "Wireless Mouse");
        CustomerOrder order = order(10L, 7L, CustomerOrder.OrderStatus.DELIVERED);
        CustomerOrderItem item = item(99L, 10L, 42L);
        when(products.findByIdAndStatus(42L, Product.ProductStatus.ACTIVE)).thenReturn(Optional.of(product));
        when(orders.findByOrderNumberAndAccountId("ORD-1", 7L)).thenReturn(Optional.of(order));
        when(items.findByOrderIdOrderByIdAsc(10L)).thenReturn(List.of(item));
        when(reviews.existsByAccountIdAndOrderItemIdAndProductId(7L, 99L, 42L)).thenReturn(false);
        when(reviews.save(any(CustomerProductReview.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewResponse response = service.create(7L, 42L,
                new ReviewRequest("ORD-1", 99L, 5, "Great", "Works perfectly."));

        assertEquals(42L, response.productId());
        assertEquals(5, response.rating());
        assertEquals("APPROVED", response.status());
        verify(reviews).save(any(CustomerProductReview.class));
    }

    @Test void rejectsReviewBeforeDelivery() {
        when(products.findByIdAndStatus(42L, Product.ProductStatus.ACTIVE)).thenReturn(Optional.of(product(42L, "Keyboard")));
        when(orders.findByOrderNumberAndAccountId("ORD-2", 7L))
                .thenReturn(Optional.of(order(11L, 7L, CustomerOrder.OrderStatus.PROCESSING)));

        CustomerAuthException ex = assertThrows(CustomerAuthException.class, () -> service.create(7L, 42L,
                new ReviewRequest("ORD-2", 5L, 4, null, "Good.")));

        assertEquals(HttpStatus.CONFLICT, ex.status());
        assertEquals("ORDER_NOT_DELIVERED", ex.code());
        verify(reviews, never()).save(any());
    }

    @Test void rejectsProductNotPurchasedInSelectedOrderItem() {
        CustomerOrder order = order(12L, 7L, CustomerOrder.OrderStatus.DELIVERED);
        when(products.findByIdAndStatus(42L, Product.ProductStatus.ACTIVE)).thenReturn(Optional.of(product(42L, "Mouse")));
        when(orders.findByOrderNumberAndAccountId("ORD-3", 7L)).thenReturn(Optional.of(order));
        when(items.findByOrderIdOrderByIdAsc(12L)).thenReturn(List.of(item(77L, 12L, 100L)));

        CustomerAuthException ex = assertThrows(CustomerAuthException.class, () -> service.create(7L, 42L,
                new ReviewRequest("ORD-3", 77L, 4, null, "Good.")));

        assertEquals(HttpStatus.BAD_REQUEST, ex.status());
        assertEquals("PRODUCT_NOT_PURCHASED", ex.code());
    }

    @Test void rejectsDuplicateReviewForSameOrderItemProductAndCustomer() {
        CustomerOrder order = order(13L, 7L, CustomerOrder.OrderStatus.DELIVERED);
        when(products.findByIdAndStatus(42L, Product.ProductStatus.ACTIVE)).thenReturn(Optional.of(product(42L, "Mouse")));
        when(orders.findByOrderNumberAndAccountId("ORD-4", 7L)).thenReturn(Optional.of(order));
        when(items.findByOrderIdOrderByIdAsc(13L)).thenReturn(List.of(item(88L, 13L, 42L)));
        when(reviews.existsByAccountIdAndOrderItemIdAndProductId(7L, 88L, 42L)).thenReturn(true);

        CustomerAuthException ex = assertThrows(CustomerAuthException.class, () -> service.create(7L, 42L,
                new ReviewRequest("ORD-4", 88L, 4, null, "Good.")));

        assertEquals(HttpStatus.CONFLICT, ex.status());
        assertEquals("REVIEW_ALREADY_EXISTS", ex.code());
    }

    @Test void summaryUsesApprovedAverageAndCount() {
        when(products.findByIdAndStatus(42L, Product.ProductStatus.ACTIVE)).thenReturn(Optional.of(product(42L, "Mouse")));
        when(reviews.averageApproved(42L)).thenReturn(4.26D);
        when(reviews.countByProductIdAndStatus(42L, CustomerProductReview.Status.APPROVED)).thenReturn(12L);

        var summary = service.summary(42L);

        assertEquals(new BigDecimal("4.3"), summary.averageRating());
        assertEquals(12L, summary.reviewCount());
    }

    private Product product(Long id, String name) {
        Product product = new Product();
        product.setId(id);
        product.setProductName(name);
        product.setStatus(Product.ProductStatus.ACTIVE);
        return product;
    }

    private CustomerOrder order(Long id, Long accountId, CustomerOrder.OrderStatus status) {
        CustomerOrder order = new CustomerOrder();
        ReflectionTestUtils.setField(order, "id", id);
        order.setOrderNumber("ORD-" + id);
        order.setAccountId(accountId);
        order.setStatus(status);
        return order;
    }

    private CustomerOrderItem item(Long id, Long orderId, Long productId) {
        CustomerOrderItem item = new CustomerOrderItem();
        ReflectionTestUtils.setField(item, "id", id);
        item.setOrderId(orderId);
        item.setProductId(productId);
        item.setProductName("Product " + productId);
        item.setProductCode("P" + productId);
        return item;
    }
}