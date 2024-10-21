package arden.java.kudago.repository;

import arden.java.kudago.dto.response.places.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    @Query("FROM Location l JOIN FETCH l.events WHERE l.id = :id")
    Optional<Location> findById(Long id);
}
