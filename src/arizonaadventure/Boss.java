/*
 * File added by Nathan MacLeod 2020
 */
package arizonaadventure;

/**
 *
 * @author macle
 */
public interface Boss extends Updatable {
    
    public boolean bossDefeated();

    public void update(double timePassed, ArizonaAdventure game);
    
    public double getHealthPercentage();
    
    public void endMusic();
    
}
