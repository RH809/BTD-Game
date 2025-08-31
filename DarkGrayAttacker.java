import java.awt.Color;


public class DarkGrayAttacker extends Attacker{ // level 7 attacker

    public DarkGrayAttacker(AttackerManager attackerManager) {
        super(attackerManager, 0, Color.DARK_GRAY, 1000, 100, 16);
    }

    @Override
    public String getName() {
        return "Dark Gray Attacker";
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\nSpeed: Medium";
    }
    
}
