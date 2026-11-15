package _9.PsoftArena.dto.response;

import _9.PsoftArena.domain.model.StatusTurma;
import _9.PsoftArena.domain.model.Turma;

public record TurmaResponseDTO(
        String nome,
        Long codigo,
        String periodoLetivo,
        StatusTurma statusTurma,
        String professorNome,
        String codigoMatriculaDocente
        ) {
 
    public static TurmaResponseDTO de(Turma turma) {
        return new TurmaResponseDTO(
                turma.getNome(),
                turma.getCodigo(),
                turma.getPeriodoLetivo(),
                turma.getStatusTurma(),
                turma.getProfessor().getNome(),
                turma.getProfessor().getMatriculaDocente().getCodigo()
        );
    }
}