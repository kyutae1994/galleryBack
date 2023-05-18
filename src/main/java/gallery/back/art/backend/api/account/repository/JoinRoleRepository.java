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

    // SELECT a.`role` FROM member_authority ma INNER join authority a WHERE ma.member_id = 4 AND ma.authority_id = a.authority_id
    public List<Role> findRoleByAccountId(Long id) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMemberAuthorityMapping memberAuthorityMapping = QMemberAuthorityMapping.memberAuthorityMapping;
        QAuthority authority = QAuthority.authority;
        return queryFactory
                .select(authority.role)
                .from(memberAuthorityMapping)
                .join(authority).on(memberAuthorityMapping.id.eq(authority.id)).fetchJoin()
                .fetch();

    }

    public String findIdByEmail(String loginId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember member = QMember.member;
        return queryFactory
                .select(member.id)
                .from(member)
                .where(member.email.eq(loginId)).fetch().get(0).toString();
    }
}
