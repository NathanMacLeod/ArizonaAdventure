/*
 * File added by Nathan MacLeod 2020
 */
package arizonaadventure;

import java.awt.Graphics2D;
/**
 *
 * @author macle
 */
public class HealthPickup extends Pickup {
    private static Sprite sprite = new Sprite("healthpickup.png", 80);
    private static double width = 40;
    private static double height = 70;
    
    public HealthPickup(double x, double y) {
        super(x, y, generateSquareHitbox(width, height));
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        sprite.draw(g, x, y, 0);
    }
}
