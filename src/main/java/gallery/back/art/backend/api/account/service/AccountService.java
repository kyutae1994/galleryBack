package gallery.back.art.backend.api.account.service;

import gallery.back.art.backend.api.account.repository.AccountRepository;
import gallery.back.art.backend.common.auth.JwtToken;
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
    private final AccountRepository accountRepository;

    public JwtToken login(String email) throws CustomException {

        String accountId = accountRepository.findByEmail(email).getId().toString();

        // 검증된 인증 정보로 JWT 토큰 생성
        try{
            String refreshToken = jwtTokenProvider.createRefreshToken(email);
            String accessToken = jwtTokenProvider.createAccessToken(email);

            JwtToken token = JwtToken.builder()
                    .userId(accountId)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            return token;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SYSTEM_ERROR, this.getClass().getName());
        }
    }
}
