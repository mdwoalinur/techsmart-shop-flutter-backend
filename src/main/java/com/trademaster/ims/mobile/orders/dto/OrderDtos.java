package com.trademaster.ims.mobile.orders.dto;
import jakarta.validation.Valid;import jakarta.validation.constraints.*;import java.math.BigDecimal;import java.time.Instant;import java.util.List;
public final class OrderDtos{private OrderDtos(){}
 public record OrderPage(List<OrderSummary>content,int page,int size,long totalElements,int totalPages,boolean first,boolean last){}
 public record OrderSummary(String orderNumber,Instant submittedAt,String orderStatus,String customerVisibleStatusLabel,String paymentStatus,BigDecimal grandTotal,String currency,int totalQuantity,int itemCount,String firstItemName,String firstItemImageUrl,int additionalItemCount,String deliveryMethodName,boolean cancellationEligible,boolean returnEligible){}
 public record OrderDetail(String orderNumber,Instant submittedAt,Instant updatedAt,String orderStatus,String customerVisibleStatusLabel,String paymentStatus,String accountingStatus,String currency,BigDecimal subtotal,BigDecimal taxTotal,BigDecimal deliveryCharge,BigDecimal discountTotal,BigDecimal grandTotal,List<OrderItemSnapshot>items,DeliverySnapshot delivery,String customerNote,List<TimelineEntry>timeline,EligibilityResponse cancellationEligibility,ReturnEligibilityResponse returnEligibility,boolean documentAvailable){}
 public record OrderItemSnapshot(Long itemId,Long productId,Long variationId,String productName,String productCode,String variationName,String imageUrl,BigDecimal unitPrice,int quantity,BigDecimal lineSubtotal,BigDecimal taxRate,BigDecimal taxAmount){}
 public record DeliverySnapshot(String recipientName,String phone,String address,String deliveryMethodName){}
 public record TimelineEntry(String status,String title,String description,Instant occurredAt,boolean completed,boolean current,String note){}
 public record EligibilityResponse(boolean eligible,String reasonCode,String message,String existingRequestStatus){}
 public record CancellationRequestDto(@NotBlank String reasonCode,@Size(max=500)String reasonText,@NotBlank @Size(max=100)String idempotencyKey){}
 public record CancellationRequestResponse(String orderNumber,String status,String reasonCode,String reasonText,Instant requestedAt,String message){}
 public record ReturnEligibilityResponse(boolean eligible,String reasonCode,String message,int returnWindowDays,List<ReturnableOrderItem>items,String existingRequestStatus){}
 public record ReturnableOrderItem(Long itemId,String productName,String variationName,int orderedQuantity,int remainingReturnableQuantity){}
 public record ReturnRequestDto(@NotBlank @Size(max=100)String idempotencyKey,@NotBlank String preferredResolution,@Size(max=500)String customerComment,@NotEmpty List<@Valid ReturnItemRequest>items){}
 public record ReturnItemRequest(@NotNull Long orderItemId,@Min(1)int quantity,@NotBlank String reasonCode,@Size(max=500)String reasonText){}
 public record ReturnRequestResponse(String requestNumber,String orderNumber,String status,String preferredResolution,Instant requestedAt,List<ReturnRequestItemResponse>items,String message){}
 public record ReturnRequestItemResponse(Long orderItemId,String productName,int requestedQuantity,String reasonCode,String reasonText){}
 public record OrderDocumentResponse(String orderNumber,String fileName,String contentType,String documentTitle,String html){}
}
