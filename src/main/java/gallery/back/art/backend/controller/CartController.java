package gallery.back.art.backend.controller;

import gallery.back.art.backend.entity.Cart;
import gallery.back.art.backend.entity.Item;
import gallery.back.art.backend.repository.CartRepository;
import gallery.back.art.backend.repository.ItemRepository;
import gallery.back.art.backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final JwtService jwtService;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    // 카트 정보 조회
    @GetMapping("/api/cart/items")
    public ResponseEntity getCartItems(@CookieValue(value = "token", required = false) String token) {
        if (!jwtService.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        int memberId = jwtService.getId(token);
        List<Cart> carts = cartRepository.findByMemberId(memberId);
        List<Integer> itemIds = carts.stream().map(Cart::getItemId).toList();
        List<Item> items = itemRepository.findByIdIn(itemIds);

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    // 장바구니를 담았을 때
    @PostMapping("/api/cart/items/{itemId}")
    public ResponseEntity pushCartItem(@PathVariable("itemId") int itemId, @CookieValue(value = "token", required = false) String token) {
        if (!jwtService.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        int memberId = jwtService.getId(token);
        Cart cart = cartRepository.findByMemberIdAndItemId(memberId, itemId);

        if (cart == null) { // 아직 cart에 안담았으면 추가
            Cart newCart = new Cart();
            newCart.setMemberId(memberId);
            newCart.setItemId(itemId);
            cartRepository.save(newCart);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
