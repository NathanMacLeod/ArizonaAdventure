/*
 * File added by Nathan MacLeod 2020
 */
package arizonaadventure;

import java.awt.Graphics2D;
import java.awt.Color;
/**
 *
 * @author macle
 */
public class ColorFlash extends Effect {
    private double growTime, decayTime;
    private int width, height;
    private double life;
    float r, g, b;
    
    public ColorFlash(double growTime, double decayTime, int width, int height, float r, float g, float b) {
        this.growTime = growTime;
        this.decayTime = decayTime;
        this.width = width;
        this.height = height;
        this.life = 0;
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        life += timePassed;
    }
    
    public boolean done() {
        return life >= growTime + decayTime;
    }
    
    public void draw(Graphics2D gra) {
        float alpha = 0;
        if(life < growTime) {
            alpha = (float) (life/growTime);
        }
        else if(life < growTime + decayTime) {
            alpha = (float) (1 - (life - growTime)/decayTime);
        }
        else {
            alpha = 0;
        }
        gra.setColor(new Color(r, g, b, alpha));
        gra.fillRect(0, 0, width, height);
    }
}
