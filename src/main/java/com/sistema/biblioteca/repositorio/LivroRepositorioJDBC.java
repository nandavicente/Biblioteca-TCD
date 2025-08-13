package com.sistema.biblioteca.repositorio;

import com.sistema.biblioteca.entidade.Livro;
import com.sistema.biblioteca.entidade.Usuario;
import com.sistema.biblioteca.repositorio.ConexaoBanco;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroRepositorioJDBC implements IRepositorio<Livro> {

    @Override
    public void salvar(Livro obj) {
        String sql = "INSERT INTO livro (titulo, autor, editora, ano_publicacao, na_lixeira) VALUES (?, ?, ?, ?, false)";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, obj.getTitulo());
            stmt.setString(2, obj.getAutor());
            stmt.setString(3, obj.getEditora());
            stmt.setInt(4, obj.getAnoPublicacao());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar livro", e);
        }
    }

    @Override
    public void atualizar(Livro obj) {
        String sql = "UPDATE livro SET titulo=?, autor=?, editora=?, ano_publicacao=? WHERE id=?";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, obj.getTitulo());
            stmt.setString(2, obj.getAutor());
            stmt.setString(3, obj.getEditora());
            stmt.setInt(4, obj.getAnoPublicacao());
            stmt.setLong(5, obj.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar livro", e);
        }
    }

    @Override
    public void excluir(Long id) {
        String sql = "DELETE FROM livro WHERE id=?";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Livro buscarPorId(Long id) {
        String sql = "SELECT * FROM livro WHERE id=?";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearLivro(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar livro por ID", e);
        }
        return null;
    }

    @Override
    public List<Livro> buscarTodos() {
        String sql = "SELECT * FROM livro WHERE na_lixeira=false";
        List<Livro> lista = new ArrayList<>();
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearLivro(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os livros", e);
        }
        return lista;
    }

    @Override
    public void moverParaLixeira(Livro obj) {
        moverParaLixeiraPorId(obj.getId());
    }

    @Override
    public void moverParaLixeiraPorId(Long id) {
        String sql = "UPDATE livro SET na_lixeira=true WHERE id=?";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void moverColecaoParaLixeira(List<Livro> entidades) {
        String sql = "UPDATE livro SET na_lixeira=true WHERE id=?";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Livro livro : entidades) {
                stmt.setLong(1, livro.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao mover coleção para a lixeira", e);
        }
    }

    @Override
    public List<Livro> recuperarTodosDaLixeira() {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT * FROM livro WHERE na_lixeira=true";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearLivro(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Livro recuperarDaLixeiraPorId(Long id) {
        String sql = "SELECT * FROM livro WHERE id=? AND na_lixeira=true";
        try (Connection conn = ConexaoBanco.getConexao();
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

    @Override
    public void restaurarPorId(Long id) {
        String sql = "UPDATE livro SET na_lixeira=false WHERE id=?";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao restaurar livro", e);
        }
    }

    public void restaurarTodosDaLixeira() {
        String sql = "UPDATE livro SET na_lixeira=false WHERE na_lixeira=true";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void excluirDefinitivo(Livro obj) {
        excluirDefinitivoPorId(obj.getId());
    }

    @Override
    public void excluirDefinitivoPorId(Long id) {
        String sql = "DELETE FROM livro WHERE id=? AND na_lixeira=true";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void esvaziarLixeira() {
        String sql = "DELETE FROM livro WHERE na_lixeira=true";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao esvaziar a lixeira", e);
        }
    }


    private Livro mapearLivro(ResultSet rs) throws SQLException {
        Livro livro = new Livro();
        livro.setId(rs.getLong("id"));
        livro.setTitulo(rs.getString("titulo"));
        livro.setAutor(rs.getString("autor"));
        livro.setEditora(rs.getString("editora"));
        livro.setAnoPublicacao(rs.getInt("ano_publicacao"));
        livro.setNaLixeira(rs.getBoolean("na_lixeira"));
        return livro;
    }
}
