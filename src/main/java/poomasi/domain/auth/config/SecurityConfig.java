package poomasi.domain.auth.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;
import poomasi.domain.auth.security.filter.JwtLogoutFilter;
import poomasi.domain.auth.security.filter.CustomUsernamePasswordAuthenticationFilter;
import poomasi.domain.auth.security.filter.JwtAuthenticationFilter;
import poomasi.domain.auth.security.handler.OAuth2FailureHandler;
import poomasi.domain.auth.security.handler.OAuth2SuccessHandler;
import poomasi.domain.auth.security.userdetail.OAuth2UserDetailServiceImpl;
import poomasi.domain.auth.security.userdetail.UserDetailsServiceImpl;
import poomasi.domain.auth.token.blacklist.service.BlacklistJpaService;
import poomasi.domain.auth.token.util.JwtUtil;
import poomasi.domain.auth.token.whitelist.service.RefreshTokenWhitelistService;


@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = false) // 인가 처리에 대한 annotation
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final MvcRequestMatcher.Builder mvc;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final UserDetailsServiceImpl userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;
    private final BlacklistJpaService blacklistService;
    private final RefreshTokenWhitelistService refreshTokenWhitelistService;

    @Autowired
    private OAuth2UserDetailServiceImpl oAuth2UserDetailServiceImpl;



    /*@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/error");
    }
*/
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Description("순서 : logout -> Oauth2 -> jwt -> login ")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        //기본 폼로그인 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);

        //http basic 비활성화
        http.httpBasic(AbstractHttpConfigurer::disable);

        //csrf 해제
        http.csrf(AbstractHttpConfigurer::disable);

        //cors 설정
        http.cors(cors -> cors
                .configurationSource(corsConfigurationSource));

        //세션 해제
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //기본 로그아웃 비활성화
        http.logout(AbstractHttpConfigurer::disable);


        http.authorizeHttpRequests((authorize) -> authorize


                // 기본 경로 및 테스트 경로
                // 인증 및 인가가 필요한 부분을 "authenticated"로 표시해주세요

                //건호 api
                .requestMatchers("/oauth2/authentication/kakao").authenticated()
                .requestMatchers("api/order/**").authenticated()
                .requestMatchers(HttpMethod.POST, "api/logout").authenticated()

                //진택 api
                .requestMatchers(HttpMethod.POST, "/api/member/update/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/member/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/reiusse").authenticated()

                //지민 api
                .requestMatchers(HttpMethod.GET, "/api/image/**").permitAll()

                //풍헌 api

                /*.requestMatchers(HttpMethod.POST, "/api/farm/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/review/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/image/**").permitAll()
                .requestMatchers("/api/member/sign-up", "/api/login", "/api/reissue", "/api/payment/**", "/api/order/**", "api/reservation/**", "/api/v1/farmer/reservations").permitAll()
                .requestMatchers("/api/need-auth/**").authenticated()
                .requestMatchers("/api/logout").permitAll()*/
                .anyRequest().permitAll()
        );

        //oauth2 filter
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(oAuth2UserDetailServiceImpl)
                        )
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                );

        //username password filter
        CustomUsernamePasswordAuthenticationFilter customUsernameFilter =
                new CustomUsernamePasswordAuthenticationFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenWhitelistService);
        customUsernameFilter.setFilterProcessesUrl("/api/login");
        http.addFilterAt(customUsernameFilter, UsernamePasswordAuthenticationFilter.class);

        //jwt filter
        http.addFilterAfter(new JwtAuthenticationFilter(jwtUtil, userDetailsService, blacklistService),
                OAuth2LoginAuthenticationFilter.class);

        //logout filter
        JwtLogoutFilter customLogoutFilter = new JwtLogoutFilter(jwtUtil, blacklistService, refreshTokenWhitelistService);
        http.addFilterAfter(customLogoutFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

}




