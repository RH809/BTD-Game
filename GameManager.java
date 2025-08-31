import java.awt.Graphics;
import java.util.ArrayList;
/**
 * Class that manages all of the other managers
 * Runs the game ticks
 * Starts new levels
 */
public class GameManager {
    // sends instructions to other managers, such as move
    
    public static final int VALUE_INCREASE = 5;


    private AttackerManager attackerManager;
    private DefenderManager defenderManager;
    private ProjectileManager projectileManager;
    private Player player;

    private SidePanel sidePanel;


    private int level, tick = 0, spawnTicks = 30;
    private int levelReward;
    private boolean waiting = false, spawnsRemaining = true;

    private int[] limits;
    private int[] limitChange;
    private ArrayList<Integer> spawns;
    /*
     * 0 = Red Attacker
     * 1 = Orange Attacker
     * 2 = Green Attacker
     * 3 = Cyan Attacker
     * 4 = Pink Attacker
     * 5 = Magenta Attacker
     * 6 = Gray Attacker
     * 7 = Dark Gray Attacker
     * 8 = Black Attacker
     * 9 = Rainbow Attacker
     */

    public GameManager(Player player){
        this.player = player;
        attackerManager = new AttackerManager(player);
        defenderManager = new DefenderManager(this);
        projectileManager = new ProjectileManager(this);

        spawns = new ArrayList<>();
        limits = new int[10];
        limitChange = new int[10];
    }

    public void setSidePanel(SidePanel sidePanel){
        this.sidePanel = sidePanel;
    }

    public void newGame(){
        // reset
        System.out.println("new game");
        player.reset();
        attackerManager.reset();
        defenderManager.reset();
        projectileManager.reset();
        sidePanel.reset();
        spawns.clear();

        limits[0] = 8;
        limits[1] = 0;
        limits[2] = 0;
        limits[3] = 0;
        limits[4] = 0;
        limits[5] = 0;
        limits[6] = 0;
        limits[7] = 0;
        limits[8] = 0;
        limits[9] = 0;

        limitChange[0] = 2;
        limitChange[1] = 0;
        limitChange[2] = -4;
        limitChange[3] = -8;
        limitChange[4] = -9;
        limitChange[5] = -12;
        limitChange[6] = -15;
        limitChange[7] = -12;
        limitChange[8] = -14;
        limitChange[9] = -8;

        level = 0;
        levelReward = 30;
        SoundPlayer.playStartGame();
        waitForStart();
    }

    public void waitForStart(){
        level++;
        if(level > 1){
            player.addMoney(levelReward);
            levelReward += VALUE_INCREASE;
        }
        waiting = true;
        sidePanel.requestNextRound();
    }

    public void nextLevel(){
        tick = 0;
        if(level % 5 == 0) {
            if(spawnTicks > 10) spawnTicks -= 2;

            if(level < 10) limitChange[0] += 4;
            else limitChange[0] = 0;
            if(level < 15) limitChange[1] += 4;
            else limitChange[1] = 0;
            if(level < 25) limitChange[2] += 4;
            else limitChange[2] = 0;
            if(level < 30) limitChange[3] += 4;
            else limitChange[3] = 0;
            if(level < 40) limitChange[4] += 3;
            else limitChange[4] = 0;
            limitChange[5] += 3;
            limitChange[6] += 3;
            limitChange[7] += 2;
            limitChange[8] += 2;
            limitChange[9] += 1;

            System.out.println("Limit changes:");
            for(int i = 0; i < limitChange.length; i++){
                System.out.print(limitChange[i] + " ");
            }
            System.out.println();
        }
        for(int i = 0; i < limits.length; i++){
            if(limitChange[i] >= 0){
                limits[i] += limitChange[i]; // update spawning numbers
                for(int j = 0; j < limits[i]; j++){
                    spawns.add(i);
                }
            }
        }
        System.out.println("Limits:");
        for(int i = 0; i < limits.length; i++){
            System.out.print(limits[i] + " ");
        }
        System.out.println();
        spawnsRemaining = true;
        waiting = false;
        SoundPlayer.playStartLevel();
    }


    /**
     * creates the next attacker
     */
    public void newAttacker(){
        
        int index = (int)(Math.random() * spawns.size());
        Attacker a;
        switch(spawns.get(index)){
            case 0:
                a = new RedAttacker(attackerManager);
                break;
            case 1:
                a = new OrangeAttacker(attackerManager);
                break;
            case 2:
                a = new GreenAttacker(attackerManager);
                break;
            case 3:
                a = new CyanAttacker(attackerManager);
                break;
            case 4:
                a = new PinkAttacker(attackerManager);
                break;
            case 5:
                a = new MagentaAttacker(attackerManager);
                break;
            case 6:
                a = new GrayAttacker(attackerManager);
                break;
            case 7:
                a = new DarkGrayAttacker(attackerManager);
                break;
            case 8:
                a = new BlackAttacker(attackerManager);
                break;
            case 9:
                a = new RainbowAttacker(attackerManager);
                break;
            default:
                System.out.println("invalid number");
                return;
        }
        spawns.remove(index);
        attackerManager.add(a);
        if(spawns.size() == 0) spawnsRemaining = false;
    }

    /**
     * calls tick functions of all of the managers
     * creates new attacker if needed TODO fix this
     */
    public void tick(){
        tick++;
        if(!waiting && tick % spawnTicks == 0 && spawnsRemaining) {
            newAttacker();
        }
        boolean hasAttackers = attackerManager.getAttackers().size() > 0;
        attackerManager.tick();
        if(!waiting) defenderManager.tick();
        projectileManager.tick();
        if(hasAttackers && attackerManager.getAttackers().size() == 0 && !spawnsRemaining) { // last attacker is gone
            waitForStart();
        }
    }

    public Defender place(double x, double y, Defender defender){

        Defender newDefender = null;

        if(defender instanceof BasicTurret){
            newDefender = new BasicTurret(defenderManager, x, y);
        }
        else if(defender instanceof SpinningTurret){
            newDefender = new SpinningTurret(defenderManager, x, y);
        }
        else if(defender instanceof SniperTurret){
            newDefender = new SniperTurret(defenderManager, x, y);
        }
        else if(defender instanceof BombTurret){
            newDefender = new BombTurret(defenderManager, x, y);
        }
        else if(defender instanceof PulsingTurret){
            newDefender = new PulsingTurret(defenderManager, x, y);
        }
        else if (defender instanceof BoomerangTurret){
            newDefender = new BoomerangTurret(defenderManager, x, y);
        }
        else if(defender instanceof OrbitingTurret){
            newDefender = new OrbitingTurret(defenderManager, x, y);
        }
        else if(defender instanceof CrushingTurret){
            newDefender = new CrushingTurret(defenderManager, x, y);
        }
        else if(defender instanceof MachineGunTurret){
            newDefender = new MachineGunTurret(defenderManager, x, y);
        }
        defenderManager.add(newDefender);
        player.spendMoney(defender.getCost());
        return newDefender;
    }

    public void draw(Graphics g){
        attackerManager.draw(g);
        defenderManager.draw(g);
        projectileManager.draw(g);
    }

    public void selectTarget(Target target){
        sidePanel.selectTarget(target);
    }

    public AttackerManager getAttackerManager(){
        return attackerManager;
    }

    public DefenderManager getDefenderManager(){
        return defenderManager;
    }

    public ProjectileManager getProjectileManager(){
        return projectileManager;
    }

    public Player getPlayer(){
        return player;
    }

    public int getLevel(){
        return level;
    }

    public boolean isWaiting(){
        return waiting;
    }


}
