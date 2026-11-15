package _9.PsoftArena.dto.response;

import _9.PsoftArena.domain.model.Estudante;
import _9.PsoftArena.domain.model.Matricula;

public record EstudanteResponseDTO(
    Matricula matricula,
    String nome,
    String email
) {
    public static EstudanteResponseDTO de(Estudante e) {
        return new EstudanteResponseDTO(e.getMatricula(), e.getNome(), e.getEmail());
    }
}
