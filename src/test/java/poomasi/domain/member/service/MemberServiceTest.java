package poomasi.domain.member.service;

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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signUpSuccessTest() {
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
    void signUpEmailDuplicationTest() {
        // given
        SignupRequest signupRequest = new SignupRequest("testName", "test@example.com", "testPassword");
        given(memberRepository.findByEmailAndDeletedAtIsNull(anyString())).willReturn(Optional.of(mock(Member.class)));

        // when & then
        assertThatThrownBy(() -> memberService.signUp(signupRequest))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("businessError", BusinessError.DUPLICATE_MEMBER_EMAIL);

        verify(memberRepository, times(1)).findByEmailAndDeletedAtIsNull(signupRequest.email());
    }
//
//    @Test
//    void getMemberById() {
//    }
//
//    @Test
//    void getMemberSummary() {
//    }
//
//    @Test
//    void getAllMembersSummary() {
//    }
//
//    @Test
//    void convertToFarmer() {
//    }
//
//    @Test
//    void convertToCustomer() {
//    }
//
//    @Test
//    void findMemberById() {
//    }
//
//    @Test
//    void updateCustomer() {
//    }
//
//    @Test
//    void updateFarmer() {
//    }
//
//    @Test
//    void updateAddress() {
//    }
//
//    @Test
//    void deleteAccount() {
//    }
//
//    @Test
//    void restoreAccount() {
//    }
//
//    @Test
//    void suspendAccount() {
//    }
}