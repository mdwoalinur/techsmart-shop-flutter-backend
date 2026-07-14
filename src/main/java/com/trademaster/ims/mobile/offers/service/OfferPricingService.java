package com.trademaster.ims.mobile.offers.service;

import com.trademaster.ims.mobile.offers.model.MobileOfferProduct;
import com.trademaster.ims.mobile.offers.repository.MobileOfferProductRepository;
import org.springframework.stereotype.Service;
import java.math.*;import java.time.Instant;import java.util.*;

@Service
public class OfferPricingService {
 private final MobileOfferProductRepository products;
 public OfferPricingService(MobileOfferProductRepository products){this.products=products;}
 public OfferPrice bestPrice(Long productId, BigDecimal basePrice){return bestPrice(productId, basePrice, Instant.now());}
 public OfferPrice bestPrice(Long productId, BigDecimal basePrice, Instant now){BigDecimal original=money(basePrice);OfferPrice best=new OfferPrice(original,original,BigDecimal.ZERO,null,null);for(MobileOfferProduct op:products.findActiveForProduct(productId,now)){OfferPrice p=price(op,original);if(p.currentPrice().compareTo(best.currentPrice())<0)best=p;}return best;}
 public OfferPrice price(MobileOfferProduct op, BigDecimal basePrice){BigDecimal original=money(basePrice);BigDecimal current=switch(op.getDiscountType()){case PERCENT -> original.subtract(original.multiply(money(op.getDiscountValue())).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP));case FIXED_AMOUNT -> original.subtract(money(op.getDiscountValue()));case PRICE_OVERRIDE -> money(op.getDiscountValue());};if(current.compareTo(BigDecimal.ZERO)<0)current=BigDecimal.ZERO;current=current.setScale(2,RoundingMode.HALF_UP);BigDecimal savings=original.subtract(current).max(BigDecimal.ZERO).setScale(2,RoundingMode.HALF_UP);String label=savings.signum()<=0?null:switch(op.getDiscountType()){case PERCENT -> money(op.getDiscountValue()).stripTrailingZeros().toPlainString()+"% off";case FIXED_AMOUNT -> "Save ?"+savings.stripTrailingZeros().toPlainString();case PRICE_OVERRIDE -> "Special price";};return new OfferPrice(original.setScale(2,RoundingMode.HALF_UP),current,savings,label,op.getOfferId());}
 private BigDecimal money(BigDecimal v){return v==null?BigDecimal.ZERO:v;}
 public record OfferPrice(BigDecimal originalPrice,BigDecimal currentPrice,BigDecimal savingsAmount,String savingsLabel,Long offerId){}
}