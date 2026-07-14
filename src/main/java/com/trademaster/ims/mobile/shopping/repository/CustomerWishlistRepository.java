package com.trademaster.ims.mobile.shopping.repository;
import com.trademaster.ims.mobile.shopping.model.CustomerWishlist; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface CustomerWishlistRepository extends JpaRepository<CustomerWishlist,Long>{Optional<CustomerWishlist> findByCustomerAccountId(Long accountId);}
