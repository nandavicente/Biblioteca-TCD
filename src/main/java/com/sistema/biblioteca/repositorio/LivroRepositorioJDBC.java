package com.sistema.biblioteca.repositorio;

import com.sistema.biblioteca.entidade.Livro;
import com.sistema.biblioteca.repositorio.ConexaoBanco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroRepositorioJDBC {

    // Salvar novo livro
    public void salvar(Livro livro) {
        String sql = "INSERT INTO livro (id, titulo, autor, anoPublicacao, disponivel, naLixeira) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, livro.getId());
            stmt.setString(2, livro.getTitulo());
            stmt.setString(3, livro.getAutor());
            stmt.setInt(4, livro.getAnoPublicacao());
            stmt.setBoolean(6, livro.isNaLixeira());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Atualizar livro existente
    public void atualizar(Livro livro) {
        String sql = "UPDATE livro SET titulo = ?, autor = ?, anoPublicacao = ?, disponivel = ?, naLixeira = ? WHERE id = ?";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setInt(3, livro.getAnoPublicacao());
            stmt.setBoolean(4, livro.isDisponivel());
            stmt.setBoolean(5, livro.isNaLixeira());
            stmt.setLong(6, livro.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Excluir livro (exclusão lógica: movendo para lixeira)
    public void excluir(Long id) {
        moverParaLixeiraPorId(id);
    }

    // Buscar livro por id (apenas ativos, não na lixeira)
    public Livro buscarPorId(Long id) {
        String sql = "SELECT * FROM livro WHERE id = ? AND naLixeira = false";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearLivro(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Listar todos livros ativos (não na lixeira)
    public List<Livro> buscarTodos() {
        String sql = "SELECT * FROM livro WHERE naLixeira = false";
        List<Livro> livros = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                livros.add(mapearLivro(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livros;
    }

    // -------- Operações da Lixeira --------

    public void moverParaLixeira(Livro livro) {
        livro.setNaLixeira(true);
        atualizar(livro);
    }

    public void moverParaLixeiraPorId(Long id) {
        Livro livro = buscarPorId(id);
        if (livro != null) {
            moverParaLixeira(livro);
        }
    }

    public void moverColecaoParaLixeira(List<Livro> lista) {
        for (Livro livro : lista) {
            moverParaLixeira(livro);
        }
    }

    public List<Livro> recuperarTodosDaLixeira() {
        String sql = "SELECT * FROM livro WHERE naLixeira = true";
        List<Livro> livros = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                livros.add(mapearLivro(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livros;
    }

    public Livro recuperarDaLixeiraPorId(Long id) {
        String sql = "SELECT * FROM livro WHERE id = ? AND naLixeira = true";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearLivro(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Excluir definitivamente um livro (remoção física)
    public void excluirDefinitivo(Livro livro) {
        String sql = "DELETE FROM livro WHERE id = ?";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, livro.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluirDefinitivoPorId(Long id) {
        Livro livro = recuperarDaLixeiraPorId(id);
        if (livro != null) {
            excluirDefinitivo(livro);
        }
    }

    public void esvaziarLixeira() {
        String sql = "DELETE FROM livro WHERE naLixeira = true";
        try (Connection conn = ConexaoBD.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // mapear ResultSet para Livro
    private Livro mapearLivro(ResultSet rs) throws SQLException {
        Livro livro = new Livro();
        livro.setId(rs.getLong("id"));
        livro.setTitulo(rs.getString("titulo"));
        livro.setAutor(rs.getString("autor"));
        livro.setAnoPublicacao(rs.getInt("anoPublicacao"));
        livro.setDisponivel(rs.getBoolean("disponivel"));
        livro.setNaLixeira(rs.getBoolean("naLixeira"));
        return livro;
    }
}
