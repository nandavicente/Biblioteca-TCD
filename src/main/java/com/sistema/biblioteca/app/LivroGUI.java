package com.sistema.biblioteca.app;

import com.sistema.biblioteca.entidade.Livro;
import com.sistema.biblioteca.repositorio.IRepositorio;
import com.sistema.biblioteca.repositorio.LivroRepositorioJDBC;
import com.sistema.biblioteca.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LivroGUI extends JFrame {
    private IRepositorio<Livro> livroRepo = new LivroRepositorioJDBC();
    private JTable tabelaAtivos;
    private JTable tabelaLixeira;
    private JTabbedPane tabbedPane;

    public LivroGUI() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println("Não foi possível aplicar o LookAndFeel Nimbus");
        }

        setTitle("Gerenciamento de Livros");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(Cores.AZUL_ESCURO);

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnVoltar = Desenha.BotaoEstilizado("⬅", Cores.AZUL_MEDIO, Color.WHITE, 40, 40, 80, 80, 14);
        btnVoltar.addActionListener(e -> dispose());
        painelTopo.add(btnVoltar);
        add(painelTopo, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 40, 20, 40));

        // Painel livros ativos
        JPanel painelAtivos = new JPanel(new BorderLayout());
        tabelaAtivos = new JTable();
        tabelaAtivos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        painelAtivos.add(new JScrollPane(tabelaAtivos), BorderLayout.CENTER);

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

        btnAdicionar.addActionListener(e -> adicionarLivro());
        btnEditar.addActionListener(e -> editarLivro());
        btnMoverLixeira.addActionListener(e -> moverParaLixeira());

        // Painel lixeira
        JPanel painelLixeira = new JPanel(new BorderLayout());
        tabelaLixeira = new JTable();
        tabelaLixeira.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        painelLixeira.add(new JScrollPane(tabelaLixeira), BorderLayout.CENTER);

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

        btnRestaurar.addActionListener(e -> restaurarLivro());
        btnRestaurarTodos.addActionListener(e -> restaurarTodosLivros());
        btnExcluirDef.addActionListener(e -> excluirDefinitivo());
        btnEsvaziar.addActionListener(e -> esvaziarLixeira());

        tabbedPane.addTab("Livros Ativos", painelAtivos);
        tabbedPane.addTab("Lixeira", painelLixeira);

        add(tabbedPane);
        atualizarTabelas();
    }

    private void estilizaBotao(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(Cores.AZUL_MEDIO);
        btn.setForeground(Color.white);
        btn.setPreferredSize(new Dimension(190, 40));
    }

    private void atualizarTabelas() {
        DefaultTableModel modeloAtivos = new DefaultTableModel(
                new Object[]{"ID", "Título", "Autor", "Editora", "Ano Publicação"}, 0
        ){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        livroRepo.buscarTodos().forEach(l ->
                modeloAtivos.addRow(new Object[]{
                        l.getId(),
                        l.getTitulo(),
                        l.getAutor(),
                        l.getEditora(),
                        l.getAnoPublicacao()
                })
        );
        tabelaAtivos.setModel(modeloAtivos);
        ajustarColunas(tabelaAtivos);

        DefaultTableModel modeloLixeira = new DefaultTableModel(
                new Object[]{"ID", "Título", "Autor", "Editora", "Ano Publicação"}, 0
        ){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        livroRepo.recuperarTodosDaLixeira().forEach(l ->
                modeloLixeira.addRow(new Object[]{
                        l.getId(),
                        l.getTitulo(),
                        l.getAutor(),
                        l.getEditora(),
                        l.getAnoPublicacao()
                })
        );
        tabelaLixeira.setModel(modeloLixeira);
        ajustarColunas(tabelaLixeira);

        tabbedPane.setTitleAt(0, "Livros Ativos (" + livroRepo.buscarTodos().size() + ")");
        tabbedPane.setTitleAt(1, "Lixeira (" + livroRepo.recuperarTodosDaLixeira().size() + ")");
    }

    private void ajustarColunas(JTable tabela) {
        tabela.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(120);
    }

    private void adicionarLivro() {
        JTextField tituloField = new JTextField();
        JTextField autorField = new JTextField();
        JTextField editoraField = new JTextField();
        JTextField anoField = new JTextField();

        Object[] mensagem = {
                "Título:", tituloField,
                "Autor:", autorField,
                "Editora:", editoraField,
                "Ano de Publicação:", anoField
        };

        int opcao = JOptionPane.showConfirmDialog(
                this, mensagem, "Adicionar Livro", JOptionPane.OK_CANCEL_OPTION
        );
        if (opcao == JOptionPane.OK_OPTION) {
            if (tituloField.getText().trim().isEmpty() ||
                    autorField.getText().trim().isEmpty() ||
                    editoraField.getText().trim().isEmpty() ||
                    anoField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Dados inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Livro novo = new Livro();
            novo.setTitulo(tituloField.getText());
            novo.setAutor(autorField.getText());
            novo.setEditora(editoraField.getText());
            novo.setAnoPublicacao(Integer.parseInt(anoField.getText()));
            livroRepo.salvar(novo);

            atualizarTabelas();
        }
    }

    private void editarLivro() {
        int linha = tabelaAtivos.getSelectedRow();
        if (linha >= 0) {
            Long id = (Long) tabelaAtivos.getValueAt(linha, 0);
            Livro livro = livroRepo.buscarPorId(id);

            JTextField tituloField = new JTextField(livro.getTitulo());
            JTextField autorField = new JTextField(livro.getAutor());
            JTextField editoraField = new JTextField(livro.getEditora());
            JTextField anoField = new JTextField(String.valueOf(livro.getAnoPublicacao()));

            Object[] mensagem = {
                    "Título:", tituloField,
                    "Autor:", autorField,
                    "Editora:", editoraField,
                    "Ano de Publicação:", anoField
            };

            int opcao = JOptionPane.showConfirmDialog(
                    this, mensagem, "Editar Livro", JOptionPane.OK_CANCEL_OPTION
            );
            if (opcao == JOptionPane.OK_OPTION) {
                livro.setTitulo(tituloField.getText());
                livro.setAutor(autorField.getText());
                livro.setEditora(editoraField.getText());
                livro.setAnoPublicacao(Integer.parseInt(anoField.getText()));
                livroRepo.atualizar(livro);
                atualizarTabelas();
            }
        }
    }

    private void moverParaLixeira() {
        int[] linhas = tabelaAtivos.getSelectedRows();
        if (linhas.length > 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Mover " + linhas.length + " livro(s) para a lixeira?",
                    "Confirmação", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                List<Livro> lista = new ArrayList<>();
                for (int linha : linhas) {
                    Long id = (Long) tabelaAtivos.getValueAt(linha, 0);
                    Livro livro = livroRepo.buscarPorId(id);
                    if (livro != null) lista.add(livro);
                }
                livroRepo.moverColecaoParaLixeira(lista);
                atualizarTabelas();
            }
        }
    }

    private void restaurarLivro() {
        int[] linhas = tabelaLixeira.getSelectedRows();
        if (linhas.length > 0) {
            for (int linha : linhas) {
                Long id = (Long) tabelaLixeira.getValueAt(linha, 0);
                livroRepo.restaurarPorId(id);
            }
            atualizarTabelas();
        }
    }

    private void restaurarTodosLivros() {
        int confirm = JOptionPane.showConfirmDialog(
                this, "Deseja restaurar todos os livros da lixeira?",
                "Confirmação", JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            if (livroRepo instanceof LivroRepositorioJDBC jdbcRepo) {
                jdbcRepo.restaurarTodosDaLixeira();
                atualizarTabelas();
            }
        }
    }

    private void excluirDefinitivo() {
        int[] linhas = tabelaLixeira.getSelectedRows();
        if (linhas.length > 0) {
            int confirm = JOptionPane.showConfirmDialog(
                    this, "Excluir definitivamente os livros selecionados?",
                    "Confirmação", JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                for (int linha : linhas) {
                    Long id = (Long) tabelaLixeira.getValueAt(linha, 0);
                    livroRepo.excluirDefinitivoPorId(id);
                }
                atualizarTabelas();
            }
        }
    }

    private void esvaziarLixeira() {
        int confirm = JOptionPane.showConfirmDialog(
                this, "Esvaziar toda a lixeira?",
                "Confirmação", JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            livroRepo.esvaziarLixeira();
            atualizarTabelas();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LivroGUI().setVisible(true));
    }
}