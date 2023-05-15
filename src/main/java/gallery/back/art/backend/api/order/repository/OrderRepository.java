package gallery.back.art.backend.api.order.repository;

import gallery.back.art.backend.api.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByMemberIdOrderByIdDesc(Long memberId);
}
