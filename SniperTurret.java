import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;


public class SniperTurret extends Defender{

    protected boolean piercingShots = false, splitShot = false;

    public SniperTurret(DefenderManager defenderManager){
        this(defenderManager, 0, 0);
    }

    public SniperTurret(DefenderManager defenderManager, double x, double y){
        super(defenderManager, x, y, 3000, 200, 90, 450, 50, 400);
    }

    @Override
    public void initializeUpgrades() {
        upgradeCost1 = 200;
        upgradeCost2 = 200;
        upgradeCost3 = 300;

        upgradeName1 = "Faster reload";
        upgradeName2 = "Split shot";
        upgradeName3 = "Piercing shots";
    }

    @Override
    public void upgrade1() {
        reloadTicks = 300;
    }

    @Override
    public void upgrade2() {
        splitShot = true;
    }

    @Override
    public void upgrade3() {
        piercingShots = true;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.BLUE);
        if(cancel) g.setColor(x > 2050 ? Color.GRAY : Color.RED);
        if(selected){
            Ellipse2D.Double selected = new Ellipse2D.Double(x - 125 * Coordinator.SIZE_MULTIPLIER, y - 125 * Coordinator.SIZE_MULTIPLIER,
                250 * Coordinator.SIZE_MULTIPLIER, 250 * Coordinator.SIZE_MULTIPLIER);
            g2.draw(selected);
        }
        Rectangle2D.Double rect = new Rectangle2D.Double(x - width / 2, y - height / 2, width, height);
        AffineTransform at = AffineTransform.getRotateInstance(rotation, x, y);
        Shape rotatedRect = at.createTransformedShape(rect);
        g2.draw(rotatedRect);
    }

    @Override
    public void shoot() {
        SniperProjectile p = new SniperProjectile(defenderManager.getGameManager().getProjectileManager(), x + (width / 2) * Math.cos(rotation),
            y + (width / 2) * Math.sin(rotation), x, y, rotation, damage, radius, piercingShots);
        defenderManager.getGameManager().getProjectileManager().add(p);
        if(splitShot){
            SniperProjectile p2 = new SniperProjectile(defenderManager.getGameManager().getProjectileManager(), x + (width / 2) * Math.cos(rotation),
                y + (width / 2) * Math.sin(rotation), x, y, rotation + Math.toRadians(20), damage, radius, piercingShots);
            defenderManager.getGameManager().getProjectileManager().add(p2);

            SniperProjectile p3 = new SniperProjectile(defenderManager.getGameManager().getProjectileManager(), x + (width / 2) * Math.cos(rotation),
                y + (width / 2) * Math.sin(rotation), x, y, rotation - Math.toRadians(20), damage, radius, piercingShots);
            defenderManager.getGameManager().getProjectileManager().add(p3);
        }
    }

    @Override
    public String getName() {
        return "Sniper Turret";
    }

    @Override
    public String getDescription() {
        return "Damage: 50\nRange: Unlimited\nReload Speed: Slow";
    }
    
}
