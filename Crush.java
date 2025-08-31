import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Crush extends ExplosionProjectile{

    protected int tickCount = 0;
    protected boolean shockwave;

    public Crush(ProjectileManager projectileManager, double x, double y, int radius, int damage, boolean shockwave){
        super(projectileManager, x, y, x, y, radius, 0, 0, damage, 0);
        this.shockwave = shockwave;
    }

    @Override
    public void move(){
        tickCount++;
        projectileManager.explosion(this);
        if(tickCount >= 15){
            if(shockwave){
                Shockwave s = new Shockwave(projectileManager, x, y, defenderX, defenderY, (int)(radius / Coordinator.SIZE_MULTIPLIER), 25, 80, 2);
                projectileManager.add(s);
            }
            destroy();
        }
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.BLUE);
        Ellipse2D.Double ellip = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
        Ellipse2D.Double ellip1 = new Ellipse2D.Double(x - (radius - (5 * Coordinator.SIZE_MULTIPLIER)), y - (radius - (5 * Coordinator.SIZE_MULTIPLIER)), 2 * (radius - (5 * Coordinator.SIZE_MULTIPLIER)), 2 * (radius - (5 * Coordinator.SIZE_MULTIPLIER)));
        Ellipse2D.Double ellip2 = new Ellipse2D.Double(x - (radius - (10 * Coordinator.SIZE_MULTIPLIER)), y - (radius - (10 * Coordinator.SIZE_MULTIPLIER)), 2 * (radius - (10 * Coordinator.SIZE_MULTIPLIER)), 2 * (radius - (10 * Coordinator.SIZE_MULTIPLIER)));
        g2.draw(ellip);
        g2.draw(ellip1);
        g2.draw(ellip2);
    }
    
}
