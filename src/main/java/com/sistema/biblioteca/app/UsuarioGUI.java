package com.sistema.biblioteca.app;

import com.sistema.biblioteca.entidade.Usuario;
import com.sistema.biblioteca.repositorio.IRepositorio;
import com.sistema.biblioteca.repositorio.UsuarioRepositorioJDBC;
import com.sistema.biblioteca.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

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

        // Definições iniciais da Interface Gráfica / Janela
        usuarioRepo = new UsuarioRepositorioJDBC();
        setTitle("Gerenciamento de Usuários");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(Cores.AZUL_ESCURO);

        // --------- Painel superior --------------
        // Painel superior com botão Voltar
        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // cima, esquerda, baixo, direita

        JButton btnVoltar = Desenha.BotaoEstilizado("⬅", Cores.AZUL_MEDIO, Color.WHITE, 40, 40, 80, 80, 14);
        btnVoltar.addActionListener(e -> {
            dispose(); // Fecha a janela atual
            //new BibliotecaAppGUI().setVisible(true); // Volta para tela inicial
        });
        painelTopo.add(btnVoltar);

        // Adiciona o painelTopo antes de tudo
        add(painelTopo, BorderLayout.NORTH);

        // ---------------- Painéis com as tabelas --------------------------------
        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 40, 20, 40));

        // Painel de usuários ativos
        JPanel painelAtivos = new JPanel(new BorderLayout());
        tabelaAtivos = new JTable();
        tabelaAtivos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // permite selecionar várias linhas
        painelAtivos.add(new JScrollPane(tabelaAtivos), BorderLayout.CENTER);

        // Rodapé do painel de usuários ativos
        JPanel painelBotoesAtivos = new JPanel();
        JButton btnAdicionar = new JButton("➕ Adicionar");
        JButton btnEditar = new JButton("✏️ Editar");
        JButton btnMoverLixeira = new JButton("🗑️ Mover para Lixeira");

        estilizaBotao(btnAdicionar);
        estilizaBotao(btnEditar);
        estilizaBotao(btnMoverLixeira);

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
        tabelaLixeira.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // permite selecionar várias linhas
        painelLixeira.add(new JScrollPane(tabelaLixeira), BorderLayout.CENTER);

        // Rodapé do painel de lixeira
        JPanel painelBotoesLixeira = new JPanel();
        JButton btnRestaurar = new JButton("♻️ Restaurar selecionado");
        JButton btnExcluirDef = new JButton("❌ Excluir Definitivo");
        JButton btnEsvaziar = new JButton("🧹 Esvaziar Lixeira");
        JButton btnRestaurarTodos = new JButton("♻️ Restaurar Todos");

        estilizaBotao(btnRestaurar);
        estilizaBotao(btnExcluirDef);
        estilizaBotao(btnEsvaziar);
        estilizaBotao(btnRestaurarTodos);

        painelBotoesLixeira.add(btnRestaurar);
        painelBotoesLixeira.add(btnRestaurarTodos);
        painelBotoesLixeira.add(btnExcluirDef);
        painelBotoesLixeira.add(btnEsvaziar);
        painelLixeira.add(painelBotoesLixeira, BorderLayout.SOUTH);

        btnRestaurar.addActionListener(e -> restaurarUsuario());
        btnRestaurarTodos.addActionListener(e -> restaurarTodosUsuarios());
        btnExcluirDef.addActionListener(e -> excluirDefinitivo());
        btnEsvaziar.addActionListener(e -> esvaziarLixeira());

        tabbedPane.addTab("Usuários Ativos", painelAtivos);
        tabbedPane.addTab("Lixeira", painelLixeira);

        add(tabbedPane);

        atualizarTabelas();
    }

    private void estilizaBotao(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 13)); // fonte
        btn.setBackground(Cores.AZUL_MEDIO); // cor de fundo
        btn.setForeground(Color.white);  // cor do texto
        btn.setPreferredSize(new Dimension(190, 40)); // tamanho preferido
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

        int opcao = JOptionPane.showConfirmDialog(
                this,
                mensagem,
                "Adicionar Usuário",
                JOptionPane.OK_CANCEL_OPTION
        );
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

            int opcao = JOptionPane.showConfirmDialog(
                    this,
                    mensagem,
                    "Editar Usuário",
                    JOptionPane.OK_CANCEL_OPTION
            );
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
        int[] linhas = tabelaAtivos.getSelectedRows();
        if (linhas.length > 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Mover " + linhas.length + " usuário(s) para a lixeira?",
                    "Confirmação", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                List<Usuario> lista = new ArrayList<>();
                for (int linha : linhas) {
                    Long id = (Long) tabelaAtivos.getValueAt(linha, 0);
                    Usuario usuario = usuarioRepo.buscarPorId(id);
                    if (usuario != null) {
                        lista.add(usuario);
                    }
                }
                usuarioRepo.moverColecaoParaLixeira(lista);
                atualizarTabelas();
            }
        }
    }

    private void restaurarUsuario() {
        int[] linhas = tabelaLixeira.getSelectedRows();
        if (linhas.length > 0) {
            for (int linha : linhas) {
                Long id = (Long) tabelaLixeira.getValueAt(linha, 0);
                usuarioRepo.restaurarPorId(id);
            }
            atualizarTabelas();
        }
    }

    private void restaurarTodosUsuarios() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Deseja restaurar todos os usuários da lixeira?",
            "Confirmação",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            if (usuarioRepo instanceof UsuarioRepositorioJDBC jdbcRepo) {
                jdbcRepo.restaurarTodosDaLixeira();
                atualizarTabelas();
            }
        }
    }

    private void excluirDefinitivo() {
        int[] linhas = tabelaLixeira.getSelectedRows(); // várias linhas
        if (linhas.length > 0) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Excluir definitivamente os usuários selecionados?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                for (int linha : linhas) {
                    Long id = (Long) tabelaLixeira.getValueAt(linha, 0);
                    usuarioRepo.excluirDefinitivoPorId(id);
                }
                atualizarTabelas();
            }
        }
    }

    private void esvaziarLixeira() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Esvaziar toda a lixeira?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            usuarioRepo.esvaziarLixeira();
            atualizarTabelas();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UsuarioGUI().setVisible(true));
    }
}
