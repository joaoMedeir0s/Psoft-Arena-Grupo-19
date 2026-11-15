package _9.PsoftArena.dto.response;

import _9.PsoftArena.domain.model.Matricula;
import _9.PsoftArena.domain.model.Professor;

public record ProfessorResponseDTO(
    Matricula matriculaDocente,
    String nome,
    String email
) {
    public static ProfessorResponseDTO de(Professor professor) {
        return new ProfessorResponseDTO(professor.getMatriculaDocente(),
                                        professor.getNome(),
                                        professor.getEmail());

    }
}
