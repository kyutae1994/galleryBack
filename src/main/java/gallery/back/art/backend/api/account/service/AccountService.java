package gallery.back.art.backend.api.account.service;

import gallery.back.art.backend.common.auth.JwtToken;
import gallery.back.art.backend.common.auth.JwtTokenProvider;
import gallery.back.art.backend.common.code.ErrorCode;
import gallery.back.art.backend.common.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtToken login(String email, String password) throws CustomException {
        // Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 검증된 인증 정보로 JWT 토큰 생성
        try{
            JwtToken token = jwtTokenProvider.createRefreshToken(email);
            return token;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SYSTEM_ERROR, this.getClass().getName());
        }
    }
}
