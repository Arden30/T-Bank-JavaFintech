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
}
