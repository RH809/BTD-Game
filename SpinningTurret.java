import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;


public class SpinningTurret extends Defender{

    protected boolean doubleShot = false;
    protected int direction = -1;

    public SpinningTurret(DefenderManager defenderManager){
        this(defenderManager, 0, 0);
    }

    public SpinningTurret(DefenderManager defenderManager, double x, double y){
        super(defenderManager, x, y, 200, 120, 120, 20, 20, 750);
        toggle();
        priority = null;
    }

    @Override
    public void initializeUpgrades() {
        upgradeCost1 = 250;
        upgradeCost2 = 400;
        upgradeCost3 = 500;
        upgradeName1 = "Increased damage";
        upgradeName2 = "Faster reload";
        upgradeName3 = "Double shot";
    }

    @Override
    public void upgrade1() {
        damage = 40;
    }

    @Override
    public void upgrade2() {
        reloadTicks = 10;
    }

    @Override
    public void upgrade3() {
        doubleShot = true;
    }

    @Override
    public void move(){

        if(reloadCount != 0) reloadCount = (reloadCount + 1) % reloadTicks; // reload if it does not have bullet
        else if(!defenderManager.getGameManager().isWaiting()){ // otherwise shoot and start reload count
            rotation = (rotation + direction * Math.toRadians(18)) % (Math.PI * 2); // rotate by 18 degrees
            shoot();
            reloadCount++;
        }
        update();
    }

    @Override
    public void draw(Graphics g){
        double width = this.width - 20 * Coordinator.SIZE_MULTIPLIER;
        double height = this.height - 20 * Coordinator.SIZE_MULTIPLIER;
        super.draw(g);
        Graphics2D g2 = (Graphics2D)g;

        Ellipse2D.Double ellipse = new Ellipse2D.Double(x - (width / 2 - (int)(10 * Coordinator.SIZE_MULTIPLIER)), y - (height / 2 - (int)(10 * Coordinator.SIZE_MULTIPLIER)), width - (int)(20 * Coordinator.SIZE_MULTIPLIER), height - (int)(20 * Coordinator.SIZE_MULTIPLIER));
        Rectangle2D.Double rect = new Rectangle2D.Double(x + (width / 2) * Math.cos(rotation) - (width / 4), y + (width / 2) * Math.sin(rotation) - (int)(20 * Coordinator.SIZE_MULTIPLIER), width / 2, (int)(40 * Coordinator.SIZE_MULTIPLIER));
        AffineTransform at = AffineTransform.getRotateInstance(rotation, x + (width / 2) * Math.cos(rotation), y + (width / 2) * Math.sin(rotation));
        Shape rotatedRect = at.createTransformedShape(rect);
        g2.draw(ellipse);
        g2.draw(rotatedRect);
        if(doubleShot){
            Rectangle2D.Double rect2 = new Rectangle2D.Double(x - (width / 2) * Math.cos(rotation) - (width / 4), y - (width / 2) * Math.sin(rotation) - (int)(20 * Coordinator.SIZE_MULTIPLIER), width / 2, (int)(40 * Coordinator.SIZE_MULTIPLIER));
            AffineTransform at2 = AffineTransform.getRotateInstance(rotation, x - (width / 2) * Math.cos(rotation), y - (width / 2) * Math.sin(rotation));
            Shape rotatedRect2 = at2.createTransformedShape(rect2);
            g2.draw(rotatedRect2);
        }
    }

    @Override
    public void shoot() {
        BasicProjectile p = new BasicProjectile(defenderManager.getGameManager().getProjectileManager(), x + (width / 2) * Math.cos(rotation),
            y + (width / 2) * Math.sin(rotation), x, y, rotation, damage, radius);
        defenderManager.getGameManager().getProjectileManager().add(p);
        if(doubleShot){
            BasicProjectile p2 = new BasicProjectile(defenderManager.getGameManager().getProjectileManager(), x - (width / 2) * Math.cos(rotation),
                y - (width / 2) * Math.sin(rotation), x, y, rotation + Math.PI, damage, radius);
            defenderManager.getGameManager().getProjectileManager().add(p2);
        }
    }

    @Override
    public void toggle(){
        if(direction == 1){ // switch to counter-clockwise
            direction = -1;
            targetButton.setText("Spinning counter-clockwise");
            targetButton.setBackground(Color.CYAN);
        }
        else{ // switch to clockwise
            direction = 1;
            targetButton.setText("Spinning clockwise");
            targetButton.setBackground(Color.YELLOW);
        }
    }

    @Override
    public String getName() {
        return "Spinning Turret";
    }

    @Override
    public String getDescription() {
        return "Damage: 15 - 25\nRange: Low\nReload Speed: Fast";
    }
}
