package com.siemens.einkaufsliste.gui;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

class RoundedCornerBorder implements Border {
    private final int arc;
    private final Color borderColor;
    private final int thickness;

    public RoundedCornerBorder(int arc, Color borderColor, int thickness) {
        this.arc = arc;
        this.borderColor = borderColor;
        this.thickness = thickness;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        int pad = thickness + 4;
        return new Insets(pad, pad, pad, pad);
    }

    @Override
    public boolean isBorderOpaque() { return false; }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape round = new RoundRectangle2D.Float(x, y, width - 1, height - 1, arc, arc);
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(thickness));
        g2.draw(round);
        g2.dispose();
    }

    public static void doBackground(JButton btn, int arc){
        btn.setUI(new BasicButtonUI() {
                @Override
                public void paint(Graphics g, JComponent c) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    Shape clip = new RoundRectangle2D.Float(
                            0, 0, c.getWidth() - 1, c.getHeight() - 1, arc, arc
                    );
                    g2.setClip(clip);

                    // Hintergrund einfarbig (ohne Verlauf) füllen – mit c.getBackground()
                    g2.setColor(c.getBackground());
                    g2.fillRect(0, 0, c.getWidth(), c.getHeight());

                    // Danach den Button-Inhalt (Text, Fokus etc.) zeichnen lassen
                    super.paint(g2, c);
                    g2.dispose();
                }
            });
    }

    public static void createButton(JButton btn, int backColor, int borderColor, int arc, int thickness){
        btn.setBackground(new Color(backColor));
        btn.setContentAreaFilled(true);
        btn.setOpaque(false); 
        btn.setBorder(new RoundedCornerBorder(arc, new Color(borderColor), thickness));
        
        RoundedCornerBorder.doBackground(btn, arc);
    }
}
