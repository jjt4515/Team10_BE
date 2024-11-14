package poomasi.domain.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import poomasi.domain.member._profile.dto.request.AddressUpdateRequest;
import poomasi.domain.member._profile.entity.MemberProfile;
import poomasi.domain.member.dto.request.CustomerUpdateRequest;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.repository.MemberRepository;
import poomasi.global.error.BusinessException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static poomasi.domain.member.entity.Role.ROLE_CUSTOMER;
import static poomasi.domain.member.entity.Role.ROLE_FARMER;
import static poomasi.global.error.BusinessError.INVALID_ROLE;
import static poomasi.global.error.BusinessError.MEMBER_ALREADY_CUSTOMER;

@ExtendWith(MockitoExtension.class)
class MemberCustomerServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberCustomerService memberCustomerService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .name("testName")
                .email("test@example.com")
                .password("password")
                .role(ROLE_CUSTOMER)
                .build();
    }

    @Test
    @DisplayName("농부 -> 고객 전환 성공 테스트")
    void convertToCustomer_success() {
        // given
        member.setRole(ROLE_FARMER);
        given(memberService.findMemberById(member.getId())).willReturn(member);

        // when
        memberCustomerService.convertToCustomer(member.getId());

        // then
        assertEquals(ROLE_CUSTOMER, member.getRole());
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    @DisplayName("이미 고객일 때 고객 전환 실패 테스트")
    void convertToCustomer_alreadyCustomer() {
        // given
        member.setRole(ROLE_CUSTOMER);
        given(memberService.findMemberById(member.getId())).willReturn(member);

        // when & then
        assertThatThrownBy(() -> memberCustomerService.convertToCustomer(member.getId()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("businessError", MEMBER_ALREADY_CUSTOMER);
    }

    @Test
    @DisplayName("회원 정보 업데이트 성공 테스트")
    void updateCustomer_success() {
        // given
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                "UpdatedName",
                "updated@example.com",
                "newPassword",
                "9876543210");

        doAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            member.setName(customerUpdateRequest.name());
            member.setEmail(customerUpdateRequest.email());
            member.setPassword(customerUpdateRequest.password());
            member.getOrCreateProfile().setPhoneNumber(customerUpdateRequest.phoneNumber());
            return null;
        }).when(memberService).updateCommonAttributes(any(Member.class), anyString(), anyString(), anyString(), anyString());

        given(memberRepository.save(member)).willReturn(member);

        // when
        Member updatedMember = memberCustomerService.updateCustomer(member, customerUpdateRequest);

        // then
        assertEquals("UpdatedName", updatedMember.getName());
        assertEquals("updated@example.com", updatedMember.getEmail());
        verify(memberService, times(1)).updateCommonAttributes(member, customerUpdateRequest.name(), customerUpdateRequest.email(), customerUpdateRequest.password(), customerUpdateRequest.phoneNumber());
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    @DisplayName("회원 정보 업데이트 실패 테스트 - Role이 다름")
    void updateCustomer_invalidRole() {
        // given
        member.setRole(ROLE_FARMER);
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest("UpdatedName", "updated@example.com", "newPassword", "9876543210");

        // when & then
        assertThatThrownBy(() -> memberCustomerService.updateCustomer(member, customerUpdateRequest))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("businessError", INVALID_ROLE);
    }

    @Test
    @DisplayName("회원 주소 업데이트 성공 테스트")
    void updateAddress_success() {
        // given
        AddressUpdateRequest addressUpdateRequest = new AddressUpdateRequest("defaultAddress", "addressDetail", 10.0, 20.0);
        MemberProfile profile = new MemberProfile();
        member.setMemberProfile(profile);

        // when
        memberCustomerService.updateAddress(member, addressUpdateRequest);

        // then
        assertEquals("defaultAddress", profile.getDefaultAddress());
        assertEquals("addressDetail", profile.getAddressDetail());
        assertEquals(10.0, profile.getCoordinateX());
        assertEquals(20.0, profile.getCoordinateY());
        verify(memberRepository, times(1)).save(member);
    }
}
