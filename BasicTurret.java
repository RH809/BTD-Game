import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


import java.awt.Shape;
import java.awt.geom.AffineTransform;

public class BasicTurret extends Defender{

    public BasicTurret(DefenderManager defenderManager){
        this(defenderManager, 0, 0);
    }
    public BasicTurret(DefenderManager defenderManager, double x, double y){
        super(defenderManager, x, y, 200, 80, 40, 90, 10, 150);
    }

    @Override
    public void initializeUpgrades(){
        upgradeCost1 = 50;
        upgradeCost2 = 75;
        upgradeCost3 = 100;
        upgradeName1 = "Increased range";
        upgradeName2 = "Increased damage";
        upgradeName3 = "Faster reload";
    }

    @Override
    public void upgrade1(){
        radius = (int)(230 * Coordinator.SIZE_MULTIPLIER);
    }

    @Override
    public void upgrade2(){
        damage = 20;
    }

    @Override
    public void upgrade3(){
        reloadTicks = 75;
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        Graphics2D g2 = (Graphics2D) g;

        Rectangle2D.Double rect = new Rectangle2D.Double(x - width / 2, y - height / 2, width, height);
        AffineTransform at = AffineTransform.getRotateInstance(rotation, x, y);
        Shape rotatedRect = at.createTransformedShape(rect);
        g2.draw(rotatedRect);
    }

    public void shoot(){
        //System.out.println(rotation * 180 / Math.PI);
        BasicProjectile p = new BasicProjectile(defenderManager.getGameManager().getProjectileManager(), x + (width / 2) * Math.cos(rotation),
            y + (width / 2) * Math.sin(rotation), x, y, rotation, damage, radius);
        defenderManager.getGameManager().getProjectileManager().add(p);
    }
    @Override
    public String getName() {
        return "Basic Turret";
    }
    @Override
    public String getDescription() {
        return "Damage: 10 - 20\nRange: Medium\nReload Speed: Medium";
    }
    
}
