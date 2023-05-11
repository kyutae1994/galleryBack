package gallery.back.art.backend.common.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] secretByteKey = DatatypeConverter.parseBase64Binary(secretKey);
        this.key = Keys.hmacShaKeyFor(secretByteKey);
    }

    public JwtToken createAccessToken(String userLoginId, String refreshToken) {
        Date now = new Date();
        Claims claim = Jwts.claims().setSubject(userLoginId);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        claim.put("roles", authorities);

        String accessToken = Jwts.builder()
                .setClaims(claim)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+ 1000 * 60 * 30))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return JwtToken.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    public JwtToken createRefreshToken(String userLoginId) {
        Date now = new Date();
        Claims claim = Jwts.claims().setSubject(userLoginId);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        claim.put("roles", authorities);

        String accessToken = Jwts.builder()
                .setClaims(claim)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+ 1000 * 60 * 30))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        String refreshToken = Jwts.builder()
                .setClaims(claim)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+ 1000 * 60 * 60 * 36))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return JwtToken.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    public Authentication getAuthentication(String accessToken) {
        //토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("roles") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    //토큰에서 회원 정보 추출
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public int getId(String token) {
        Claims claims = this.parseClaims(token);

        if (claims != null) {
            return Integer.parseInt(claims.get("id").toString());
        }

        return 0;
    }
}

