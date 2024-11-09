package poomasi.domain.image.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import poomasi.domain.member._profile.repository.MemberProfileRepository;

@Component
@RequiredArgsConstructor
public class MemberProfileOwnerValidator implements ImageOwnerValidator{
    private final MemberProfileRepository memberProfileRepository;

    @Override
    public boolean validateOwner(Long memberId, Long referenceId) {
        return memberProfileRepository.findById(referenceId)
                .filter(memberProfile -> memberProfile.getMember().getId().equals(memberId))
                .isPresent();
    }
}
