package gallery.back.art.backend.api.account.controller;

import gallery.back.art.backend.api.account.entity.Authority;
import gallery.back.art.backend.api.account.entity.Member_Authority_Mapping;
import gallery.back.art.backend.api.account.repository.Account_Authority_Repository;
import gallery.back.art.backend.api.account.repository.AuthorityRepository;
import gallery.back.art.backend.common.auth.JwtToken;
import gallery.back.art.backend.api.account.entity.Member;
import gallery.back.art.backend.common.auth.Role;
import gallery.back.art.backend.api.account.repository.AccountRepository;
import gallery.back.art.backend.api.account.service.AccountService;
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
    private final Account_Authority_Repository account_authority_repository;
    private final AccountService accountService;
    private final BCryptPasswordEncoder encoder;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map<String, String> params, HttpServletResponse res) throws CustomException {
        Member member = accountRepository.findByEmail(params.get("email"));

        if (member != null && encoder.matches(params.get("password"), member.getPassword())) {
            JwtToken token = accountService.login(params.get("email"), params.get("password"));
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

        if (accountRepository.findByEmail(params.get("email")) == null) {
            Authority authority = authorityRepository.findIDByRole(Role.USER);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:MM:ss");

            Member member = Member.builder()
                    .email(params.get("email"))
                    .password(encoder.encode(params.get("password")))
                    .name(params.get("name"))
                    .birthDate(params.get("year") + params.get("month") + params.get("day"))
                    .createDate(LocalDateTime.now().format(formatter))
                    .build();

            Member_Authority_Mapping memberAuthorityMapping = Member_Authority_Mapping.builder()
                            .member(member)
                            .authority(authority)
                            .build();

            accountRepository.save(member);
            account_authority_repository.save(memberAuthorityMapping);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
