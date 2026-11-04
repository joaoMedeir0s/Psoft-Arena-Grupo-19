package _9.PsoftArena.domain.model;

public class Estudante {
    private String matricula; // Chave única (US2)
    private String nome;
    private String email;

    public Estudante(String matricula, String nome, String email) {
        this.matricula = matricula;
        this.nome = nome;
        this.email = email;
    }

    // Getters e Setters
    public String getMatricula() { return matricula; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
