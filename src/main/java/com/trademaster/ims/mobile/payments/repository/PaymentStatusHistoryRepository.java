package com.trademaster.ims.mobile.payments.repository;
import com.trademaster.ims.mobile.payments.model.PaymentStatusHistory;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface PaymentStatusHistoryRepository extends JpaRepository<PaymentStatusHistory,Long>{List<PaymentStatusHistory>findByPaymentIdOrderByOccurredAtAsc(Long paymentId);}
