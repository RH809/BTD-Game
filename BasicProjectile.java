import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class BasicProjectile extends Projectile{

    public BasicProjectile(ProjectileManager projectileManager, double x, double y, double defenderX, double defenderY, double rotation, int damage, double range) {
        super(projectileManager, x, y, defenderX, defenderY, 20, 10, rotation, 10.0, damage, range);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.BLUE);
        Rectangle2D.Double rect = new Rectangle2D.Double(x - width / 2, y - height / 2, width, height);
        AffineTransform at = AffineTransform.getRotateInstance(rotation, x, y);
        Shape rotatedRect = at.createTransformedShape(rect);
        g2.fill(rotatedRect);
    }
    
}
