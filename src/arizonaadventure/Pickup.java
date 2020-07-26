/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.util.ArrayList;
/**
 *
 * @author macle
 */
public class Pickup extends MoveingEntity {
    private static double speed = 150;
    private boolean consumed;
    
    public Pickup(double x, double y, ArrayList<Vector2D> hitbox) {
        super(x, y, hitbox, 0);
        consumed = false;
    }
    
    public boolean isConsumed() {
        return consumed;
    }
    
    public void setConsumed() {
        consumed = true;
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        moveEntity(-speed * timePassed, 0, 0);
    }
}
