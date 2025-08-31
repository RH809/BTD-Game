import java.awt.Graphics;
/**
 * Parent class for projectiles
 */
public abstract class Projectile {

    protected ProjectileManager projectileManager; // manager of all of the projectiles
    protected double x, y; // position of projectile
    protected int width, height; // dimensions of projectile
    protected double defenderX, defenderY; // position of defender that fired projectile
    protected double speed; // speed of projectile
    protected double rotation; // direction of rotation of projectile
    protected int damage; // damage the projectile deals to attackers
    protected double range; // range of the projectile (how far it can travel from defender until it is destroyed)

    public Projectile(ProjectileManager projectileManager, double x, double y, double defenderX, double defenderY, int width, int height, double rotation, double speed, int damage, double range){
        this.projectileManager = projectileManager;
        this.x = x;
        this.y = y;
        this.defenderX = defenderX;
        this.defenderY = defenderY;
        this.width = (int)(width * Coordinator.SIZE_MULTIPLIER);
        this.height = (int)(height * Coordinator.SIZE_MULTIPLIER);
        this.speed = speed * Coordinator.SIZE_MULTIPLIER;
        this.rotation = rotation;
        this.damage = damage;
        this.range = range;
    }

    /**
     * moves in the direction of its rotation
     */
    public void move(){
        x += speed * Math.cos(rotation);
        y += speed * Math.sin(rotation);
        if(projectileManager.checkCollision(this)){ // determines if there has been any collisions; if so, destroy
            SoundPlayer.playHit();
            destroy();
        }
        if(x < 0 || y < 0 || x > 2000 * Coordinator.SIZE_MULTIPLIER || y > 1200 * Coordinator.SIZE_MULTIPLIER || Math.sqrt(Math.pow(x - defenderX, 2) + Math.pow(y - defenderY, 2)) > range) destroy(); // destroy if the projectile has travelled outside its range
    }

    public void destroy(){
        projectileManager.remove(this);
    }

    public abstract void draw(Graphics g);

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public double getRotation(){
        return rotation;
    }

    public int getDamage(){
        return damage;
    }
}
