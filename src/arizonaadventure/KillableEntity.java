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
public class KillableEntity extends MoveingEntity {
    
    protected double hp;
    
    public KillableEntity(double x, double y, ArrayList<Vector2D> hitbox, double hp) {
        super(x, y, hitbox, 0);
        this.hp = hp;
    }
    
    public void takeDamage(double damage) {
        hp -= damage;
    }
    
    public boolean isDead() {
        return hp <= 0;
    }
}
