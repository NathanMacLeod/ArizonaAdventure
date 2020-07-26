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
public class Projectile extends MoveingEntity {
    
    protected Vector2D velocity;
    private boolean isFriendly;
    private double damage;
    private boolean collided = false;
    private double size;
    
    public Projectile(double x, double y, ArrayList<Vector2D> hitbox, Vector2D velocity, double damage, double size, boolean isFriendly) {
        super(x, y, hitbox, 0);
        this.velocity = velocity;
        this.isFriendly = isFriendly;
        this.damage = damage;
        this.size = size;
    }
    
    public boolean hasCollided() {
        return collided;
    }
    
    private void addExplosions(ArizonaAdventure game) {
        game.addExplosion(new ExplosionEffect(x, y, (int) (size * 1), 0.1));
    }
    
    protected boolean checkForCollision(ArizonaAdventure game) {
        if(isFriendly) {
            for(KillableEntity enemy : game.getEnemies()) {
                if(hitboxesIntersecting(enemy)) {
                    enemy.takeDamage(damage);
                    collided = true;
                    addExplosions(game);
                    return true;
                }
            }
        }
        else {
            Player player = game.getPlayer();
            if(hitboxesIntersecting(player)) {
                player.takeDamage(damage);
                collided = true;
                addExplosions(game);
                return true;
            }
        }
        return false;
    }
    
    protected void move(double timePassed) {
        Vector2D translation = velocity.scale(timePassed);
        moveEntity(translation.x, translation.y, 0);
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        move(timePassed);
        checkForCollision(game);
    }
    
}
