import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

/**
 * Class to handle the drawing in the background that don't move
 */
public class BackgroundPanel extends JPanel{


    public static final BasicStroke GROUND_STROKE = new BasicStroke((int)(50 * Coordinator.SIZE_MULTIPLIER), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    public static final Font backgroundFont = new Font("Dialog", 10, (int)(60 * Coordinator.SIZE_MULTIPLIER));

    public BackgroundPanel(){
        setLayout(null);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        setBackground(Color.BLACK);
        g.setColor(Color.WHITE);
        Graphics2D g2 = (Graphics2D) g;
        // Optimization ------------------------------------
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int nPoints = 5000;
        // -------------------------------------------------
        double x1 = -1, y1 = -1;
        double x0 = 0;
        // Alternative 1 ---------------------
        g2.setStroke(GROUND_STROKE);
        // -----------------------------------
        // draw path of attackers
        double y0 = (59.8776 * 11) * Coordinator.SIZE_MULTIPLIER;
        for (int i = 0; i < nPoints; i++) {
            double t = 130 * i / nPoints;
            x1 = (-0.000017914 * (Math.pow(t, 4)) + 0.00353185 * (Math.pow(t, 3)) - 0.206458 * (Math.pow(t, 2)) + 4.24157 * (t) - 0.0158984) * 17;
            y1 = 1200 - ((-0.0000421384 * (Math.pow(t, 4)) + 0.00825601 * (Math.pow(t, 3)) - 0.492829 * (Math.pow(t, 2)) + 8.66422 * (t) + 59.8776) * 11); 
            x1 *= Coordinator.SIZE_MULTIPLIER;
            y1 *= Coordinator.SIZE_MULTIPLIER;
            if((x0 > 0 || x1 > 0 )&& x0 != x1 && y0 != y1) {
                g2.draw(new Line2D.Double(x0, y0, x1, y1));
            }
            x0 = x1;
            y0 = y1;
        }

        g2.draw(new Line2D.Double(0, (1200 - 59.8776 * 11) * Coordinator.SIZE_MULTIPLIER, -30 * Coordinator.SIZE_MULTIPLIER,
            (1200 - 56 * 11) * Coordinator.SIZE_MULTIPLIER));
    }
}
