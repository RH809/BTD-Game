import java.awt.Color;


public class GrayAttacker extends Attacker{ // level 7 attacker

    public GrayAttacker(AttackerManager attackerManager) {
        super(attackerManager, 1, Color.GRAY, 500, 50, 14);
    }

    @Override
    public String getName() {
        return "Gray Attacker";
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\nSpeed: Fast";
    }
    
}
