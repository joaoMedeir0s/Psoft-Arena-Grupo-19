package _9.PsoftArena.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import _9.PsoftArena.domain.model.Estrategia;
import _9.PsoftArena.domain.model.TipoEstrategia;

public interface EstrategiaRepository extends JpaRepository<Estrategia, Long> {
    
    List<Estrategia> findByTipo(TipoEstrategia tipo);

}
