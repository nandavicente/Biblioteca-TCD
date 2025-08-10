package com.sistema.biblioteca.entidade;

public class Usuario {
    // Atributos
    private final int id;
    private String nome;
    private String matricula;
    private String email;

    // Construtor
    public Usuario(int id, String nome, String matricula, String email) {
        this.id = id;
        this.nome = nome;
        this.matricula = matricula;
        this.email = email;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

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

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    // Metodo_para exibir
    public void exibirInfo() {
        System.out.println("Nome: " + nome);
        System.out.println("Matrícula: " + matricula);
        System.out.println("Email: " + email);
    }
}
