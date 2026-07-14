package com.trademaster.ims.mobile.payments.repository;
import com.trademaster.ims.mobile.payments.model.PaymentEvent;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface PaymentEventRepository extends JpaRepository<PaymentEvent,Long>{Optional<PaymentEvent>findByProviderAndGatewayEventId(String provider,String gatewayEventId);List<PaymentEvent>findByPaymentIdOrderByCreatedAtAsc(Long paymentId);}
