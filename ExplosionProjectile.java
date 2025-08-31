public abstract class ExplosionProjectile extends Projectile{

    protected int radius;

    public ExplosionProjectile(ProjectileManager projectileManager, double x, double y, double defenderX, double defenderY, int radius, double rotation, double speed, int damage, double range){
        super(projectileManager, x, y, defenderX, defenderY, 0, 0, rotation, speed, damage, range);
        this.radius = (int)(radius * Coordinator.SIZE_MULTIPLIER);
    }

    public int getRadius(){
        return radius;
    }
}
