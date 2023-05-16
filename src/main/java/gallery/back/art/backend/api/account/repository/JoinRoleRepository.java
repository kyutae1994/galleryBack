package gallery.back.art.backend.api.account.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gallery.back.art.backend.api.account.entity.QMember;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JoinRoleRepository {

    private final EntityManager em;

    // SELECT `role`
    // FROM authority a, (SELECT authority_id as id FROM member_authority MA WHERE member_id = 4) ma
    // WHERE a.authority_id = ma.id
    public List<String> findRoleByAccountId(Long id) {

//        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
//        QMemberAuthorityMapping ma = QMemberAuthorityMapping.memberAuthorityMapping;
//        JPAQuery<Long> auth = queryFactory
//                .select(ma.authority.id)
//                .from(ma)
//                .where(ma.member.id.eq(id));
//
//        QAuthority a = QAuthority.authority;
//        queryFactory
//                .select(a.role)
//                .from(a)
//                .join(auth).on()
        return em.createQuery("SELECT a.role " +
                "FROM Authority a, (SELECT ma.authority.id as id FROM MemberAuthorityMapping ma WHERE ma.member.id = :id) t " +
                "WHERE a.id = t.id").setParameter("id", id).getResultList();
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
