import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

import javax.swing.JButton;
/**
 * Parent class for all of the defenders
 */
public abstract class Defender {
    // can prioritize closest to self or farthest down the track 
    // move is rotation
    public enum Priority{
        DISTANCE,
        PLACE
    };

    protected DefenderManager defenderManager; // manager of the defenders
    protected Player player;
    protected double radius; // attack radius of the defender
    protected double rotation = 0; // rotation
    protected double x, y; // position
    protected Priority priority; // type of priority (either targets attacker farthest down the track or closest attacker)
    protected int reloadTicks; // number of ticks it takes to reload
    protected int reloadCount; // counting ticks for reloading
    protected int damage;

    protected Attacker target; // current targeted attacker

    protected int width, height; // width and height of rectangle
    protected int cost; // cost of the defender
    protected int sellAmount;

    protected boolean cancel, selected;

    protected JButton upgrade1, upgrade2, upgrade3, sell, targetButton;
    protected boolean upgraded1 = false, upgraded2 = false, upgraded3 = false;
    protected int upgradeCost1, upgradeCost2, upgradeCost3;
    protected String upgradeName1, upgradeName2, upgradeName3;

    public Defender(DefenderManager defenderManager, double x, double y, double radius, int width, int height, int reloadTicks, int damage, int cost){
        this.defenderManager = defenderManager;
        if(defenderManager != null) player = defenderManager.getGameManager().getPlayer();
        this.x = x;
        this.y = y;
        this.radius = radius * Coordinator.SIZE_MULTIPLIER;
        this.width = (int)(width * Coordinator.SIZE_MULTIPLIER);
        this.height = (int)(height * Coordinator.SIZE_MULTIPLIER);
        this.cost = cost;
        sellAmount = (int)(cost * 0.75);

        this.reloadTicks = reloadTicks;
        this.damage = damage;
        reloadCount = 0;


        upgrade1 = new JButton();
        upgrade2 = new JButton();
        upgrade3 = new JButton();

        upgrade1.setFocusable(false);
        upgrade1.setFocusPainted(false);
        upgrade2.setFocusable(false);
        upgrade2.setFocusPainted(false);
        upgrade3.setFocusable(false);
        upgrade3.setFocusPainted(false);

        initializeUpgrades();

        upgrade1.setUI(SidePanel.WHITE_BUTTON_UI);
        upgrade2.setUI(SidePanel.WHITE_BUTTON_UI);
        upgrade3.setUI(SidePanel.WHITE_BUTTON_UI);

        upgrade1.setFont(SidePanel.TEXT_FONT);
        upgrade2.setFont(SidePanel.TEXT_FONT);
        upgrade3.setFont(SidePanel.TEXT_FONT);

        upgrade1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                upgrade1();
                upgraded1 = true;
                player.spendMoney(upgradeCost1);
                sellAmount += (int)(upgradeCost1 * 0.75);
                upgrade1.setEnabled(false);
                upgrade1.setBackground(Color.BLUE);
                upgrade1.setText(upgradeName1 + " (purchased)");
                SoundPlayer.playBuy();
            }
        });

        upgrade2.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                upgrade2();
                upgraded2 = true;
                player.spendMoney(upgradeCost2);
                sellAmount += (int)(upgradeCost2 * 0.75);
                upgrade2.setEnabled(false);
                upgrade2.setBackground(Color.BLUE);
                upgrade2.setText(upgradeName2 + " (purchased)");
                SoundPlayer.playBuy();
            }
        });

        upgrade3.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                upgrade3();
                upgraded3 = true;
                player.spendMoney(upgradeCost3);
                sellAmount += (int)(upgradeCost3 * 0.75);
                upgrade3.setEnabled(false);
                upgrade3.setBackground(Color.BLUE);
                upgrade3.setText(upgradeName3 + " (purchased)");
                SoundPlayer.playBuy();
            }
        });

        upgrade1.setText(upgradeName1 + " ($" + upgradeCost1 + ")");
        upgrade2.setText(upgradeName2 + " ($" + upgradeCost2 + ")");
        upgrade3.setText(upgradeName3 + " ($" + upgradeCost3 + ")");

        sell = new JButton();
        sell.setBackground(Color.WHITE);
        sell.setFocusable(false);
        sell.setFocusPainted(false);
        sell.setFont(SidePanel.LABEL_FONT);
        sell.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                player.addMoney(sellAmount);
                destroy();
                SoundPlayer.playSell();
            }
        });

        
        targetButton = new JButton();
        targetButton.setFocusable(false);
        targetButton.setFocusPainted(false);
        targetButton.setFont(SidePanel.LABEL_FONT);

        priority = Priority.DISTANCE;
        toggle(); // toggle so that default is target attacker that is farthest down the track

        targetButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                toggle();
            }
        });
        
    }

    public abstract void initializeUpgrades();

    public abstract void upgrade1();

    public abstract void upgrade2();

    public abstract void upgrade3();

    public void move(){
        target = defenderManager.setTarget(this); // determine target
        if(target != null) { // set rotation to face target
            double dx = target.getX() - x;
            double dy = target.getY() - y;

            rotation = Math.atan2(dy, dx);
        }
        
        if(reloadCount != 0) reloadCount = (reloadCount + 1) % reloadTicks; // reload if it does not have bullet
        else if(target != null) { // otherwise shoot and start reload count
            shoot();
            reloadCount++;
        }
        update();
    }

    public void update(){
        if(!upgraded1){
            upgrade1.setEnabled(player.getMoney() >= upgradeCost1);
            upgrade1.setBackground((player.getMoney() >= upgradeCost1) ? Color.GREEN : Color.RED);
        }
        if(!upgraded2){
            upgrade2.setEnabled(player.getMoney() >= upgradeCost2);
            upgrade2.setBackground((player.getMoney() >= upgradeCost2) ? Color.GREEN : Color.RED);
        }
        if(!upgraded3){
            upgrade3.setEnabled(player.getMoney() >= upgradeCost3);
            upgrade3.setBackground((player.getMoney() >= upgradeCost3) ? Color.GREEN : Color.RED);
        }
        sell.setText("Sell ($" + sellAmount + ")");
    }

    public JButton getUpgrade1() { return upgrade1; }
    public JButton getUpgrade2() { return upgrade2; }
    public JButton getUpgrade3() { return upgrade3; }
    public JButton getSellButton() { return sell; }
    public JButton getTargetButton() { return targetButton; }

    public abstract void shoot();

    public void destroy(){
        defenderManager.remove(this);
    }

    public void draw(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.BLUE);
        if(cancel) g.setColor(x > 2050 * Coordinator.SIZE_MULTIPLIER ? Color.GRAY : Color.RED);
        if(selected){
            Ellipse2D.Double range = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
            g2.draw(range);
        }
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getRadius(){
        return radius;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public Priority getPriority(){
        return priority;
    }

    public int getCost(){
        return cost;
    }

    public void setCancel(boolean c){
        cancel = c;
    }

    public void select(){
        selected = true;
    }

    public void deselect(){
        selected = false;
    }

    public void toggle(){ // default toggling is choosing priority
        if(priority == Priority.DISTANCE){ // switch to place
            priority = Priority.PLACE;
            targetButton.setText("Targeting leading attacker");
            targetButton.setBackground(Color.CYAN);
        }
        else{ // switch to distance
            priority = Priority.DISTANCE;
            targetButton.setText("Targeting closest attacker");
            targetButton.setBackground(Color.YELLOW);
        }
    }

    public abstract String getName();

    public abstract String getDescription();

}
