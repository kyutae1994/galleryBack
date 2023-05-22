package gallery.back.art.backend.api.item.controller;

import gallery.back.art.backend.api.item.dto.ItemTokenDto;
import gallery.back.art.backend.api.item.entity.Item;
import gallery.back.art.backend.api.item.repository.ItemRepository;
import gallery.back.art.backend.common.auth.JwtTokenProvider;
import gallery.back.art.backend.common.dto.BaseResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // TODO - refresh 토큰이 안들어옴 필터에
    
    @GetMapping("/api/items")
    public ResponseEntity getItems(HttpServletRequest req, HttpServletResponse resp) {
        String token = jwtTokenProvider.getAccessToken(req.getHeader("authorization"));
        if (!jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Long accountId = jwtTokenProvider.getAccountId(token);
        String accessToken = resp.getHeader("accessToken");
        String refreshToken = resp.getHeader("refreshToken");
        List<Item> items = itemRepository.findAll();
        ItemTokenDto dto = ItemTokenDto.builder()
                .userId(accountId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .itemList(items)
                .build();
        return ResponseEntity.ok(BaseResponseDto.of(dto));
    }

}
