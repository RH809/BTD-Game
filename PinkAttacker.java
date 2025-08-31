import java.awt.Color;


public class PinkAttacker extends Attacker{ // level 5 attacker

    public PinkAttacker(AttackerManager attackerManager) {
        super(attackerManager, 0, Color.PINK, 50, 5, 10);
    }

    @Override
    public String getName() {
        return "Pink Attacker";
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\nSpeed: Medium";
    }
    
}
