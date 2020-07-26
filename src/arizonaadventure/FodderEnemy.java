/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.awt.Graphics2D;

/**
 *
 * @author macle
 */
public class FodderEnemy extends KillableEntity {
    private static Sprite sprite = null;
    
    private double speed;
    
    public FodderEnemy(double x, double y) {
        super(x, y, generateSquareHitbox(60, 30), 10, 40);
        speed = 225;
        if(sprite == null) {
            sprite = new Sprite("cokeplane.png", (int)(60 * 1.1));
        }
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        sprite.draw(g, x, y, orientation);
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        moveEntity(-speed * timePassed, 0, 0);
    }
    
}
