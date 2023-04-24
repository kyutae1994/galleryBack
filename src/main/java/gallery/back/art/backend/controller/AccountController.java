package gallery.back.art.backend.controller;

import gallery.back.art.backend.configuration.JwtTokenProvider;
import gallery.back.art.backend.dto.JwtToken;
import gallery.back.art.backend.entity.Member;
import gallery.back.art.backend.entity.Role;
import gallery.back.art.backend.repository.AccountRepository;
import gallery.back.art.backend.service.AccountService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder encoder;

    @PostMapping("/login")
    public ResponseEntity<Integer> login(@RequestBody Map<String, String> params, HttpServletResponse res) {
//        Member member = accountRepository.findByEmailAndPassword(params.get("email"), params.get("password"));

//        if (member != null) {
        JwtToken token = accountService.login(params.get("email"), params.get("password"));
        Claims claims = jwtTokenProvider.parseClaims(token.toString());
        int id = Integer.parseInt(claims.get("id").toString());
        return ResponseEntity.ok(id);
//        }

//        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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
        Member member = new Member();

        member.setEmail(params.get("email"));
        member.setPassword(encoder.encode(params.get("password")));
        member.setName(params.get("name"));
        member.setBirthDate(params.get("year") + params.get("month") + params.get("day"));
        member.setRole(Role.USER);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:MM:ss");
        member.setCreateDate(LocalDateTime.now().format(formatter));

        accountRepository.save(member);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
