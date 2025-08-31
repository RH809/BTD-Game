import java.awt.Color;

public class RedAttacker extends Attacker{ // level 1 attacker

    public RedAttacker(AttackerManager attackerManager) {
        super(attackerManager, 2, Color.RED, 10, 1, 2);
    }

    @Override
    public String getName() {
        return "Red Attacker";
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\nSpeed: Slow";
    }
    
}
