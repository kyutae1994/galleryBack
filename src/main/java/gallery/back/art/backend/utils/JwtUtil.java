package gallery.back.art.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {

    public static String getUserName(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("userName", String.class);
    }

    public static boolean isExpired(String token, String secretKey) {
        // 만료가 지금보다 전이다.
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }

    public static String createJwt(String userName, String secretKey, Long expiredMs) {
        Claims claims = Jwts.claims();
        claims.put("userName", userName);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 언제 이슈
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs)) // 언제 끝나느지
                .signWith(SignatureAlgorithm.HS256, secretKey) // 뭘로 싸인
                .compact();
    }
}
