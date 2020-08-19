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
public class SpikeObstacle extends KillableEntity {
    private static final String slam = "spikeslam.wav";
    private static double restY = 75;
    private static double upLength = 3;
    private static int width = 100;
    private static int height = 35;
    private static int postHeight = 600;
    private static int postWidth = 40;
    private KillableEntity postHitbox;
    private SpikeState state;
    private double dropSpeed = 650;
    private double riseSpeed = 200;
    private double fallY = 580;
    private double leftSpeed = -MechBoss.getWalkSpeed() * 3.0/2;
    private CooldownTimer downTime;
    private CooldownTimer upTime;
    private static Sprite spikeHead = new Sprite("spikehead.png", (int) (width * 1.7));
    private static Sprite post = new Sprite("spikepost.png", (int) (postHeight * 1.3));
    
    public SpikeObstacle(double x, ArizonaAdventure game, double time) {
        super(x, restY, generateSquareHitbox(width, height), 100000000, 1);

        state = SpikeState.Risen;
        
        postHitbox = new KillableEntity(x, restY - (height + postHeight)/2.0, generateSquareHitbox(postWidth, postHeight), 100000000, 1) {
            
            public boolean entityOffLeft() {
                return SpikeObstacle.this.entityOffLeft();
            }
            
            public void draw(Graphics2D g) {
                
            }
        };
        downTime = new CooldownTimer(1/0.75);
        upTime = new CooldownTimer(1/upLength);
        
        downTime.resetTimer();
        setDropTime(time);
        
        game.addNewEnemy(postHitbox);
        game.addNewEnemy(this);
    }
    
    public static int getWidth() {
        return width;
    }
    
    public static double getUpTime() {
        return upLength;
    }
    
    public void setDropTime(double time) {
        upTime.setTime(time);
    }
    
    private enum SpikeState {
        Risen, Rising, Falling, Fallen;
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        double moveY = 0;
        switch(state) {
            case Risen:
                upTime.updateTimer(timePassed);
                if(upTime.tryToFire()) {
                    state = SpikeState.Falling;
                }
                break;
            case Fallen:
                downTime.updateTimer(timePassed);
                if(downTime.tryToFire()) {
                    state = SpikeState.Rising;
                }
                break;
            case Rising:
                moveY = -riseSpeed * timePassed;
                if(y + moveY < restY) {
                    moveY = restY - y;
                    state = SpikeState.Risen;
                }
                break;
            case Falling:
                moveY = dropSpeed * timePassed;
                if(y + moveY > fallY) {
                    moveY = fallY - y;
                    state = SpikeState.Fallen;
                    if(!super.entityOutOfBounds(game)) {
                         SoundManager.play(slam);
                    }
                }
                break;
        }
        moveEntity(leftSpeed * timePassed, moveY, 0);
        postHitbox.moveEntity(leftSpeed * timePassed, moveY, 0);
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        post.draw(g, postHitbox.x, postHitbox.y, 0);
        spikeHead.draw(g, x, y, 0);
    }
    
}
