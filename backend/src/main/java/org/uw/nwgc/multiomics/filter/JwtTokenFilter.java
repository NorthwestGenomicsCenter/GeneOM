package org.uw.nwgc.multiomics.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.uw.nwgc.multiomics.core.user.JwtUserDetails;
import org.uw.nwgc.multiomics.core.user.LazyUserDetails;
import org.uw.nwgc.multiomics.core.user.UserService;
import org.uw.nwgc.multiomics.util.JwtUtil;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Log4j2
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Autowired
    public JwtTokenFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException, IOException {
        try {
            String accessToken = parseJwt(request, "accessToken");
            String refreshToken = parseJwt(request, "refreshToken");

            if (accessToken != null && jwtUtil.validateJwtToken(accessToken)) {
                String username = jwtUtil.getUserNameFromJwtToken(accessToken);
                List<GrantedAuthority> authorities = jwtUtil.getAuthoritiesFromToken(accessToken);
                UserDetails userDetails = new JwtUserDetails(username, authorities);
                setAuthentication(request, userDetails);
            } else if (refreshToken != null && jwtUtil.validateJwtToken(refreshToken)) {
                String username = jwtUtil.getUserNameFromJwtToken(refreshToken);
                UserDetails userDetails = new LazyUserDetails(username, userService);
                UsernamePasswordAuthenticationToken authentication = setAuthentication(request, userDetails);

                HttpHeaders newHeaders = jwtUtil.tokenHeaders(authentication);
                addHeaderIfAbsent(response, "x-access-token", newHeaders);
                addHeaderIfAbsent(response, "x-refresh-token", newHeaders);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication", e);
            throw new BadCredentialsException("Cannot parse JWT token", e);
        }
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken setAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    private void addHeaderIfAbsent(HttpServletResponse response, String headerName, HttpHeaders headers) {
        if (response.getHeader(headerName) == null) {
            response.addHeader(headerName, Objects.requireNonNull(headers.get(headerName)).getFirst());
        }
    }

    private String parseJwt(HttpServletRequest request, String tokenName) {
        if (request.getCookies() == null) return null;

        // Check for the access token in the request
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(tokenName)) {
                return cookie.getValue();
            }
        }

        return null;
    }
}

//    private final JwtUtil jwtUtil;
//    private final UserService userService;
//
//    @Autowired
//    public JwtTokenFilter(JwtUtil jwtUtil, UserService userService) {
//        this.jwtUtil = jwtUtil;
//        this.userService = userService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, IOException {
//        try {
//            String accessToken = parseJwt(request, "accessToken");
//            String refreshToken = parseJwt(request, "refreshToken");
//            UserDetails userDetails;
//            if (accessToken != null && jwtUtil.validateJwtToken(accessToken)) {
//                String username = jwtUtil.getUserNameFromJwtToken(accessToken);
//                userDetails = userService.loadUserByUsername(username);
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
//                        userDetails.getAuthorities());
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            } else {
//                if (refreshToken != null && jwtUtil.validateJwtToken(refreshToken)) {
//                    String username = jwtUtil.getUserNameFromJwtToken(refreshToken);
//                    userDetails = userService.loadUserByUsername(username);
//                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
//                            userDetails.getAuthorities());
//                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//
//                    HttpHeaders newHeaders = jwtUtil.tokenHeaders(authentication);
//                    if (response.getHeader("x-access-token") == null) {
//                        response.addHeader("x-access-token", Objects.requireNonNull(newHeaders.get("x-access-token")).getFirst());
//                    }
//
//                    if (response.getHeader("x-refresh-token") == null) {
//                        response.addHeader("x-refresh-token", Objects.requireNonNull(newHeaders.get("x-refresh-token")).getFirst());
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error("Cannot set user authentication", e);
//            throw new BadCredentialsException("Cannot parse JWT token", e);
//        }
//        filterChain.doFilter(request, response);
//    }
//
//    private String parseJwt(HttpServletRequest request, String tokenName) {
//        if (request.getCookies() == null) return null;
//
//        // Check for the access token in the request
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//            if (cookie.getName().equals(tokenName)) {
//                return cookie.getValue();
//            }
//        }
//
//        return null;
//    }

