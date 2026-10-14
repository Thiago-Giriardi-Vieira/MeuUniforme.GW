package br.com.estoque.view;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.File;
import java.net.URL;

/** Cores, fontes e componentes padronizados para seguir o protótipo do sistema. */
public final class Estilo {

    public static final Color FUNDO = new Color(237, 237, 237);   // mesmo tom do fundo do logo
    public static final Color AZUL = new Color(28, 39, 64);
    public static final Color VERDE = new Color(46, 204, 113);
    public static final Color CAMPO = new Color(217, 217, 217);
    public static final Color CINZA_BOTAO = new Color(204, 204, 204);
    public static final Color BORDA = new Color(190, 190, 190);
    public static final Color ESTOQUE_BAIXO = new Color(255, 225, 225);
    public static final Color LINK = new Color(30, 70, 160);

    private Estilo() {}

    /** Visual "Metal": respeita as cores dos componentes em qualquer computador. */
    public static void instalarLookAndFeel() {
        try {
            System.setProperty("swing.boldMetal", "false");
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignorada) {
            // segue com o visual padrão
        }
    }

    public static Font fonte(int estilo, float tamanho) {
        return new Font(Font.SANS_SERIF, estilo, 12).deriveFont(tamanho);
    }

    // ---------------------------------------------------------------- botões

    public static JButton botaoPrimario(String texto) {
        return new BotaoArredondado(texto, AZUL, Color.WHITE, 8, 18);
    }

    public static JButton botaoVerde(String texto) {
        return new BotaoArredondado(texto, VERDE, Color.WHITE, 8, 16);
    }

    public static JButton botaoSecundario(String texto) {
        return new BotaoArredondado(texto, CINZA_BOTAO, Color.BLACK, 8, 18);
    }

    /** Botão pequeno (paginação e botões "+"). */
    public static JButton botaoPagina(String texto, boolean ativo) {
        return ativo ? new BotaoArredondado(texto, AZUL, Color.WHITE, 4, 10)
                     : new BotaoArredondado(texto, new Color(222, 222, 222), Color.BLACK, 4, 10);
    }

    /** Seta "←" do protótipo. */
    public static JButton botaoVoltar() {
        JButton b = new JButton("\u2190");
        b.setFont(fonte(Font.PLAIN, 22f));
        b.setForeground(new Color(60, 60, 60));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setToolTipText("Voltar");
        return b;
    }

    /** Botão com aparência de link (ex.: "Esqueci minha senha"). */
    public static JButton link(String texto) {
        JButton b = new JButton("<html><u>" + texto + "</u></html>");
        b.setFont(fonte(Font.PLAIN, 11f));
        b.setForeground(LINK);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ---------------------------------------------------------------- campos

    public static <T extends JTextComponent> T campo(T c) {
        c.setBackground(CAMPO);
        c.setFont(fonte(Font.PLAIN, 12f));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return c;
    }

    /** Campo de texto que mostra uma dica em cinza enquanto está vazio. */
    public static JTextField campoComDica(String dica, int colunas) {
        JTextField c = new JTextField(colunas) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(120, 120, 120));
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(dica, getInsets().left, y);
                    g2.dispose();
                }
            }
        };
        return campo(c);
    }

    public static <T extends JComboBox<?>> T combo(T c) {
        c.setBackground(CAMPO);
        c.setFont(fonte(Font.PLAIN, 12f));
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, 30)); // mesma altura dos campos de texto
        return c;
    }

    public static JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fonte(Font.BOLD, 11f));
        l.setForeground(new Color(30, 30, 30));
        return l;
    }

    /** Linha "rótulo à esquerda, campo à direita" para as janelas de cadastro. */
    public static void linhaForm(JPanel painel, int linha, String rotulo, JComponent campo) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = linha;
        g.insets = new Insets(5, 4, 5, 4);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        painel.add(rotulo(rotulo), g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        painel.add(campo, g);
    }

    // ---------------------------------------------------------------- logo e mensagens

    /**
     * Logo da escola, redimensionado para a altura pedida.
     * Procura "/logo.png" no projeto (pasta src) ou "logo.png" na pasta de execução.
     * Se não achar, mostra o nome da escola em texto.
     */
    public static JComponent logo(int altura) {
        ImageIcon icone = null;
        URL url = Estilo.class.getResource("/logo.png");
        if (url != null) {
            icone = new ImageIcon(url);
        } else if (new File("logo.png").exists()) {
            icone = new ImageIcon("logo.png");
        }
        if (icone != null && icone.getIconHeight() > 0) {
            int largura = icone.getIconWidth() * altura / icone.getIconHeight();
            Image img = icone.getImage().getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
            return new JLabel(new ImageIcon(img));
        }
        JLabel l = new JLabel("<html><div style='text-align:center'><b>ESCOLA MUNICIPAL</b><br>"
                + "PREFEITO GERALDO WETZEL</div></html>", SwingConstants.CENTER);
        l.setForeground(new Color(0, 51, 153));
        return l;
    }

    /** Mostra um erro: validações como aviso; falhas técnicas com o detalhe. */
    public static void erro(Component pai, RuntimeException e) {
        if (e instanceof IllegalArgumentException) {
            JOptionPane.showMessageDialog(pai, e.getMessage(), "Verifique os dados", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Throwable causa = e.getCause() != null ? e.getCause() : e;
        JOptionPane.showMessageDialog(pai, e.getMessage() + "\n\nDetalhe: " + causa.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // ---------------------------------------------------------------- botão arredondado

    private static class BotaoArredondado extends JButton {
        private final Color cor;

        BotaoArredondado(String texto, Color cor, Color corTexto, int vertical, int horizontal) {
            super(texto);
            this.cor = cor;
            setForeground(corTexto);
            setFont(fonte(Font.BOLD, 12f));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(vertical, horizontal, vertical, horizontal));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            ButtonModel m = getModel();
            Color fundo = cor;
            if (!isEnabled()) fundo = new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 110);
            else if (m.isPressed()) fundo = cor.darker();
            else if (m.isRollover()) fundo = mais(cor, 25);
            g2.setColor(fundo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }

        private static Color mais(Color c, int d) {
            return new Color(Math.min(255, c.getRed() + d), Math.min(255, c.getGreen() + d),
                    Math.min(255, c.getBlue() + d));
        }
    }
}
