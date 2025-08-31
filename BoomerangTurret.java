import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;


public class BoomerangTurret extends Defender{

    protected double boomerangSpeed = 3.5 * Coordinator.SIZE_MULTIPLIER;
    protected int boomerangsAvailable = 1;

    public BoomerangTurret(DefenderManager defenderManager){
        this(defenderManager, 0, 0);
    }

    public BoomerangTurret(DefenderManager defenderManager, double x, double y){
        super(defenderManager, x, y, 300, 100, 100, 40, 10, 300);
    }

    @Override
    public void initializeUpgrades() {
        upgradeCost1 = 100;
        upgradeCost2 = 200;
        upgradeCost3 = 250;

        upgradeName1 = "Increased range";
        upgradeName2 = "Faster boomerang";
        upgradeName3 = "Two boomerangs";
    }

    @Override
    public void upgrade1() {
        radius = 400 * Coordinator.SIZE_MULTIPLIER;
    }

    @Override
    public void upgrade2() {
        boomerangSpeed = 7 * Coordinator.SIZE_MULTIPLIER;
    }

    @Override
    public void upgrade3() {
        boomerangsAvailable++;
    }

    @Override
    public void draw(Graphics g){
        super.draw(g);
        Graphics2D g2 = (Graphics2D) g;

        Ellipse2D.Double ellip = new Ellipse2D.Double(x - width / 2, y - height / 2, width, height);
        Ellipse2D.Double ellip2 = new Ellipse2D.Double(x - (12.5 * Coordinator.SIZE_MULTIPLIER), y - (12.5 * Coordinator.SIZE_MULTIPLIER),
            (int)(25 * Coordinator.SIZE_MULTIPLIER), (int)(25 * Coordinator.SIZE_MULTIPLIER));
        g2.draw(ellip);
        g2.draw(ellip2);
    }

    @Override
    public void move(){
        target = defenderManager.setTarget(this); // determine target
        if(target != null) { // set rotation to face target
            double dx = target.getX() - x;
            double dy = target.getY() - y;

            rotation = Math.atan2(dy, dx);
        }
        
        if(reloadCount != 0) reloadCount = (reloadCount + 1) % reloadTicks; // reload if it does not have bullet
        else if(target != null && boomerangsAvailable > 0) { // otherwise shoot and start reload count
            shoot();
            boomerangsAvailable--;
            if(boomerangsAvailable > 0) reloadCount++;
        }
        update();
    }

    @Override
    public void shoot() {
        BoomerangProjectile p = new BoomerangProjectile(defenderManager.getGameManager().getProjectileManager(), x, y, x, y, rotation, damage, radius, boomerangSpeed, this);
        defenderManager.getGameManager().getProjectileManager().add(p);
    }

    public void boomerangReturned(){
        boomerangsAvailable++;
        if(reloadCount == 0) reloadCount++;
    }

    @Override
    public String getName() {
        return "Boomerang Turret";
    }

    @Override
    public String getDescription() {
        return "Damage: 10 - 25\nRange: Medium\nReload Speed: Medium";
    }
    
}
