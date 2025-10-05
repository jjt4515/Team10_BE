package poomasi.domain.auth.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.auth.token.blacklist.service.BlacklistJpaService;
import poomasi.domain.auth.token.blacklist.service.AccessTokenBlacklistService;
import poomasi.domain.auth.token.util.JwtUtil;

import java.io.IOException;
import java.io.PrintWriter;

@Description("access token을 검증하는 필터")
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AccessTokenBlacklistService accessTokenBlacklistService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("[JwtAuthenticationFilter] - jwt 인증 필터입니다");
        long start = System.currentTimeMillis();

        String requestHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = null;

        String requestUri = request.getRequestURI();
        if ("/oauth2/authentication/kakao".equals(requestUri)) {
            log.info("[JwtAuthenticationFilter] - 카카오 인증 요청이므로 필터를 통과합니다.");
            // 요청을 그대로 다음 필터로 넘김
            filterChain.doFilter(request, response);
            return;
        }

        if("/api/login".equals(requestUri)) {
            log.info("[JwtAuthenticationFilter] - 로그인 요청이므로 필터를 통과합니다.");
            // 요청을 그대로 다음 필터로 넘김
            filterChain.doFilter(request, response);
            return;
        }


        if (requestHeader == null || !requestHeader.startsWith("Bearer ")) {
            log.info("[JwtAuthenticationFilter] : access token을 header로 갖지 않았으므로 다음 필터로 이동합니다");
            filterChain.doFilter(request, response);
            return;
        }else{ //존재하고 Bearer로 존재한다면
            log.info("[JwtAuthenticationFilter] : access token 추출하기");
            accessToken = requestHeader.substring(7);
        }

        log.info("[JwtAuthenticationFilter] : access token 추출 완료: " + accessToken);

        if (accessToken == null) {
            log.info("access token이 존재하지 않아서 다음 filter로 넘어갑니다.");
            filterChain.doFilter(request, response);
            return;
        }

        if(accessTokenBlacklistService.hasAccessToken(accessToken)){
            log.info("블랙리스트에 있는 토큰입니다.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"" + "token is in Blacklist");
            return;
        }

        // 유효성 검사
        if(!jwtUtil.validateAccessToken(accessToken)) {
            log.warn("JWT 필터 - [인증 실패] - 위조된 토큰입니다.");
            PrintWriter writer = response.getWriter();
            writer.print("위조된 토큰입니다.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 만료 검사
        if(jwtUtil.isTokenExpired(accessToken)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"" + "token is expired");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("토큰 검증 완료");
        String username = jwtUtil.getEmailFromToken(accessToken);
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);

        // (ID, password, auth)
        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetailsImpl, null, userDetailsImpl.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        long end = System.currentTimeMillis();
        log.info("jwt 인증 처리 시간: {} ms", end - start);

        filterChain.doFilter(request, response);
        return;
    }




}
