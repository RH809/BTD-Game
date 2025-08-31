import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * Parent class for all attackers
 */
public abstract class Attacker {
    // movement using parametric equation
    // \left(-0.000017914t^{4}+0.00353185t^{3}-0.206458t^{2}+4.24157t-0.0158984,-0.0000421384t^{4}+0.00825601t^{3}-0.492829t^{2}+8.66422t+59.8776\right)
    // 0 - 103.18
    
    public static final int RADIUS = (int)(20 * Coordinator.SIZE_MULTIPLIER); // radius of the circle
    protected AttackerManager attackerManager; // manager of all of the attackers
    protected double move_tick = 0; // the tick that determines position (input to the position function)
    protected double x, y; // position
    protected int count = 1; // count that determines the move tick
    protected int speedIndex; // the index of the corresponding speed in AttackManger's speed array
    protected Color color; // color of the attacker
    protected int damage; // damage the attack does if it reaches the end
    protected int health; // health of the attacker
    protected int moneyDrop; // cash amount gained from killing the attacker
    protected int stunTicks; // how long the stun will last
    protected ArrayList<Integer> shockwaves; // id of all shockwaves that hit (to avoid re-hit)
    protected ArrayList<Integer> pulses; // id of all pulses that hit (to avoid re-hit) 
    protected ArrayList<Integer> boomerangs; // id of all boomerangs that hit (to avoid re-hit)

    public Attacker(AttackerManager attackerManager, int speedIndex, Color color, int health, int moneyDrop, int damage){
        this.attackerManager = attackerManager;
        this.speedIndex = speedIndex;
        this.color = color;
        this.health = health;
        this.moneyDrop = moneyDrop;
        this.damage = damage;
        shockwaves = new ArrayList<Integer>();
        pulses = new ArrayList<Integer>();
        boomerangs = new ArrayList<Integer>();
        // starting position:
        x = 0; // x starting rounds to 0
        y = (1200 - (11 * 60)) * Coordinator.SIZE_MULTIPLIER; // y starting rounds to 60 (need to multiply by 11 for scaling and subtract from 1200 because bottom of the screen is 1200)
    }

    /**
     * moves the attacker by setting its position
     */
    public void move(){
        if(stunTicks > 0){
            if(Math.random() > 0.25) setPosition(); // 25% chance of not moving
            stunTicks--;
        }
        else
            setPosition();
    }

    /**
     * sets position based on the move tick
     */
    private void setPosition(){
        if(health <= 0){ // kill the attacker if it has <= 0 health
            destroy();
            SoundPlayer.playPop();
            attackerManager.grantMoneyDrop(moneyDrop);
            return;
        }
        count++;
        if(count >= AttackerManager.COUNT_LIMIT / AttackerManager.speeds[speedIndex]) { // determine if the attacker has reached the end
            attackerManager.damagePlayer(damage);
            SoundPlayer.playDamage();
            destroy();
        }
        set_move_tick(); // set move tick based on the count

        x = (-0.000017914 * (Math.pow(move_tick, 4)) + 0.00353185 * (Math.pow(move_tick, 3)) - 0.206458 * (Math.pow(move_tick, 2)) + 4.24157 * (move_tick) - 0.0158984);
        y = (-0.0000421384 * (Math.pow(move_tick, 4)) + 0.00825601 * (Math.pow(move_tick, 3)) - 0.492829 * (Math.pow(move_tick, 2)) + 8.66422 * (move_tick) + 59.8776);

        x *= 17 * Coordinator.SIZE_MULTIPLIER;
        y *= 11;
        y = 1200 - y;
        y *= Coordinator.SIZE_MULTIPLIER;
    }

    /**
     * sets move tick based on speed and count
     */
    private void set_move_tick(){
        move_tick = AttackerManager.ticks[speedIndex][count - 1];
    }

    public void destroy(){
        attackerManager.remove(this);
    }

    public void addShockwave(int id){
        shockwaves.add(id);
    }

    public boolean checkShockwave(int id){
        return shockwaves.contains(id);
    }

    public void addPulse(int id){
        pulses.add(id);
    }

    public boolean checkPulse(int id){
        return pulses.contains(id);
    }

    public void addBoomerang(int id){
        boomerangs.add(id);
    }

    public boolean checkBoomerang(int id){
        return boomerangs.contains(id);
    }

    public void stun(int stunTicks){
        this.stunTicks = Math.max(stunTicks, this.stunTicks);
    }

    public void draw(Graphics g) {
        g.setColor(color);
        Ellipse2D.Double oval = new Ellipse2D.Double(x - Attacker.RADIUS / 2, y - Attacker.RADIUS / 2, Attacker.RADIUS, Attacker.RADIUS);
        ((Graphics2D)g).fill(oval);

        if(stunTicks > 0){
            g.setColor(Color.BLUE);
            Ellipse2D.Double stun = new Ellipse2D.Double(x - Attacker.RADIUS / 2, y - Attacker.RADIUS / 2, Attacker.RADIUS, Attacker.RADIUS);
            ((Graphics2D)g).draw(stun);
        }
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public double getCompletion(){
        return count / (AttackerManager.COUNT_LIMIT / AttackerManager.speeds[speedIndex]);
    }

    public void takeDamage(int damage){
        health -= damage;
    }

    public abstract String getName();

    public String getDescription(){
        return "Health: " + health + "\nDamage: " + damage + "\nMoney drop: $" + moneyDrop;
    }

}
