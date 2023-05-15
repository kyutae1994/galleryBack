package gallery.back.art.backend.api.item.repository;

import gallery.back.art.backend.api.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByIdIn(List<Long> ids);
}
