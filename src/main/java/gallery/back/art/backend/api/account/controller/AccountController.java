package gallery.back.art.backend.api.account.controller;

import gallery.back.art.backend.api.account.entity.Authority;
import gallery.back.art.backend.api.account.repository.AuthorityRepository;
import gallery.back.art.backend.common.auth.JwtTokenProvider;
import gallery.back.art.backend.api.account.entity.Member;
import gallery.back.art.backend.common.auth.Role;
import gallery.back.art.backend.api.account.repository.AccountRepository;
import gallery.back.art.backend.api.account.service.AccountService;
import gallery.back.art.backend.common.code.ErrorCode;
import gallery.back.art.backend.common.dto.BaseResponseDto;
import gallery.back.art.backend.common.error.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final AuthorityRepository authorityRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder encoder;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map<String, String> params, HttpServletResponse res) throws CustomException {
        Member member = accountRepository.findByEmail(params.get("email"));

        if (member != null && encoder.matches(params.get("password"), member.getPassword())) {
            String token = accountService.login(params.get("email"), params.get("password"));
            return ResponseEntity.ok(BaseResponseDto.of(token));
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletResponse res) {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        res.addCookie(cookie);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @GetMapping("/check")
//    public ResponseEntity check(@CookieValue(value = "token", required = false) String token) {
//        Claims claims = jwtTokenProvider.parseClaims(token);
//
//        if (claims != null) {
//            int id = Integer.parseInt(claims.get("id").toString());
//            return new ResponseEntity<>(id, HttpStatus.OK);
//        }
//
//        return new ResponseEntity<>(null, HttpStatus.OK);
//    }

    /**
     * 중복검사
     * @param params
     * @return check
     */
    @PostMapping("/duplicate")
    public ResponseEntity duplicate(@RequestBody Map<String, String> params) {
        Member member = accountRepository.findByEmail(params.get("email"));

        boolean check = false;
        if (member != null)
            check = true;
        return new ResponseEntity<>(check, HttpStatus.OK);
    }

    /**
     * 회원가입
     * @param params
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody Map<String, String> params) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:MM:ss");

        Member member = Member.builder()
                        .email(params.get("email"))
                        .password(encoder.encode(params.get("password")))
                        .name(params.get("name"))
                        .birthDate(params.get("year") + params.get("month") + params.get("day"))
                        .createDate(LocalDateTime.now().format(formatter))
                        .build();

        Authority authority = Authority.builder()
                                .role(Role.USER)
                                .build();

        accountRepository.save(member);
        authorityRepository.save(authority);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
