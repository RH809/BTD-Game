import java.awt.Graphics;
import java.util.ArrayList;

public class ProjectileManager {
    private ArrayList<Projectile> projectiles; // list of projectiles
    private GameManager gameManager;
    private Point p1, p2, p3, p4; // points for determining collision

    public ProjectileManager(GameManager gameManager){
        projectiles = new ArrayList<Projectile>();
        this.gameManager = gameManager;
    }

    public void reset(){
        projectiles = new ArrayList<Projectile>();
    }

    public void tick(){
        for(int i = 0; i < projectiles.size(); i++){
            projectiles.get(i).move();
        }
    }

    public void draw(Graphics g){
        for(int i = 0; i < projectiles.size(); i++){
            projectiles.get(i).draw(g);
        }
    }

    /**
     * Checks for collision between projectile and an attacker
     */
    public boolean checkCollision(Projectile p){
        // initialize the 4 corner points
        ArrayList<Attacker> attackers = gameManager.getAttackerManager().getAttackers();
        double length = Math.sqrt(Math.pow(p.getWidth() / 2, 2) + Math.pow(p.getHeight() / 2, 2));
        double angleToCorner = Math.atan((p.getWidth() * 1.0) / p.getHeight());
        p1 = new Point(p.getX() + length * Math.cos(p.getRotation() - angleToCorner), p.getY() - length * Math.sin(p.getRotation() - angleToCorner));
        p2 = new Point(p.getX() + length * Math.cos(p.getRotation() + angleToCorner), p.getY() - length * Math.sin(p.getRotation() + angleToCorner));
        p3 = new Point(p.getX() + length * Math.cos(p.getRotation() - angleToCorner + Math.PI), p.getY() - length * Math.sin(p.getRotation() - angleToCorner + Math.PI));
        p4 = new Point(p.getX() + length * Math.cos(p.getRotation() + angleToCorner + Math.PI), p.getY() - length * Math.sin(p.getRotation() + angleToCorner + Math.PI));
        Point center = new Point(p.getX(), p.getY());
        for(int i = 0; i < attackers.size(); i++){
            /*
             * for each attacker:
             * check if center of attacker is within range (radius + distance from rectangle center to corner)
             * find the point farthest away from center out of 4 corner points
             * determine line 1 and line 2
             * for each line:
             * identify a, b, c (a >= b)
             * find angle A
             * A = cos^-1((a^2 - b^2 - c^2) / (-2 * b * c)) = cos^-1(b^2 + c^2 - a^2)/(2 * b * c)
             * if angle A > 90 skip this line
             * if angle A = 90 use corner point between the two lines
             * if angle < 90:
             * d^2 = (c^2 + b^2 - a^2) / (2 * c)
             * x^2 = b^2 + d^2
             * check x to see if it is <= radius of attacker
             */
            Attacker attacker = attackers.get(i);
            Point attackerCenter = new Point(attacker.getX(), attacker.getY());
            if(Point.distance(center, attackerCenter) > (length + Attacker.RADIUS)){
                continue;
            }

            double p1Dist = Point.distance(p1, attackerCenter);
            double p2Dist = Point.distance(p2, attackerCenter);
            double p3Dist = Point.distance(p3, attackerCenter);
            double p4Dist = Point.distance(p4, attackerCenter);

            double max = Math.max(Math.max(p1Dist, p2Dist), Math.max(p3Dist, p4Dist));
            double a, b, c, d2, x, angleA;

            if(max == p1Dist){
                // ignore p1
                // p3 is corner

                // start with p2 to p3
                max = Math.max(p2Dist, p3Dist);
                if(max == p2Dist){
                    a = p2Dist;
                    b = p3Dist;  
                }
                else{
                    a = p3Dist;
                    b = p2Dist;
                }
                c = p.getWidth();
                angleA = Math.acos((Math.pow(b, 2) + Math.pow(c, 2) - Math.pow(a, 2)) / (2 * b * c));
                if(angleA < 90){
                    // swap to the other side
                    max = Math.max(p3Dist, p4Dist);
                    if(max == p3Dist){
                        a = p3Dist;
                        b = p4Dist;  
                    }
                    else{
                        a = p4Dist;
                        b = p3Dist;
                    }
                    c = p.getHeight();
                }
                else if(angleA == 90){
                    if(Point.distance(p3, attackerCenter) <= (length + Attacker.RADIUS)){ // check distance from corner
                        // ***collision***
                        if(p instanceof BoomerangProjectile){
                            int id = ((BoomerangProjectile)p).getId();
                            if(!attacker.checkBoomerang(id)){
                                attacker.takeDamage(p.getDamage());
                                attacker.addBoomerang(id);
                            }
                        }
                        else{
                            attacker.takeDamage(p.getDamage());
                        }
                        return true;
                    }
                    continue;
                }
                // otherwise continue on
                // assuming the other side will have angleA > 90
            }
            else if(max == p2Dist){
                // ignore p2
                // p4 is corner

                // start with p1 and p4
                max = Math.max(p1Dist, p4Dist);
                if(max == p1Dist){
                    a = p1Dist;
                    b = p4Dist;  
                }
                else{
                    a = p4Dist;
                    b = p1Dist;
                }
                c = p.getWidth();
                angleA = Math.acos((Math.pow(b, 2) + Math.pow(c, 2) - Math.pow(a, 2)) / (2 * b * c));
                if(angleA < 90){
                    // swap to the other side
                    max = Math.max(p3Dist, p4Dist);
                    if(max == p3Dist){
                        a = p3Dist;
                        b = p4Dist;  
                    }
                    else{
                        a = p4Dist;
                        b = p3Dist;
                    }
                    c = p.getHeight();
                }
                else if(angleA == 90){
                    if(Point.distance(p4, attackerCenter) <= (length + Attacker.RADIUS)){ // check distance from corner
                        // ***collision***
                        if(p instanceof BoomerangProjectile){
                            int id = ((BoomerangProjectile)p).getId();
                            if(!attacker.checkBoomerang(id)){
                                attacker.takeDamage(p.getDamage());
                                attacker.addBoomerang(id);
                            }
                        }
                        else{
                            attacker.takeDamage(p.getDamage());
                        }
                        return true;
                    }
                    continue;
                }
            }
            else if(max == p3Dist){
                // ignore p3
                // p1 is corner

                // start with p1 and p2
                max = Math.max(p1Dist, p2Dist);
                if(max == p1Dist){
                    a = p1Dist;
                    b = p2Dist;  
                }
                else{
                    a = p2Dist;
                    b = p1Dist;
                }
                c = p.getHeight();
                angleA = Math.acos((Math.pow(b, 2) + Math.pow(c, 2) - Math.pow(a, 2)) / (2 * b * c));
                if(angleA < 90){
                    // swap to the other side
                    max = Math.max(p1Dist, p4Dist);
                    if(max == p1Dist){
                        a = p1Dist;
                        b = p4Dist;  
                    }
                    else{
                        a = p4Dist;
                        b = p1Dist;
                    }
                    c = p.getWidth();
                }
                else if(angleA == 90){
                    if(Point.distance(p1, attackerCenter) <= (length + Attacker.RADIUS)){ // check distance from corner
                        // ***collision***
                        if(p instanceof BoomerangProjectile){
                            int id = ((BoomerangProjectile)p).getId();
                            if(!attacker.checkBoomerang(id)){
                                attacker.takeDamage(p.getDamage());
                                attacker.addBoomerang(id);
                            }
                        }
                        else{
                            attacker.takeDamage(p.getDamage());
                        }
                        return true;
                    }
                    continue;
                }
            }
            else{
                // ignore p4
                // p2 is corner

                // start with p1 and p2
                max = Math.max(p1Dist, p2Dist);
                if(max == p1Dist){
                    a = p1Dist;
                    b = p2Dist;  
                }
                else{
                    a = p2Dist;
                    b = p1Dist;
                }
                c = p.getHeight();
                angleA = Math.acos((Math.pow(b, 2) + Math.pow(c, 2) - Math.pow(a, 2)) / (2 * b * c));
                if(angleA < 90){
                    // swap to the other side
                    max = Math.max(p2Dist, p3Dist);
                    if(max == p2Dist){
                        a = p2Dist;
                        b = p3Dist;  
                    }
                    else{
                        a = p3Dist;
                        b = p2Dist;
                    }
                    c = p.getWidth();
                }
                else if(angleA == 90){
                    if(Point.distance(p2, attackerCenter) <= (length + Attacker.RADIUS)){ // check distance from corner
                        // ***collision***
                        if(p instanceof BoomerangProjectile){
                            int id = ((BoomerangProjectile)p).getId();
                            if(!attacker.checkBoomerang(id)){
                                attacker.takeDamage(p.getDamage());
                                attacker.addBoomerang(id);
                            }
                        }
                        else{
                            attacker.takeDamage(p.getDamage());
                        }
                        return true;
                    }
                    continue;
                }
            }

            d2 = (Math.pow(c, 2) + Math.pow(b, 2) - Math.pow(a, 2)) / (2 * c);
            x = Math.sqrt(Math.pow(b, 2) + d2);
            if(x <= Attacker.RADIUS){ 
                // ***collision*** --> deals damage to attack that it collided with
                
                if(p instanceof BoomerangProjectile){
                    int id = ((BoomerangProjectile)p).getId();
                    if(!attacker.checkBoomerang(id)){
                        attacker.takeDamage(p.getDamage());
                        attacker.addBoomerang(id);
                    }
                }
                else{
                    attacker.takeDamage(p.getDamage());
                }
                return true;
            }
        }
        return false;
    }

    public void explosion(ExplosionProjectile p){
        ArrayList<Attacker> attackers = gameManager.getAttackerManager().getAttackers();
        for(int i = attackers.size() - 1; i >= 0; i--){
            if(Math.sqrt(Math.pow(p.getX() - attackers.get(i).getX(), 2) + Math.pow(p.getY() - attackers.get(i).getY(), 2)) <=
                p.getRadius() + Attacker.RADIUS){
                Attacker a = attackers.get(i);
                if(p instanceof Shockwave){
                    int id = ((Shockwave)p).getId();
                    if(!a.checkShockwave(id)){
                        a.takeDamage(p.getDamage());
                        a.addShockwave(id);
                    }
                }
                else if(p instanceof Pulse){
                    int id = ((Pulse)p).getId();
                    if(!a.checkPulse(id)){
                        a.takeDamage(p.getDamage());
                        a.stun(((Pulse)p).getStunTicks());
                        a.addPulse(id);
                    }
                }
                else attackers.get(i).takeDamage(p.getDamage());
                    
                    
            }
        }
    }

    public void remove(Projectile p){
        projectiles.remove(p);
    }

    public void add(Projectile p){
        projectiles.add(p);
    }
}
