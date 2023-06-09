package gallery.back.art.backend.api.order.controller;

import gallery.back.art.backend.api.order.dto.OrderTokenDto;
import gallery.back.art.backend.common.auth.JwtTokenProvider;
import gallery.back.art.backend.api.order.dto.OrderDto;
import gallery.back.art.backend.api.order.entity.Order;
import gallery.back.art.backend.api.cart.repository.CartRepository;
import gallery.back.art.backend.api.order.repository.OrderRepository;
import gallery.back.art.backend.common.dto.BaseResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "주문 API", description = "Swagger 주문 API")
public class OrderController {

    private final JwtTokenProvider jwtTokenProvider;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    @GetMapping("/orders")
    @Operation(summary = "주문 조회", description = "주문했던 항목이 조회 됩니다.")
    public ResponseEntity getOrder(HttpServletRequest req, HttpServletResponse resp) {
        String token = jwtTokenProvider.getAccessToken(req.getHeader("authorization"));
        if (!jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Long accountId = jwtTokenProvider.getAccountId(token);
        String accessToken = resp.getHeader("accessToken");
        String refreshToken = resp.getHeader("refreshToken");
        List<Order> orders = orderRepository.findByMemberIdOrderByIdDesc(accountId);
        OrderTokenDto dto = OrderTokenDto.builder()
                .userId(accountId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .orders(orders)
                .build();

        return ResponseEntity.ok(BaseResponseDto.of(dto));
    }

    /**
     * 주문하기
     * @param orderDto
     * @param req
     * @return
     */
    @Transactional
    @PostMapping("/orders")
    @Operation(summary = "주문하기", description = "주문 됩니다.")
    public ResponseEntity pushOrder(@RequestBody OrderDto orderDto, HttpServletRequest req) {
        String token = jwtTokenProvider.getAccessToken(req.getHeader("authorization"));
        if (!jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Long memberId = jwtTokenProvider.getAccountId(token);
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
