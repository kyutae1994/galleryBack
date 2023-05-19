package gallery.back.art.backend.api.account.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.sql.JPASQLQuery;
import gallery.back.art.backend.api.account.entity.QAuthority;
import gallery.back.art.backend.api.account.entity.QMember;
import gallery.back.art.backend.api.account.entity.QMemberAuthorityMapping;
import gallery.back.art.backend.common.auth.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JoinRoleRepository {

    @PersistenceContext
    private final EntityManager em;

    public List<Role> findRoleByAccountId(Long id) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMemberAuthorityMapping memberAuthorityMapping = QMemberAuthorityMapping.memberAuthorityMapping;
        QAuthority authority = QAuthority.authority;
        return queryFactory
                .select(authority.role)
                .from(memberAuthorityMapping)
                .join(authority).on(memberAuthorityMapping.authority.id.eq(authority.id)).fetchJoin()
                .where(memberAuthorityMapping.member.id.eq(id))
                .fetch();
    }

    // TODO - 맨 처음 플젝 킬때 로그 아웃 되있게 하기, 노션 querydsl 정리

    public String findIdByEmail(String loginId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember member = QMember.member;
        return queryFactory
                .select(member.id)
                .from(member)
                .where(member.email.eq(loginId)).fetch().get(0).toString();
    }
}
