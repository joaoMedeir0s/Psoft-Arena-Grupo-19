package _9.PsoftArena.controller.dto;

import _9.PsoftArena.domain.model.StatusTurma;
import _9.PsoftArena.domain.model.Turma;

public record TurmaResponseDTO(
        Long id,
        String codigo,
        String nome,
        String periodoLetivo,
        StatusTurma statusTurma,
        String professorMatricula,
        String professorNome
        ) {
 
    public static TurmaResponseDTO de(Turma turma) {
        return new TurmaResponseDTO(
                turma.getId(),
                turma.getCodigo(),
                turma.getNome(),
                turma.getPeriodoLetivo(),
                turma.getStatusTurma(),
                turma.getProfessor().getMatriculaDocente(),
                turma.getProfessor().getNome()
        );
    }
}