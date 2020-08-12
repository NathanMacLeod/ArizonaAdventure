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
public class TheTower extends KillableEntity implements Boss {
    private static Sprite sprite;
    private static final String nuke = "nuke.wav";
    
    private double shakeMagnitude = 0;
    private double shakePeriod = 0.06;
    
    private CooldownTimer explosionTimer;
    private CooldownTimer winDelay;
    private static double startHP = 100000;
    private static double width = 1000;
    private static double height = 1000;
    private static double spawnX = 1000 + width / 1.5;
    private static double spawnY = 150;
    private static double finalX = 1250;
    private static double antennaHeight = height / 2.5;
    private static double speed = 20;
    private int nExplosions = 150;
    private double collapseSpeed = 0;
    private double life = 0;
    private boolean crashedInto = false;
    private boolean collapsed = false;
    
    public TheTower(ArizonaAdventure game) {
        super(spawnX, spawnY, generateSquareHitbox(width, height), startHP, 600);
        explosionTimer = new CooldownTimer(21);
        winDelay = new CooldownTimer(1/5.0);
        winDelay.resetTimer();
        game.addNewEnemy(this);
    }
    
    public static void loadSprite() {
        sprite = new Sprite("skyScraper.png", (int) height);
    }
    
    public void endMusic() {
        
    }
    
    public double getHealthPercentage() {
        return (crashedInto)? 0 : hp / startHP;
    }
    
    public boolean bossDefeated() {
        return hp <= 0;
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        sprite.draw(g, x, y, orientation);
    }
    
    public void takeDamage(double d) {
        //do nothing lol
    }
    
    private void collapse(double timePassed, ArizonaAdventure game) {
        if(!collapsed) {
            collapseSpeed += Math.pow((1 + collapseSpeed), 0.7) * 2 * timePassed;

            shakeMagnitude += 2 * timePassed;

            double jostleVel = shakeMagnitude * Math.PI / (shakePeriod) * Math.cos(life * Math.PI / (shakePeriod)) +
            shakeMagnitude * 0.1 * 7 / shakePeriod * Math.cos(life * 7 / shakePeriod);
            double move = -jostleVel * timePassed;


            moveEntity(move, collapseSpeed * timePassed, 0);
            explosionTimer.updateTimer(timePassed);
            if(nExplosions > 0 && explosionTimer.tryToFire()) {
                double xC = x - width/2.0 + Math.random() * (width/2.0 + 1000 - finalX);
                double yC = y + antennaHeight - height/2.0 + Math.random() * (600 - (spawnY - height/2.0 - antennaHeight));
                game.addExplosion(new ExplosionEffect(xC, yC, (int) (160 + Math.random() * 90), 0.35));
                SoundManager.play(explosion);
                nExplosions--;
            }
            if(y > 1700 & y - 1700 < collapseSpeed * timePassed) {
                SoundManager.play(nuke);
                game.addEffect(new ColorFlash(0.1, 5.0, (int) game.getGameWidth(), (int) game.getGameHeight(), 1.0f, 1.0f, 1.0f));
                game.addEffect(new ColorFlash(0.6, 30, (int) game.getGameWidth(), (int) game.getGameHeight(), 0.2f, 0.2f, 0.2f));
                collapsed = true;
            }
        }
        else {
            winDelay.updateTimer(timePassed);
            if(winDelay.tryToFire()) {
                kill();
            }
        }
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        life += timePassed;
        if(x > finalX) {
            moveEntity(-timePassed * speed, 0, 0);
        }
        if(!crashedInto) {
            if(hitboxesIntersecting(game.getPlayer())) {
                game.getPlayer().kill();
                crashedInto = true;
                SoundManager.play("collapse.wav");
            }
        }
        else {
            collapse(timePassed, game);
        }
    }
}
