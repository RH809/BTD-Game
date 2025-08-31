import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Pulse extends ExplosionProjectile{

    public static int nextID = 0;
    
    protected int stunTicks;
    protected int id;
    protected int radiusSpeed;

    public Pulse(ProjectileManager projectileManager, double x, double y, int damage, double range, int radiusSpeed, int stunTicks){
        super(projectileManager, x, y, x, y, 0, 0, 0, damage, range);
        this.radiusSpeed = (int)(radiusSpeed * Coordinator.SIZE_MULTIPLIER);
        this.stunTicks = stunTicks;
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
        if(radius >= range) destroy();
    }

    public int getId(){
        return id;
    }

    public int getStunTicks(){
        return stunTicks;
    }
}
