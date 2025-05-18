package hu.f1calendar.repository;

import hu.f1calendar.model.Race;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;


@Repository
public interface RaceRepository extends JpaRepository<Race, Long> {

    List<Race> findByDateAfter(LocalDate date);

    List<Race> findByCircuitId(Long circuitId);

    List<Race> findByCircuitName(String circuitName);
}

