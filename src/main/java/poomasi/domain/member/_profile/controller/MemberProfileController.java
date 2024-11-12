package poomasi.domain.member._profile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poomasi.domain.member._profile.service.MemberProfileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/member/profile")
public class MemberProfileController {

    private final MemberProfileService memberProfileService;
}
