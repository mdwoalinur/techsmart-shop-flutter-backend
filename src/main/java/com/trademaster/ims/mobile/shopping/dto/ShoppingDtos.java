package com.trademaster.ims.mobile.shopping.dto;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.Instant; import java.util.List;
public final class ShoppingDtos { private ShoppingDtos(){}
 public record AddCartItemRequest(@NotNull Long productId,Long variationId,@NotNull @Min(1) @Max(99) Integer quantity){}
 public record UpdateCartItemQuantityRequest(@NotNull @Min(1) @Max(99) Integer quantity){}
 public record MergeCartItemRequest(@NotNull Long productId,Long variationId,@NotNull @Min(1) @Max(99) Integer quantity){}
 public record MergeSessionCartRequest(@NotBlank @Size(max=100) String requestId,@NotNull List<@Valid MergeCartItemRequest> items){}
 public record ValidationWarning(Long productId,Long variationId,String code,String message){}
 public record CartItemResponse(Long itemId,Long productId,Long variationId,String productName,String productCode,String sku,String variationName,String imageUrl,BigDecimal unitPrice,int quantity,BigDecimal lineSubtotal,String stockLabel,boolean availableForPurchase,String validationMessage){}
 public record CartResponse(Long cartId,List<CartItemResponse> items,int totalItemLines,int totalQuantity,BigDecimal subtotal,List<ValidationWarning> validationWarnings,Instant updatedAt){}
 public record AddWishlistItemRequest(@NotNull Long productId){}
 public record MergeSessionWishlistRequest(@NotBlank @Size(max=100) String requestId,@NotNull List<@NotNull Long> productIds){}
 public record WishlistItemResponse(Long productId,String productName,String imageUrl,BigDecimal sellingPrice,String stockLabel,String category){}
 public record WishlistResponse(Long wishlistId,List<WishlistItemResponse> items,int totalItems,List<ValidationWarning> validationWarnings,Instant updatedAt){}
}
