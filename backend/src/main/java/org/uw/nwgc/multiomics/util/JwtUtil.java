package org.uw.nwgc.multiomics.util;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.uw.nwgc.multiomics.core.user.AuthorityEnum;
import org.uw.nwgc.multiomics.core.user.CurrentUserResolver;
import org.uw.nwgc.multiomics.core.user.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
public class JwtUtil {

    public static final int ACCESS_EXPIRATION_SECONDS = 300;
    public static final int REFRESH_EXPIRATION_SECONDS = 60 * 60 * 2; // TWO HOURS
    private final CurrentUserResolver currentUserResolver;
    private final SecretKey secretKey;

    public JwtUtil(
            @Value("${JWT_SECRET:DEFAULT_DEVELOPMENT_KEY_CRASH_IN_PRODUCTION_DEFAULT_DEVELOPMENT_KEY_CRASH_IN_PRODUCTION}") String jwtSecret,
            CurrentUserResolver currentUserResolver) {
        this.secretKey = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA512");
        this.currentUserResolver = currentUserResolver;
    }

    public HttpHeaders tokenHeaders(Authentication authentication) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-access-token", generateJwtToken(authentication, ACCESS_EXPIRATION_SECONDS));
        headers.add("x-refresh-token", generateJwtToken(authentication, REFRESH_EXPIRATION_SECONDS));

        return headers;
    }

    public String generateJwtToken(Authentication authentication, int expiration) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User user = currentUserResolver.resolve(principal);

        Claims claims = Jwts.claims()
                .add("firstName", user.getFirstName())
                .add("lastName", user.getLastName())
                .add("permissions", user.getAuthorities()
                        .stream()
                        .map(AuthorityEnum::getAuthority)
                        .toList())
                .add("firstLogin", user.getFirstLogin())
                .build();

        return Jwts.builder()
                .subject((user.getUsername()))
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + expiration * 1000L))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        var claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseClaimsJws(token).getBody();

        List<String> roles = claims.get("permissions", List.class);

        return roles == null ? List.of() : roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(authToken)
                    .getPayload();
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
