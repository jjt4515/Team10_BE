package poomasi.domain.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import poomasi.domain.member.dto.request.ConvertToFarmerRequest;
import poomasi.domain.member.dto.request.FarmerUpdateRequest;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.entity.Role;
import poomasi.domain.member.repository.MemberRepository;
import poomasi.domain.store.entity.Store;
import poomasi.domain.store.service.StoreService;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberFarmerServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private StoreService storeService;

    @InjectMocks
    private MemberFarmerService memberFarmerService;

    private Member customerMember;
    private Member farmerMember;

    @BeforeEach
    void setUp() {
        customerMember = Member.builder()
                .id(1L)
                .name("Customer")
                .email("customer@example.com")
                .password("password")
                .role(Role.ROLE_CUSTOMER)
                .build();

        farmerMember = Member.builder()
                .id(2L)
                .name("Farmer")
                .email("farmer@example.com")
                .password("password")
                .role(Role.ROLE_FARMER)
                .build();
    }

    @Test
    @DisplayName("회원 -> 농부로 변환 성공 테스트")
    void convertToFarmer_success() {
        // given
        ConvertToFarmerRequest convertToFarmerRequest = new ConvertToFarmerRequest("name", "address", "phone");
        given(memberRepository.save(customerMember)).willReturn(customerMember);

        // when
        memberFarmerService.convertToFarmer(customerMember, convertToFarmerRequest);

        // then
        assertEquals(Role.ROLE_FARMER, customerMember.getRole());
        verify(memberRepository, times(1)).save(customerMember);
    }

    @Test
    @DisplayName("이미 농부인 회원 변환 시도 시 예외 발생 테스트")
    void convertToFarmer_alreadyFarmer() {
        ConvertToFarmerRequest convertToFarmerRequest = new ConvertToFarmerRequest("name", "address", "phone");
        // when & then
        assertThatThrownBy(() -> memberFarmerService.convertToFarmer(farmerMember, convertToFarmerRequest))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("businessError", BusinessError.MEMBER_ALREADY_FARMER);
    }

    @Test
    @DisplayName("농부 정보 업데이트 성공 테스트")
    void updateFarmer_success() {
        // given
        FarmerUpdateRequest farmerUpdateRequest = new FarmerUpdateRequest(
                "UpdatedFarmer",
                "farmer@newemail.com",
                "newPassword",
                "1234567890",
                "NewStoreName",
                "NewStoreAddress");
        Store store = new Store();
        farmerMember.setStore(store);

        doAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            member.setName(farmerUpdateRequest.name());
            member.setEmail(farmerUpdateRequest.email());
            member.setPassword(farmerUpdateRequest.password());
            member.getOrCreateProfile().setPhoneNumber(farmerUpdateRequest.phoneNumber());
            return null;
        }).when(memberService).updateCommonAttributes(any(Member.class), anyString(), anyString(), anyString(), anyString());


        given(memberRepository.save(farmerMember)).willReturn(farmerMember);

        // when
        Member updatedMember = memberFarmerService.updateFarmer(farmerMember, farmerUpdateRequest);

        // then
        assertEquals("UpdatedFarmer", updatedMember.getName());
        assertEquals("farmer@newemail.com", updatedMember.getEmail());
        assertEquals("NewStoreName", updatedMember.getStore().getName());
        assertEquals("NewStoreAddress", updatedMember.getStore().getAddress());

        verify(memberService, times(1)).updateCommonAttributes(farmerMember, farmerUpdateRequest.name(), farmerUpdateRequest.email(), farmerUpdateRequest.password(), farmerUpdateRequest.phoneNumber());
        verify(memberRepository, times(1)).save(farmerMember);
    }

    @Test
    @DisplayName("농부가 아닌 회원이 농부 정보 업데이트 시도 시 예외 발생 테스트")
    void updateFarmer_invalidRole() {
        // given
        FarmerUpdateRequest farmerUpdateRequest = new FarmerUpdateRequest(
                "UpdatedFarmer",
                "farmer@newemail.com",
                "newPassword",
                "1234567890",
                "NewStoreName",
                "NewStoreAddress");

        // when & then
        assertThatThrownBy(() -> memberFarmerService.updateFarmer(customerMember, farmerUpdateRequest))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("businessError", BusinessError.INVALID_ROLE);

        verify(memberRepository, never()).save(any(Member.class));
    }
}
