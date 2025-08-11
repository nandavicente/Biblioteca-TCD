package com.sistema.biblioteca.app;

import com.sistema.biblioteca.entidade.Usuario;
import com.sistema.biblioteca.repositorio.IRepositorio;
import com.sistema.biblioteca.repositorio.UsuarioRepositorioJDBC;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UsuarioGUI extends JFrame {
    private IRepositorio<Usuario> usuarioRepo = new UsuarioRepositorioJDBC();
    private JTable tabelaAtivos;
    private JTable tabelaLixeira;
    private JTabbedPane tabbedPane;

    public UsuarioGUI() {
        // Aplicar Look and Feel Nimbus
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println("Não foi possível aplicar o LookAndFeel Nimbus");
        }

        usuarioRepo = new UsuarioRepositorioJDBC();
        setTitle("Gerenciamento de Usuários");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Painel de usuários ativos
        JPanel painelAtivos = new JPanel(new BorderLayout());
        tabelaAtivos = new JTable();
        painelAtivos.add(new JScrollPane(tabelaAtivos), BorderLayout.CENTER);

        JPanel painelBotoesAtivos = new JPanel();
        JButton btnAdicionar = new JButton("➕ Adicionar");
        JButton btnEditar = new JButton("✏️ Editar");
        JButton btnMoverLixeira = new JButton("🗑️ Mover para Lixeira");

        painelBotoesAtivos.add(btnAdicionar);
        painelBotoesAtivos.add(btnEditar);
        painelBotoesAtivos.add(btnMoverLixeira);
        painelAtivos.add(painelBotoesAtivos, BorderLayout.SOUTH);

        btnAdicionar.addActionListener(e -> adicionarUsuario());
        btnEditar.addActionListener(e -> editarUsuario());
        btnMoverLixeira.addActionListener(e -> moverParaLixeira());

        // Painel da lixeira
        JPanel painelLixeira = new JPanel(new BorderLayout());
        tabelaLixeira = new JTable();
        painelLixeira.add(new JScrollPane(tabelaLixeira), BorderLayout.CENTER);

        JPanel painelBotoesLixeira = new JPanel();
        JButton btnRestaurar = new JButton("♻️ Restaurar");
        JButton btnExcluirDef = new JButton("❌ Excluir Definitivo");
        JButton btnEsvaziar = new JButton("🧹 Esvaziar Lixeira");

        painelBotoesLixeira.add(btnRestaurar);
        painelBotoesLixeira.add(btnExcluirDef);
        painelBotoesLixeira.add(btnEsvaziar);
        painelLixeira.add(painelBotoesLixeira, BorderLayout.SOUTH);

        btnRestaurar.addActionListener(e -> restaurarUsuario());
        btnExcluirDef.addActionListener(e -> excluirDefinitivo());
        btnEsvaziar.addActionListener(e -> esvaziarLixeira());

        tabbedPane.addTab("Usuários Ativos", painelAtivos);
        tabbedPane.addTab("Lixeira", painelLixeira);

        add(tabbedPane);

        atualizarTabelas();
    }

    private void atualizarTabelas() {
        DefaultTableModel modeloAtivos = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Matrícula", "Email"}, 0
        ){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // vai bloquear a edição na tabela
            }
        };

        usuarioRepo.buscarTodos().forEach(u ->
                modeloAtivos.addRow(new Object[]{u.getId(), u.getNome(), u.getMatricula(), u.getEmail()})
        );
        tabelaAtivos.setModel(modeloAtivos);
        ajustarColunas(tabelaAtivos);

        DefaultTableModel modeloLixeira = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Matrícula", "Email"}, 0
        ){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usuarioRepo.recuperarTodosDaLixeira().forEach(u ->
                modeloLixeira.addRow(new Object[]{u.getId(), u.getNome(), u.getMatricula(), u.getEmail()})
        );
        tabelaLixeira.setModel(modeloLixeira);
        ajustarColunas(tabelaLixeira);

        tabbedPane.setTitleAt(0, "Usuários Ativos (" + usuarioRepo.buscarTodos().size() + ")");
        tabbedPane.setTitleAt(1, "Lixeira (" + usuarioRepo.recuperarTodosDaLixeira().size() + ")");
    }

    private void ajustarColunas(JTable tabela) {
        tabela.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(200);
    }

    private void adicionarUsuario() {
        JTextField nomeField = new JTextField();
        JTextField matriculaField = new JTextField();
        JTextField emailField = new JTextField();

        Object[] mensagem = {
                "Nome:", nomeField,
                "Matrícula:", matriculaField,
                "Email:", emailField
        };

        int opcao = JOptionPane.showConfirmDialog(this, mensagem, "Adicionar Usuário", JOptionPane.OK_CANCEL_OPTION);
        if (opcao == JOptionPane.OK_OPTION) {
            if (nomeField.getText().trim().isEmpty() || matriculaField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
                JOptionPane.showMessageDialog(this, "Dados inválidos. Verifique os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario novo = new Usuario();
            novo.setNome(nomeField.getText());
            novo.setMatricula(matriculaField.getText());
            novo.setEmail(emailField.getText());
            usuarioRepo.salvar(novo);

            atualizarTabelas(); // recarrega os dados do banco e atualiza a GUI
        }
    }

    private void editarUsuario() {
        int linha = tabelaAtivos.getSelectedRow();
        if (linha >= 0) {
            Long id = (Long) tabelaAtivos.getValueAt(linha, 0);
            Usuario usuario = usuarioRepo.buscarPorId(id);

            JTextField nomeField = new JTextField(usuario.getNome());
            JTextField matriculaField = new JTextField(usuario.getMatricula());
            JTextField emailField = new JTextField(usuario.getEmail());

            Object[] mensagem = {
                    "Nome:", nomeField,
                    "Matrícula:", matriculaField,
                    "Email:", emailField
            };

            int opcao = JOptionPane.showConfirmDialog(this, mensagem, "Editar Usuário", JOptionPane.OK_CANCEL_OPTION);
            if (opcao == JOptionPane.OK_OPTION) {
                if (nomeField.getText().trim().isEmpty() || matriculaField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
                    JOptionPane.showMessageDialog(this, "Dados inválidos. Verifique os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                usuario.setNome(nomeField.getText());
                usuario.setMatricula(matriculaField.getText());
                usuario.setEmail(emailField.getText());
                usuarioRepo.atualizar(usuario);
                atualizarTabelas();
            }
        }
    }

    private void moverParaLixeira() {
        int linha = tabelaAtivos.getSelectedRow();
        if (linha >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Mover para lixeira?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Long id = (Long) tabelaAtivos.getValueAt(linha, 0);
                usuarioRepo.moverParaLixeiraPorId(id);
                atualizarTabelas();
            }
        }
    }

    private void restaurarUsuario() {
        int linha = tabelaLixeira.getSelectedRow();
        if (linha >= 0) {
            Long id = (Long) tabelaLixeira.getValueAt(linha, 0);
            Usuario usuario = usuarioRepo.recuperarDaLixeiraPorId(id);
            if (usuario != null) {
                usuario.setNaLixeira(false);
                usuarioRepo.atualizar(usuario);
            }
            atualizarTabelas();
        }
    }

    private void excluirDefinitivo() {
        int linha = tabelaLixeira.getSelectedRow();
        if (linha >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Excluir definitivamente este usuário?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Long id = (Long) tabelaLixeira.getValueAt(linha, 0);
                usuarioRepo.excluirDefinitivoPorId(id);
                atualizarTabelas();
            }
        }
    }

    private void esvaziarLixeira() {
        int confirm = JOptionPane.showConfirmDialog(this, "Esvaziar toda a lixeira?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            usuarioRepo.esvaziarLixeira();
            atualizarTabelas();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UsuarioGUI().setVisible(true));
    }
}
