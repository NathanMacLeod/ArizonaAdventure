/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.util.ArrayList;
import java.awt.Graphics2D;

/**
 *
 * @author macle
 */
public class TrainBoss implements Boss {
    
    private TrainCar[] cars;
    private int currentCar;
    private boolean movingCars;
    private final double desiredFinalPos = 0.5;
    private final double moveSpeed = 50;
    private final double shakeMagnitude = 25;
    private final double shakePeriod = 8;
    private double life;
    
    public TrainBoss(ArizonaAdventure game) {
        life = 0;
        currentCar = 0;
        movingCars = true;
        cars = new TrainCar[] {
            new TrainCar(0, 0, 450, 100), 
            new TrainCar(0, 0, 450, 100), 
            new TrainCar(0, 0, 450, 100), 
            new TrainCar(0, 0, 450, 100)
        };
        
        
        
        double gap = 30;
        double x = game.getGameWidth() + gap;
        double y = game.getGameHeight() * 0.95;
        
        for(int i = 0; i < cars.length; i++) {
            cars[i].moveEntity(x + cars[i].width/2.0, y - cars[i].height/2.0, 0);
            x += cars[i].width + gap;
            game.addNewEnemy(cars[i]);
            switch(i) {
                case 0:
                    cars[i].spawnTurrets(game, new int[] {0, 0, 0});
                    break;
                case 1:
                    cars[i].spawnTurrets(game, new int[] {0, 1, 0, 1});
                    break;
                case 2:
                    cars[i].spawnTurrets(game, new int[] {0, 2});
                    break;
                case 3:
                    cars[i].spawnTurrets(game, new int[] {0, 1, 2, 0});
                    break;
            }
            
        }
    }
    
    private class Turret extends KillableEntity {
        private double width;
        private double height;
        
        public Turret(double x, double y, double width, double height, double hp) {
            super(x, y, generateSquareHitbox(width, height), hp);
            this.width = width;
            this.height = height;
        }
        
        public void shoot(double timePassed, ArizonaAdventure game) {
            
        }
        
        public void update(double timePassed, ArizonaAdventure game) {
            shoot(timePassed, game);
        }
    }
    
    private class MissileTurret extends Turret {
        CooldownTimer fire;
        
        public MissileTurret() {
            super(0, 0, 40, 60, 300);
            fire = new CooldownTimer(0.33);
            fire.randomize();
        }
    
        public void shoot(double timePassed, ArizonaAdventure game) {
            fire.updateTimer(timePassed);
            if(fire.tryToFire()) {
                Player player = game.getPlayer();
                Vector2D dir = new Vector2D(player.x - x, player.y - y);
                game.addNewProjectile(new EnemyRocket(x, y, dir.getAngle(), 20));
            }
        }
    }
    
    private class BasicTurret extends Turret {
        CooldownTimer fire;
        
        public BasicTurret() {
            super(0, 0, 30, 50, 200);
            fire = new CooldownTimer(0.8);
            fire.randomize();
        }
        
        public void shoot(double timePassed, ArizonaAdventure game) {
            fire.updateTimer(timePassed);
            if(fire.tryToFire()) {
                Player player = game.getPlayer();
                Vector2D velocity = new Vector2D(player.x - x, player.y - y).getUnitVector().scale(250);
                game.addNewProjectile(new BasicEnemyBullet(x, y, velocity));
            }
        }
    }
    
    private class BurstTurret extends Turret {
        CooldownTimer burst;
        CooldownTimer fire;
        final int burstSize = 25;
        int burstCount = 0;
        
        public BurstTurret() {
            super(0, 0, 70, 60, 650);
            burst = new CooldownTimer(0.18);
            fire = new CooldownTimer(10);
        }
        
        public void shoot(double timePassed, ArizonaAdventure game) {
            if(burstCount == 0) {
                burst.updateTimer(timePassed);
                if(burst.tryToFire()) {
                    burstCount = burstSize;
                    fire.cutTimer();
                }
            }
            if(burstCount != 0) {
                fire.updateTimer(timePassed);
                if(fire.tryToFire()) {
                    burstCount--;
                    Player player = game.getPlayer();
                    Vector2D velocity = new Vector2D(player.x - x, player.y - y).getUnitVector().scale(250);
                    game.addNewProjectile(new BasicEnemyBullet(x, y, velocity));
                }
            }
        }
    }
    
    private class TrainCar extends KillableEntity {
        private ArrayList<Turret> turrets;
        private double width, height;
        protected Sprite carSprite;
        
        public TrainCar(double x, double y, double width, double height) {
            super(x, y, generateSquareHitbox(width, height), 700);
            this.width = width;
            this.height = height;
            turrets = new ArrayList();
        }
        
        protected void spawnTurrets(ArizonaAdventure game, int[] turretTypes) {
            int nTurrets = turretTypes.length;
            for(int i = 0; i < nTurrets; i++) {
                Turret t = null;
                int type = turretTypes[i];
                switch(type) {
                    case 0:
                        t = new BasicTurret();
                        break;
                    case 1:
                        t = new MissileTurret();
                        break;
                    case 2:
                        t = new BurstTurret();
                        break;
                }
                double xPos = x - width/2.0 + (i + 1) * width/(nTurrets + 1);
                double yPos = y - height/2.0 - t.height/2.0;
                t.moveEntity(xPos, yPos, 0);
                game.addNewEnemy(t);
                turrets.add(t);
            }
        }
        
        public void takeDamage(double damage) {
            super.takeDamage(damage);
            if(isDead()) {
                for(Turret t : turrets) {
                    t.kill();
                }
            }
        }
        
        //dont rotate orientation
        public void moveEntity(double x, double y, double orientation) {
            super.moveEntity(x, y, orientation);
            for(Turret t : turrets) {
                t.moveEntity(x, y, 0);
            }
        }
        
        public void draw(Graphics2D g) {
            super.draw(g);
        }
        
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        life += timePassed;
        if(currentCar == cars.length) {
            return; //all cars dead
        }
        
        if(cars[currentCar].isDead()) {
            movingCars = true;
            currentCar++;
            if(currentCar == cars.length) {
                return; //all cars dead
            }
        }
        
        if(movingCars) {
            double finalCoord = game.getGameWidth() * desiredFinalPos;
            TrainCar curr = cars[currentCar];
            double move = -moveSpeed * timePassed;
            for(TrainCar c : cars) {
                if(!c.isDead()) {
                    c.moveEntity(move, 0, 0);
                }
            }
            if (curr.x - curr.width/2.0 < finalCoord) {
                movingCars = false;
            }
        }
        else {
            double jostleVel = shakeMagnitude * Math.cos(life * Math.PI / (shakePeriod)) +
                    shakeMagnitude * 0.1 * Math.cos(life * 7 / shakePeriod);
            double move = -jostleVel * timePassed;
            for(TrainCar c : cars) {
                if(!c.isDead()) {
                    c.moveEntity(move, 0, 0);
                }
            }
        }
    }
    
    public boolean bossDefeated() {
        return cars[cars.length - 1].isDead();
    }
    
}
