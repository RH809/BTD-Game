import java.awt.Color;


public class BlackAttacker extends Attacker{ // level 7 attacker

    public BlackAttacker(AttackerManager attackerManager) {
        super(attackerManager, 0, Color.BLACK, 5000, 500, 18);
    }

    @Override
    public String getName() {
        return "Black Attacker";
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\nSpeed: Medium";
    }
    
}
