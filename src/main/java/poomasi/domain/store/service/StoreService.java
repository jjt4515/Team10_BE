package poomasi.domain.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.service.MemberService;
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
    private final MemberService memberService;

    @Transactional
    public void addStore(StoreRegisterRequest storeRegisterRequest, Member member) {
        if (member.getStore() != null) {
            throw new BusinessException(BusinessError.STORE_ALREADY_EXISTS);
        }

        Store store = storeRegisterRequest.toEntity(member);
        store = storeRepository.save(store);
        member.setStore(store);
    }

    public StoreResponse getStore(Long memberId) {
        Member member = memberService.findMemberById(memberId);
        return StoreResponse.fromEntity(member.getStore());
    }

    private Store getStore(Member member) {
        return storeRepository.findByOwnerId(member.getId())
                .orElseThrow(() -> new BusinessException(BusinessError.STORE_NOT_FOUND));
    }

    @Transactional
    public void updateStore(StoreRegisterRequest storeRegisterRequest, Member member) {
        Store store = getStore(member);
        store.updateStore(storeRegisterRequest);
    }
}
