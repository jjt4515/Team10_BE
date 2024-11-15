package poomasi.domain.image.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import poomasi.domain.image.entity.ImageType;
import poomasi.domain.member.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class MemberProfileOwnerValidator implements ImageOwnerValidator{
    private final MemberRepository memberRepository;

    @Override
    public boolean supports(ImageType type) {
        return type == ImageType.MEMBER_PROFILE;
    }

    @Override
    public boolean validateOwner(Long memberId, Long referenceId) {
        return memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .filter(member -> member.getMemberProfile().getId().equals(referenceId))
                .isPresent();
    }
}
