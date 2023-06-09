package gallery.back.art.backend.common.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class JwtToken {

    private String userId;
    private String accessToken;
    private String refreshToken;
}