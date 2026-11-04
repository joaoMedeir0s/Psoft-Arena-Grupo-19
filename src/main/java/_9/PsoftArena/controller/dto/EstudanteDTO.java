package _9.PsoftArena.controller.dto;

import _9.PsoftArena.domain.model.Estudante;

public class EstudanteDTO {
    private String matricula;
    private String nome;
    private String email;

    public EstudanteDTO() {}

    public EstudanteDTO(Estudante estudante) {
        this.matricula = estudante.getMatricula();
        this.nome = estudante.getNome();
        this.email = estudante.getEmail();
    }

    // Getters e Setters
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}