package com.book.gpt.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

@Component
@Lazy
public class JwtAuthorizationFilter extends UsernamePasswordAuthenticationFilter {
    @Value("${jwt.secret}")
    private String secretKey;
    @Autowired
    private UserDetailsService userDetailsService; // UserDetailsService 주입


    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            String token = extractTokenFromRequest(request);

            if (token != null) {
                UserDetails userDetails = validateToken(token);

                // 사용자의 권한 정보 가져오기
                Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

                // 권한 정보 출력
                authorities.forEach(authority -> {
                    System.out.println("Authority: " + authority.getAuthority());
                });

                JwtAuthentication jwtAuthentication = new JwtAuthentication(userDetails.getUsername());
                Authentication authentication = new UsernamePasswordAuthenticationToken(jwtAuthentication, null, jwtAuthentication.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            chain.doFilter(request, response);

        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        chain.doFilter(request, response);
    }

    public String generateToken(String username, String role) {
        Date now = new Date();

        // 1일 (24시간) 후의 날짜와 시간 계산
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR, 24); // 24시간 추가
        Date expirationDate = calendar.getTime();


        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)  // 사용자의 권한 정보를 토큰에
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key)
                .compact();
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + header);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    public UserDetails validateToken(String token) {
        // 토큰 검증 및 사용자 정보 추출
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

        Claims body = claimsJws.getBody();
        String username = body.getSubject();

        return userDetailsService.loadUserByUsername(username); // 사용자 정보를 UserDetailsService를 통해 가져옴
    }
    public String getIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

}