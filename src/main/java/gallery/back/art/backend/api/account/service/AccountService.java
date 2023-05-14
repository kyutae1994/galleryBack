package gallery.back.art.backend.api.account.service;

import gallery.back.art.backend.common.auth.JwtTokenProvider;
import gallery.back.art.backend.common.code.ErrorCode;
import gallery.back.art.backend.common.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final JwtTokenProvider jwtTokenProvider;

    public String login(String email) throws CustomException {

        // 검증된 인증 정보로 JWT 토큰 생성
        try{
            String token = jwtTokenProvider.createRefreshToken(email);
            return token;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SYSTEM_ERROR, this.getClass().getName());
        }
    }
}
