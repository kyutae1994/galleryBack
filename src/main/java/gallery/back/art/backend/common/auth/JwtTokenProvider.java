package gallery.back.art.backend.common.auth;

import gallery.back.art.backend.api.account.repository.AccountRepository;
import gallery.back.art.backend.common.util.CommonUtil;
import gallery.back.art.backend.config.CustomerDetails;
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
    private final AccountRepository accountRepository;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        byte[] secretByteKey = DatatypeConverter.parseBase64Binary(secretKey);
        this.key = Keys.hmacShaKeyFor(secretByteKey);
    }

    /**
     * JWT 토큰 발급
     * @param accountId - 유저 고유번호
     * @param accountLoginId - 유저 Login 시 사용된 Account Id
     * @param tokenTime - token 유지 시간
     */
    private String createToken(String accountId, String accountLoginId, String tokenTime){
        Date now = new Date();
        Claims claim = Jwts.claims().setSubject(accountLoginId);
        return Jwts.builder()
                .setClaims(claim)
                .setId(accountId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Long.parseLong(tokenTime)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 토큰 발급
     * @param accountLoginId - 유저 Login 시 사용된 Account Id
     */
    public String createAccessToken(String accountLoginId) {
        String accountId = accountRepository.findByEmail(accountLoginId).getId().toString();
        String tokenTime = String.valueOf(new Date(System.currentTimeMillis()+ 1000 * 60 * 30));
        return createToken(accountId, accountLoginId, tokenTime);
    }

    /**
     * JWT 토큰 발급
     * @param accountLoginId - 유저 Login 시 사용된 Account Id
     */
    public String createRefreshToken(String accountLoginId) {
        String accountId = accountRepository.findByEmail(accountLoginId).getId().toString();
        String tokenTime = String.valueOf(new Date(System.currentTimeMillis()+ 1000 * 60 * 60 * 36));
        return createToken(accountId, accountLoginId, tokenTime);
    }

    /**
     * 토큰에서 회원 고유번호 추출
     * @param token - Access Token
     */
    public Long getAccountId(String token) {
        String accessToken = getAccessToken(token);
        String accountId = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getId();
        return Long.parseLong(accountId);
    }

    /**
     * 토큰에서 회원 로그인 ID 추출 (email)
     * @param token - Access Token
     */
    public String getAccountLoginId(String token) {
        String accessToken = getAccessToken(token);
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getSubject();
    }

    public Authentication getAuthentication(String token){
        UserDetails userDetails = getJwtUser(token);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private CustomerDetails getJwtUser(String token) {
        CustomerDetails customerDetails = new CustomerDetails(getAccountLoginId(token));
        Long userId = getAccountId(token);
        List<GrantedAuthority> authorities = new ArrayList<>();
        // TODO - JPQL 적용시켜보기
        IndistAdminApiManagerDto dto = indistAdminApiManagerService.getManagerDetailById(userId);
        List<String> userAuthorityList = List.of(dto.getManagerAuthorityNames().split(","));
        for(String userAuthorityName : userAuthorityList ){
            // "ROLE_" 가 반드시 있어야 Security가 인식함
            String userRole = "ROLE_" + userAuthorityName;
            authorities.add(new SimpleGrantedAuthority(userRole));
        }
        customerDetails.setAuthorities(authorities);
        return customerDetails;
    }

    /**
     * Bearer를 제외한 순수 Access Token 확보
     * @param accessToken - Access Token
     */
    public String getAccessToken(String accessToken) {
        if (CommonUtil.isStringEmpty(accessToken)) return null;
        if (!accessToken.contains("Bearer ")) return accessToken;
        return accessToken.split("Bearer ")[1];
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
}

