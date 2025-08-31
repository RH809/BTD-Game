import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
/**
 * Component on which all of the game objects (attackers, defenders, projectiles) are drawn
 */
public class GamePanel extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener{

    public static final Font STAT_FONT = new Font("Dialog", 10, (int)(60 * Coordinator.SIZE_MULTIPLIER));

    private GameManager gm;
    private SidePanel sidePanel;
    private Defender boughtDefender = null;
    private Target target = null;
    private JFrame frame;
    private boolean alreadyBought = false;

    public GamePanel(GameManager gm, JFrame frame){
        this.gm = gm;
        this.frame = frame;
        setBackground(Color.CYAN);
        setPreferredSize(new Dimension((int)(2500 * Coordinator.SIZE_MULTIPLIER), (int)(1200 * Coordinator.SIZE_MULTIPLIER)));
    }

    public void setSidePanel(SidePanel sidePanel){
        this.sidePanel = sidePanel;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // Optimization ------------------------------------
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1f));
        gm.draw(g); // call draw methods of all of the game objects
        if(boughtDefender != null){
            boughtDefender.draw(g);
        }
        if(target != null){
            target.draw(g);
        }

        // draw player info
        if(Coordinator.playing){
            g.setFont(STAT_FONT);
            g.setColor(Color.CYAN);
            g.drawString("Round " + gm.getLevel(), (int)(10 * Coordinator.SIZE_MULTIPLIER),
                (int)(50 * Coordinator.SIZE_MULTIPLIER));
            g.setColor(Color.RED); 
            g.drawString(gm.getPlayer().getHealth() + "", (int)(10 * Coordinator.SIZE_MULTIPLIER),
                (int)(110 * Coordinator.SIZE_MULTIPLIER));
            g.setColor(Color.GREEN);
            g.drawString("$" + gm.getPlayer().getMoney() + "", (int)(10 * Coordinator.SIZE_MULTIPLIER),
                (int)(170 * Coordinator.SIZE_MULTIPLIER));
        }
    }
    
    public void setTarget(Target target){
        this.target = target;
    }

    public void removeTarget(){
        target = null;
    }

    public void setBoughtDefender(Defender d){
        boughtDefender = d;
    }

    public void removeBoughtDefender(){
        boughtDefender = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(boughtDefender == null){
            dispatchEvent(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(boughtDefender == null){
            if(target == null) dispatchEvent(e);
            else{
                target.setPosition(e.getX(), e.getY());
                boolean checkMouse = !(gm.getAttackerManager().checkMouse(e, target.getRadius() - (int)(20 * Coordinator.SIZE_MULTIPLIER)));
                target.setInvalid(e.getX() > 2050 * Coordinator.SIZE_MULTIPLIER || e.getX() < 0 || e.getY() < 0 ||
                    e.getY() > 1200 * Coordinator.SIZE_MULTIPLIER || checkMouse);
            }
        }
        else{
            boughtDefender.setPosition(e.getX(), e.getY());
            boolean checkMouse = ((gm.getDefenderManager().checkMouse(e, (Math.max(boughtDefender.getWidth(), boughtDefender.getHeight())) / 2)) != null) || gm.getAttackerManager().checkMouse(e, (Math.max(boughtDefender.getWidth(), boughtDefender.getHeight())) / 2);
            boughtDefender.setCancel(e.getX() > 2050 * Coordinator.SIZE_MULTIPLIER - boughtDefender.getWidth() / 2 || e.getX() < boughtDefender.getWidth() / 2 || e.getY() < boughtDefender.getHeight() / 2 ||
                e.getY() > 1200 * Coordinator.SIZE_MULTIPLIER - boughtDefender.getHeight() / 2 || checkMouse);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(boughtDefender == null && e.getX() > 2050 * Coordinator.SIZE_MULTIPLIER){ // only dispatch events if the click is on the side panel
            dispatchEvent(e);
        }
        else{
            if(e.getX() > 2050 * Coordinator.SIZE_MULTIPLIER && alreadyBought){ // cancel
                boughtDefender = null;
                sidePanel.cancel();
            }
            else if(alreadyBought){ // place defender if valid
                if(e.getX() > 2050 * Coordinator.SIZE_MULTIPLIER - boughtDefender.getWidth() / 2 || e.getX() < boughtDefender.getWidth() / 2 || e.getY() < boughtDefender.getHeight() / 2 || e.getY() > 1200 * Coordinator.SIZE_MULTIPLIER - boughtDefender.getHeight() / 2 ||
                    ((gm.getDefenderManager().checkMouse(e, (Math.max(boughtDefender.getWidth(), boughtDefender.getHeight())) / 2)) != null) || gm.getAttackerManager().checkMouse(e, (Math.max(boughtDefender.getWidth(), boughtDefender.getHeight())) / 2)) return;
                boolean selectTarget = (boughtDefender instanceof TargetDefender);
                Defender newDefender = gm.place(e.getX(), e.getY(), boughtDefender);
                sidePanel.select(newDefender); // place defender and set as selected
                boughtDefender = null;
                if(selectTarget){ // if turret needs to select target, start target selection process
                    sidePanel.selectTarget(((TargetDefender)newDefender).getTarget());
                }
                SoundPlayer.playBuy();
                SoundPlayer.playPlace();
            }
            else if(target != null){ // select target if valid
                boolean checkMouse = !(gm.getAttackerManager().checkMouse(e, target.getRadius() - 20));
                target.setInvalid(e.getX() > 2050 * Coordinator.SIZE_MULTIPLIER || e.getX() < 0 || e.getY() < 0 ||
                    e.getY() > 1200 * Coordinator.SIZE_MULTIPLIER || checkMouse);
                if(!target.isInvalid()){
                    target = null;
                    sidePanel.targetSelected();
                }
            }
            else if(e.getX() <= 2050 * Coordinator.SIZE_MULTIPLIER){ // select/deselect defender
                Defender selected = gm.getDefenderManager().checkMouse(e, (int)(40 * Coordinator.SIZE_MULTIPLIER));
                if(selected == null && sidePanel.hasSelected()){
                    sidePanel.deselect();
                }
                else if(selected != null){
                    sidePanel.select(selected);
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        alreadyBought = (boughtDefender != null);
        if(boughtDefender == null){
            dispatchEvent(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(boughtDefender == null){
            dispatchEvent(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(boughtDefender == null){
            dispatchEvent(e);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if(boughtDefender == null){
            dispatchEvent(e);
        }
    }

    private void dispatchEvent(MouseEvent e) {
        Point glassPanePoint = e.getPoint();
        Container container = frame.getContentPane();
        Point containerPoint = SwingUtilities.convertPoint(this,
                glassPanePoint, container);
        if (containerPoint.y < 0) { // we're not in the content pane
            // Could have special code to handle mouse events over
            // the menu bar or non-system window decorations, such as
            // the ones provided by the Java look and feel.
        } else {
            // The mouse event is probably over the content pane.
            // Find out exactly which component it's over.
            Component component = SwingUtilities.getDeepestComponentAt(
                    container, containerPoint.x, containerPoint.y);
            if (component != null) {
                // Forward events to component below
                Point componentPoint = SwingUtilities.convertPoint(
                        this, glassPanePoint, component);
                component.dispatchEvent(new MouseEvent(component, e
                        .getID(), e.getWhen(), e.getModifiers(),
                        componentPoint.x, componentPoint.y, e
                                .getClickCount(), e.isPopupTrigger()));
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        dispatchScrollEvent(e);
    }

    private void dispatchScrollEvent(MouseWheelEvent e) {
        Point glassPanePoint = e.getPoint();
        Container container = frame.getContentPane();
        Point containerPoint = SwingUtilities.convertPoint(this,
                glassPanePoint, container);

        if (containerPoint.y < 0) { // we're not in the content pane
            // Could have special code to handle mouse events over
            // the menu bar or non-system window decorations, such as
            // the ones provided by the Java look and feel.
        } else {
            // The mouse event is probably over the content pane.
            // Find out exactly which component it's over.
            Component component = SwingUtilities.getDeepestComponentAt(
                    container, containerPoint.x, containerPoint.y);

            if (component != null) {
                // Forward events to component below
                Point componentPoint = SwingUtilities.convertPoint(
                        this, glassPanePoint, component);
                component.dispatchEvent(new MouseWheelEvent(component, e
                        .getID(), e.getWhen(), e.getModifiers(),
                        componentPoint.x, componentPoint.y, e.getClickCount(),
                        e.isPopupTrigger(), e.getScrollType(), e.getScrollAmount(),
                        e.getWheelRotation()));
            }
        }
    }
}
