/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

/**
 *
 * @author macle
 */
public interface Boss extends Updatable {
    
    public boolean bossDefeated();

    public void update(double timePassed, ArizonaAdventure game);
    
}
