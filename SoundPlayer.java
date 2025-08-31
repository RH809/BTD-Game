
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer {

    public static boolean on = true; // whether or not sound is on

    public static final String BUY = "Sounds/Buy.wav";
    public static final String SELL = "Sounds/Sell.wav";
    public static final String DAMAGE = "Sounds/Damage.wav";
    public static final String HIT = "Sounds/Hit.wav";
    public static final String LEVEL_UP = "Sounds/Level Up.wav";
    public static final String PLACE = "Sounds/Place.wav";
    public static final String POP = "Sounds/Pop.wav";
    public static final String EXPLOSION = "Sounds/Explosion.wav";
    public static final String CRUSH = "Sounds/Crush.wav";
    public static final String MG_RELOAD = "Sounds/Machine Gun Reload.wav";
    public static final String START_LEVEL = "Sounds/Start Level.wav";
    public static final String START_GAME = "Sounds/Start Game.wav";
    public static final String GAME_OVER = "Sounds/Game Over.wav";

    private static Semaphore hitSemaphore = new Semaphore(4, true);
    private static Semaphore popSemaphore = new Semaphore(8, true);
    private static Semaphore explosionSemaphore = new Semaphore(5, true);
    private static Semaphore crushSemaphore = new Semaphore(5, true);
    private static Semaphore mgReloadSemaphore = new Semaphore(3, true);

    /**
     * turn off the sound
     */
    public static void turnOff() { on = false; }
    /**
     * turn on the sound
     */
    public static void turnOn() { on = true; }

    public static void playBuy(){ playSound(BUY); }

    public static void playSell() { playSound(SELL); }

    public static void playDamage() { playSound(DAMAGE); }

    public static void playHit() {
        if(hitSemaphore.tryAcquire()){
            playSound(HIT, hitSemaphore);
        }
    }

    public static void playLevelUp() { playSound(LEVEL_UP); }

    public static void playPlace() { playSound(PLACE); }

    public static void playPop() {
        if(popSemaphore.tryAcquire()){
            playSound(POP, popSemaphore);
        }
     }

    public static void playExplosion() {
        if(explosionSemaphore.tryAcquire()){
            playSound(EXPLOSION, explosionSemaphore);
        }
    }

    public static void playCrush() {
        if(crushSemaphore.tryAcquire()){
            playSound(CRUSH, crushSemaphore);
        }
    }

    public static void playMGReload() {
        if(mgReloadSemaphore.tryAcquire()){
            playSound(MG_RELOAD, mgReloadSemaphore);
        }
    }

    public static void playStartLevel() { playSound(START_LEVEL); }

    public static void playStartGame() { playSound(START_GAME); }

    public static void playGameOver() { playSound(GAME_OVER); }

    /**
     * @param audio - path of the sound file to be played
     * @param semaphore - semaphore of the audio to be released when audio finishes
     * 
     * play the sound from the given audio file
     */
    public static void playSound(String audio, Semaphore semaphore) {
        if(on){
            Thread thread = new Thread(new Runnable(){
                public void run(){
                    try {
                        //System.out.println("playing audio");
                        File f = new File(audio);
                        //System.out.println(f.getName());
                        AudioInputStream audioStream = AudioSystem.getAudioInputStream(f);
                        
                        Clip clip = AudioSystem.getClip();
                        clip.open(audioStream);
                        clip.addLineListener(e -> {
                            if (e.getType() == LineEvent.Type.STOP) {
                                semaphore.release();
                            }
                        });
                        clip.start();
                    } catch (UnsupportedAudioFileException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (LineUnavailableException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
	}

    public static void playSound(String audio) {
        if(on){
            Thread thread = new Thread(new Runnable(){
                public void run(){
                    try {
                        //System.out.println("playing audio");
                        File f = new File(audio);
                        //System.out.println(f.getName());
                        AudioInputStream audioStream = AudioSystem.getAudioInputStream(f);
                        
                        Clip clip = AudioSystem.getClip();
                        clip.open(audioStream);
                        clip.start();
                    } catch (UnsupportedAudioFileException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (LineUnavailableException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
	}


}
