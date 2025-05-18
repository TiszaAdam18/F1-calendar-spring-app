package hu.f1calendar.repository;

import hu.f1calendar.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    List<Driver> findByNationality(String nationality);


    List<Driver> findByTeamId(Long teamId);


    Optional<Driver> findByPermanentNumber(Integer permanentNumber);
}

