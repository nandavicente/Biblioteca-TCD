package com.sistema.biblioteca.app;

import com.sistema.biblioteca.entidade.Emprestimo;
import com.sistema.biblioteca.repositorio.EmprestimoRepositorioJDBC;
import com.sistema.biblioteca.repositorio.IRepositorio;
import com.sistema.biblioteca.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoGUI extends JFrame {
    private IRepositorio<Emprestimo> emprestimoRepo = new EmprestimoRepositorioJDBC();
    private JTable tabelaAtivos;
    private JTable tabelaLixeira;
    private JTabbedPane tabbedPane;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public EmprestimoGUI() {
        // Look and Feel Nimbus
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println("Não foi possível aplicar o LookAndFeel Nimbus");
        }

        setTitle("Gerenciamento de Empréstimos");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(Cores.AZUL_ESCURO);

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnVoltar = Desenha.BotaoEstilizado("←", Cores.AZUL_MEDIO, Color.WHITE, 40, 40, 80, 80, 14);
        btnVoltar.addActionListener(e -> {
            dispose();
            // new BibliotecaAppGUI().setVisible(true);
        });
        painelTopo.add(btnVoltar);

        add(painelTopo, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 40, 20, 40));

        // Painel de empréstimos ativos
        JPanel painelAtivos = new JPanel(new BorderLayout());
        tabelaAtivos = new JTable();
        tabelaAtivos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        painelAtivos.add(new JScrollPane(tabelaAtivos), BorderLayout.CENTER);

        JPanel painelBotoesAtivos = new JPanel();
        JButton btnAdicionar = new JButton("+ Adicionar");
        JButton btnEditar = new JButton("✎ Editar");
        JButton btnDevolver = new JButton("✔ Registrar Devolução");
        JButton btnMoverLixeira = new JButton("🗑️ Mover para Lixeira");

        estilizaBotao(btnAdicionar);
        estilizaBotao(btnEditar);
        estilizaBotao(btnDevolver);
        estilizaBotao(btnMoverLixeira);

        painelBotoesAtivos.add(btnAdicionar);
        painelBotoesAtivos.add(btnEditar);
        painelBotoesAtivos.add(btnDevolver);
        painelBotoesAtivos.add(btnMoverLixeira);
        painelAtivos.add(painelBotoesAtivos, BorderLayout.SOUTH);

        btnAdicionar.addActionListener(e -> adicionarEmprestimo());
        btnEditar.addActionListener(e -> editarEmprestimo());
        btnDevolver.addActionListener(e -> registrarDevolucao());
        btnMoverLixeira.addActionListener(e -> moverParaLixeira());

        // Painel da lixeira
        JPanel painelLixeira = new JPanel(new BorderLayout());
        tabelaLixeira = new JTable();
        tabelaLixeira.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        painelLixeira.add(new JScrollPane(tabelaLixeira), BorderLayout.CENTER);

        JPanel painelBotoesLixeira = new JPanel();
        JButton btnRestaurar = new JButton("↻ Restaurar selecionado");
        JButton btnExcluirDef = new JButton("✖ Excluir Definitivo");
        JButton btnEsvaziar = new JButton("🗑️ Esvaziar Lixeira");
        JButton btnRestaurarTodos = new JButton("↻ Restaurar Todos");

        estilizaBotao(btnRestaurar);
        estilizaBotao(btnExcluirDef);
        estilizaBotao(btnEsvaziar);
        estilizaBotao(btnRestaurarTodos);

        painelBotoesLixeira.add(btnRestaurar);
        painelBotoesLixeira.add(btnRestaurarTodos);
        painelBotoesLixeira.add(btnExcluirDef);
        painelBotoesLixeira.add(btnEsvaziar);
        painelLixeira.add(painelBotoesLixeira, BorderLayout.SOUTH);

        btnRestaurar.addActionListener(e -> restaurarEmprestimo());
        btnRestaurarTodos.addActionListener(e -> restaurarTodosEmprestimos());
        btnExcluirDef.addActionListener(e -> excluirDefinitivo());
        btnEsvaziar.addActionListener(e -> esvaziarLixeira());

        tabbedPane.addTab("Empréstimos Ativos", painelAtivos);
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
                new Object[]{"ID", "ID Livro", "ID Usuário", "Data Empréstimo", "Data Prevista", "Data Real"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        emprestimoRepo.buscarTodos().forEach(e -> modeloAtivos.addRow(new Object[]{
                e.getId(),
                e.getIdLivro(),
                e.getIdUsuario(),
                e.getDataEmprestimo().format(dtf),
                e.getDataPrevista().format(dtf),
                e.getDataReal() != null ? e.getDataReal().format(dtf) : ""
        }));
        tabelaAtivos.setModel(modeloAtivos);
        ajustarColunas(tabelaAtivos);

        DefaultTableModel modeloLixeira = new DefaultTableModel(
                new Object[]{"ID", "ID Livro", "ID Usuário", "Data Empréstimo", "Data Prevista", "Data Real"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        emprestimoRepo.recuperarTodosDaLixeira().forEach(e -> modeloLixeira.addRow(new Object[]{
                e.getId(),
                e.getIdLivro(),
                e.getIdUsuario(),
                e.getDataEmprestimo().format(dtf),
                e.getDataPrevista().format(dtf),
                e.getDataReal() != null ? e.getDataReal().format(dtf) : ""
        }));
        tabelaLixeira.setModel(modeloLixeira);
        ajustarColunas(tabelaLixeira);

        tabbedPane.setTitleAt(0, "Empréstimos Ativos (" + emprestimoRepo.buscarTodos().size() + ")");
        tabbedPane.setTitleAt(1, "Lixeira (" + emprestimoRepo.recuperarTodosDaLixeira().size() + ")");
    }

    private void ajustarColunas(JTable tabela) {
        tabela.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(120);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(120);
    }

    private void adicionarEmprestimo() {
        JTextField idLivroField = new JTextField();
        JTextField idUsuarioField = new JTextField();
        JTextField dataEmprestimoField = new JTextField();
        JTextField dataPrevistaField = new JTextField();

        Object[] mensagem = {
                "ID Livro:", idLivroField,
                "ID Usuário:", idUsuarioField,
                "Data Empréstimo (dd/MM/yyyy):", dataEmprestimoField,
                "Data Prevista (dd/MM/yyyy):", dataPrevistaField
        };

        int opcao = JOptionPane.showConfirmDialog(
                this,
                mensagem,
                "Adicionar Empréstimo",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opcao == JOptionPane.OK_OPTION) {
            try {
                int idLivro = Integer.parseInt(idLivroField.getText().trim());
                int idUsuario = Integer.parseInt(idUsuarioField.getText().trim());
                var dataEmprestimo = LocalDate.parse(dataEmprestimoField.getText().trim(), dtf);
                var dataPrevista = LocalDate.parse(dataPrevistaField.getText().trim(), dtf);

                Emprestimo novo = new Emprestimo();
                novo.setIdLivro(idLivro);
                novo.setIdUsuario(idUsuario);
                novo.setDataEmprestimo(dataEmprestimo);
                novo.setDataPrevista(dataPrevista);
                novo.setNaLixeira(false);

                emprestimoRepo.salvar(novo);
                atualizarTabelas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dados inválidos. Verifique os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarEmprestimo() {
        int linha = tabelaAtivos.getSelectedRow();
        if (linha >= 0) {
            Long id = (Long) tabelaAtivos.getValueAt(linha, 0);
            Emprestimo emprestimo = emprestimoRepo.buscarPorId(id);

            JTextField idLivroField = new JTextField(String.valueOf(emprestimo.getIdLivro()));
            JTextField idUsuarioField = new JTextField(String.valueOf(emprestimo.getIdUsuario()));
            JTextField dataEmprestimoField = new JTextField(emprestimo.getDataEmprestimo().format(dtf));
            JTextField dataPrevistaField = new JTextField(emprestimo.getDataPrevista().format(dtf));
            JTextField dataRealField = new JTextField(
                emprestimo.getDataReal() != null ? emprestimo.getDataReal().format(dtf) : ""
            );

            Object[] mensagem = {
                    "ID Livro:", idLivroField,
                    "ID Usuário:", idUsuarioField,
                    "Data Empréstimo (dd/MM/yyyy):", dataEmprestimoField,
                    "Data Prevista (dd/MM/yyyy):", dataPrevistaField,
                    "Data Real (devolução) (dd/MM/yyyy):", dataRealField
            };

            int opcao = JOptionPane.showConfirmDialog(
                    this,
                    mensagem,
                    "Editar Empréstimo",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (opcao == JOptionPane.OK_OPTION) {
                try {
                    int idLivro = Integer.parseInt(idLivroField.getText().trim());
                    int idUsuario = Integer.parseInt(idUsuarioField.getText().trim());
                    var dataEmprestimo = LocalDate.parse(dataEmprestimoField.getText().trim(), dtf);
                    var dataPrevista = LocalDate.parse(dataPrevistaField.getText().trim(), dtf);

                    LocalDate dataReal = null;
                    String dataRealStr = dataRealField.getText().trim();
                    if (!dataRealStr.isEmpty()) {
                        dataReal = LocalDate.parse(dataRealStr, dtf);
                    }

                    emprestimo.setIdLivro(idLivro);
                    emprestimo.setIdUsuario(idUsuario);
                    emprestimo.setDataEmprestimo(dataEmprestimo);
                    emprestimo.setDataPrevista(dataPrevista);
                    emprestimo.setDataReal(dataReal);

                    emprestimoRepo.atualizar(emprestimo);
                    atualizarTabelas();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Dados inválidos. Verifique os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void registrarDevolucao() {
        int linha = tabelaAtivos.getSelectedRow();
        if (linha >= 0) {
            Long id = (Long) tabelaAtivos.getValueAt(linha, 0);
            Emprestimo emprestimo = emprestimoRepo.buscarPorId(id);

            if (emprestimo != null) {
                emprestimo.registrarDevolucao(); // atualiza o objeto (seta dataReal para hoje)
                emprestimoRepo.atualizar(emprestimo); // salva no banco
                atualizarTabelas();
                JOptionPane.showMessageDialog(this, "Devolução registrada com sucesso.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um empréstimo para registrar a devolução.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void moverParaLixeira() {
        int[] linhas = tabelaAtivos.getSelectedRows();
        if (linhas.length > 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Mover " + linhas.length + " empréstimo(s) para a lixeira?",
                    "Confirmação", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                List<Emprestimo> lista = new ArrayList<>();
                for (int linha : linhas) {
                    Long id = (Long) tabelaAtivos.getValueAt(linha, 0);
                    Emprestimo e = emprestimoRepo.buscarPorId(id);
                    if (e != null) {
                        lista.add(e);
                    }
                }
                emprestimoRepo.moverColecaoParaLixeira(lista);
                atualizarTabelas();
            }
        }
    }

    private void restaurarEmprestimo() {
        int[] linhas = tabelaLixeira.getSelectedRows();
        if (linhas.length > 0) {
            for (int linha : linhas) {
                Long id = (Long) tabelaLixeira.getValueAt(linha, 0);
                emprestimoRepo.restaurarPorId(id);
            }
            atualizarTabelas();
        }
    }

    private void restaurarTodosEmprestimos() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja restaurar todos os empréstimos da lixeira?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            if (emprestimoRepo instanceof EmprestimoRepositorioJDBC jdbcRepo) {
                jdbcRepo.restaurarTodosDaLixeira();
                atualizarTabelas();
            }
        }
    }

    private void excluirDefinitivo() {
        int[] linhas = tabelaLixeira.getSelectedRows();
        if (linhas.length > 0) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Excluir definitivamente os empréstimos selecionados?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                for (int linha : linhas) {
                    Long id = (Long) tabelaLixeira.getValueAt(linha, 0);
                    emprestimoRepo.excluirDefinitivoPorId(id);
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
            emprestimoRepo.esvaziarLixeira();
            atualizarTabelas();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmprestimoGUI().setVisible(true));
    }
}

