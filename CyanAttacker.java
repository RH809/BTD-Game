import java.awt.Color;


public class CyanAttacker extends Attacker{ // level 4 attacker

    public CyanAttacker(AttackerManager attackerManager) {
        super(attackerManager, 1, Color.CYAN, 40, 4, 8);
    }

    @Override
    public String getName() {
        return "Cyan Attacker";
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\nSpeed: Fast";
    }
    
}
