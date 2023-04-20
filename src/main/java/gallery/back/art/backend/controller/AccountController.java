package gallery.back.art.backend.controller;

import gallery.back.art.backend.entity.Member;
import gallery.back.art.backend.repository.MemberRepository;
import gallery.back.art.backend.service.AccountService;
import gallery.back.art.backend.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map<String, String> params, HttpServletResponse res) {
        Member member = memberRepository.findByEmailAndPassword(params.get("email"), params.get("password"));

        if (member != null) {
            int id = member.getId();
            String token = accountService.login(member.getEmail(), member.getPassword());

            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true); // javascript로는 접근 x  보안때문에(해커들이 자바스크립트 쿠키로 시도 가능해서) XSS와 같은 공격이 차단
            cookie.setPath("/"); // /는 최상위 경로 의미, 경로에 관계없이 쿠키를 받고자 한다면 루트로 설정하면 된다. 최상위 경로이기에 서버에서 문제없이 받음

            res.addCookie(cookie); // 클라이언트에 쿠키저장요청
            return new ResponseEntity<>(id, HttpStatus.OK);
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

//    @PostMapping("/login")
//    public ResponseEntity<String> login() {
//        return ResponseEntity.ok().body(accountService.login("", ""));
//    }

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletResponse res) {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        res.addCookie(cookie);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/check")
    public ResponseEntity check(@CookieValue(value = "token", required = false) String token) {
        Claims claims = jwtService.getClaims(token);

        if (claims != null) {
            int id = Integer.parseInt(claims.get("id").toString());
            return new ResponseEntity<>(id, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * 중복검사
     * @param params
     * @return check
     */
    @PostMapping("/duplicate")
    public ResponseEntity duplicate(@RequestBody Map<String, String> params) {
        Member member = memberRepository.findByEmail(params.get("email"));

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
        member.setPassword(params.get("password"));
        member.setName(params.get("name"));
        member.setBirthDate(params.get("year") + params.get("month") + params.get("day"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:MM:ss");
        member.setCreateDate(LocalDateTime.now().format(formatter));

        memberRepository.save(member);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
