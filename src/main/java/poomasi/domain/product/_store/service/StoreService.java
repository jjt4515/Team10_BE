package poomasi.domain.product._store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product._store.dto.StoreFeeRequest;
import poomasi.domain.product._store.dto.StoreRegisterRequest;
import poomasi.domain.product._store.dto.StoreResponse;
import poomasi.domain.product._store.entity.Store;
import poomasi.domain.product._store.repository.StoreRepository;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public void addStore(StoreRegisterRequest storeRegisterRequest) {
        Member member = getMember();
        Store store = storeRegisterRequest.toEntity(member);
        storeRepository.save(store);
    }

    public StoreResponse getStore() {
        Member member = getMember();
        Store store = storeRepository.findByMemberId(member.getId()).orElse(null);
        if (store == null) {
            return null;
        }
        return StoreResponse.fromEntity(store);
    }

    private Member getMember() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        Object impl = authentication.getPrincipal();
        return ((UserDetailsImpl) impl).getMember();
    }

    @Transactional
    public void updateStore(StoreRegisterRequest storeRegisterRequest) {
        Member member = getMember();
        Store store = storeRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new BusinessException(BusinessError.STORE_NOT_FOUND));
        store.updateStore(storeRegisterRequest);
    }

    @Transactional
    public void updateFee(StoreFeeRequest storeFeeRequest) {
        Member member = getMember();
        Store store = storeRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new BusinessException(BusinessError.STORE_NOT_FOUND));
        store.updateFee(storeFeeRequest);
    }
}
