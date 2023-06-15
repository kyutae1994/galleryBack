package gallery.back.art.backend.api.cart.controller;

import gallery.back.art.backend.api.cart.dto.CartTokenDto;
import gallery.back.art.backend.common.auth.JwtTokenProvider;
import gallery.back.art.backend.api.cart.entity.Cart;
import gallery.back.art.backend.api.item.entity.Item;
import gallery.back.art.backend.api.cart.repository.CartRepository;
import gallery.back.art.backend.api.item.repository.ItemRepository;
import gallery.back.art.backend.common.dto.BaseResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "장바구니 API", description = "Swagger 장바구니 API")
public class CartController {

    private final JwtTokenProvider jwtTokenProvider;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    /**
     * 카트 정보 조회
     */
    @GetMapping("/items")
    @Operation(summary = "장바구니 조회", description = "장바구니 항목이 조회 됩니다.")
    public ResponseEntity getCartItems(HttpServletRequest req, HttpServletResponse resp) {
        String token = jwtTokenProvider.getAccessToken(req.getHeader("authorization"));
        if (!jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Long accountId = jwtTokenProvider.getAccountId(token);
        String accessToken = resp.getHeader("accessToken");
        String refreshToken = resp.getHeader("refreshToken");
        List<Cart> carts = cartRepository.findByMemberId(accountId);
        List<Long> itemIds = carts.stream().map(Cart::getItemId).toList();
        List<Item> items = itemRepository.findByIdIn(itemIds);
        CartTokenDto dto = CartTokenDto.builder()
                .userId(accountId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .itemList(items)
                .build();

        return ResponseEntity.ok(BaseResponseDto.of(dto));
    }

    /**
     * 장바구니를 담았을 때
     * @param itemId
     */
    @PostMapping("/items/{itemId}")
    @Operation(summary = "장바구니 등록", description = "장바구니 항목에 등록이 됩니다.")
    public ResponseEntity pushCartItem(@PathVariable("itemId") Long itemId, HttpServletRequest req, HttpServletResponse resp) {
        String token = jwtTokenProvider.getAccessToken(req.getHeader("authorization"));
        if (!jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Long accountId = jwtTokenProvider.getAccountId(token);
        String accessToken = resp.getHeader("accessToken");
        String refreshToken = resp.getHeader("refreshToken");

        Cart cart = cartRepository.findByMemberIdAndItemId(accountId, itemId);

        if (cart == null) { // 아직 cart에 안담았으면 추가
            Cart newCart = new Cart();
            newCart.setMemberId(accountId);
            newCart.setItemId(itemId);
            cartRepository.save(newCart);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 장바구니에서 항목 지우기
     * @param itemId
     * @return
     */
    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "장바구니 항목 삭제", description = "장바구니 항목이 삭제 됩니다.")
    public ResponseEntity removeCartItem(@PathVariable("itemId") Long itemId, HttpServletRequest req, HttpServletResponse resp) {

        String token = jwtTokenProvider.getAccessToken(req.getHeader("authorization"));
        if (!jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Long accountId = jwtTokenProvider.getAccountId(token);
        Cart cart = cartRepository.findByMemberIdAndItemId(accountId, itemId);

        cartRepository.delete(cart);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
