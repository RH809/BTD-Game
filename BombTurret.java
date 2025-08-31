import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;


public class BombTurret extends TargetDefender{

    protected boolean shockwave = false;

    public BombTurret(DefenderManager defenderManager){
        this(defenderManager, 0, 0);
    }

    public BombTurret(DefenderManager defenderManager, double x, double y){
        super(defenderManager, x, y, 750, 150, 150, 900, 50, 1000);
        target = new Target(x, y, 50, 750);
    }

    

    @Override
    public void initializeUpgrades() {
        upgradeCost1 = 250;
        upgradeCost2 = 350;
        upgradeCost3 = 500;

        upgradeName1 = "Shockwaves";
        upgradeName2 = "Faster reload";
        upgradeName3 = "Bigger explosions";
        
    }

    @Override
    public void upgrade1() {
        shockwave = true;
    }

    @Override
    public void upgrade2() {
        reloadTicks = 700;
    }

    @Override
    public void upgrade3() {
        target.setRadius(75);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.BLUE);
        if(cancel) g.setColor(x > 2050 * Coordinator.SIZE_MULTIPLIER ? Color.GRAY : Color.RED);
        if(selected){
            Ellipse2D.Double range = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
            g2.draw(range);
            if(targetSelected && target != null){
                target.draw(g);
            }
        }
        Rectangle2D.Double rect = new Rectangle2D.Double(x - width / 2, y - height / 2, width, height);
        Ellipse2D.Double ellip = new Ellipse2D.Double(x - (int)(50 * Coordinator.SIZE_MULTIPLIER), y - (int)(50 * Coordinator.SIZE_MULTIPLIER),
            (int)(100 * Coordinator.SIZE_MULTIPLIER), (int)(100 * Coordinator.SIZE_MULTIPLIER));
        double p = ((double) reloadCount) / reloadTicks;
        if(p == 0) p = 1;
        Ellipse2D.Double fill = new Ellipse2D.Double(x - (50 * Coordinator.SIZE_MULTIPLIER * p), y - (50 * Coordinator.SIZE_MULTIPLIER * p),
            100 * Coordinator.SIZE_MULTIPLIER * p, 100 * Coordinator.SIZE_MULTIPLIER * p);
        g2.draw(rect);
        g2.draw(ellip);
        g2.fill(fill);
    }

    @Override
    public void move(){
        if(reloadCount != 0) reloadCount = (reloadCount + 1) % reloadTicks; // reload if it does not have bullet
        else if(targetSelected) { // otherwise shoot and start reload count
            shoot();
            reloadCount++;
        }
        update();
    }

    @Override
    public void shoot() {
        if(targetSelected){
            BombProjectile p = new BombProjectile(defenderManager.getGameManager().getProjectileManager(), x, y, (int)(target.getRadius() / Coordinator.SIZE_MULTIPLIER),
                Math.atan2((target.getY() - y), (target.getX() - x)), 75, damage, target.getX(), target.getY(), shockwave);
            defenderManager.getGameManager().getProjectileManager().add(p);
        }
        
    }

    @Override
    public String getName() {
        return "Bomb Turret";
    }

    @Override
    public String getDescription() {
        return "Damage: Explosion - 50, Shockwave - 20\nRange: High\nReload Speed: Slow";
    }
    
}
