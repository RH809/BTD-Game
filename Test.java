import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

// Test class
public class Test extends JComponent implements MouseMotionListener{

    private TestDefender testDefender;

    public Test(){
        setPreferredSize(new Dimension(500, 700));
        setVisible(true);

        testDefender = new TestDefender();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.CYAN);
        g.drawRect(200, 200, 50, 50);
        testDefender.draw(g);
    }
    public static void main(String[] args) {
        for(int i = 0; i < 10; i++){
            SoundPlayer.playExplosion();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    

}


