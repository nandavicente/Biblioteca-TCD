package com.sistema.biblioteca.repositorio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {

    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String USUARIO = "root";
    private static final String SENHA = "123";


    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
