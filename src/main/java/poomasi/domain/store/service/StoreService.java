package poomasi.domain.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.store.dto.StoreRegisterRequest;
import poomasi.domain.store.dto.StoreResponse;
import poomasi.domain.store.entity.Store;
import poomasi.domain.store.repository.StoreRepository;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public void addStore(StoreRegisterRequest storeRegisterRequest) {
        Member member = getMember();
        Store store = storeRegisterRequest.toEntity(member);
        member.setStore(store);
        storeRepository.save(store);
    }

    public StoreResponse getStore() {
        Member member = getMember();
        Store store = getStore(member);
        return StoreResponse.fromEntity(store);
    }

    private Store getStore(Member member) {
        return storeRepository.findByOwnerId(member.getId())
                .orElseThrow(() -> new BusinessException(BusinessError.STORE_NOT_FOUND));
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
        Store store = getStore(member);
        store.updateStore(storeRegisterRequest);
    }
}
