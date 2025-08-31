import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;


public class OrbitingTurret extends Defender{

    protected int numOrbiters = 2;
    protected OrbitingProjectile[] orbiters;
    protected int direction = -1;

    public OrbitingTurret(DefenderManager defenderManager){
        super(defenderManager, 0, 0, 200, 100, 100, 10, 10, 500);
    }
    

    public OrbitingTurret(DefenderManager defenderManager, double x, double y){
        super(defenderManager, x, y, 200, 100, 100, 10, 10, 500);

        upgrade3.removeActionListener(upgrade3.getActionListeners()[0]);
        upgrade3.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                upgrade3();
                player.spendMoney(upgradeCost3);
                sellAmount += (int)(upgradeCost3 * 0.75);
                upgrade3.setText(upgradeName3 + " ($" + upgradeCost3 + ")");
                if(numOrbiters >= 6){
                    upgrade3.setEnabled(false);
                    upgrade3.setBackground(Color.BLUE);
                    upgrade3.setText(upgradeName3 + " (maxed out)");
                    upgraded3 = true;
                }
                SoundPlayer.playBuy();
            }
        });

        priority = null;
        toggle();

        orbiters = new OrbitingProjectile[6];
        OrbitingProjectile p = new OrbitingProjectile(defenderManager.getGameManager().getProjectileManager(), x - radius, y, x, y, damage, radius);
        defenderManager.getGameManager().getProjectileManager().add(p);
        orbiters[0] = p;

        p = new OrbitingProjectile(defenderManager.getGameManager().getProjectileManager(), x - radius, y, x, y, damage, radius);
        defenderManager.getGameManager().getProjectileManager().add(p);
        orbiters[1] = p;
    }

    @Override
    public void initializeUpgrades() {
        upgradeCost1 = 150;
        upgradeCost2 = 200;
        upgradeCost3 = 200;
        upgradeName1 = "Increased damage";
        upgradeName2 = "Faster orbit";
        upgradeName3 = "+1 orbiter";
    }

    @Override
    public void upgrade1() {
        damage = 15;
    }

    @Override
    public void upgrade2() {
        reloadTicks = 6;
    }

    @Override
    public void upgrade3() {
        if(numOrbiters >= 6) return;
        OrbitingProjectile p = new OrbitingProjectile(defenderManager.getGameManager().getProjectileManager(), x - radius, y, x, y, damage, radius);
        defenderManager.getGameManager().getProjectileManager().add(p);
        orbiters[numOrbiters] = p;
        numOrbiters++;
        shoot();
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.BLUE);
        if(cancel) g.setColor(x > 2050 * Coordinator.SIZE_MULTIPLIER ? Color.GRAY : Color.RED);
        if(selected){
            Ellipse2D.Double selected = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
            g2.draw(selected);
        }
        Ellipse2D.Double ellip = new Ellipse2D.Double(x - (width - 30 * Coordinator.SIZE_MULTIPLIER) / 2, y - (height - 30 * Coordinator.SIZE_MULTIPLIER) / 2,
            width - 30 * Coordinator.SIZE_MULTIPLIER, height - 30 * Coordinator.SIZE_MULTIPLIER);
        g2.draw(ellip);
        for(int i = 0; i < 6; i++){
            ellip = new Ellipse2D.Double(x - 7.5 * Coordinator.SIZE_MULTIPLIER - (width - 30 * Coordinator.SIZE_MULTIPLIER) / 2,
                y - 7.5 * Coordinator.SIZE_MULTIPLIER, 15 * Coordinator.SIZE_MULTIPLIER, 15 * Coordinator.SIZE_MULTIPLIER);
            AffineTransform at = AffineTransform.getRotateInstance(i * Math.PI / 3, x, y);
            Shape newEllip = at.createTransformedShape(ellip);
            g2.draw(newEllip);
        }
    }

    @Override
    public void move(){
        if(reloadCount != 0) reloadCount = (reloadCount + 1) % reloadTicks; // reload if it does not have bullet
        else if(!defenderManager.getGameManager().isWaiting()){ // otherwise shoot and start reload count
            rotation = (rotation + direction * Math.toRadians(5)) % (Math.PI * 2); // rotate by 5 degrees
            shoot();
            reloadCount++;
        }
        update();
    }

    @Override
    public void shoot() {
        for(int i = 0; i < numOrbiters; i++){
            orbiters[i].setAngle(rotation + (i * 2 * Math.PI / numOrbiters));
        }
    }

    @Override
    public void destroy(){
        for(int i = 0; i < numOrbiters; i++){
            orbiters[i].destroy();
        }
        defenderManager.remove(this);
    }

    @Override
    public void toggle(){
        if(direction == 1){ // switch to counter-clockwise
            direction = -1;
            targetButton.setText("Orbiting counter-clockwise");
            targetButton.setBackground(Color.CYAN);
        }
        else{ // switch to clockwise
            direction = 1;
            targetButton.setText("Orbiting clockwise");
            targetButton.setBackground(Color.YELLOW);
        }
    }

    @Override
    public String getName() {
        return "Orbiting Turret";
    }

    @Override
    public String getDescription() {
        return "Damage: 10 - 15\nRange: Medium\n Reload Speed: N/A";
    }
    
}
