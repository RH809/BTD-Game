import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;


public class MachineGunTurret extends Defender{

    protected int capacity = 100, bulletsRemaining = 100;
    protected boolean reloading = false;
    protected int fireTicks = 7;

    public MachineGunTurret(DefenderManager defenderManager){
        this(defenderManager, 0, 0);
    }
    public MachineGunTurret(DefenderManager defenderManager, double x, double y){
        super(defenderManager, x, y, 175, 140, 140, 15, 10, 1250);
    }
    @Override
    public void initializeUpgrades() {
        upgradeCost1 = 300;
        upgradeCost2 = 600;
        upgradeCost3 = 700;
        upgradeName1 = "Increased range";
        upgradeName2 = "Faster reload";
        upgradeName3 = "Increased capacity";
    }
    @Override
    public void upgrade1() {
        radius = 220 * Coordinator.SIZE_MULTIPLIER;
    }
    @Override
    public void upgrade2() {
        reloadTicks = 10;
    }
    @Override
    public void upgrade3() {
        capacity = 150;
        bulletsRemaining += 50;
    }

    @Override
    public void draw(Graphics g){
        double width = this.width - 20 * Coordinator.SIZE_MULTIPLIER;
        double height = this.height - 20 * Coordinator.SIZE_MULTIPLIER;
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.BLUE);
        if(cancel) g.setColor(x > 2050 * Coordinator.SIZE_MULTIPLIER ? Color.GRAY : Color.RED);
        if(selected){
            Ellipse2D.Double range = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
            g2.draw(range);
        }
        Ellipse2D.Double ellip1 = new Ellipse2D.Double(x - width / 2, y - height / 2, width, height);
        Ellipse2D.Double ellip2 = new Ellipse2D.Double(x - width / 4, y - height / 4, width / 2, height / 2);
        double p = ((double) bulletsRemaining) / capacity;
        Ellipse2D.Double fill = new Ellipse2D.Double(x - (width / 4) * p, y - (height / 4) * p,
            (width / 2) * p, (height / 2) * p);

        Rectangle2D.Double rect = new Rectangle2D.Double(x + (width / 3) * Math.cos(rotation) - (width / 3), y + (width / 3) * Math.sin(rotation) - 20 * Coordinator.SIZE_MULTIPLIER, width * 2 / 3, 40 * Coordinator.SIZE_MULTIPLIER);
        AffineTransform at = AffineTransform.getRotateInstance(rotation, x + (width / 3) * Math.cos(rotation), y + (width / 3) * Math.sin(rotation));
        Shape rotatedRect = at.createTransformedShape(rect);

        g2.draw(ellip1);
        g2.draw(ellip2);
        g2.fill(fill);
        g2.draw(rotatedRect);

    }

    @Override
    public void move(){
        if(reloading){ // reload until reaches capacity
            if(reloadCount != 0) reloadCount = (reloadCount + 1) % reloadTicks;
            else { 
                bulletsRemaining++;
                reloadCount++;
                if(bulletsRemaining >= capacity) reloading = false;
            }
        }
        else{ // fire until out of bullets
            target = defenderManager.setTarget(this); // determine target
            if(target != null) { // set rotation to face target
                double dx = target.getX() - x;
                double dy = target.getY() - y;

                rotation = Math.atan2(dy, dx);
            }

            if(reloadCount != 0) reloadCount = (reloadCount + 1) % fireTicks;
            else if(target != null) {
                shoot();
                reloadCount++;
                bulletsRemaining--;
                if(bulletsRemaining <= 0) {
                    reloading = true;
                    SoundPlayer.playMGReload();
                }
            }
        }
        
        update();
    }

    @Override
    public void shoot() {
        BasicProjectile p = new BasicProjectile(defenderManager.getGameManager().getProjectileManager(), x + (width / 2) * Math.cos(rotation),
            y + (width / 2) * Math.sin(rotation), x, y, rotation, damage, radius);
        defenderManager.getGameManager().getProjectileManager().add(p);
    }
    @Override
    public String getName() {
        return "Machine Gun Turret";
    }
    @Override
    public String getDescription() {
        return "Damage: 10\nRange: Medium\nReload Speed: Fast";
    }
    
}
