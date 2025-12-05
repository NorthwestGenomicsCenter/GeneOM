package org.uw.nwgc.multiomics.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.uw.nwgc.multiomics.core.user.UserService;
import org.uw.nwgc.multiomics.filter.JwtTokenFilter;
import org.uw.nwgc.multiomics.util.JwtUtil;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserService userService;
    private final JwtUtil jwtTokenUtil;
    private final AppConfig appConfig;

    public SecurityConfig(
            UserService userService,
            JwtUtil jwtTokenUtil,
            AppConfig appConfig
    ) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.appConfig = appConfig;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String base = appConfig.getServletPath();

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(
                        authorizeHttp -> {
                            authorizeHttp.requestMatchers(PathPatternRequestMatcher.withDefaults().matcher("/")).permitAll();
                            authorizeHttp.requestMatchers(PathPatternRequestMatcher.withDefaults().matcher((base + "/auth/**"))).permitAll();
                            authorizeHttp.requestMatchers(PathPatternRequestMatcher.withDefaults().matcher((base + "/favicon.svg"))).permitAll();
                            authorizeHttp.requestMatchers(PathPatternRequestMatcher.withDefaults().matcher((base + "/css/*"))).permitAll();
                            authorizeHttp.requestMatchers(PathPatternRequestMatcher.withDefaults().matcher((base + "/error"))).permitAll();
                            authorizeHttp.anyRequest().authenticated();
                        }
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)

//                .oauth2Login(withDefaults())
//                .httpBasic(withDefaults())
                .build();
    }

    @Bean
    public JwtTokenFilter authTokenFilterBean() throws Exception {
        return new JwtTokenFilter(jwtTokenUtil, userService);
    }

}
