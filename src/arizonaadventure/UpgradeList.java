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
public class UpgradeList {
    
    public int health;
    public int fireRate;
    public int fireVolume;
    public boolean miniShip;
    public boolean missiles;
    
    public UpgradeList() {
        reset();
    }
    
    public void reset() {
        fireVolume = 0;
        health = 0;
        fireRate = 0;
        miniShip = false;
        missiles = false;
    }
}
