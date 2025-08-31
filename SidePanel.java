import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
/**
 * Class to draw everything in the side panel (shop, info - defenders and attackers, upgrades, cancel)
 */
public class SidePanel extends JPanel{

    public static final Font TEXT_FONT = new Font("Dialog", 10, (int)(20 * Coordinator.SIZE_MULTIPLIER));
    public static final Font LABEL_FONT = new Font("Dialog", 10, (int)(15 * Coordinator.SIZE_MULTIPLIER));
    public static final Font SMALL_FONT = new Font("Dialog", 10, (int)(12 * Coordinator.SIZE_MULTIPLIER));
    public static final int WIDTH = (int)(450 * Coordinator.SIZE_MULTIPLIER);
    public static final int SCROLL_BAR_WIDTH = ((Integer)UIManager.get("ScrollBar.width")).intValue();
    public static final int OFFSET = 0; // 30 from top, 7 from bottom
    public static final int VERTICAL_GAP = (int)(5 * Coordinator.SIZE_MULTIPLIER);
    public static final int HEADER_HEIGHT = (int)(100 * Coordinator.SIZE_MULTIPLIER);
    public static final int BODY_HEIGHT = (int)(1025 * Coordinator.SIZE_MULTIPLIER);
    public static final int ROUND_HEIGHT = (int)(75 * Coordinator.SIZE_MULTIPLIER);

    public static final MetalButtonUI WHITE_BUTTON_UI = new MetalButtonUI() {
        @Override
        protected Color getDisabledTextColor(){
            return Color.WHITE;
        }
    };

    public enum View{
        Tabs, // defender and attacker tabs
        Info, // info on a type of defender or attacker
        Upgrades, // upgrading a placed defender
        Cancel, // when dragging a defender onto field, player can drag back to side panel to cancel purchase
        Target // when selecting target for a defender
    };

    public enum Tab{
        Defender,
        Attacker
    }

    private Player player;
    private GamePanel gamePanel;
    private GameManager gameManager;

    private View view, previous; // what type of view to be using for the side panel; previous - previous view to determine if view needs to be changed
    private Tab tab; // the tab that is in focus

    private JPanel header; // header of the view panel to show which view it is in
    private CardLayout headerLayout;
    private JPanel body; // body of the view panel that shows all of the info
    private CardLayout bodyLayout;
    private JPanel round;
    // tabs: header - defender/attacker buttons, body - scroll pane of all of the options for defenders and all of the attackers (considered "home page")
    // info: header - name of attacker/defender + button to return "home", body - image + description
    // upgrade: header - name of defender and level + button to return "home", body - image + options for upgrade
    // cancel: header - "Select placement of defender", body - red rectangle with large X and "cancel" (return "home" after defender is placed/buy is cancelled)

    private JPanel tabsBody, tabsHead, infoBody, infoHead, upgradesBody, upgradesHead, cancelBody, cancelHead, targetBody, targetHead;

    private JLabel infoName, infoImage;
    private JTextPane infoDescription;

    private JLabel upgradesName, upgradesImage;
    private JPanel upgradesButtonPanel;

    private JButton nextRound, autoPlay;
    private boolean requestingNextRound, autoplaying;

    private JScrollPane scrollPane; // scroll pane, switches between the two panels
    private JPanel defenderScrollPanel, attackerScrollPanel; // scroll panels for the defender and attacker tabs

    private ArrayList<DefenderPanel> defenderPanels; // list of panels for defenders to display in scroll pane for defender tab
    // panels show image, name, cost (for defenders), info button, and buy button (for defenders) TODO create class for this

    private Defender selected; // selected defender, only for upgrades

    private JButton defender, attacker; // buttons for the defender and attacker tab

    // shop --> buttons for each type of defender; hovering over shows info
    // info --> image + text
    // upgrades --> display using the individual upgrade classes (as buttons)
    // cancel --> one big button

    // when buying through shop: click --> defender follows mouse (red or blue circle depending on if valid placement) --> escape or clicking on cancel cancels the buy / clicking on valid spot places

    public SidePanel(Player player, GamePanel gamePanel, GameManager gm){
        this.player = player;
        this.gamePanel = gamePanel;
        gameManager = gm;
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        view = View.Tabs;
        tab = Tab.Defender;
    }

