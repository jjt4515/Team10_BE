package poomasi.domain.auth.token.reissue.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.auth.token.reissue.dto.ReissueRequest;
import poomasi.domain.auth.token.reissue.dto.ReissueResponse;
import poomasi.domain.auth.token.reissue.service.ReissueTokenService;
import poomasi.domain.member.entity.Member;

@RestController
public class ReissueTokenController {

    @Autowired
    private ReissueTokenService reissueTokenService;

    @Secured({"ROLE_FARMER", "ROLE_CUSTOMER"})
    @PostMapping("/api/reissue")
    public ResponseEntity<ReissueResponse> reissue(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ReissueRequest reissueRequest){
        Member member = userDetails.getMember();
        return ResponseEntity.ok(reissueTokenService.reissueToken(member.getId(), reissueRequest));
    }

}
