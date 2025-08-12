package com.sistema.biblioteca.app;

import com.sistema.biblioteca.util.Cores;
import com.sistema.biblioteca.util.Desenha;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class BibliotecaAppGUI extends JFrame {

    public BibliotecaAppGUI() {
        setTitle("Sistema de Biblioteca");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Cores.AZUL_MEDIO_ESCURO);

        setLayout(new BorderLayout());

        // ---------- TOPO ----------
        JPanel topo = new JPanel(new BorderLayout());

        // Logo da faculdade
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/logoIF.png"))); // imagem
        Image img = icon.getImage().getScaledInstance(65, 80, Image.SCALE_SMOOTH); // redimensionar
        JLabel logo = new JLabel(new ImageIcon(img)); // adiciona a imagem em uma label para ser adicionada ao painel
        logo.setBorder(BorderFactory.createEmptyBorder(40, 60, 20, 20)); // padding para label
        topo.add(logo, BorderLayout.WEST); // adiciona a label ao painel

        // Nome do programa
        JLabel nomePrograma = new JLabel("Sistema de Gerenciamento de Biblioteca", SwingConstants.RIGHT);
        nomePrograma.setFont(new Font("Arial", Font.BOLD, 26));
        nomePrograma.setForeground(Cores.AZUL_CLARO);
        nomePrograma.setBorder(BorderFactory.createEmptyBorder(40, 10, 10, 40));
        topo.add(nomePrograma, BorderLayout.CENTER);

        add(topo, BorderLayout.NORTH);

        // ---------- CENTRO ----------
        // Painel de botões (esquerda)
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new BoxLayout(painelBotoes, BoxLayout.Y_AXIS));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(90, 20, 20, 40));
        painelBotoes.setPreferredSize(new Dimension(400, getHeight())); // largura fixa


        JButton btnUsuarios = Desenha.BotaoEstilizado("Gerenciar Usuários", Cores.AZUL_CLARO, Cores.AZUL_MEDIO_ESCURO, 300, 100, 80, 80, 20);
        JButton btnLivros = Desenha.BotaoEstilizado("Gerenciar Livros", Cores.AZUL_MEDIO_CLARO, Color.WHITE, 300, 100, 80, 80, 20);
        JButton btnEmprestimos = Desenha.BotaoEstilizado("Gerenciar Empréstimos", Cores.AZUL_MEDIO, Cores.AZUL_CLARO, 300, 100, 80, 80, 20);

        // define espaçamento entre os botões
        painelBotoes.add(btnUsuarios);
        painelBotoes.add(Box.createRigidArea(new Dimension(0, 30)));
        painelBotoes.add(btnLivros);
        painelBotoes.add(Box.createRigidArea(new Dimension(0, 30)));
        painelBotoes.add(btnEmprestimos);

        // Painel de texto informativo (direita)
        JTextPane info = new JTextPane();
        info.setContentType("text/html"); // Permite HTML
        info.setText(
            "<html><body style='text-align:left; font-family:Arial; font-size:18px; color:white;'>"
            + "<h2 style='color:#54ACBF;'>Bem-vindo(a) ao Sistema de Biblioteca IFNMG!</h2>"
            + "<p>Aqui você pode gerenciar <b>usuários</b>, <b>livros</b> e <b>empréstimos</b>.</p>"
            + "<p>Facilite a organização e o controle da sua biblioteca.</p>"
            + "</body></html>"
        );
        info.setEditable(false);
        info.setBackground(getBackground());
        info.setBorder(BorderFactory.createEmptyBorder(120, 100, 20, 10));

        JScrollPane scrollTexto = new JScrollPane(info);
        scrollTexto.setBorder(null);
        scrollTexto.setPreferredSize(new Dimension(500, getHeight())); // largura fixa
        scrollTexto.setOpaque(false);
        scrollTexto.getViewport().setOpaque(false); // <- ESSENCIAL


        // Adiciona ao painel central
        JPanel centro = new JPanel(new BorderLayout());
        centro.add(painelBotoes, BorderLayout.EAST);
        centro.add(scrollTexto, BorderLayout.WEST);

        add(centro, BorderLayout.CENTER);


        // ---------- RODAPÉ ----------
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton btnSair = Desenha.BotaoEstilizado("🚪 Sair", Cores.AZUL_MEDIO, Cores.AZUL_CLARO, 100, 40, 20, 20, 20);
        btnSair.addActionListener(e -> System.exit(0));

        JLabel desenvolvidoPor = new JLabel("Desenvolvido por: Aline Soares, João Vitor Ribeiro e Maria Fernanda Vicente");
        desenvolvidoPor.setHorizontalAlignment(SwingConstants.RIGHT);
        desenvolvidoPor.setForeground(Cores.AZUL_MEDIO_CLARO);

        rodape.add(btnSair, BorderLayout.WEST);
        rodape.add(desenvolvidoPor, BorderLayout.EAST);

        add(rodape, BorderLayout.SOUTH);

        topo.setOpaque(false);
        painelBotoes.setOpaque(false);
        centro.setOpaque(false);
        rodape.setOpaque(false);
        info.setOpaque(false);

        // ---------- AÇÕES ----------
        btnUsuarios.addActionListener(e -> new UsuarioGUI().setVisible(true));
        btnLivros.addActionListener(e -> JOptionPane.showMessageDialog(this, "Tela de Livros em desenvolvimento"));
        btnEmprestimos.addActionListener(e -> JOptionPane.showMessageDialog(this, "Tela de Empréstimos em desenvolvimento"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BibliotecaAppGUI().setVisible(true));
    }
}
