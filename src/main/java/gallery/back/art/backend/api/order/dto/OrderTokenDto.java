package gallery.back.art.backend.api.order.dto;

import gallery.back.art.backend.api.order.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class OrderTokenDto {

    private Long userId;
    private String accessToken;
    private String refreshToken;
    private List<Order> orders;

    @Builder
    public OrderTokenDto(Long userId, String accessToken, String refreshToken, List<Order> orders) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.orders = orders;
    }
}
