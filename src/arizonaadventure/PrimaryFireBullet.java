/*
 * File added by Nathan MacLeod 2020
 */
package arizonaadventure;

import java.util.ArrayList;
import java.awt.Graphics2D;

/**
 *
 * @author macle
 */
public class PrimaryFireBullet extends Projectile {
    private static final String boom = "lowcrack.wav";
    private static Sprite sprite = new Sprite("playerbullet.png", (int)25);
    
    public PrimaryFireBullet(double x, double y, double damage) {
        super(x, y, generateSquareHitbox(25, 10), new Vector2D(600, 0), damage, 25, true);
        sprite = new Sprite(sprite);
    }
    
    protected void addExplosion(ArizonaAdventure game) {
        super.addExplosion(game);
        SoundManager.play(boom);
        
    }
     
    public void draw(Graphics2D g) {
        //super.draw(g);
        sprite.draw(g, x, y, orientation);
    }
    
}
