import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Coordinator that runs the game
 */
public class Coordinator extends JFrame{

    public static final double SIZE_MULTIPLIER = 0.6; // Multiplier that resizes everything to fit screen

    public static double tickRate = 150.0;
    public static JPanel framePanel, start, game, end;
    public static CardLayout frameLayout;
    public static GameManager gm;
    public static Player player;
    public static GamePanel gamePanel;

    public static boolean playing = false;

    public Coordinator(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds((int)(50 * SIZE_MULTIPLIER), (int)(100 * SIZE_MULTIPLIER), (int)(2500 * SIZE_MULTIPLIER), (int)(1200 * SIZE_MULTIPLIER));
        setLayout(new BorderLayout());
        
        
        setBackground(Color.WHITE);

        framePanel = new JPanel();
        frameLayout = new CardLayout();
        framePanel.setLayout(frameLayout);
        add(framePanel);

        player = new Player();

        gm = new GameManager(player);

        gamePanel = new GamePanel(gm, this);

        gamePanel.addMouseListener(gamePanel);
        gamePanel.addMouseMotionListener(gamePanel);
        gamePanel.addMouseWheelListener(gamePanel);
        
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setPreferredSize(new Dimension((int)(2050 * SIZE_MULTIPLIER), (int)(1200 * SIZE_MULTIPLIER)));

        SidePanel sidePanel = new SidePanel(player, gamePanel, gm);
        sidePanel.setPreferredSize(new Dimension((int)(450 * SIZE_MULTIPLIER), (int)(1200 * SIZE_MULTIPLIER)));
        sidePanel.initialize();
        gamePanel.setSidePanel(sidePanel);
        gm.setSidePanel(sidePanel);

        start = new JPanel(null);
        framePanel.add("start", start);
        JButton play = new JButton("PLAY");
        play.setFont(new Font("Dialog", 10, (int)(50 * SIZE_MULTIPLIER)));
        play.setBounds((int)((2500 / 2 - 500 / 2) * SIZE_MULTIPLIER), (int)((1200 / 2 - 240 / 2) * SIZE_MULTIPLIER),
             (int)(500 * SIZE_MULTIPLIER),(int)(240 * SIZE_MULTIPLIER));
        play.setFocusable(false);
        play.setFocusPainted(false);
        play.setBackground(Color.WHITE);
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                runGame();
            }
        });
        start.add(play);

        game = new JPanel();
        game.setLayout(new BorderLayout());
        framePanel.add("game", game);

        game.add(backgroundPanel, BorderLayout.CENTER);
        game.add(sidePanel, BorderLayout.EAST);
        setGlassPane(gamePanel);
        gamePanel.setVisible(true);

        end = new JPanel(null);
        framePanel.add("end", end);

        JButton newGame = new JButton("NEW GAME");
        newGame.setFont(new Font("Dialog", 10, (int)(50 * SIZE_MULTIPLIER)));
        newGame.setBounds((int)((2500 / 2 - 500 / 2) * SIZE_MULTIPLIER), (int)((1200 / 2 - 240 / 2 - 200) * SIZE_MULTIPLIER),
            (int)(500 * SIZE_MULTIPLIER),(int)(240  *SIZE_MULTIPLIER));
        newGame.setFocusable(false);
        newGame.setFocusPainted(false);
        newGame.setBackground(Color.WHITE);
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                runGame();
            }
        });
        end.add(newGame);

        JButton exit = new JButton("EXIT");
        exit.setFont(new Font("Dialog", 10, (int)(50 * SIZE_MULTIPLIER)));
        exit.setBounds((int)((2500 / 2 - 500 / 2) * SIZE_MULTIPLIER), (int)((1200 / 2 - 240 / 2 + 200) * SIZE_MULTIPLIER),
            (int)(500 * SIZE_MULTIPLIER),(int)(240  *SIZE_MULTIPLIER));
        exit.setFocusable(false);
        exit.setFocusPainted(false);
        exit.setBackground(Color.WHITE);
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                setVisible(false);
                dispose();
                System.exit(ABORT);
            }
        });
        end.add(exit);


        frameLayout.show(framePanel, "start");
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                new Coordinator();
            }
        });
        
    }

    public void runGame(){
        frameLayout.show(framePanel, "game");
        playing = true;
        gamePanel.setVisible(true);
        gm.newGame();
        // instead of waiting for specific amount of time, do certain amount of ticks and then show (divide movement by ticks between frames)
        
        new Thread(() -> {
            double unprocessedSeconds = 0;
            long prevTime = System.nanoTime(), currTime, passedTime;
            double secondsPerTick = 1 / tickRate;
            boolean ticked = false;
            while(player.getHealth() > 0){
                secondsPerTick = 1 / tickRate; // time for each tick
                currTime = System.nanoTime();
                passedTime = currTime - prevTime;
                prevTime = currTime;
                unprocessedSeconds += passedTime / 1000000000.0; // determine number of unprocessed seconds since last run through loop
    
                while(unprocessedSeconds > secondsPerTick){ // process for all of the unprocessed seconds
                    gm.tick();
                    unprocessedSeconds -= secondsPerTick;
                    ticked = true;
                }
                if(ticked){
                    gamePanel.repaint();
                    
                }
                ticked = false;
    
            }
            gamePanel.setVisible(false);
            frameLayout.show(framePanel, "end");
            SoundPlayer.playGameOver();
            playing = false;
        }).start();
        
    }
}