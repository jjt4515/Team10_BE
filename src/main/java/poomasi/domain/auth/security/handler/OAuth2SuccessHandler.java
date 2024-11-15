package poomasi.domain.auth.security.handler;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.auth.token.refreshtoken.service.RefreshTokenService;
import poomasi.domain.auth.token.util.JwtUtil;
import poomasi.domain.member.entity.Member;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Value("${spring.security.redirect_url}")
    private String redirectUrl;

    @Description("TODO : Oauth2.0 로그인이 성공하면 server access, refresh token을 발급하는 메서드")
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info("[Oauth2 success handler] - OAuth2 로그인 성공적으로 완료되었습니다. access/refresh token 발급합니다.");

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        Member member = userDetailsImpl.getMember();
        Long memberId = member.getId();

        String accessToken = jwtUtil.generateAccessTokenById(memberId);
        String refreshToken = jwtUtil.generateRefreshTokenById(memberId);

        response.addCookie(createCookie("refresh", refreshToken));
        response.setStatus(HttpStatus.OK.value());

        //refresh token db에 저장
        refreshTokenService.putRefreshToken(refreshToken, memberId);
        response.sendRedirect(redirectUrl+"/access=" + accessToken);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}


