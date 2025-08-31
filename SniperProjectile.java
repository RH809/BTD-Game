import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class SniperProjectile extends Projectile{

    protected boolean piercing;

    public SniperProjectile(ProjectileManager projectileManager, double x, double y, double defenderX, double defenderY, double rotation, int damage, double range, boolean piercing) {
        super(projectileManager, x, y, defenderX, defenderY, 60, 20, rotation, 40.0, damage, range);
        this.piercing = piercing;
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

    public void move(){
        x += speed * Math.cos(rotation);
        y += speed * Math.sin(rotation);
        if(projectileManager.checkCollision(this)){ // determines if there has been any collisions; if so, destroy
            SoundPlayer.playHit();
            if(!piercing) destroy();
        }
        if(x < 0 || y < 0 || x > 2000 * Coordinator.SIZE_MULTIPLIER || y > 1200 * Coordinator.SIZE_MULTIPLIER ||
            Math.sqrt(Math.pow(x - defenderX, 2) + Math.pow(y - defenderY, 2)) > range) destroy(); // destroy if the projectile has travelled outside its range
    }
    

    
}
