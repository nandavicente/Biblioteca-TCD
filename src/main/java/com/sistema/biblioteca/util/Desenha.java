package com.sistema.biblioteca.util;

import javax.swing.*;
import java.awt.*;

public class Desenha {

    // Método_ para estilizar botões
    public static JButton BotaoEstilizado(String texto, Color bgColor, Color fontColor, int width, int height, int roundedWidth, int roundedHeight) {
         JButton botao = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor); // Cor de fundo
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), roundedWidth, roundedHeight); // arredondamento
                super.paintComponent(g);
                g2.dispose();
            }
        };

        botao.setFont(new Font("Arial", Font.PLAIN, 20));
        botao.setFocusPainted(false);
        botao.setForeground(fontColor); // cor do texto
        botao.setContentAreaFilled(false); // impede pintura quadrada
        botao.setBorderPainted(false);     // tira borda padrão
        botao.setPreferredSize(new Dimension(width, height));
        botao.setMaximumSize(new Dimension(width, height));
        botao.setAlignmentX(Component.RIGHT_ALIGNMENT);

        return botao;
    }
}
