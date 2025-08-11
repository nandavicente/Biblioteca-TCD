package com.sistema.biblioteca.repositorio;

import com.sistema.biblioteca.entidade.Usuario;
import com.sistema.biblioteca.repositorio.ConexaoBanco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepositorioJDBC implements IRepositorio<Usuario> {

    @Override
    public void salvar(Usuario obj) {
        String sql = "INSERT INTO usuario (nome, matricula, email, na_lixeira) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, obj.getNome());
            stmt.setString(2, obj.getMatricula());
            stmt.setString(3, obj.getEmail());
            stmt.setBoolean(4, obj.isNaLixeira());
            stmt.executeUpdate();

            // pega o ID gerado e coloca no objeto
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                obj.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void atualizar(Usuario obj) {
        String sql = "UPDATE usuario SET nome=?, matricula=?, email=?, na_lixeira=? WHERE id=?";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, obj.getNome());
            stmt.setString(2, obj.getMatricula());
            stmt.setString(3, obj.getEmail());
            stmt.setBoolean(4, obj.isNaLixeira());
            stmt.setLong(5, obj.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void excluir(Long id) {
        String sql = "DELETE FROM usuario WHERE id=?";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Usuario buscarPorId(Long id) {
        String sql = "SELECT * FROM usuario WHERE id=? AND na_lixeira=false";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUsuario(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Usuario> buscarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE na_lixeira=false";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void moverParaLixeira(Usuario obj) {
        moverParaLixeiraPorId(obj.getId());
    }

    @Override
    public void moverParaLixeiraPorId(Long id) {
        String sql = "UPDATE usuario SET na_lixeira=true WHERE id=?";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void moverColecaoParaLixeira(List<Usuario> lista) {
        for (Usuario u : lista) {
            moverParaLixeiraPorId(u.getId());
        }
    }

    @Override
    public List<Usuario> recuperarTodosDaLixeira() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE na_lixeira=true";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Usuario recuperarDaLixeiraPorId(Long id) {
        String sql = "SELECT * FROM usuario WHERE id=? AND na_lixeira=true";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUsuario(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void excluirDefinitivo(Usuario obj) {
        excluirDefinitivoPorId(obj.getId());
    }

    @Override
    public void excluirDefinitivoPorId(Long id) {
        String sql = "DELETE FROM usuario WHERE id=? AND na_lixeira=true";
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
        String sql = "DELETE FROM usuario WHERE na_lixeira=true";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método auxiliar para mapear ResultSet -> Usuario
    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setNome(rs.getString("nome"));
        u.setMatricula(rs.getString("matricula"));
        u.setEmail(rs.getString("email"));
        u.setNaLixeira(rs.getBoolean("na_lixeira"));
        return u; // novo setter
    }
}
