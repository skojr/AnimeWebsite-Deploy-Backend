package com.example.websitebackend.service;

import com.example.websitebackend.model.CustomUser;
import com.example.websitebackend.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final UserRepository userRepository;
    @Value("${security.jwt.secret-key}")
    private String secretKey;
    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Extraction methods
    public String extractUsername(String token) {
        return this.extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return this.extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public String extractRole(String token) {
        return this.extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return (Claims)Jwts.parserBuilder().setSigningKey(this.getSignInKey()).build().parseClaimsJws(token).getBody();
    }

    private Date extractExpiration(String token) {
        return (Date)this.extractClaim(token, Claims::getExpiration);
    }

    // Generation methods
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        CustomUser user = (CustomUser)this.userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> {
            return new UsernameNotFoundException("User not found");
        });
        claims.put("userId", user.getId());
        claims.put("role", user.getRole());
        return this.generateToken(claims, userDetails);
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername()).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + this.jwtExpiration)).signWith(this.getSignInKey(), SignatureAlgorithm.HS256).compact();
    }

    // Miscellaneous Methods
    private Key getSignInKey() {
        byte[] keyBytes = (byte[])Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = this.extractUsername(token);
        return username.equals(userDetails.getUsername()) && !this.isTokenExpired(token);
    }

    public Long validateAndExtractUserId(HttpServletRequest request) throws JwtException {
        String token = extractJwtFromRequest(request);

        if (token == null || token.isEmpty()) {
            throw new JwtException("Missing or invalid JWT token");
        }

        try {
            return this.extractUserId(token); // Assuming `extractUserId` is implemented to parse the token
        } catch (Exception e) {
            throw new JwtException("Invalid JWT token: " + e.getMessage());
        }
    }

    public String extractJwtFromRequest(HttpServletRequest request) {
        // Retrieve JWT from cookies or headers
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null; // Return null if no token found
    }

    private boolean isTokenExpired(String token) {
        return this.extractExpiration(token).before(new Date());
    }
}
