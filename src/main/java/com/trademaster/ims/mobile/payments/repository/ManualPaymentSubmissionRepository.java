package com.trademaster.ims.mobile.payments.repository;
import com.trademaster.ims.mobile.payments.model.ManualPaymentSubmission;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface ManualPaymentSubmissionRepository extends JpaRepository<ManualPaymentSubmission,Long>{Optional<ManualPaymentSubmission>findByMethodAndTransactionReference(String method,String ref);Optional<ManualPaymentSubmission>findByPaymentId(Long paymentId);}
