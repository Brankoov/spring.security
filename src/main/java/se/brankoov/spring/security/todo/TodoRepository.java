package se.brankoov.spring.security.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.brankoov.spring.security.user.CustomUser;

import java.util.List;
import java.util.UUID;

@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {

    // Spring Data JPA är smart!
    // Genom att döpa metoden så här fattar den: "Hämta alla Todos där User matchar inparametern"
    List<Todo> findAllByUser(CustomUser user);

    // Sortera dem så nyaste kommer först (frivilligt men snyggt)
    List<Todo> findAllByUserOrderByCreatedDateDesc(CustomUser user);
}