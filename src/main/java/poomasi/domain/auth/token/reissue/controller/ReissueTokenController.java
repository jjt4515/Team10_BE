package poomasi.domain.auth.token.reissue.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import poomasi.domain.auth.token.reissue.dto.ReissueRequest;
import poomasi.domain.auth.token.reissue.dto.ReissueResponse;
import poomasi.domain.auth.token.reissue.service.ReissueTokenService;
import poomasi.domain.auth.token.util.JwtUtil;

@RestController
@RequiredArgsConstructor
public class ReissueTokenController {

    private final ReissueTokenService reissueTokenService;
    private final JwtUtil jwtUtil;

    @PostMapping("/api/reissue")
    public ResponseEntity<ReissueResponse> reissue(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                   @RequestBody ReissueRequest reissueRequest){

        String token = authorizationHeader.replace("Bearer ", "");

        Long memberId = jwtUtil.getIdFromToken(token);

        return ResponseEntity.ok(reissueTokenService.reissueToken(memberId, reissueRequest));
    }

}
