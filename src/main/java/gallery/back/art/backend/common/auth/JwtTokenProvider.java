package gallery.back.art.backend.common.auth;

import gallery.back.art.backend.api.account.repository.AccountRepository;
import gallery.back.art.backend.api.account.repository.JoinRoleRepository;
import gallery.back.art.backend.common.util.CommonUtil;
import gallery.back.art.backend.config.CustomerDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final JoinRoleRepository joinRoleRepository;

    @Autowired
    public JwtTokenProvider(JoinRoleRepository joinRoleRepository) {
        this.joinRoleRepository = joinRoleRepository;
    }

    @Value("${jwt.secret}")
    private String JWT_SECRET_KEY;
    @Value("${jwt.accessTokenValidationSecond}")
    private String TOKEN_VALIDATION_SECOND;
    @Value("${jwt.refreshTokenValidationSecond}")
    private String REFRESH_TOKEN_VALIDATION_SECOND;

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
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY)
                .compact();
    }

    /**
     * JWT 토큰 발급
     * @param accountLoginId - 유저 Login 시 사용된 Account Id
     */
    public String createAccessToken(String accountLoginId) {
        String accountId = joinRoleRepository.findIdByEmail(accountLoginId);
        return createToken(accountId, accountLoginId, TOKEN_VALIDATION_SECOND);
    }

    /**
     * JWT 토큰 발급
     * @param accountLoginId - 유저 Login 시 사용된 Account Id
     */
    public String createRefreshToken(String accountLoginId) {
        String accountId = joinRoleRepository.findIdByEmail(accountLoginId);
        return createToken(accountId, accountLoginId, REFRESH_TOKEN_VALIDATION_SECOND);
    }

    /**
     * 토큰에서 회원 고유번호 추출
     * @param token - Access Token
     */
    public Long getAccountId(String token) {
        String accessToken = getAccessToken(token);
        String accountId = Jwts.parserBuilder().setSigningKey(JWT_SECRET_KEY).build().parseClaimsJws(accessToken).getBody().getId();
        return Long.parseLong(accountId);
    }

    /**
     * 토큰에서 회원 로그인 ID 추출 (email)
     * @param token - Access Token
     */
    public String getAccountLoginId(String token) {
        String accessToken = getAccessToken(token);
        String accountLoginId = Jwts.parser().setSigningKey(JWT_SECRET_KEY).parseClaimsJws(accessToken).getBody().getSubject();
        return accountLoginId;
    }

    /**
     * 유저 정보를 받아온다
     * @param token
     * @return Authentication
     */
    public Authentication getAuthentication(String token){
        UserDetails userDetails = getJwtUser(token);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /**
     * 토큰 정보에 권한을 조회에서 추가해준다.
     * @param token
     * @return CustomerDetails
     */
    private CustomerDetails getJwtUser(String token) {
        CustomerDetails customerDetails = new CustomerDetails(getAccountLoginId(token));
        Long userId = getAccountId(token);
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<Role> userAuthorityList = joinRoleRepository.findRoleByAccountId(userId);
        for(Role userAuthorityName : userAuthorityList ){
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

    /**
     * 토큰 유효값 체크
     * @param token
     * @return boolean
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(JWT_SECRET_KEY).build().parseClaimsJws(token);
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
}

