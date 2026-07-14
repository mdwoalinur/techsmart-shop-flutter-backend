package com.trademaster.ims.mobile.payments.controller;

import com.trademaster.ims.mobile.payments.dto.PaymentDtos.*;
import com.trademaster.ims.mobile.payments.service.CustomerPaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentReviewController{
 private final CustomerPaymentService service; public PaymentReviewController(CustomerPaymentService service){this.service=service;}
 @GetMapping("/reviews") @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasAuthority('PAYMENT_APPROVE')") public Page<ReviewSummary> reviews(@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="20")int size){return service.reviews(PageRequest.of(page,size,Sort.by("createdAt").descending()));}
 @PostMapping("/reviews/{id}/approve") @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasAuthority('PAYMENT_APPROVE')") public ReviewSummary approve(@PathVariable Long id,@Valid @RequestBody(required=false)ReviewDecisionRequest q){return service.approveReview(id,q==null?new ReviewDecisionRequest(null,null,null):q);} 
 @PostMapping("/reviews/{id}/reject") @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasAuthority('PAYMENT_REJECT')") public ReviewSummary reject(@PathVariable Long id,@Valid @RequestBody(required=false)ReviewDecisionRequest q){return service.rejectReview(id,q==null?new ReviewDecisionRequest(null,null,"Payment rejected."):q);} 
 @PostMapping("/customer/{paymentNumber}/reconcile") @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasAuthority('PAYMENT_RECONCILE')") public ReconciliationResult reconcile(@PathVariable String paymentNumber,@Valid @RequestBody ReconciliationRequest q){return service.reconcile(paymentNumber,q);} 
 @PostMapping("/customer/{paymentNumber}/refunds") @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasAuthority('PAYMENT_REFUND')") public RefundResult refund(@PathVariable String paymentNumber,@Valid @RequestBody RefundRequest q){return service.refund(paymentNumber,q);} 
}
