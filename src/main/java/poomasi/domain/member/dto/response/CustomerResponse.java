package poomasi.domain.member.dto.response;

import poomasi.domain.member._profile.dto.response.CustomerProfileResponse;
import poomasi.domain.member.entity.Member;

public record CustomerResponse(
        Long id,
        String name,
        String email,
        String role,
        CustomerProfileResponse memberProfile
) {
    public static CustomerResponse fromEntity(Member member) {
        return new CustomerResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getRole().name(),
                member.getMemberProfile() != null ? CustomerProfileResponse.fromEntity(member.getMemberProfile()) : null
        );
    }
}