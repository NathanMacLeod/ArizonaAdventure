/*
 * File added by Nathan MacLeod 2020
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
    protected boolean expired = false;
    private double size;
    
    public Projectile(double x, double y, ArrayList<Vector2D> hitbox, Vector2D velocity, double damage, double size, boolean isFriendly) {
        super(x, y, hitbox, 0);
        this.velocity = velocity;
        this.isFriendly = isFriendly;
        this.damage = damage;
        this.size = size;
    }
    
    public boolean expired() {
        return expired;
    }
    
    public void deleteActions() {
        
    }
    
    protected void addExplosion(ArizonaAdventure game) {
        game.addExplosion(new ExplosionEffect(x, y, (int) (size * 1), 0.1));
    }
    
    protected boolean checkForCollision(ArizonaAdventure game) {
        if(isFriendly) {
            for(KillableEntity enemy : game.getEnemies()) {
                if(hitboxesIntersecting(enemy)) {
                    enemy.takeDamage(damage);
                    expired = true;
                    addExplosion(game);
                    return true;
                }
            }
        }
        else {
            Player player = game.getPlayer();
            if(hitboxesIntersecting(player)) {
                player.takeDamage(damage);
                expired = true;
                addExplosion(game);
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
