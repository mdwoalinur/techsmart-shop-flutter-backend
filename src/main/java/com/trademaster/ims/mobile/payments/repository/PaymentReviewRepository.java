package com.trademaster.ims.mobile.payments.repository;
import com.trademaster.ims.mobile.payments.model.PaymentReview;import org.springframework.data.domain.*;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface PaymentReviewRepository extends JpaRepository<PaymentReview,Long>{Optional<PaymentReview>findByPaymentId(Long paymentId);Page<PaymentReview>findByStatus(PaymentReview.ReviewStatus status,Pageable p);}
