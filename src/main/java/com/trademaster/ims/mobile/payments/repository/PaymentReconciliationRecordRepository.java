package com.trademaster.ims.mobile.payments.repository;
import com.trademaster.ims.mobile.payments.model.PaymentReconciliationRecord;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface PaymentReconciliationRecordRepository extends JpaRepository<PaymentReconciliationRecord,Long>{Optional<PaymentReconciliationRecord>findByProviderAndSettlementReference(String provider,String ref);List<PaymentReconciliationRecord>findByPaymentId(Long paymentId);}
