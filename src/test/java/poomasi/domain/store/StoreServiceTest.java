package poomasi.domain.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.repository.MemberRepository;
import poomasi.domain.member.service.MemberService;
import poomasi.domain.store.dto.StoreRegisterRequest;
import poomasi.domain.store.entity.Store;
import poomasi.domain.store.repository.StoreRepository;
import poomasi.domain.store.service.StoreService;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberService memberService;  // MemberService를 @Mock으로 선언

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.builder().build();
    }

    @Test
    @DisplayName("Store 추가 성공")
    void addStore_StoreAddedSuccessfully() {
        // given
        StoreRegisterRequest request = mock(StoreRegisterRequest.class);
        Store store = mock(Store.class);

        when(request.toEntity(any(Member.class))).thenReturn(store);

        // when
        storeService.addStore(request, testMember);

        // then
        verify(storeRepository, times(1)).save(store);
    }

//    @Test
//    @DisplayName("Store가 존재할 경우 조회 성공")
//    void getStore_StoreExists_ReturnStoreResponse() {
//        // given
//        // Store 객체를 실제 값으로 초기화
//        Store store = new Store(1L, "testStore", "testDescription", "testAddress", testMember, "testContact");
//
//        // memberService와 memberRepository가 testMember를 반환하도록 설정
//        when(memberService.findMemberById(testMember.getId())).thenReturn(testMember);
//        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
//
//        // storeRepository가 store 객체를 반환하도록 설정
//        when(storeRepository.findByOwnerId(testMember.getId())).thenReturn(Optional.of(store));
//
//        // when
//        StoreResponse response = storeService.getStore(testMember.getId());
//
//        verify(storeRepository, times(1)).findByOwnerId(testMember.getId());
//    }

    @Test
    void updateStore_StoreExists_StoreUpdatedSuccessfully() {
        // given
        StoreRegisterRequest request = mock(StoreRegisterRequest.class);
        Store store = mock(Store.class);

        when(storeRepository.findByOwnerId(testMember.getId())).thenReturn(Optional.of(store));

        // when
        storeService.updateStore(request, testMember);

        // then
        verify(store, times(1)).updateStore(request);
    }

    @Test
    void updateStore_StoreDoesNotExist_ThrowsBusinessException() {
        // given
        StoreRegisterRequest request = mock(StoreRegisterRequest.class);
        when(storeRepository.findByOwnerId(testMember.getId())).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> storeService.updateStore(request, testMember));
        assertEquals(BusinessError.STORE_NOT_FOUND, exception.getBusinessError());
    }

}
