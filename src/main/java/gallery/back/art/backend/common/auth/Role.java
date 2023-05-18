package gallery.back.art.backend.common.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("ROLE_ADMIN", "관리자"), USER("ROLE_USER", "일반 사용자"),
    PRODUCT("ROLE_PRODUCT", "상품");

    private final String key;
    private final String title;
}
