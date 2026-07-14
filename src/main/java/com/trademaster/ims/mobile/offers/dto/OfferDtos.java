package com.trademaster.ims.mobile.offers.dto;
import com.trademaster.ims.mobile.catalog.dto.*;import java.math.BigDecimal;import java.time.Instant;import java.util.List;
public final class OfferDtos{private OfferDtos(){}
 public record OfferSummary(Long id,String code,String title,String subtitle,String description,String bannerUrl,String channel,Instant startAt,Instant endAt,long productCount){}
 public record OfferDetail(Long id,String code,String title,String subtitle,String description,String bannerUrl,String channel,Instant startAt,Instant endAt,long productCount){}
 public record OfferPrice(BigDecimal originalPrice,BigDecimal currentPrice,BigDecimal savingsAmount,String savingsLabel,Long offerId,String offerTitle){}
 public record OfferProduct(Long id,String productCode,String sku,String name,String description,BigDecimal sellingPrice,BigDecimal originalPrice,BigDecimal savingsAmount,String savingsLabel,BigDecimal taxRate,String imageUrl,MobileCategorySummaryResponse category,MobileUnitSummaryResponse unit,MobileStockAvailabilityResponse stock,Long offerId,String offerTitle){}
 public record OfferProductPage(List<OfferProduct> content,int page,int size,long totalElements,int totalPages,boolean first,boolean last){}
}