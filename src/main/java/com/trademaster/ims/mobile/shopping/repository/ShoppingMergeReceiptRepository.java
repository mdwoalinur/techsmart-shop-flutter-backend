package com.trademaster.ims.mobile.shopping.repository;
import com.trademaster.ims.mobile.shopping.model.ShoppingMergeReceipt; import org.springframework.data.jpa.repository.JpaRepository;
public interface ShoppingMergeReceiptRepository extends JpaRepository<ShoppingMergeReceipt,Long>{boolean existsByCustomerAccountIdAndMergeTypeAndRequestId(Long accountId,String type,String requestId);}
