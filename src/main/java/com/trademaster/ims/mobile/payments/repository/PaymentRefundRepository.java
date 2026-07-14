package com.trademaster.ims.mobile.payments.repository;
import com.trademaster.ims.mobile.payments.model.PaymentRefund;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface PaymentRefundRepository extends JpaRepository<PaymentRefund,Long>{Optional<PaymentRefund>findByPaymentIdAndIdempotencyKey(Long paymentId,String key);List<PaymentRefund>findByPaymentId(Long paymentId);}
