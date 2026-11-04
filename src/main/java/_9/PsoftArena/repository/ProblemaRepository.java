package _9.PsoftArena.repository;

import _9.PsoftArena.domain.model.Problema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemaRepository extends JpaRepository<Problema, String> {
}