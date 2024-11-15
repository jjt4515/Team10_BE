package poomasi.domain.auth.token.reissue.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import poomasi.domain.auth.token.reissue.dto.ReissueRequest;
import poomasi.domain.auth.token.reissue.dto.ReissueResponse;
import poomasi.domain.auth.token.reissue.service.ReissueTokenService;

@RestController
@RequiredArgsConstructor
public class ReissueTokenController {

    private final ReissueTokenService reissueTokenService;

    @PostMapping("/api/reissue")
    public ResponseEntity<ReissueResponse> reissue(@RequestBody ReissueRequest reissueRequest){
        return ResponseEntity.ok(reissueTokenService.reissueToken(reissueRequest));
    }

}
