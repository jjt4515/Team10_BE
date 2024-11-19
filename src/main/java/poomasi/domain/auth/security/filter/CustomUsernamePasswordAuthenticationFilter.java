package poomasi.domain.auth.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.auth.token.util.JwtUtil;
import poomasi.domain.auth.token.whitelist.service.RefreshTokenWhitelistService;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenWhitelistService refreshTokenWhitelistService;

    @Description("인증 시도 메서드")
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{

        log.info("email - password 기반으로 인증을 시도 합니다 : CustomUsernamePasswordAuthenticationFilter");
        ObjectMapper loginRequestMapper = new ObjectMapper();
        String email = null;
        String password = null;

        try {
            BufferedReader reader = request.getReader();
            Map<String, String> credentials = loginRequestMapper.readValue(reader, Map.class);
            email = credentials.get("email");
            password = credentials.get("password");
            log.info("유저 정보를 출력합니다. email : "+ email + "password : " + password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        log.info("CustomUsernamePasswordAuthenticationFilter : authentication token 생성 완료");
        return this.authenticationManager.authenticate(authToken);

    }

    @Override
    @Description("로그인 성공 시, accessToken과 refreshToken 발급")
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        UserDetailsImpl customUserDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        String role = customUserDetails.getAuthority();
        Long memberId = customUserDetails.getMember().getId();

        String accessToken = jwtUtil.generateAccessTokenById(memberId);
        String refreshToken = jwtUtil.generateRefreshTokenById(memberId);

        log.info("username password 기반 로그인 성공 . cookie에 토큰을 넣어 발급합니다.");
        createCookie("refresh", refreshToken, response);
        response.setStatus(HttpStatus.OK.value());

        refreshTokenWhitelistService.putRefreshToken(refreshToken, memberId);

        response.setContentType("application/json");  // Content-Type 설정
        response.getWriter().write("{\"access\": \"" + accessToken + "\", \"refresh\": \"" + refreshToken + "\"}");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        log.info("usename password 기반 로그인 실패. ");
        response.setStatus(401);
    }

    private void createCookie(String key, String value, HttpServletResponse response){
        ResponseCookie cookie = ResponseCookie.from(key, value)
                .path("/")
                .sameSite("None")
                .httpOnly(true)
                .secure(true)
                .domain("poomasi.shop")
                .maxAge(60*60*24*7)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

}