    /**
     * initialize all components
     */
    public void initialize(){
        // create header of side panel
        header = new JPanel();
        header.setLocation(0, 0);
        header.setPreferredSize(new Dimension(WIDTH, HEADER_HEIGHT));
        header.setBackground(Color.WHITE);
        headerLayout = new CardLayout();
        header.setLayout(headerLayout);
        add(header, BorderLayout.NORTH);

        // button for defenders
        defender = new JButton("Defenders");
        defender.setBounds(0, 0, WIDTH / 2, HEADER_HEIGHT);
        defender.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if(tab != Tab.Defender){
                    tab = Tab.Defender;
                    switchToDefenderList(); // when button is clicked and not on defender tab, switch to defender tab
                }
            }
            
        });
        defender.setFocusPainted(false);
        defender.setFocusable(false);
        defender.setFont(TEXT_FONT);

        // button for attackers
        attacker = new JButton("Attackers");
        attacker.setBounds(WIDTH / 2, 0, WIDTH / 2, HEADER_HEIGHT);
        attacker.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if(tab != Tab.Attacker){
                    tab = Tab.Attacker;
                    switchToAttackerList(); // when button is clicked and not on attacker tab, switch to attacker tab
                }
            }
            
        });
        attacker.setFocusPainted(false);
        attacker.setFocusable(false);
        attacker.setFont(TEXT_FONT);

        // start off with "home page"
        view = View.Tabs;
        previous = View.Tabs;
        tabsHead = new JPanel();
        tabsHead.setLocation(0, 0);
        tabsHead.setPreferredSize(new Dimension(WIDTH, HEADER_HEIGHT));
        tabsHead.setBackground(Color.WHITE);
        tabsHead.setLayout(null);
        tabsHead.add(defender);
        tabsHead.add(attacker);
        header.add("tabs", tabsHead);

        // create body for side panel
        body = new JPanel();
        bodyLayout = new CardLayout();
        body.setLayout(bodyLayout);
        body.setLocation(0, (int)(100 * Coordinator.SIZE_MULTIPLIER));
        body.setPreferredSize(new Dimension(WIDTH, BODY_HEIGHT));
        body.setBackground(Color.WHITE);
        add(body, BorderLayout.CENTER);

        // initialize scrollpanels and scrollpanes for defenders and attackers

        scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);

        defenderScrollPanel = new JPanel();
        defenderScrollPanel.setLayout(null);
        defenderScrollPanel.setLocation(0, 0);
        defenderScrollPanel.setPreferredSize(new Dimension(WIDTH - SCROLL_BAR_WIDTH, (int)(1600 * Coordinator.SIZE_MULTIPLIER)));

        attackerScrollPanel = new JPanel();
        attackerScrollPanel.setLayout(null);
        attackerScrollPanel.setLocation(0, 0);
        attackerScrollPanel.setPreferredSize(new Dimension(WIDTH, (int)(1300 * Coordinator.SIZE_MULTIPLIER)));

        tabsBody = new JPanel();
        tabsBody.setLayout(new BorderLayout());
        tabsBody.setPreferredSize(new Dimension(WIDTH, BODY_HEIGHT));
        tabsBody.setBackground(Color.BLUE);
        body.add("tabs", tabsBody);
        tabsBody.add(scrollPane);

        initializeDefenderList();
        initializeAttackerList();
        
        // start on defender tab
        switchToDefenderList();

        // set up cancel view
        cancelHead = new JPanel();
        cancelHead.setBackground(Color.RED);
        cancelHead.setLocation(0, 0);
        cancelHead.setPreferredSize(new Dimension(WIDTH, HEADER_HEIGHT));
        header.add("cancel", cancelHead);

        cancelBody = new JPanel();
        cancelBody.setLayout(new BorderLayout());
        cancelBody.setBackground(Color.RED);
        cancelBody.setPreferredSize(new Dimension(WIDTH, BODY_HEIGHT));
        JLabel cancel = new JLabel("Cancel", JLabel.CENTER);
        cancel.setFont(new Font("Dialog", 10, (int)(50 * Coordinator.SIZE_MULTIPLIER)));
        cancel.setPreferredSize(new Dimension(WIDTH, BODY_HEIGHT / 2));
        JLabel X = new JLabel("X", JLabel.CENTER);
        X.setFont(new Font("Dialog", 10, (int)(300 * Coordinator.SIZE_MULTIPLIER)));
        X.setPreferredSize(new Dimension(WIDTH, (int)(BODY_HEIGHT * 0.75)));
        cancelBody.add(X, BorderLayout.NORTH);
        cancelBody.add(cancel, BorderLayout.SOUTH);
        body.add("cancel", cancelBody);

        // set up selecting target view
        targetHead = new JPanel();
        targetHead.setBackground(Color.BLUE);
        targetHead.setLocation(0, 0);
        targetHead.setPreferredSize(new Dimension(WIDTH, HEADER_HEIGHT));
        header.add("target", targetHead);

        targetBody = new JPanel();
        targetBody.setLayout(new BorderLayout());
        targetBody.setBackground(Color.BLUE);
        targetBody.setPreferredSize(new Dimension(WIDTH, BODY_HEIGHT));
        JLabel target = new JLabel("Select target", JLabel.CENTER);
        target.setFont(new Font("Dialog", 10, (int)(50 * Coordinator.SIZE_MULTIPLIER)));
        target.setPreferredSize(new Dimension(WIDTH, BODY_HEIGHT / 2));
        targetBody.add(target, BorderLayout.CENTER);
        body.add("target", targetBody);

        // set up info view
        infoHead = new JPanel();
        infoHead.setLayout(new BorderLayout());
        infoHead.setBackground(Color.WHITE);
        infoHead.setLocation(0, 0);
        infoHead.setPreferredSize(new Dimension(WIDTH, HEADER_HEIGHT));
        infoHead.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        header.add("info", infoHead);

        JLabel infoLabel = new JLabel("Info", JLabel.CENTER);
        infoLabel.setFont(new Font("Dialog", 10, (int)(40 * Coordinator.SIZE_MULTIPLIER)));
        infoLabel.setPreferredSize(new Dimension((int)(350 * Coordinator.SIZE_MULTIPLIER), HEADER_HEIGHT));
        infoHead.add(infoLabel, BorderLayout.EAST);

        JButton backButton = new JButton("<<");
        backButton.setFocusable(false);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension((int)(100 * Coordinator.SIZE_MULTIPLIER), (int)(50 * Coordinator.SIZE_MULTIPLIER)));
        backButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                view = View.Tabs;
                deselect();
            }
        }); 
        infoHead.add(backButton, BorderLayout.WEST);

        infoBody = new JPanel();
        infoBody.setLayout(new BorderLayout());
        infoBody.setBackground(Color.WHITE);
        infoBody.setPreferredSize(new Dimension(WIDTH, BODY_HEIGHT));

        infoName = new JLabel();
        infoName.setFont(new Font("Dialog", 10, (int)(30 * Coordinator.SIZE_MULTIPLIER)));
        infoName.setBackground(Color.WHITE);
        infoName.setPreferredSize(new Dimension(WIDTH, (int)(200 * Coordinator.SIZE_MULTIPLIER)));
        infoBody.add(infoName, BorderLayout.NORTH);

        infoImage = new JLabel();
        infoImage.setBackground(Color.WHITE);
        infoImage.setPreferredSize(new Dimension(WIDTH, (int)(300 * Coordinator.SIZE_MULTIPLIER)));
        infoBody.add(infoImage, BorderLayout.CENTER);

        infoDescription = new JTextPane();
        infoDescription.setEditable(false);
        infoDescription.setFocusable(false);
        infoDescription.setBackground(Color.WHITE);
        infoDescription.setPreferredSize(new Dimension(WIDTH, (int)(525 * Coordinator.SIZE_MULTIPLIER)));
        infoBody.add(infoDescription, BorderLayout.SOUTH);

        body.add("info", infoBody);

        // set up upgrade view
        upgradesHead = new JPanel();
        upgradesHead.setLayout(new BorderLayout());
        upgradesHead.setBackground(Color.WHITE);
        upgradesHead.setLocation(0, 0);
        upgradesHead.setPreferredSize(new Dimension(WIDTH, HEADER_HEIGHT));
        upgradesHead.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        header.add("upgrades", upgradesHead);

        JLabel upgradesLabel = new JLabel("Upgrades", JLabel.CENTER);
        upgradesLabel.setFont(new Font("Dialog", 10, (int)(40 * Coordinator.SIZE_MULTIPLIER)));
        upgradesLabel.setPreferredSize(new Dimension((int)(350 * Coordinator.SIZE_MULTIPLIER), HEADER_HEIGHT));
        upgradesHead.add(upgradesLabel, BorderLayout.EAST);

        backButton = new JButton("<<");
        backButton.setFocusable(false);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension((int)(100 * Coordinator.SIZE_MULTIPLIER), (int)(50 * Coordinator.SIZE_MULTIPLIER)));
        backButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                view = View.Tabs;
                deselect();
            }
        }); 
        upgradesHead.add(backButton, BorderLayout.WEST);

        upgradesBody = new JPanel();
        upgradesBody.setLayout(new BorderLayout());
        upgradesBody.setBackground(Color.WHITE);
        upgradesBody.setPreferredSize(new Dimension(WIDTH, BODY_HEIGHT));

        upgradesName = new JLabel();
        upgradesName.setFont(new Font("Dialog", 10, (int)(30 * Coordinator.SIZE_MULTIPLIER)));
        upgradesName.setBackground(Color.WHITE);
        upgradesName.setPreferredSize(new Dimension(WIDTH, (int)(200 * Coordinator.SIZE_MULTIPLIER)));
        upgradesBody.add(upgradesName, BorderLayout.NORTH);

        upgradesImage = new JLabel();
        upgradesImage.setBackground(Color.WHITE);
        upgradesImage.setPreferredSize(new Dimension(WIDTH, (int)(125 * Coordinator.SIZE_MULTIPLIER)));
        upgradesBody.add(upgradesImage, BorderLayout.CENTER);

        upgradesButtonPanel = new JPanel();
        upgradesButtonPanel.setBackground(Color.WHITE);
        upgradesButtonPanel.setLayout(new FlowLayout());
        upgradesButtonPanel.setPreferredSize(new Dimension(WIDTH, (int)(700 * Coordinator.SIZE_MULTIPLIER)));
        upgradesBody.add(upgradesButtonPanel, BorderLayout.SOUTH);


        body.add("upgrades", upgradesBody);

        // initialize next round button
        
        round = new JPanel();
        round.setLayout(new BorderLayout());
        round.setPreferredSize(new Dimension(WIDTH, ROUND_HEIGHT));
        round.setBackground(Color.RED);
        add(round, BorderLayout.SOUTH);

        nextRound = new JButton("Start Round");
        nextRound.setBackground(Color.GREEN);
        nextRound.setFocusable(false);
        nextRound.setFocusPainted(false);
        nextRound.setUI(WHITE_BUTTON_UI);
        nextRound.setLocation(0, 0);
        nextRound.setPreferredSize(new Dimension(WIDTH - ROUND_HEIGHT * 2, ROUND_HEIGHT));
        nextRound.setFont(TEXT_FONT);
        nextRound.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                nextRound.setBackground(Color.GRAY);
                nextRound.setEnabled(false);
                nextRound.setText("Playing Round");

                gameManager.nextLevel();
                requestingNextRound = false;
            }
        });
        round.add(nextRound, BorderLayout.WEST);

        autoPlay = new JButton("Auto Play Off");
        autoPlay.setBackground(Color.LIGHT_GRAY);
        autoPlay.setFocusable(false);
        autoPlay.setFocusPainted(false);
        autoPlay.setLocation(WIDTH - ROUND_HEIGHT * 2, 0);
        autoPlay.setPreferredSize(new Dimension(ROUND_HEIGHT * 2, ROUND_HEIGHT));
        autoPlay.setFont(LABEL_FONT);
        autoPlay.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                toggleAutoplay();
                if(autoplaying && requestingNextRound){
                    requestNextRound();
                }
            }
        });
        round.add(autoPlay, BorderLayout.EAST);
    }

    /**
     * initializes all of the panels that go in the defenders list TODO fix this
     */
    private void initializeDefenderList(){
        defenderPanels = new ArrayList<DefenderPanel>(); 

        DefenderPanel bt = new DefenderPanel(new BasicTurret(null), player);
        bt.setBounds(0, OFFSET + VERTICAL_GAP, DefenderPanel.WIDTH, DefenderPanel.HEIGHT);
        defenderScrollPanel.add(bt);
        defenderPanels.add(bt);

        DefenderPanel brt = new DefenderPanel(new BoomerangTurret(null), player);
        brt.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 1 + VERTICAL_GAP * 2, DefenderPanel.WIDTH, DefenderPanel.HEIGHT);
        defenderScrollPanel.add(brt);
        defenderPanels.add(brt);

        DefenderPanel pt = new DefenderPanel(new PulsingTurret(null), player);
        pt.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 2 + VERTICAL_GAP * 3, DefenderPanel.WIDTH, DefenderPanel.HEIGHT);
        defenderScrollPanel.add(pt);
        defenderPanels.add(pt);
        
        DefenderPanel snt = new DefenderPanel(new SniperTurret(null), player);
        snt.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 3 + VERTICAL_GAP * 4, DefenderPanel.WIDTH, DefenderPanel.HEIGHT);
        defenderScrollPanel.add(snt);
        defenderPanels.add(snt);

        DefenderPanel ot = new DefenderPanel(new OrbitingTurret(null), player);
        ot.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 4 + VERTICAL_GAP * 5, DefenderPanel.WIDTH, DefenderPanel.HEIGHT);
        defenderScrollPanel.add(ot);
        defenderPanels.add(ot);

        DefenderPanel ct = new DefenderPanel(new CrushingTurret(null), player);
        ct.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 5 + VERTICAL_GAP * 6, DefenderPanel.WIDTH, DefenderPanel.HEIGHT);
        defenderScrollPanel.add(ct);
        defenderPanels.add(ct);

        DefenderPanel spt = new DefenderPanel(new SpinningTurret(null), player);
        spt.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 6 + VERTICAL_GAP * 7, DefenderPanel.WIDTH, DefenderPanel.HEIGHT);
        defenderScrollPanel.add(spt);
        defenderPanels.add(spt);
        
        DefenderPanel bot = new DefenderPanel(new BombTurret(null), player);
        bot.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 7 + VERTICAL_GAP * 8, DefenderPanel.WIDTH, DefenderPanel.HEIGHT);
        defenderScrollPanel.add(bot);
        defenderPanels.add(bot);

        DefenderPanel mgt = new DefenderPanel(new MachineGunTurret(null), player);
        mgt.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 8 + VERTICAL_GAP * 9, DefenderPanel.WIDTH, DefenderPanel.HEIGHT);
        defenderScrollPanel.add(mgt);
        defenderPanels.add(mgt);

        
        

        // DefenderPanel dp1 = new DefenderPanel(new TestDefender(null), player);
        // dp1.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 1 + VERTICAL_GAP * 2, DefenderPanel.WIDTH, DefenderPanel.HEIGHT);
        // defenderScrollPanel.add(dp1);
        // defenderPanels.add(dp1);
    }

    public class DefenderPanel extends JPanel{
        public static final int HEIGHT = (int)(100 * Coordinator.SIZE_MULTIPLIER), WIDTH = SidePanel.WIDTH - SCROLL_BAR_WIDTH,
            INFO_HEIGHT = (int)(50 * Coordinator.SIZE_MULTIPLIER), INFO_WIDTH = (int)(90 * Coordinator.SIZE_MULTIPLIER);
        private JLabel name;
        private JButton buyButton, infoButton;
        private Player player;
        private int cost;
        public DefenderPanel(Defender defender, Player player){
            setBackground(Color.BLUE);
            this.player = player;
            cost = defender.getCost();
            defender.select();

            setLayout(null);

            this.name = new JLabel(defender.getName());
            this.name.setForeground(Color.WHITE);
            this.name.setBounds((int)(20 * Coordinator.SIZE_MULTIPLIER), 0, WIDTH - INFO_WIDTH - HEIGHT - (int)(70 * Coordinator.SIZE_MULTIPLIER), HEIGHT);
            this.name.setFont(LABEL_FONT);
            add(this.name);

            buyButton = new JButton("Buy ($" + defender.getCost() + ")");
            buyButton.setBounds(WIDTH - HEIGHT - (int)(40 * Coordinator.SIZE_MULTIPLIER), 0, HEIGHT + (int)(40 * Coordinator.SIZE_MULTIPLIER), HEIGHT);
            buyButton.setFont(LABEL_FONT);
            buyButton.setBackground(Color.WHITE);
            buyButton.setFocusable(false);
            buyButton.setFocusPainted(false);
            buyButton.setUI(WHITE_BUTTON_UI);
            buyButton.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e){
                    view = View.Cancel;
                    gamePanel.setBoughtDefender(defender);
                }
            });
            add(buyButton);

            infoButton = new JButton("Info");
            infoButton.setBackground(Color.WHITE);
            infoButton.setBounds(WIDTH - HEIGHT - INFO_WIDTH - (int)(55 * Coordinator.SIZE_MULTIPLIER), (HEIGHT - INFO_HEIGHT) / 2, INFO_WIDTH, INFO_HEIGHT);
            infoButton.setFont(LABEL_FONT);
            infoButton.setFocusable(false);
            infoButton.setFocusPainted(false);
            infoButton.setEnabled(true);
            infoButton.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e){
                    view = View.Info;
                    infoName.setText(defender.getName());
                    infoName.setHorizontalAlignment(JLabel.CENTER);
                    infoName.setHorizontalAlignment(JLabel.CENTER);
                    infoDescription.setText(defender.getDescription());
                    StyledDocument doc = infoDescription.getStyledDocument();
                    SimpleAttributeSet center = new SimpleAttributeSet();
                    StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
                    doc.setParagraphAttributes(0, doc.getLength(), center, false);
                    infoDescription.setFont(new Font("Dialog", 10, (int)(20 * Coordinator.SIZE_MULTIPLIER)));
                }
            });
            add(infoButton);
        }

        public void update(){
            buyButton.setEnabled(player.getMoney() >= cost);
            buyButton.setBackground((player.getMoney() >= cost) ? Color.GREEN : Color.RED);
        }
    }

    /**
     * initialize all of the panels that go in the attackers list TODO complete this
     */
    private void initializeAttackerList(){
        AttackerPanel redAP = new AttackerPanel(new RedAttacker(null));
        redAP.setBounds(0, OFFSET + VERTICAL_GAP, AttackerPanel.WIDTH, AttackerPanel.HEIGHT);
        attackerScrollPanel.add(redAP);

        AttackerPanel orangeAP = new AttackerPanel(new OrangeAttacker(null));
        orangeAP.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 1 + VERTICAL_GAP * 2, AttackerPanel.WIDTH, AttackerPanel.HEIGHT);
        attackerScrollPanel.add(orangeAP);

        AttackerPanel greenAP = new AttackerPanel(new GreenAttacker(null));
        greenAP.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 2 + VERTICAL_GAP * 3, AttackerPanel.WIDTH, AttackerPanel.HEIGHT);
        attackerScrollPanel.add(greenAP);

        AttackerPanel cyanAP = new AttackerPanel(new CyanAttacker(null));
        cyanAP.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 3 + VERTICAL_GAP * 4, AttackerPanel.WIDTH, AttackerPanel.HEIGHT);
        attackerScrollPanel.add(cyanAP);

        AttackerPanel pinkAP = new AttackerPanel(new PinkAttacker(null));
        pinkAP.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 4 + VERTICAL_GAP * 5, AttackerPanel.WIDTH, AttackerPanel.HEIGHT);
        attackerScrollPanel.add(pinkAP);

        AttackerPanel magentaAP = new AttackerPanel(new MagentaAttacker(null));
        magentaAP.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 5 + VERTICAL_GAP * 6, AttackerPanel.WIDTH, AttackerPanel.HEIGHT);
        attackerScrollPanel.add(magentaAP);

        AttackerPanel grayAP = new AttackerPanel(new GrayAttacker(null));
        grayAP.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 6 + VERTICAL_GAP * 7, AttackerPanel.WIDTH, AttackerPanel.HEIGHT);
        attackerScrollPanel.add(grayAP);

        AttackerPanel darkGrayAP = new AttackerPanel(new DarkGrayAttacker(null));
        darkGrayAP.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 7 + VERTICAL_GAP * 8, AttackerPanel.WIDTH, AttackerPanel.HEIGHT);
        attackerScrollPanel.add(darkGrayAP);

        AttackerPanel blackAP = new AttackerPanel(new BlackAttacker(null));
        blackAP.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 8 + VERTICAL_GAP * 9, AttackerPanel.WIDTH, AttackerPanel.HEIGHT);
        attackerScrollPanel.add(blackAP);

        AttackerPanel rainbowAP = new AttackerPanel(new RainbowAttacker(null));
        rainbowAP.setBounds(0, OFFSET + DefenderPanel.HEIGHT * 9 + VERTICAL_GAP * 10, AttackerPanel.WIDTH, AttackerPanel.HEIGHT);
        attackerScrollPanel.add(rainbowAP);
    }

    public class AttackerPanel extends JPanel{
        public static final int HEIGHT = (int)(100 * Coordinator.SIZE_MULTIPLIER), WIDTH = SidePanel.WIDTH - SCROLL_BAR_WIDTH,
            INFO_HEIGHT = (int)(50 * Coordinator.SIZE_MULTIPLIER), INFO_WIDTH = (int)(90 * Coordinator.SIZE_MULTIPLIER);
        private JLabel name;
        private JButton infoButton;
        public AttackerPanel(Attacker attacker){
            setLayout(null);
            setBackground(Color.RED);

            this.name = new JLabel(attacker.getName());
            this.name.setForeground(Color.WHITE);
            this.name.setBounds(20, 0, (int)(125 * Coordinator.SIZE_MULTIPLIER), HEIGHT);
            this.name.setFont(LABEL_FONT);
            add(this.name);

            infoButton = new JButton("Info");
            infoButton.setBackground(Color.WHITE);
            infoButton.setBounds(WIDTH - INFO_WIDTH - (int)(30 * Coordinator.SIZE_MULTIPLIER), (HEIGHT - INFO_HEIGHT) / 2, INFO_WIDTH, INFO_HEIGHT);
            infoButton.setFont(LABEL_FONT);
            infoButton.setFocusable(false);
            infoButton.setFocusPainted(false);
            infoButton.setEnabled(true);
            infoButton.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e){
                    view = View.Info;
                    infoName.setText(attacker.getName());
                    infoName.setHorizontalAlignment(JLabel.CENTER);
                    infoImage.setHorizontalAlignment(JLabel.CENTER);
                    infoDescription.setText(attacker.getDescription());
                    StyledDocument doc = infoDescription.getStyledDocument();
                    SimpleAttributeSet center = new SimpleAttributeSet();
                    StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
                    doc.setParagraphAttributes(0, doc.getLength(), center, false);
                    infoDescription.setFont(new Font("Dialog", 10, (int)(20 * Coordinator.SIZE_MULTIPLIER)));
                }
            });
            add(infoButton);
        }
    }

    /**
     * switches to the defenders tab
     */
    private void switchToDefenderList(){
        defender.setBackground(Color.WHITE);
        defender.setForeground(Color.BLACK);
        defender.setBorderPainted(true);
        defender.setRolloverEnabled(false);
        attacker.setBackground(new Color(200, 200, 200, 150));
        attacker.setForeground(new Color(150, 150, 150, 200));
        attacker.setBorderPainted(true);
        attacker.setRolloverEnabled(true);

        scrollPane.setViewportView(defenderScrollPanel);
    }

    /**
     * switches to the attacker tab
     */
    private void switchToAttackerList(){
        attacker.setBackground(Color.WHITE);
        attacker.setForeground(Color.BLACK);
        attacker.setBorderPainted(true);
        attacker.setRolloverEnabled(false);
        defender.setBackground(new Color(200, 200, 200, 150));
        defender.setForeground(new Color(150, 150, 150, 200));
        defender.setBorderPainted(true);
        defender.setRolloverEnabled(true);
        
        scrollPane.setViewportView(attackerScrollPanel);
    }

    public void cancel(){
        view = View.Tabs;
        deselect();
    }

    public void targetSelected(){
        ((TargetDefender)selected).targetSelected();
        select(selected);
    }

    public void reset(){
        selected = null;
        view = View.Tabs;
        tab = Tab.Defender;
        if(autoplaying) toggleAutoplay();
        gamePanel.removeBoughtDefender();
    }

    public void selectTarget(Target target){
        gamePanel.setTarget(target);
        view = View.Target;
    }

    public void select(Defender d){
        if(selected != null) selected.deselect();
        selected = d;
        selected.select();

        upgradesName.setText(selected.getName());
        upgradesName.setHorizontalAlignment(JLabel.CENTER);
        upgradesImage.setHorizontalAlignment(JLabel.CENTER);

        upgradesButtonPanel.removeAll();

        JButton upgrade1 = selected.getUpgrade1();
        upgrade1.setPreferredSize(new Dimension ((int)(400 * Coordinator.SIZE_MULTIPLIER), (int)(125 * Coordinator.SIZE_MULTIPLIER)));
        JButton upgrade2 = selected.getUpgrade2();
        upgrade2.setPreferredSize(new Dimension ((int)(400 * Coordinator.SIZE_MULTIPLIER), (int)(125 * Coordinator.SIZE_MULTIPLIER)));
        JButton upgrade3 = selected.getUpgrade3();
        upgrade3.setPreferredSize(new Dimension ((int)(400 * Coordinator.SIZE_MULTIPLIER), (int)(125 * Coordinator.SIZE_MULTIPLIER)));

        upgradesButtonPanel.add(upgrade1);
        upgradesButtonPanel.add(upgrade2);
        upgradesButtonPanel.add(upgrade3);

        JLabel spacer1 = new JLabel();
        spacer1.setPreferredSize(new Dimension((int)(450 * Coordinator.SIZE_MULTIPLIER), (int)(15 * Coordinator.SIZE_MULTIPLIER)));
        upgradesButtonPanel.add(spacer1);

        JButton targetButton = selected.getTargetButton();
        if(targetButton != null){
            targetButton.setPreferredSize(new Dimension((int)(250 * Coordinator.SIZE_MULTIPLIER), (int)(75 * Coordinator.SIZE_MULTIPLIER)));
            upgradesButtonPanel.add(targetButton);
        }

        JLabel spacer2 = new JLabel();
        spacer2.setPreferredSize(new Dimension((int)(450 * Coordinator.SIZE_MULTIPLIER), (int)(25 * Coordinator.SIZE_MULTIPLIER)));
        upgradesButtonPanel.add(spacer2);

        JButton sell = selected.getSellButton();
        sell.setPreferredSize(new Dimension((int)(150 * Coordinator.SIZE_MULTIPLIER), (int)(40 * Coordinator.SIZE_MULTIPLIER)));
        sell.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                deselect();
            }
        });
        upgradesButtonPanel.add(sell);


        view = View.Upgrades;
    }

    public boolean hasSelected(){
        return selected != null;
    }

    public void deselect(){
        if(selected != null) {
            selected.deselect();
        }
        selected = null;
        view = View.Tabs;
    }

    public void requestNextRound(){
        if(autoplaying){
            nextRound.setBackground(Color.GRAY);
            nextRound.setEnabled(false);
            nextRound.setText("Playing Round");
            gameManager.nextLevel();
            requestingNextRound = false;
        }
        else{
            nextRound.setText("Start Round");
            nextRound.setBackground(Color.GREEN);
            nextRound.setEnabled(true);
            requestingNextRound = true;
            if(gameManager.getLevel() > 1) SoundPlayer.playLevelUp();
        }
    }

    public void toggleAutoplay(){
        autoplaying = !autoplaying;
        autoPlay.setText("Auto Play " + ((autoplaying) ? "On" : "Off"));
        autoPlay.setBackground((autoplaying) ? Color.GREEN : Color.LIGHT_GRAY);
    }

    /**
     * updates the side panel based on the view
     */
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        for(DefenderPanel dp : defenderPanels){
            dp.update();
        }
        if(view != previous){
            if(view == View.Tabs){
                headerLayout.show(header, "tabs");
                bodyLayout.show(body, "tabs");
                if(round.getComponentCount() == 0) {
                    round.add(nextRound, BorderLayout.WEST);
                    round.add(autoPlay, BorderLayout.EAST);
                }
            }
            else if(view == View.Info){
                headerLayout.show(header, "info");
                bodyLayout.show(body, "info");
                if(round.getComponentCount() == 0) {
                    round.add(nextRound, BorderLayout.WEST);
                    round.add(autoPlay, BorderLayout.EAST);
                }
            }
            else if(view == View.Upgrades){
                headerLayout.show(header, "upgrades");
                bodyLayout.show(body, "upgrades");
                if(round.getComponentCount() == 0) {
                    round.add(nextRound, BorderLayout.WEST);
                    round.add(autoPlay, BorderLayout.EAST);
                }
            }
            else if(view == View.Cancel){
                headerLayout.show(header, "cancel");
                bodyLayout.show(body, "cancel");
                round.setBackground(Color.RED);
                round.remove(nextRound);
                round.remove(autoPlay);
            }   
            else if(view == View.Target){
                headerLayout.show(header, "target");
                bodyLayout.show(body, "target");
                round.setBackground(Color.BLUE);
                round.remove(nextRound);
                round.remove(autoPlay);
            }
        }
        previous = view;
    }
}
