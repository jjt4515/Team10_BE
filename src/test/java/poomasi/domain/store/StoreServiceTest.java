package poomasi.domain.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product._store.dto.StoreFeeRequest;
import poomasi.domain.product._store.dto.StoreRegisterRequest;
import poomasi.domain.product._store.dto.StoreResponse;
import poomasi.domain.product._store.entity.Store;
import poomasi.domain.product._store.repository.StoreRepository;
import poomasi.domain.product._store.service.StoreService;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Member testMember;

    @BeforeEach
    void setUp() {
        // 테스트 멤버와 Authentication 설정
        testMember = Member.builder().id(1L).build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new UserDetailsImpl(testMember));
    }

    @Test
    void addStore_StoreAddedSuccessfully() {
        // given
        StoreRegisterRequest request = mock(StoreRegisterRequest.class);
        Store store = mock(Store.class);

        when(request.toEntity(any(Member.class))).thenReturn(store);

        // when
        storeService.addStore(request);

        // then
        verify(storeRepository, times(1)).save(store);
    }

    @Test
    void getStore_StoreExists_ReturnStoreResponse() {
        // given
        Store store = new Store(1L, "test","test","test",testMember,"test","test",100);
        when(storeRepository.findByMemberId(testMember.getId())).thenReturn(Optional.of(store));

        // when
        StoreResponse response = storeService.getStore();

        // then
        assertNotNull(response);
        verify(storeRepository, times(1)).findByMemberId(testMember.getId());
    }

    @Test
    void getStore_StoreDoesNotExist_ReturnNull() {
        // given
        when(storeRepository.findByMemberId(testMember.getId())).thenReturn(Optional.empty());

        // when
        StoreResponse response = storeService.getStore();

        // then
        assertNull(response);
    }

    @Test
    void updateStore_StoreExists_StoreUpdatedSuccessfully() {
        // given
        StoreRegisterRequest request = mock(StoreRegisterRequest.class);
        Store store = mock(Store.class);

        when(storeRepository.findByMemberId(testMember.getId())).thenReturn(Optional.of(store));

        // when
        storeService.updateStore(request);

        // then
        verify(store, times(1)).updateStore(request);
    }

    @Test
    void updateStore_StoreDoesNotExist_ThrowsBusinessException() {
        // given
        StoreRegisterRequest request = mock(StoreRegisterRequest.class);
        when(storeRepository.findByMemberId(testMember.getId())).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> storeService.updateStore(request));
        assertEquals(BusinessError.STORE_NOT_FOUND, exception.getBusinessError());
    }

    @Test
    void updateFee_StoreExists_FeeUpdatedSuccessfully() {
        // given
        StoreFeeRequest feeRequest = mock(StoreFeeRequest.class);
        Store store = mock(Store.class);

        when(storeRepository.findByMemberId(testMember.getId())).thenReturn(Optional.of(store));

        // when
        storeService.updateFee(feeRequest);

        // then
        verify(store, times(1)).updateFee(feeRequest);
    }

    @Test
    void updateFee_StoreDoesNotExist_ThrowsBusinessException() {
        // given
        StoreFeeRequest feeRequest = mock(StoreFeeRequest.class);
        when(storeRepository.findByMemberId(testMember.getId())).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> storeService.updateFee(feeRequest));
        assertEquals(BusinessError.STORE_NOT_FOUND, exception.getBusinessError());
    }
}