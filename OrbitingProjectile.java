import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class OrbitingProjectile extends Projectile{

    protected double theta = 0;
    protected int tick = 0, damageInterval = 6;

    public OrbitingProjectile(ProjectileManager projectileManager, double x, double y, double defenderX, double defenderY, int damage, double range){
        super(projectileManager, x, y, defenderX, defenderY, 10, 10, Math.PI / 4, 0, damage, range);
        
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

    @Override
    public void move(){
        x = defenderX - (Math.cos(theta) * range);
        y = defenderY - (Math.sin(theta) * range);
        if(tick == 0 && projectileManager.checkCollision(this)){
            SoundPlayer.playHit();
        }
        tick = (tick + 1) % damageInterval;
    }

    public void setAngle(double theta){
        this.theta = theta;
    }
    
}
