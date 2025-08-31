import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Shockwave extends ExplosionProjectile{

    public static int nextID = 0;

    protected int startRadius, radiusChange, radiusSpeed;
    protected int id;

    public Shockwave(ProjectileManager projectileManager, double x, double y, double defenderX, double defenderY, int radius, int damage, int radiusChange, int radiusSpeed){
        super(projectileManager, x, y, defenderX, defenderY, radius, 0, 0, damage, 0);
        startRadius = radius;
        this.radiusChange = (int)(radiusChange * Coordinator.SIZE_MULTIPLIER);
        this.radiusSpeed = (int)(radiusSpeed * Coordinator.SIZE_MULTIPLIER);
        id = ++nextID;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.BLUE);
        Ellipse2D.Double ellip = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
        g2.draw(ellip);
    }

    @Override
    public void move(){
        projectileManager.explosion(this);
        radius += radiusSpeed;
        if(radius >= startRadius + radiusChange) destroy();
    }

    public int getId(){
        return id;
    }
    
}
