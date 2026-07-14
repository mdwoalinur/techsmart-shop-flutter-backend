package com.trademaster.ims.mobile.payments.controller;

import com.trademaster.ims.mobile.auth.security.CustomerAuthentication;
import com.trademaster.ims.mobile.payments.dto.PaymentDtos.*;
import com.trademaster.ims.mobile.payments.service.CustomerPaymentService;
import com.trademaster.ims.mobile.payments.service.MobileWalletPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mobile/v1")
public class MobilePaymentController{
 private final CustomerPaymentService service; private final MobileWalletPaymentService walletService; public MobilePaymentController(CustomerPaymentService service,MobileWalletPaymentService walletService){this.service=service;this.walletService=walletService;}
 private long account(Authentication a){return ((CustomerAuthentication)a).accountId();}
 @GetMapping("/orders/{orderNumber}/payment-methods") public List<PaymentMethodResponse> methods(Authentication a,@PathVariable String orderNumber){return service.methods(account(a),orderNumber);} 
 @PostMapping("/orders/{orderNumber}/payments/initiate") @ResponseStatus(HttpStatus.CREATED) public PaymentInitiationResult initiate(Authentication a,@PathVariable String orderNumber,@Valid @RequestBody PaymentInitiationRequest q){return service.initiate(account(a),orderNumber,q);} 
 @PostMapping("/orders/{orderNumber}/payments/manual") @ResponseStatus(HttpStatus.CREATED) public ManualPaymentResult manual(Authentication a,@PathVariable String orderNumber,@Valid @RequestBody ManualPaymentRequest q){return service.manual(account(a),orderNumber,q);} 
 @PostMapping("/orders/{orderNumber}/payments/cod") @ResponseStatus(HttpStatus.CREATED) public CodSelectionResult cod(Authentication a,@PathVariable String orderNumber,@Valid @RequestBody CodSelectionRequest q){return service.cod(account(a),orderNumber,q);} 
 @GetMapping("/orders/{orderNumber}/mobile-wallet-providers") public List<MobileWalletProviderResponse> walletProviders(Authentication a,@PathVariable String orderNumber){return walletService.providers(account(a),orderNumber);} 
 @PostMapping("/orders/{orderNumber}/payments/mobile-wallet/initiate") @ResponseStatus(HttpStatus.CREATED) public MobileWalletSessionResult initiateWallet(Authentication a,@PathVariable String orderNumber,@Valid @RequestBody MobileWalletInitiationRequest q){return walletService.initiate(account(a),orderNumber,q);} 
 @PostMapping("/payments/mobile-wallet/{attemptReference}/confirm") public PaymentStatusResult confirmWallet(Authentication a,@PathVariable String attemptReference,@Valid @RequestBody MobileWalletConfirmRequest q){return walletService.confirm(account(a),attemptReference,q);} 
 @PostMapping("/orders/{orderNumber}/payments/cancel") public PaymentStatusResult cancel(Authentication a,@PathVariable String orderNumber){return service.cancel(account(a),orderNumber);} @GetMapping("/orders/{orderNumber}/payments/status") public PaymentStatusResult status(Authentication a,@PathVariable String orderNumber){return service.status(account(a),orderNumber);} 
 @PostMapping("/payments/webhooks/{provider}") public WebhookResult webhook(@PathVariable String provider,@RequestBody String body,@RequestHeader(name="X-TechSmart-Signature",required=false)String signature){return service.webhook(provider,body,signature);} 
}


