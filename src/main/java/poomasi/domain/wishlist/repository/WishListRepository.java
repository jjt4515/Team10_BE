package poomasi.domain.wishlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poomasi.domain.wishlist.entity.WishList;
import poomasi.global.common.ServiceType;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    List<WishList> findByMemberId(Long memberId);

    void deleteByMemberIdAndObjectIdAndType(Long member_id, Long objectId, ServiceType type);
}
