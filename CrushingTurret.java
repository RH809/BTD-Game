import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class CrushingTurret extends Defender{

    protected boolean shockwave = false;

    public CrushingTurret(DefenderManager defenderManager){
        this(defenderManager, 0, 0);
    }

    public CrushingTurret(DefenderManager defenderManager, double x, double y){
        super(defenderManager, x, y, 150, 170, 170, 1300, 100, 750);
        priority = null;
        targetButton = null;
    }

    @Override
    public void initializeUpgrades() {
        upgradeCost1 = 200;
        upgradeCost2 = 300;
        upgradeCost3 = 400;

        upgradeName1 = "Increased range";
        upgradeName2 = "Shockwaves";
        upgradeName3 = "Faster reload";

    }

    @Override
    public void upgrade1() {
        radius = (int)(200 * Coordinator.SIZE_MULTIPLIER);
    }

    @Override
    public void upgrade2() {
        shockwave = true;
    }

    @Override
    public void upgrade3() {
        reloadTicks = 850;
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
        Rectangle2D.Double rect = new Rectangle2D.Double(x - (width - 100 * Coordinator.SIZE_MULTIPLIER) / 2, y - (height - 100 * Coordinator.SIZE_MULTIPLIER) / 2,
            width - 100 * Coordinator.SIZE_MULTIPLIER, height - 100 * Coordinator.SIZE_MULTIPLIER);
        Ellipse2D.Double ellip1 = new Ellipse2D.Double((x + (width - 50 * Coordinator.SIZE_MULTIPLIER) / 2), y - 12.5 * Coordinator.SIZE_MULTIPLIER,
            25 * Coordinator.SIZE_MULTIPLIER,25 * Coordinator.SIZE_MULTIPLIER);
        Ellipse2D.Double ellip2 = new Ellipse2D.Double((x - (width - 50 * Coordinator.SIZE_MULTIPLIER) / 2) - 25 * Coordinator.SIZE_MULTIPLIER, y - 12.5 * Coordinator.SIZE_MULTIPLIER,
            25 * Coordinator.SIZE_MULTIPLIER, 25 * Coordinator.SIZE_MULTIPLIER);
        Ellipse2D.Double ellip3 = new Ellipse2D.Double(x - 12.5 * Coordinator.SIZE_MULTIPLIER, (y + (height - 50 * Coordinator.SIZE_MULTIPLIER) / 2),
            25 * Coordinator.SIZE_MULTIPLIER, 25 * Coordinator.SIZE_MULTIPLIER);
        Ellipse2D.Double ellip4 = new Ellipse2D.Double(x  - 12.5 * Coordinator.SIZE_MULTIPLIER, (y - (height - 50 * Coordinator.SIZE_MULTIPLIER) / 2) - 25 * Coordinator.SIZE_MULTIPLIER,
            25 * Coordinator.SIZE_MULTIPLIER, 25 * Coordinator.SIZE_MULTIPLIER);

        g2.draw(rect);
        g2.draw(ellip1);
        g2.draw(ellip2);
        g2.draw(ellip3);
        g2.draw(ellip4);

        Ellipse2D.Double ellip = new Ellipse2D.Double(x - (width - 50 * Coordinator.SIZE_MULTIPLIER) / 2, y - (height - 50 * Coordinator.SIZE_MULTIPLIER) / 2,
            (width - 50 * Coordinator.SIZE_MULTIPLIER), (height - 50 * Coordinator.SIZE_MULTIPLIER));
        double p = ((double)reloadCount) / reloadTicks;
        if(p == 0) p = 1;
        Ellipse2D.Double fillEllip = new Ellipse2D.Double(x - ((width - 50 * Coordinator.SIZE_MULTIPLIER) * p) / 2, y - ((height - 50 * Coordinator.SIZE_MULTIPLIER) * p) / 2,
            (width - 50 * Coordinator.SIZE_MULTIPLIER) * p, (height - 50 * Coordinator.SIZE_MULTIPLIER) * p);
        g2.draw(ellip);
        g2.fill(fillEllip);
    }

    @Override
    public void move(){
        if(reloadCount != 0) reloadCount = (reloadCount + 1) % reloadTicks; // reload if it does not have bullet
        else { // otherwise shoot and start reload count
            shoot();
            SoundPlayer.playCrush();
            reloadCount++;
        }
        update();
    }

    @Override
    public void shoot() {
        Crush p = new Crush(defenderManager.getGameManager().getProjectileManager(), x, y, (int)(radius / Coordinator.SIZE_MULTIPLIER), damage, shockwave);
        defenderManager.getGameManager().getProjectileManager().add(p);
    }

    @Override
    public String getName() {
        return "Crushing Turret";
    }

    @Override
    public String getDescription() {
        return "Damage: Crush - 100, Shockwave - 25\nRange: Low\nReload Speed: Slow";
    }
    
}
