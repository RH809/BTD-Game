import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class TargetDefender extends Defender{ // parent class for turrets that have targetting feature

    protected Target target;
    protected boolean targetSelected = false;

    public TargetDefender(DefenderManager defenderManager, double x, double y, double radius, int width, int height, int reloadTicks, int damage, int cost){
        super(defenderManager, x, y, radius, width, height, reloadTicks, damage, cost);
        targetButton.removeActionListener(targetButton.getActionListeners()[0]); // replace priority toggle
        targetButton.setText("Change target");
        targetButton.setBackground(Color.CYAN);
        targetButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                targetSelected = false;
                targetButton.setText("Selecting target");
                targetButton.setBackground(Color.YELLOW);
                defenderManager.getGameManager().selectTarget(target);
            }
        });
    }

    public void targetSelected(){
        targetSelected = true;
        targetButton.setText("Change target");
        targetButton.setBackground(Color.CYAN);
    }

    public Target getTarget(){
        return target;
    }

    
}
