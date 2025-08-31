import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class TestDefender{
    public static final String path = "Images/test.png";
    private Image i;
    private BufferedImage image;

    public TestDefender(){
        ImageIcon icon = new ImageIcon(path);
        i = icon.getImage();
        image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        // paint the Icon to the BufferedImage.
        icon.paintIcon(null, g, 0,0);
        g.dispose();
    }

    public void draw(Graphics g){
        Graphics2D g2 = ((Graphics2D)g);

        double rotationRequired = Math.toRadians (45);
        double locationX = image.getWidth() / 2;
        double locationY = image.getHeight() / 2;
        AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

        // Drawing the rotated image at the required drawing locations
        BufferedImage newImage = op.filter(image, null);
        g2.drawImage(newImage, 300, 300, null);
        
        g2.drawImage(rotate(image, 45), 100, 100, null);
    }

    public static BufferedImage rotate(BufferedImage bimg, double angle) {
        double sin = Math.abs(Math.sin(Math.toRadians(angle))),
               cos = Math.abs(Math.cos(Math.toRadians(angle)));
        int w = bimg.getWidth();
        int h = bimg.getHeight();
        int neww = (int) Math.floor(w*cos + h*sin),
            newh = (int) Math.floor(h*cos + w*sin);
        BufferedImage rotated = new BufferedImage(neww, newh, bimg.getType());
        Graphics2D graphic = rotated.createGraphics();
        graphic.translate((neww-w)/2, (newh-h)/2);
        graphic.rotate(Math.toRadians(angle), w/2, h/2);
        graphic.drawRenderedImage(bimg, null);
        graphic.dispose();
        return rotated;
    }
}