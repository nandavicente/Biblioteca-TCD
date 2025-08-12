package com.sistema.biblioteca.repositorio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoBanco {

    private static final String URL = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String SENHA = "SUA_SENHA";

    public static Connection getConexao() throws SQLException {
        Connection conexao = DriverManager.getConnection(URL, USUARIO, SENHA);

        try (Statement stmt = conexao.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS biblioteca");
            stmt.executeUpdate("USE biblioteca");
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS usuario (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            nome VARCHAR(100) NOT NULL,
                            matricula VARCHAR(50) NOT NULL UNIQUE,
                            email VARCHAR(100) NOT NULL,
                            na_lixeira BOOLEAN DEFAULT FALSE
                        )
                    """);
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS livro (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            titulo VARCHAR(255) NOT NULL,
                            autor VARCHAR(255) NOT NULL,
                            ano_publicacao INT,
                            na_lixeira BOOLEAN DEFAULT FALSE
                        )
                    """);
        }

        // Aqui já está no schema correto
        conexao.setCatalog("biblioteca");
        return conexao;
    }
}
