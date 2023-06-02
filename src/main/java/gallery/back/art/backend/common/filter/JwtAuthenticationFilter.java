package gallery.back.art.backend.common.filter;

import gallery.back.art.backend.common.auth.JwtTokenProvider;
import gallery.back.art.backend.common.code.ErrorCode;
import gallery.back.art.backend.common.error.CustomException;
import gallery.back.art.backend.common.util.CommonUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
        log.info("==================doFilter================================");
        String uri = req.getRequestURI();
        log.info("uri = " + uri);

        Enumeration<String> headerNames = req.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name= headerNames.nextElement();
            log.info("Header Name : " + name + ", Value : " + req.getHeader(name));
        }

        try {
            String acToken = jwtTokenProvider.getAccessToken(req.getHeader("authorization"));
            resp.setHeader("accessToken", acToken);
            String reToken = req.getHeader("refreshtoken");
            log.info("acToken ===="+acToken);
            log.info("reToken ===="+reToken);
            tokenCheck(resp, acToken, reToken);
            filterChain.doFilter(req, resp);
        } catch (Exception e) {
            // 인증관련 로직에서 Error가 발생했으므로 401 status return
            log.error("에러발생");
            e.printStackTrace();
        }
    }

    private void tokenCheck(HttpServletResponse resp, String acToken, String reToken) throws CustomException {
        // Access Token Check - 없으면 그냥 PASS 시키고 어차피 Security Config에서 토큰이 없어 장애가 발생할 것이므로 따로 체크는 안함
        if(!CommonUtil.isStringEmpty(acToken)){
            // Access Token이 만료됐을 경우 Refresh Token Check
            if(!jwtTokenProvider.validateToken(acToken)){
                if(CommonUtil.isStringEmpty(reToken) || !jwtTokenProvider.validateToken(reToken)) {
                    throw new CustomException(ErrorCode.SYSTEM_FORBIDDEN_ERROR, this.getClass().getName());
                }else {
                    tokenValidationCheck(resp, acToken, reToken);
                }
            }
            String newAcToken = resp.getHeader("accessToken");
            if(!jwtTokenProvider.validateToken(acToken)){
                Authentication authentication = jwtTokenProvider.getAuthentication(newAcToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                Authentication authentication = jwtTokenProvider.getAuthentication(acToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }

    private void tokenValidationCheck(HttpServletResponse resp, String acToken, String reToken){
        // Access Token이 만료된 경우 RefreshToken 검사
        if(!jwtTokenProvider.validateToken(acToken)){
            if(!jwtTokenProvider.validateToken(reToken)){
                log.error("Access Token 만료 후 Refresh Token이 만료 > 401 status return", 401, resp);
                throw new IllegalArgumentException("만료된 토큰");
            }
            // Refresh Token이 살아있는 경우 모든 Token 갱신
            String accountLoginId = jwtTokenProvider.getAccountLoginId(reToken);
            tokenRefresh(resp, accountLoginId);
        }
    }

    private void tokenRefresh(HttpServletResponse resp, String accountLoginId) {
        String accessToken = jwtTokenProvider.createAccessToken(accountLoginId);
        String refreshToken = jwtTokenProvider.createRefreshToken(accountLoginId);
        resp.setHeader("accessToken", accessToken);
        resp.setHeader("refreshToken", refreshToken);
    }
}
