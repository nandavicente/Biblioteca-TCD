package com.sistema.biblioteca.app;

import com.sistema.biblioteca.entidade.*;
import com.sistema.biblioteca.repositorio.*;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Criar repositórios
        LivroRepositorio livroRepo = new LivroRepositorio();
        UsuarioRepositorio usuarioRepo = new UsuarioRepositorio();
        EmprestimoRepositorio emprestimoRepo = new EmprestimoRepositorio();

        // Criar algumas entidades
        Livro livro1 = new Livro(1L, "1984", "George Orwell", 1949, true);
        Livro livro2 = new Livro(2L, "Dom Casmurro", "Machado de Assis", 1899, true);
        Livro livro3 = new Livro(3L, "Dom Casmurro", "Machado de Assis", 1899, true);

        Usuario usuario1 = new Usuario(1L, "Aline", "aline@");
        Usuario usuario2 = new Usuario(2L, "João Vitor", "joao@");
        Usuario usuario3 = new Usuario(3L, "Maria Fernanda", "mfov@");

        Emprestimo emprestimo1 = new Emprestimo(1L, livro1.getId(), usuario1.getId(), LocalDate.now(), null);

        // Salvar no repositório
        livroRepo.salvar(livro1);
        livroRepo.salvar(livro2);
        livroRepo.salvar(livro3);

        usuarioRepo.salvar(usuario1);
        usuarioRepo.salvar(usuario2);
        usuarioRepo.salvar(usuario3);

        emprestimoRepo.salvar(emprestimo1);

        // Buscar e exibir livros
        System.out.println("Livros ativos:");
        for (Livro l : livroRepo.buscarTodos()) {
            System.out.println(" - " + l.getTitulo());
        }

        // Mover livro para lixeira
        livroRepo.moverParaLixeiraPorId(2L);

        System.out.println("\nLivros após mover para lixeira o livro 2:");
        for (Livro l : livroRepo.buscarTodos()) {
            System.out.println(" - " + l.getTitulo());
        }

        System.out.println("\nLivros na lixeira:");
        for (Livro l : livroRepo.recuperarTodosDaLixeira()) {
            System.out.println(" - " + l.getTitulo());
        }

        // Excluir definitivo livro 2
        livroRepo.excluirDefinitivoPorId(2L);

        System.out.println("\nLivros na lixeira após exclusão definitiva:");
        for (Livro l : livroRepo.recuperarTodosDaLixeira()) {
            System.out.println(" - " + l.getTitulo());
        }

        // Esvaziar lixeira (se houvessem mais)
        livroRepo.esvaziarLixeira();

        System.out.println("\nLivros após esvaziar lixeira:");
        for (Livro l : livroRepo.buscarTodos()) {
            System.out.println(" - " + l.getTitulo());
        }
    }
}
