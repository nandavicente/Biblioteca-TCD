package com.sistema.biblioteca.app;

import com.sistema.biblioteca.entidade.*;
import com.sistema.biblioteca.repositorio.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Força a criação do banco e das tabelas
            ConexaoBanco.getConexao().close();
            System.out.println("Banco 'biblioteca' criado/validado com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Interface principal
        new BibliotecaAppGUI().setVisible(true);
    }
}
