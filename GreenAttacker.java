import java.awt.Color;


public class GreenAttacker extends Attacker{ // level 3 attacker

    public GreenAttacker(AttackerManager attackerManager) {
        super(attackerManager, 0, Color.GREEN, 30, 3, 6);
    }

    @Override
    public String getName() {
        return "Green Attacker";
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\nSpeed: Medium";
    }
    
}
