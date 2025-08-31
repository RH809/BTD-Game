import java.awt.Color;


public class OrangeAttacker extends Attacker{ // level 2 attacker

    public OrangeAttacker(AttackerManager attackerManager) {
        super(attackerManager, 1, Color.ORANGE, 20, 2, 4);
    }

    @Override
    public String getName() {
        return "Orange Attacker";
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\nSpeed: Fast";
    }
    
}
