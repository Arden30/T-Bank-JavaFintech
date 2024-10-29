package arden.java.kudago.model.specification;

import arden.java.kudago.model.Event;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(path = "name", spec = Equal.class),
        @Spec(path = "location.name", spec = Equal.class),
        @Spec(path = "date", params = {"fromDate", "toDate"}, spec = Between.class, config = "yyyy-MM-dd'T'HH:mm:ssXXX")
})
public interface EventSpecification extends Specification<Event> {
}
