package arden.java.kudago.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String slug;

    public CategoryMemento save() {
        return new CategoryMemento(slug, name);
    }

    public void restore(CategoryMemento memento) {
        this.name = memento.name();
        this.slug = memento.slug();
    }

    public record CategoryMemento(
            String slug,
            String name
    ) {}
}
