package br.insper.prova.livro;


public class UsuarioDTO {
    private String nome;
    private String email;
    private String papel;

    // Getters e Setters
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPapel() {
        return papel;
    }
    public void setPapel(String papel) {
        this.papel = papel;
    }
}
