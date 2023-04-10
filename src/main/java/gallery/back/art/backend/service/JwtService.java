package gallery.back.art.backend.service;

import io.jsonwebtoken.Claims;

public interface JwtService {
    String getToken(String key, Object value);

    Claims getClaims(String token);

    boolean isValid(String token); // 토큰 문제 여부

    int getId(String token); // token에서 사용자 id 값 가져오기
}
