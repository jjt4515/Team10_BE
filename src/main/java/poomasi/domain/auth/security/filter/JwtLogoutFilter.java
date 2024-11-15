

package poomasi.domain.auth.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.auth.token.blacklist.service.AccessTokenBlacklistService;
import poomasi.domain.auth.token.util.JwtUtil;
import poomasi.domain.auth.token.whitelist.service.RefreshTokenWhitelistService;
import poomasi.domain.member.entity.Member;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtLogoutFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AccessTokenBlacklistService accessTokenBlacklistService;
    private final RefreshTokenWhitelistService refreshTokenWhitelistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 로그아웃 URL이 아니면 필터 실행하지 않음
        if (!"/api/logout".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 추출
        String accessToken = extractJwtFromRequest(request);
        if (accessToken == null || !jwtUtil.validateToken(accessToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid token\"}");
            return;
        }

        // if(만료되었으면) -> response 만료됨
        if(jwtUtil.isTokenExpired(accessToken)){
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\": \"logout completes\"}");
            return;
        }

        // 블랙리스팅
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object impl = authentication.getPrincipal();
        Member member = ((UserDetailsImpl) impl).getMember();
        Long memberId = member.getId();

        if(accessTokenBlacklistService.hasAccessToken(accessToken)){
            log.info("jwt logout filter - access token이 블랙리스트에 있어요.");
        }else{
            log.info("jwt logout filter - access token이 블랙리스트에 없어요. 저장할게요");
            accessTokenBlacklistService.putAccessToken(accessToken, memberId);
        }

        //db refresh token 제거
        Cookie[] cookies = request.getCookies();
        String refreshToken = getRefreshToken(cookies);

        if(refreshToken!=null) {
            log.info("jwt logout filter - refresh token을 지웁니다");
            refreshTokenWhitelistService.removeMemberRefreshToken(memberId);
        }

        // 쿠키 삭제
        clearAuthCookie(response);
        // 세션 무효화
        request.getSession().invalidate();
        // 4. SecurityContext를 비워 인증 정보 해제
        SecurityContextHolder.clearContext();

        // 로그아웃 성공 응답 설정
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\": \"Logout successful\"}");
        return;
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void clearAuthCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String getRefreshToken(Cookie[] cookies) {

        if (cookies != null) {
            // 쿠키 목록을 순회하면서 "refresh" 쿠키를 찾습니다.
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    return cookie.getValue();  // 쿠키의 값 반환
                }
            }
        }
        return null;  // "refresh" 쿠키가 없다면 null 반환
    }

}
