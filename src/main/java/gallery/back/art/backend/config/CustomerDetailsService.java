package gallery.back.art.backend.config;

import gallery.back.art.backend.api.account.entity.Member;
import gallery.back.art.backend.api.account.repository.AccountRepository;
import gallery.back.art.backend.api.account.repository.Account_Authority_Repository;
import gallery.back.art.backend.api.account.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final Account_Authority_Repository account_authority_repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = accountRepository.findByEmail(username);
        if (member == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(member.getEmail(), member.getPassword(), getAuthorities(username));
    }

    public Collection<GrantedAuthority> getAuthorities(String username) {

        Member member = accountRepository.findByEmail(username);
        List<String> string_authorities = (List<String>) account_authority_repository.findAuthorityByMember(member.getId());

        if( string_authorities == null ) {
            log.info("## 해당 계정에 부여된 권한이 없습니다. ##");
            throw new UsernameNotFoundException(username);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        for (String authority : string_authorities) {
            authorities.add(new SimpleGrantedAuthority(authority));
        }

        return authorities;

    }
}