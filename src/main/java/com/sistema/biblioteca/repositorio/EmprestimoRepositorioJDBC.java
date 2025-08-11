package com.sistema.biblioteca.repositorio;

import com.sistema.biblioteca.entidade.Emprestimo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoRepositorioJDBC implements IRepositorio<Emprestimo> {

    @Override
    public void salvar(Emprestimo obj) {
        String sql = "INSERT INTO emprestimo (id_livro, id_usuario, data_emprestimo, data_prevista, data_real, na_lixeira) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, obj.getIdLivro());
            stmt.setInt(2, obj.getIdUsuario());
            stmt.setDate(3, Date.valueOf(obj.getDataEmprestimo()));
            stmt.setDate(4, Date.valueOf(obj.getDataPrevista()));
            
            if (obj.getDataReal() != null) {
                stmt.setDate(5, Date.valueOf(obj.getDataReal()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setBoolean(6, obj.isNaLixeira());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                obj.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void atualizar(Emprestimo obj) {
        String sql = "UPDATE emprestimo SET id_livro=?, id_usuario=?, data_emprestimo=?, data_prevista=?, data_real=?, na_lixeira=? WHERE id=?";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, obj.getIdLivro());
            stmt.setInt(2, obj.getIdUsuario());
            stmt.setDate(3, Date.valueOf(obj.getDataEmprestimo()));
            stmt.setDate(4, Date.valueOf(obj.getDataPrevista()));

            if (obj.getDataReal() != null) {
                stmt.setDate(5, Date.valueOf(obj.getDataReal()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.setBoolean(6, obj.isNaLixeira());
            stmt.setLong(7, obj.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void excluir(Long id) {
        String sql = "DELETE FROM emprestimo WHERE id=?";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Emprestimo buscarPorId(Long id) {
        String sql = "SELECT * FROM emprestimo WHERE id=? AND na_lixeira=false";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapEmprestimo(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Emprestimo> buscarTodos() {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT * FROM emprestimo WHERE na_lixeira=false";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapEmprestimo(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void moverParaLixeira(Emprestimo obj) {
        moverParaLixeiraPorId(obj.getId());
    }

    @Override
    public void moverParaLixeiraPorId(Long id) {
        String sql = "UPDATE emprestimo SET na_lixeira=true WHERE id=?";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void moverColecaoParaLixeira(List<Emprestimo> lista) {
        for (Emprestimo e : lista) {
            moverParaLixeiraPorId(e.getId());
        }
    }

    @Override
    public List<Emprestimo> recuperarTodosDaLixeira() {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT * FROM emprestimo WHERE na_lixeira=true";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapEmprestimo(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Emprestimo recuperarDaLixeiraPorId(Long id) {
        String sql = "SELECT * FROM emprestimo WHERE id=? AND na_lixeira=true";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapEmprestimo(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void excluirDefinitivo(Emprestimo obj) {
        excluirDefinitivoPorId(obj.getId());
    }

    @Override
    public void excluirDefinitivoPorId(Long id) {
        String sql = "DELETE FROM emprestimo WHERE id=? AND na_lixeira=true";
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
        String sql = "DELETE FROM emprestimo WHERE na_lixeira=true";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método auxiliar para mapear ResultSet para Emprestimo
    private Emprestimo mapEmprestimo(ResultSet rs) throws SQLException {
        Emprestimo e = new Emprestimo();
        e.setId(rs.getLong("id"));
        e.setIdLivro(rs.getInt("id_livro"));
        e.setIdUsuario(rs.getInt("id_usuario"));
        e.setDataEmprestimo(rs.getDate("data_emprestimo").toLocalDate());
        e.setDataPrevista(rs.getDate("data_prevista").toLocalDate());
        Date dataReal = rs.getDate("data_real");
        if (dataReal != null) {
            e.setDataReal(dataReal.toLocalDate());
        }
        e.setNaLixeira(rs.getBoolean("na_lixeira"));
        return e;
    }
}
