package poomasi.domain.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import poomasi.domain.member.dto.request.SignupRequest;
import poomasi.domain.member.dto.response.SignUpResponse;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.repository.MemberRepository;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static poomasi.domain.member.entity.Role.ROLE_CUSTOMER;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private Long memberId;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        member = Member.builder()
                .id(memberId)
                .name("testName")
                .email("test@example.com")
                .password("password")
                .role(ROLE_CUSTOMER)
                .build();
    }

    @Test
    @DisplayName("이메일 중복 시 회원가입 성공 테스트")
    void signUp_success() {
        // given
        SignupRequest signupRequest = new SignupRequest("testName", "test@example.com", "testPassword");
        Member mockMember = mock(Member.class);
        given(memberRepository.save(any(Member.class))).willReturn(mockMember);

        // when
        SignUpResponse response = memberService.signUp(signupRequest);

        // then
        assertNotNull(response);
        assertEquals("회원 가입 성공", response.message());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("이메일 중복 시 회원가입 실패 테스트")
    void signUp_DuplicationMember() {
        // given
        SignupRequest signupRequest = new SignupRequest("testName", "test@example.com", "testPassword");
        given(memberRepository.findByEmailAndDeletedAtIsNull(anyString())).willReturn(Optional.of(mock(Member.class)));

        // when & then
        assertThatThrownBy(() -> memberService.signUp(signupRequest))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("businessError", BusinessError.DUPLICATE_MEMBER_EMAIL);

        verify(memberRepository, times(1)).findByEmailAndDeletedAtIsNull(signupRequest.email());
    }

    @Test
    @DisplayName("id로 멤버 조회 성공 테스트")
    void getMemberById_success() {
        // given
        when(memberRepository.findByIdAndDeletedAtIsNull(memberId)).thenReturn(Optional.of(member));

        // when
        var memberResponse = memberService.getMemberById(memberId);

        // then
        assertNotNull(memberResponse);
        assertEquals("testName", memberResponse.name());
        assertEquals("test@example.com", memberResponse.email());
    }

    @Test
    @DisplayName("회원 정보 없을 때 조회 실패 테스트")
    void getMemberById_memberNotFound() {
        // given
        when(memberRepository.findByIdAndDeletedAtIsNull(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMemberById(memberId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("businessError", poomasi.global.error.BusinessError.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("회원 정보 업데이트 테스트")
    void updateCommonAttributes_success() {
        // given
        String newName = "Updated Name";
        String newEmail = "updated@example.com";
        String newPassword = "newPassword";
        String newPhoneNumber = "1234567890";

        // when
        memberService.updateCommonAttributes(member, newName, newEmail, newPassword, newPhoneNumber);

        // then
        assertEquals(member.getName(), newName);
        assertEquals(member.getEmail(), newEmail);
        assertNotEquals(member.getPassword(), "password");
        assertEquals(member.getOrCreateProfile().getPhoneNumber(), newPhoneNumber);
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void deleteAccount_success() {
        // when
        memberService.deleteAccount(member);

        // then
        verify(memberRepository, times(1)).delete(member);
    }

    @Test
    @DisplayName("회원 복구 테스트")
    void restoreAccount_success() {
        // given
        member.setDeletedAt(java.time.LocalDateTime.now());
        when(memberRepository.findByIdAndDeletedAtIsNotNull(memberId)).thenReturn(Optional.of(member));

        // when
        memberService.restoreAccount(memberId);

        // then
        assertThat(member.getDeletedAt()).isNull();
        verify(memberRepository, times(1)).save(member);
    }

    @DisplayName("회원 정지 테스트")
    @Test
    void suspendAccount_success() {
        when(memberRepository.findByIdAndDeletedAtIsNull(memberId)).thenReturn(Optional.of(member));

        // when
        memberService.suspendAccount(memberId);

        // then
        assertThat(member.getOrCreateProfile().isBanned()).isTrue();
        verify(memberRepository, times(1)).save(member);
    }
}