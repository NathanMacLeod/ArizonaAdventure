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
public class CooldownTimer {
    private double fireRate; //times per second
    private double cooldownTime;
    private double currentTime;
    
    public CooldownTimer(double fireRate) {
        this.fireRate = fireRate;
        cooldownTime = 1.0 / fireRate;
        currentTime = 0;
    }
    
    public void adjustFireRate(double newFireRate) {
        fireRate = newFireRate;
        cooldownTime = 1.0 / fireRate;
        if(currentTime > cooldownTime) {
            currentTime = cooldownTime;
        }
    } 
    
    public boolean readyToFire() {
        return currentTime == 0;
    }
    
    public void updateTimer(double timePassed) {
        currentTime -= timePassed;
        if(currentTime < 0)
            currentTime = 0;
    }
    
    public void randomize() {
        currentTime = Math.random() * cooldownTime;
    }
    
    public void resetTimer() {
        currentTime = cooldownTime;
    }
    
    public void cutTimer() {
        currentTime = 0;
    }
    
    public boolean tryToFire() {
        if(readyToFire()) {
            resetTimer();
            return true;
        }
        return false;
    }
}
