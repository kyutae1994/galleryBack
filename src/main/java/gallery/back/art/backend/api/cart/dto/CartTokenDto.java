package gallery.back.art.backend.api.cart.dto;

import gallery.back.art.backend.api.item.entity.Item;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CartTokenDto {

    private Long userId;
    private String accessToken;
    private String refreshToken;
    List<Item> itemList;

    @Builder
    public CartTokenDto(Long userId, String accessToken, String refreshToken, List<Item> itemList) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.itemList = itemList;
    }
}
