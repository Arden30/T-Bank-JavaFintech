package arden.java.kudago.repository;

import arden.java.kudago.model.Event;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAll(Specification<Event> spec);

    static Specification<Event> buildSpecification(String name, String location, OffsetDateTime fromDate, OffsetDateTime toDate) {
        List<Specification<Event>> specs = new ArrayList<>();
        if (name != null) {
            specs.add((Specification<Event>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.<String>get("name"), name));
        }

        if (location != null) {
            specs.add((Specification<Event>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.<String>get("location").get("name"), location));
        }

        if (fromDate != null && toDate != null) {
            specs.add((Specification<Event>) (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("date"), fromDate, toDate));
        }

        return specs.stream().reduce(Specification::and).orElse(null);
    }
}
