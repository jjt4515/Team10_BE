package poomasi.domain.product._cart.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product._cart.dto.CartResponse;
import poomasi.domain.product._cart.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT new poomasi.domain.product._cart.dto.CartResponse(c.id, c.product.name, c.product.price, c.member.store.name) from Cart c where c.member = :member")
    List<CartResponse> findByMember(Member member);

    Optional<Cart> findByMemberIdAndProductId(Long memberId, Long productId);

    @Query("SELECT e FROM Cart e WHERE e.id IN :ids")
    List<Cart> getCartsByIdList(List<Long> ids);

    @Modifying
    @Transactional
    void deleteAllByMemberId(Long memberId);
}
