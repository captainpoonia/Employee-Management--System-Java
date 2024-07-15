package com.birdisystems.ems.config;

import com.birdisystems.ems.service.EmployeeService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@SuppressWarnings("deprecation")
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private EmployeeService customUserDetailsService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.token.prefix}")
    private String tokenPrefix;

    @Value("${jwt.header.string}")
    private String headerString;

    @Value("${jwt.expiration.time}")
    private long jwtExpirationTime;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            String token = extractJwtFromRequest(request);
            if (token != null && validateToken(token)) {
                String username = getUsernameFromToken(token);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException e) {
            logger.error("JWT authentication error", e);
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT authentication error");
            return;
        }
        chain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(headerString);
        if (bearerToken != null && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.replace(tokenPrefix, "");
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw e;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getExpiration();
        return expirationDate.before(new Date());
    }

    private String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
}
