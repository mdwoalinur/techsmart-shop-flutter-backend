package com.trademaster.ims.mobile.offers.repository;
import com.trademaster.ims.mobile.offers.model.MobileOffer;
import org.springframework.data.jpa.repository.*;import org.springframework.data.repository.query.Param;import java.time.Instant;import java.util.*;
public interface MobileOfferRepository extends JpaRepository<MobileOffer,Long>{
 Optional<MobileOffer> findByCode(String code);
 @Query("select o from MobileOffer o where o.active=true and o.visible=true and o.status=com.trademaster.ims.mobile.offers.model.MobileOffer$OfferStatus.ACTIVE and o.startAt<=:now and o.endAt>=:now order by o.displayOrder asc, o.endAt asc")
 List<MobileOffer> findVisibleActive(@Param("now") Instant now);
 @Query("select o from MobileOffer o where o.id=:id and o.active=true and o.visible=true and o.status=com.trademaster.ims.mobile.offers.model.MobileOffer$OfferStatus.ACTIVE and o.startAt<=:now and o.endAt>=:now")
 Optional<MobileOffer> findVisibleActiveById(@Param("id") Long id,@Param("now") Instant now);
}