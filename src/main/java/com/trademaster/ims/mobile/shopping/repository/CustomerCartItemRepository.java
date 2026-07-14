package com.trademaster.ims.mobile.shopping.repository;
import com.trademaster.ims.mobile.shopping.model.CustomerCartItem; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface CustomerCartItemRepository extends JpaRepository<CustomerCartItem,Long>{List<CustomerCartItem> findByCartIdOrderByIdAsc(Long cartId); Optional<CustomerCartItem> findByCartIdAndProductIdAndVariationKey(Long cartId,Long productId,Long variationKey); Optional<CustomerCartItem> findByIdAndCartId(Long id,Long cartId); void deleteByCartId(Long cartId);}
