package arden.java.kudago.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "locations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String slug;

    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Event> events = new HashSet<>();

    public LocationMemento save() {
        return new LocationMemento(slug, name);
    }

    public void restore(LocationMemento memento) {
        this.slug = memento.slug();
        this.name = memento.name();
    }

    public record LocationMemento(
            String slug,
            String name
    ) {}
}
