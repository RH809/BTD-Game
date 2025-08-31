import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class BombProjectile extends ExplosionProjectile{

    protected int totalTicks, tickCount;
    protected boolean shockwave;
    protected double targetX, targetY;

    public BombProjectile(ProjectileManager projectileManager, double x, double y, int radius, double rotation, double speed, int damage, double targetX, double targetY, boolean shockwave){
        super(projectileManager, x, y, x, y, radius, rotation, speed, damage, 0);
        this.targetX = targetX;
        this.targetY = targetY;
        this.shockwave = shockwave;

        double distance = Math.sqrt(Math.pow(targetX - x, 2) + Math.pow(targetY - y, 2));
        double ticks = distance / speed;
        totalTicks = (int) ticks * 50; // round ticks to an integer
        this.speed = distance / totalTicks; // get new speed using rounded ticks
        tickCount = 0;
    }
    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.BLUE);
        Ellipse2D.Double ellip = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
        g2.fill(ellip);
    }

    @Override
    public void move(){
        tickCount++;
        x += speed * Math.cos(rotation);
        y += speed * Math.sin(rotation);

        if(tickCount % 20 == 0) radius += (tickCount < totalTicks / 2) ? 1 : -1;

        if(tickCount >= totalTicks){
            projectileManager.explosion(this);
            SoundPlayer.playExplosion();
            if(shockwave){
                Shockwave s = new Shockwave(projectileManager, x, y, defenderX, defenderY, (int)(radius / Coordinator.SIZE_MULTIPLIER), 20, 60, 3);
                projectileManager.add(s);
            }
            destroy();
        }
    }
    
}
