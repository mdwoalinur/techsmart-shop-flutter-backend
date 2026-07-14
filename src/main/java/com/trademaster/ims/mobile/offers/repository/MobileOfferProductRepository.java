package com.trademaster.ims.mobile.offers.repository;
import com.trademaster.ims.mobile.offers.model.MobileOfferProduct;
import org.springframework.data.jpa.repository.*;import org.springframework.data.repository.query.Param;import java.time.Instant;import java.util.*;
public interface MobileOfferProductRepository extends JpaRepository<MobileOfferProduct,Long>{
 List<MobileOfferProduct> findByOfferIdAndActiveTrueOrderByDisplayOrderAscIdAsc(Long offerId);
 long countByOfferIdAndActiveTrue(Long offerId);
 @Query("select op from MobileOfferProduct op join MobileOffer o on o.id=op.offerId where op.productId=:productId and op.active=true and o.active=true and o.visible=true and o.status=com.trademaster.ims.mobile.offers.model.MobileOffer$OfferStatus.ACTIVE and o.startAt<=:now and o.endAt>=:now")
 List<MobileOfferProduct> findActiveForProduct(@Param("productId") Long productId,@Param("now") Instant now);
}