/*
 * File added by Nathan MacLeod 2020
 */
package arizonaadventure;

import static arizonaadventure.MoveingEntity.generateSquareHitbox;
import java.awt.Graphics2D;

/**
 *
 * @author macle
 */
public class BurstEnemy extends KillableEntity {
    private static Sprite sprite = new Sprite("gamerdrone.png", (int)(50 * 2));
    private static Sprite bulletSprite = new Sprite("gfuel.png", (int)(16 * 2));
    private static final String fireSound = "pew.wav";
    private CooldownTimer burst;
    private final int burstSize = 4;
    private int burstCount = 0;
    
    private double speed;
    private CooldownTimer fire;
    
    public BurstEnemy(double x, double y) {
        super(x, y, generateSquareHitbox(80, 25), 100, 35);
        speed = 135;
        fire = new CooldownTimer(5);
        burst = new CooldownTimer(0.6);
        burst.resetTimer();
    }
    
    private void shoot(double timePassed, ArizonaAdventure game) {
            if(burstCount <= 0) {
                burst.updateTimer(timePassed);
                if(burst.tryToFire()) {
                    burstCount = burstSize;
                    fire.cutTimer();
                }
            }
            else {
                fire.updateTimer(timePassed);
                if(fire.tryToFire()) {
                    SoundManager.play(fireSound);
                    burstCount--;
                    Player player = game.getPlayer();
                    Vector2D velocity = new Vector2D(player.x - x, player.y - y).getUnitVector().scale(250);
                    game.addNewProjectile(new BasicEnemyBullet(x, y, velocity, bulletSprite));
                }
            }
        }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        sprite.draw(g, x, y, orientation);
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        moveEntity(-speed * timePassed, 0, 0);
        shoot(timePassed, game);
    }
}
