

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class RainbowAttacker extends Attacker{ // level 3 attacker

    public RainbowAttacker(AttackerManager attackerManager) {
        super(attackerManager, 1, null, 10000, 1000, 20);
    }

    @Override
    public String getName() {
        return "Rainbow Attacker";
    }

    @Override
    public void draw(Graphics g){
        g.setColor(new Color((int)(Math.random() * 255 + 1), (int)(Math.random() * 255 + 1), (int)(Math.random() * 255 + 1)));
        Ellipse2D.Double oval = new Ellipse2D.Double(x - Attacker.RADIUS / 2, y - Attacker.RADIUS / 2, Attacker.RADIUS, Attacker.RADIUS);
        ((Graphics2D)g).fill(oval);

        if(stunTicks > 0){
            g.setColor(Color.BLUE);
            Ellipse2D.Double stun = new Ellipse2D.Double(x - Attacker.RADIUS / 2, y - Attacker.RADIUS / 2, Attacker.RADIUS, Attacker.RADIUS);
            ((Graphics2D)g).draw(stun);
        }
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\nSpeed: Fast";
    }
    
}
