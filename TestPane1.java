import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.Shape;

import javax.swing.JPanel;

// Test class used in Test.java
public class TestPane1 extends JPanel{
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        TestObject o = new TestObject();
        o.draw(g);
    }

    class TestObject{
        public void draw(Graphics g){
            Graphics2D g2 = (Graphics2D)g;
            Rectangle2D.Double rect = new Rectangle2D.Double(80, 60, 40, 80);
            AffineTransform at = AffineTransform.getRotateInstance(Math.random() * 2 * Math.PI, 100, 100);
            Shape rotatedRect = at.createTransformedShape(rect);
            g2.draw(rotatedRect);
        }
    }
}
