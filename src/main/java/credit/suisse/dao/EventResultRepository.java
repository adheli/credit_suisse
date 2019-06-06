package credit.suisse.dao;

import credit.suisse.pojo.EventResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventResultRepository extends JpaRepository<EventResult, Long> {
}
