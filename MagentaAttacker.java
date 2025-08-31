import java.awt.Color;


public class MagentaAttacker extends Attacker{ // level 6 attacker

    public MagentaAttacker(AttackerManager attackerManager) {
        super(attackerManager, 0, Color.MAGENTA, 100, 10, 12);
    }

    @Override
    public String getName() {
        return "Magenta Attacker";
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\nSpeed: Medium";
    }
    
}
