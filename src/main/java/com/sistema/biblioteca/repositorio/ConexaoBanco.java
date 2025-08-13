package com.sistema.biblioteca.repositorio;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class ConexaoBanco {

    private static String URL;
    private static String USUARIO;
    private static String SENHA;

    static {
        try (InputStream input = ConexaoBanco.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                throw new RuntimeException("Arquivo db.properties não encontrado no classpath");
            }
            prop.load(input);

            URL = prop.getProperty("db.url");
            USUARIO = prop.getProperty("db.user");
            SENHA = prop.getProperty("db.password");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar arquivo db.properties", e);
        }
    }

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
                        editora VARCHAR(255),
                        ano_publicacao INT,
                        na_lixeira BOOLEAN DEFAULT FALSE
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS emprestimo (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    id_livro INT NOT NULL,
                    id_usuario INT NOT NULL,
                    data_emprestimo DATE NOT NULL,
                    data_prevista DATE NOT NULL,
                    data_real DATE,
                    na_lixeira BOOLEAN DEFAULT FALSE
                )
            """);
        }

        conexao.setCatalog("biblioteca");
        return conexao;
    }
}
