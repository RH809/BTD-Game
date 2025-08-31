import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
/**
 * Class to manage all of the defenders
 * Calls their tick methods to shoot/reload
 * Determines their target through the AttackerManager
 */
public class DefenderManager {
    private ArrayList<Defender> defenders; // list of all of the defenders
    private GameManager gameManager;

    public DefenderManager(GameManager gameManager){
        this.gameManager = gameManager;
        defenders = new ArrayList<Defender>();
    }

    public void reset(){
        defenders = new ArrayList<Defender>();
    }

    public void tick(){
        for(int i = 0; i < defenders.size(); i++){
            defenders.get(i).move();
        }
    }

    public void draw(Graphics g){
        for(int i = 0; i < defenders.size(); i++){
            defenders.get(i).draw(g);
            defenders.get(i).update();
        }
    }

    public void remove(Defender d){
        defenders.remove(d);
    }

    public void add(Defender d){
        defenders.add(d);
    }

    /**
     * Set target for a defender based on their targeting priority
     */
    public Attacker setTarget(Defender d){
        ArrayList<Attacker> attackers = gameManager.getAttackerManager().getAttackers();
        Attacker target = null, curr;
        double lowDist = -1, currDist;
        for(int i = 0; i < attackers.size(); i++){
            curr = attackers.get(i);
            currDist = dist(d, curr);
            if(currDist <= d.getRadius()){
                if(d.getPriority() == Defender.Priority.DISTANCE){
                    if(currDist < lowDist || lowDist == -1){ // compare distance
                        lowDist = currDist;
                        target = curr;
                    }
                    else if(currDist == lowDist && curr.getCompletion() > target.getCompletion()){ // if same distance away prioritize ordering
                        target = curr;
                    }
                }
                else{
                    if(target == null || curr.getCompletion() > target.getCompletion()){ // prioritize ordering
                        target = curr;
                    }
                }
            }
        }
        return target;
    }

    /**
     * Helper function to determine distance between attacker and defender
     */
    private double dist(Defender d, Attacker a){
        return Math.sqrt(Math.pow((d.getX() - a.getX()), 2) + Math.pow((d.getY() - a.getY()), 2));
    }

    public GameManager getGameManager(){
        return gameManager;
    }

    public Defender checkMouse(MouseEvent e, int radius){
        double x = e.getX();
        double y = e.getY();
        for(Defender d : defenders){
            if(Math.sqrt(Math.pow(x - d.getX(), 2) + Math.pow(y - d.getY(), 2)) <= radius + (Math.max(d.getWidth(), d.getHeight())) / 2){
                return d;
            }
        }
        return null;
    }
}
