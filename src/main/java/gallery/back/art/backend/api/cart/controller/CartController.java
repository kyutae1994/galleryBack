package gallery.back.art.backend.api.cart.controller;

import gallery.back.art.backend.api.cart.dto.CartTokenDto;
import gallery.back.art.backend.common.auth.JwtTokenProvider;
import gallery.back.art.backend.api.cart.entity.Cart;
import gallery.back.art.backend.api.item.entity.Item;
import gallery.back.art.backend.api.cart.repository.CartRepository;
import gallery.back.art.backend.api.item.repository.ItemRepository;
import gallery.back.art.backend.common.dto.BaseResponseDto;
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
public class CartController {

    private final JwtTokenProvider jwtTokenProvider;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    // 카트 정보 조회
    @GetMapping("/api/cart/items")
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

//        return new ResponseEntity<>(items, HttpStatus.OK);
        return ResponseEntity.ok(BaseResponseDto.of(dto));
    }

    // 장바구니를 담았을 때
    @PostMapping("/api/cart/items/{itemId}")
    public ResponseEntity pushCartItem(@PathVariable("itemId") Long itemId, @CookieValue(value = "token", required = false) String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Long memberId = jwtTokenProvider.getAccountId(token);
        Cart cart = cartRepository.findByMemberIdAndItemId(memberId, itemId);

        if (cart == null) { // 아직 cart에 안담았으면 추가
            Cart newCart = new Cart();
            newCart.setMemberId(memberId);
            newCart.setItemId(itemId);
            cartRepository.save(newCart);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/api/cart/items/{itemId}")
    public ResponseEntity removeCartItem(@PathVariable("itemId") Long itemId, @CookieValue(value = "token", required = false) String token) {

        if (!jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Long memberId = jwtTokenProvider.getAccountId(token);
        Cart cart = cartRepository.findByMemberIdAndItemId(memberId, itemId);

        cartRepository.delete(cart);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
