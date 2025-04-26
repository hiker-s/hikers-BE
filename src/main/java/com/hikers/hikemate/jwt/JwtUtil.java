package com.hikers.hikemate.jwt;

import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.service.CourseService;
import com.hikers.hikemate.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private static final String SECRET = "yourVeryLongSuperSecretKeyThatIsAtLeast32Charsssss";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private final UserService userService;


    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12)) // 12시간
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String extractUserId(String token) {
        String trimmedToken = token.trim();

        if (trimmedToken.startsWith("Bearer ")) {
            trimmedToken = trimmedToken.substring(7);  // "Bearer " 길이만큼 자름
        }

        System.out.println("토큰 확인: " + token);  // 디버깅용
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(trimmedToken)
                .getBody();
        return claims.getSubject();
    }

    // 사용자 정보를 토큰에서 추출하는 메소드
    public User getUserFromToken(String token) {
        String userId;
        String pureToken;
        if (token.startsWith("Bearer ")) {
            pureToken = token.substring(7).trim();
            try {
                userId = this.extractUserId(pureToken);
            } catch (Exception e) {
                throw new IllegalArgumentException("유효하지 않은 토큰 입니다.");
            }
        } else {
            throw new IllegalArgumentException("유효하지 않은 토큰 형식입니다.");
        }

        return userService.findUserByUserId(userId);
    }
}
