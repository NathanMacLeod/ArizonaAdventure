
/*
 * File added by Nathan MacLeod 2020
 */
package arizonaadventure;

import java.awt.Graphics2D;

/**
 *
 * @author macle
 */
public class FodderEnemy extends KillableEntity {
    private static Sprite sprite = new Sprite("cokeplane.png", (int)(60 * 1.1));
    
    private double speed;
    
    public FodderEnemy(double x, double y) {
        super(x, y, generateSquareHitbox(60, 30), 10, 40);
        speed = 225;
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        sprite.draw(g, x, y, orientation);
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        moveEntity(-speed * timePassed, 0, 0);
    }
    
}
