import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Target {
    protected double x, y;
    protected double turretX, turretY;
    protected int radius, turretRange;
    protected boolean invalid;

    public Target(double x, double y, int radius, int turretRange){
        this.x = x;
        this.y = y;
        this.radius = (int)(radius * Coordinator.SIZE_MULTIPLIER);
        this.turretRange = (int)(turretRange * Coordinator.SIZE_MULTIPLIER);
        this.turretX = x;
        this.turretY = y;
    }

    public void draw(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g.setColor((invalid) ? Color.RED : Color.BLUE);
        Ellipse2D.Double ellip = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
        Ellipse2D.Double ellip2 = new Ellipse2D.Double(x - (radius - 10 * Coordinator.SIZE_MULTIPLIER), y - (radius - 10 * Coordinator.SIZE_MULTIPLIER),
            2 * (radius - 10 * Coordinator.SIZE_MULTIPLIER), 2 * (radius - 10 * Coordinator.SIZE_MULTIPLIER));
        g2.draw(ellip);
        g2.draw(ellip2);
    }

    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void setRadius(int radius){
        this.radius = (int)(radius * Coordinator.SIZE_MULTIPLIER);
    }

    public void setInvalid(boolean invalid){
        this.invalid = invalid || (Math.sqrt((Math.pow((x - turretX), 2)) + (Math.pow((y - turretY), 2))) > turretRange); // check if it is within valid distance too
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public int getRadius(){
        return radius;
    }

    public boolean isInvalid(){
        return invalid;
    }
}
