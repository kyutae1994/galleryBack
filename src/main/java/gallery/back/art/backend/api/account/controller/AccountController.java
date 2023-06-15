package gallery.back.art.backend.api.account.controller;

import gallery.back.art.backend.api.account.entity.Authority;
import gallery.back.art.backend.api.account.entity.MemberAuthorityMapping;
import gallery.back.art.backend.api.account.repository.AccountAuthorityRepository;
import gallery.back.art.backend.api.account.repository.AuthorityRepository;
import gallery.back.art.backend.api.account.entity.Member;
import gallery.back.art.backend.common.auth.JwtToken;
import gallery.back.art.backend.common.auth.Role;
import gallery.back.art.backend.api.account.repository.AccountRepository;
import gallery.back.art.backend.api.account.service.AccountService;
import gallery.back.art.backend.common.dto.BaseResponseDto;
import gallery.back.art.backend.common.error.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/account")
@Tag(name = "회원 API", description = "Swagger 회원 API")
public class AccountController {

    private final AuthorityRepository authorityRepository;
    private final AccountRepository accountRepository;
    private final AccountAuthorityRepository account_authority_repository;
    private final AccountService accountService;
    private final BCryptPasswordEncoder encoder;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인이 됩니다.")
    public ResponseEntity login(@RequestBody Map<String, String> params) throws CustomException {
        Member member = accountRepository.findByEmail(params.get("email"));

        if (member != null && encoder.matches(params.get("password"), member.getPassword())) {
            JwtToken token = accountService.login(params.get("email"));
            return ResponseEntity.ok(BaseResponseDto.of(token));
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃이 됩니다.")
    public ResponseEntity logout() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 중복검사
     * @param params
     * @return check
     */
    @PostMapping("/duplicate")
    @Operation(summary = "회원가입 중복검사", description = "아이디 중복검사를 합니다.")
    public ResponseEntity duplicate(@RequestBody Map<String, String> params) {
        Member member = accountRepository.findByEmail(params.get("email"));

        boolean check = false;
        if (member != null)
            check = true;
        return ResponseEntity.ok(BaseResponseDto.of(check));
    }

    /**
     * 회원가입
     * @param params
     * @return
     */
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "회원가입이 됩니다.")
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

            MemberAuthorityMapping memberAuthorityMapping = MemberAuthorityMapping.builder()
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
