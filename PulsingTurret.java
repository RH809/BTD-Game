import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;


public class PulsingTurret extends Defender{

    protected int stunTicks = 100;

    public PulsingTurret(DefenderManager defenderManager){
        this(defenderManager, 0, 0);
    }

    public PulsingTurret(DefenderManager defenderManager, double x, double y){
        super(defenderManager, x, y, 150, 80, 80, 200, 2, 300);
        priority = null;
        targetButton = null;
    }

    @Override
    public void initializeUpgrades() {
        upgradeCost1 = 150;
        upgradeCost2 = 200;
        upgradeCost3 = 200;

        upgradeName1 = "Longer stun";
        upgradeName2 = "Increased range";
        upgradeName3 = "Faster reload";
    }

    @Override
    public void upgrade1() {
        stunTicks = 250;
    }

    @Override
    public void upgrade2() {
        radius = 200 * Coordinator.SIZE_MULTIPLIER;
    }

    @Override
    public void upgrade3() {
        reloadTicks = 150;
    }

    @Override
    public void draw(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.BLUE);
        if(cancel) g.setColor(x > 2050 * Coordinator.SIZE_MULTIPLIER ? Color.GRAY : Color.RED);
        if(selected){
            Ellipse2D.Double range = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
            g2.draw(range);
        }
        Rectangle2D.Double rect1 = new Rectangle2D.Double(x - width / 6, y - height / 2, width / 3, height);
        Rectangle2D.Double rect2 = new Rectangle2D.Double(x - width / 2, y - height / 6, width, height / 3);
        Ellipse2D.Double ellip = new Ellipse2D.Double(x - width / 4, y - height / 4, width / 2, height / 2);
        g2.draw(rect1);
        g2.draw(rect2);
        g2.draw(ellip);
    }

    @Override
    public void move(){
        if(reloadCount != 0) reloadCount = (reloadCount + 1) % reloadTicks; // reload if it does not have bullet
        else { // otherwise shoot and start reload count
            shoot();
            reloadCount++;
        }
        update();
    }

    @Override
    public void shoot() {
        Pulse p = new Pulse(defenderManager.getGameManager().getProjectileManager(), x, y, damage, radius, 4, stunTicks);
        defenderManager.getGameManager().getProjectileManager().add(p);
    }

    @Override
    public String getName() {
        return "Pulsing Turret";
    }

    @Override
    public String getDescription() {
        return "Damage: 2\nRange: Low\nReload Speed: Medium";
    }
    
}
