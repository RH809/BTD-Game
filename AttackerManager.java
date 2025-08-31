import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Class to manage all of the attackers
 * Controls all of the attackers together
 */
public class AttackerManager {
    public static final int COUNT_LIMIT = 354 * 5; // count limit for speed of 1; divide by speed for corresponding count limit
    private ArrayList<Attacker> attackers; // list of all of the attackers
    public static final double[] speeds = {0.5, 1, 0.375}; // speeds that are used
    public static double[][] ticks; // 2D array of all of the tick values to follow pre-determined paths at specific speeds
    private Player player; // the player class, which allows for dealing damage and gaining cash

    static{ // set the pre-determined paths
        ticks = new double[10][10000];
        double total = 0; // total "distance"
        double totalTDiff = 0; // total difference in ticks
        double start = System.currentTimeMillis();
        double estimate = 10;
        for(int s = 0; s < speeds.length; s++){
            double t_last = estimate;
            double t_next = t_last;
            total = 0;
            totalTDiff = 0;
            for(int i = 0; i < COUNT_LIMIT / speeds[s]; i++){
                //System.out.println(i);
                do{
                    t_last = t_next;
                    t_next = t_last - numerator(t_last, speeds[s] / 5, i) / integrand(t_last);
                } while(Math.abs(t_last - t_next) >  0.001 || t_next > t_last);
                ticks[s][i] = t_next;
                t_last = t_next + estimate;
                if(i > 0) {
                    total += Math.sqrt(Math.pow(f_x(ticks[s][i]) - f_x(ticks[s][i - 1]), 2) + Math.pow(f_y(ticks[s][i]) - f_y(ticks[s][i - 1]), 2));
                    totalTDiff = ticks[s][i] - ticks[s][i - 1];
                }
            }
            System.out.println(speeds[s] + ": " + total / (COUNT_LIMIT / speeds[s]) + ", " + totalTDiff / (COUNT_LIMIT / speeds[s]));
        }
        System.out.println((System.currentTimeMillis() - start) / 1000.0);

    }

    public AttackerManager(Player player){
        attackers = new ArrayList<Attacker>();
        this.player = player;
    }

    /**
     * @param t - tick
     * @param speed
     * @param count - count number
     * @return the numerator for newton's law
     */
    private static double numerator(double t, double speed, int count){
        return arclength(t) - speed * count;
    }

    /**
     * 
     * @param t - tick 
     * @return arclength to from 0 to tick t
     */
    private static double arclength(double t){
        int N = 10000;                    // precision parameter
        double h = t / (N - 1);     // step size

        // 1/3 terms
        double sum = 1.0 / 3.0 * (integrand(0) + integrand(t));

        // 4/3 terms
        for (int i = 1; i < N - 1; i += 2) {
            double x = h * i;
            sum += 4.0 / 3.0 * integrand(x);
        }

        // 2/3 terms
        for (int i = 2; i < N - 1; i += 2) {
            double x = h * i;
            sum += 2.0 / 3.0 * integrand(x);
        }

        return sum * h;
    }

    /**
     * integrand function for arclength
    */
    private static double integrand(double t){
        return Math.sqrt(Math.pow(f_x_prime(t), 2) + Math.pow(f_y_prime(t), 2));
    }

    /**
     * derivative function of x(t)
     */
    private static double f_x_prime(double t){
        return -0.000017914 * 4 * (Math.pow(t, 3)) + 0.00353185 * 3 * (Math.pow(t, 2)) - 0.206458 * 2 * t + 4.24157;
    }

    /**
     * derivative function of y(t)
     */
    private static double f_y_prime(double t){
        return -0.0000421384 * 4 * (Math.pow(t, 3)) + 0.00825601 * 3 * (Math.pow(t, 2)) - 0.492829 * 2 * t + 8.66422;
    }

    /**
     * x position based on tick
     */
    private static double f_x(double t){
        return (-0.000017914 * (Math.pow(t, 4)) + 0.00353185 * (Math.pow(t, 3)) - 0.206458 * (Math.pow(t, 2)) + 4.24157 * (t) - 0.0158984) * 17;
    }

    /**
     * y position based on tick
     */
    private static double f_y(double t){
        return 1200 - ((-0.0000421384 * (Math.pow(t, 4)) + 0.00825601 * (Math.pow(t, 3)) - 0.492829 * (Math.pow(t, 2)) + 8.66422 * (t) + 59.8776) * 11); 
    }

    public void reset(){
        attackers = new ArrayList<Attacker>();
    }

    /**
     * run one tick
     * - move all attackers
     */
    public void tick(){
        for(int i = 0; i < attackers.size(); i++){
            attackers.get(i).move();
        }
    }

    /**
     * draw all attackers
     */
    public void draw(Graphics g){
        for(int i = 0; i < attackers.size(); i++){
            attackers.get(i).draw(g);
        }
    }

    public void remove(Attacker a){
        attackers.remove(a);
    }

    public void add(Attacker a){
        attackers.add(a);
    }

    public void damagePlayer(int damage){
        player.subtractHealth(damage);
    }

    public void grantMoneyDrop(int amount){
        player.addMoney(amount);
    }

    public ArrayList<Attacker> getAttackers(){
        return attackers;
    }

    public boolean checkMouse(MouseEvent e, int radius){
        double x = e.getX();
        double y = e.getY();
        double tickX, tickY;
        for(int i = 0; i < ticks[1].length; i++){
            tickX = (-0.000017914 * (Math.pow(ticks[1][i], 4)) + 0.00353185 * (Math.pow(ticks[1][i], 3)) - 0.206458 * (Math.pow(ticks[1][i], 2)) +
                4.24157 * (ticks[1][i]) - 0.0158984) * 17;
            tickY = 1200 - ((-0.0000421384 * (Math.pow(ticks[1][i], 4)) + 0.00825601 * (Math.pow(ticks[1][i], 3)) - 0.492829 * (Math.pow(ticks[1][i], 2)) +
                8.66422 * (ticks[1][i]) + 59.8776) * 11);
            tickX *= Coordinator.SIZE_MULTIPLIER;
            tickY *= Coordinator.SIZE_MULTIPLIER;
            if(Math.sqrt(Math.pow(x - tickX, 2) + Math.pow(y - tickY, 2)) <= radius + 25 * Coordinator.SIZE_MULTIPLIER){
                return true;
            }
            
        }
        return false;
    }

    public void drawBounds(Graphics g){
        g.setColor(Color.RED);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        double tickX, tickY;
        for(int i = 0; i < ticks[1].length; i+= 20){
            tickX = (-0.000017914 * (Math.pow(ticks[1][i], 4)) + 0.00353185 * (Math.pow(ticks[1][i], 3)) - 0.206458 * (Math.pow(ticks[1][i], 2)) +
                4.24157 * (ticks[1][i]) - 0.0158984) * 17;
            tickY = 1200 - ((-0.0000421384 * (Math.pow(ticks[1][i], 4)) + 0.00825601 * (Math.pow(ticks[1][i], 3)) - 0.492829 * (Math.pow(ticks[1][i], 2)) +
                8.66422 * (ticks[1][i]) + 59.8776) * 11);
            g2.drawOval((int)((tickX - 50) * Coordinator.SIZE_MULTIPLIER), (int)((tickY - 50) * Coordinator.SIZE_MULTIPLIER),
                (int)(100 * Coordinator.SIZE_MULTIPLIER), (int)(100 * Coordinator.SIZE_MULTIPLIER));
            
        }
    }
}
