package gallery.back.art.backend.controller;

import gallery.back.art.backend.configuration.JwtTokenProvider;
import gallery.back.art.backend.dto.OrderDto;
import gallery.back.art.backend.entity.Order;
import gallery.back.art.backend.repository.CartRepository;
import gallery.back.art.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final JwtTokenProvider jwtTokenProvider;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    @GetMapping("/api/orders")
    public ResponseEntity getOrder(@CookieValue(value = "token", required = false) String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        int memberId = jwtTokenProvider.getId(token);
        List<Order> orders = orderRepository.findByMemberIdOrderByIdDesc(memberId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/api/orders")
    public ResponseEntity pushOrder(@RequestBody OrderDto orderDto, @CookieValue(value = "token", required = false) String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        int memberId = jwtTokenProvider.getId(token);
        Order newOrder = new Order();

        newOrder.setMemberId(memberId);
        newOrder.setName(orderDto.getName());
        newOrder.setAddress(orderDto.getAddress());
        newOrder.setPayment(orderDto.getPayment());
        newOrder.setCardNumber(orderDto.getCardNumber());
        newOrder.setItems(orderDto.getItems());

        orderRepository.save(newOrder);
        cartRepository.deleteByMemberId(memberId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
