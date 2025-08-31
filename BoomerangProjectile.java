import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class BoomerangProjectile extends Projectile{

    public static int nextID = 0;

    protected int direction = 1;
    protected double theta = 0;
    protected BoomerangTurret parent;
    protected int id;

    public BoomerangProjectile(ProjectileManager projectileManager, double x, double y, double defenderX, double defenderY, double rotation, int damage, double range, double speed, BoomerangTurret parent) {
        super(projectileManager, x, y, defenderX, defenderY, 30, 30, rotation, speed, damage, range);
        this.parent = parent;
        id = ++nextID;
    } 

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        Ellipse2D.Double ellip = new Ellipse2D.Double(x - (7.5 * Coordinator.SIZE_MULTIPLIER), y - (7.5 * Coordinator.SIZE_MULTIPLIER),
            15 * Coordinator.SIZE_MULTIPLIER, 15 * Coordinator.SIZE_MULTIPLIER);
        Rectangle2D.Double rect = new Rectangle2D.Double(x - width / 2, y - (2.5 * Coordinator.SIZE_MULTIPLIER), width, 5 * Coordinator.SIZE_MULTIPLIER);
        AffineTransform at = AffineTransform.getRotateInstance(theta, x, y);
        Shape rotatedRect = at.createTransformedShape(rect);
        Rectangle2D.Double rect2 = new Rectangle2D.Double(x - (2.5 * Coordinator.SIZE_MULTIPLIER), y - height / 2, 5 * Coordinator.SIZE_MULTIPLIER, height);
        AffineTransform at2 = AffineTransform.getRotateInstance(theta, x, y);
        Shape rotatedRect2 = at2.createTransformedShape(rect2);

        g2.fill(ellip);
        g2.fill(rotatedRect);
        g2.fill(rotatedRect2);
    }

    public void move(){
        x += speed * Math.cos(rotation) * direction;
        y += speed * Math.sin(rotation) * direction;
        theta = (theta + Math.toRadians(10 * Coordinator.SIZE_MULTIPLIER)) % (Math.PI * 2);
        if(projectileManager.checkCollision(this)){ // determines if there has been any collisions
            SoundPlayer.playHit();
        }
        if(Math.sqrt(Math.pow(x - defenderX, 2) + Math.pow(y - defenderY, 2)) >= range && direction == 1) { // change direction
            direction = -1;
            id *= -1; // flip id
        }
        if(Math.abs(x - defenderX) < 3 && Math.abs(y - defenderY) < 3 && direction == -1){
            parent.boomerangReturned();
            destroy();
        }
    }

    public int getId(){
        return id;
    }
    
}
