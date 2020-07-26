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
    
    private static Sprite basicBullet;
    private static Sprite basicBody;
    private static Sprite basicTurret;
    private static Sprite lobBullet;
    private static Sprite lobBody;
    private static Sprite lobTurret;
    private static Sprite missileBody;
    private static Sprite missileTurret;
    private static Sprite burstBullet;
    private static Sprite burstBody;
    private static Sprite burstGun;
    private static Sprite pepsiCar;
    private static Sprite mtwndewCar;
    private static Sprite liptonCar;
    private static Sprite sierraCar;
    private static Sprite pepsicoCar;
    
    public static void loadSprites() {
        pepsiCar = new Sprite("pepsicar.png", 500);
        mtwndewCar = new Sprite("mtwndewcar.png", 500);
        liptonCar = new Sprite("liptoncar.png", 500);
        sierraCar = new Sprite("sierracar.png", 500);
        pepsicoCar = new Sprite("pepsicocar.png", 500);
        burstBullet = new Sprite("5hourenergy.png", 32);
        burstBody = new Sprite("5hourdome.png", (int) (55 * 1.5));
        burstGun = new Sprite("gattlegun.png", (int) 60 * 2);
        basicBody = new Sprite("phalanxBase.png", (int)(50));
        basicTurret = new Sprite("phalanxTurret.png", (int)(50 * 1.6));
        lobBody = new Sprite("teapotbase.png", (int)(50));
        lobTurret = new Sprite("teapot.png", (int)(50 * 1.6));
        lobBullet = new Sprite("teadrop.png", 25);
        basicBullet = new Sprite("monster.png", 32);
        missileBody = new Sprite("pepsiBase.png", (int) (60 * 1.25));
        missileTurret = new Sprite("pepsirocketinert.png", (int) (60 * 2));
    }
    
    public TrainBoss(ArizonaAdventure game) {
        life = 0;
        currentCar = 0;
        movingCars = true;
        cars = new TrainCar[] {
            new TrainCar(0, 0, 450, 150, new Sprite(pepsiCar)), 
            new TrainCar(0, 0, 450, 150, new Sprite(mtwndewCar)), 
            new TrainCar(0, 0, 450, 150, new Sprite(liptonCar)), 
            new TrainCar(0, 0, 450, 150, new Sprite(sierraCar)),
            new TrainCar(0, 0, 450, 150, new Sprite(pepsicoCar))
        };
        
        double init = 100;
        double gap = 30;
        double x = init + game.getGameWidth() + gap;
        double y = game.getGameHeight() * 0.92;
        
        for(int i = 0; i < cars.length; i++) {
            cars[i].moveEntity(x + cars[i].width/2.0, y - cars[i].height/2.0, 0);
            x += cars[i].width + gap;
            switch(i) {
                case 0:
                    cars[i].spawnTurrets(game, new int[] {3});
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
                 case 4:
                    cars[i].spawnTurrets(game, new int[] {1, 2, 1, 2, 1});
                    break;
            }
            game.addNewEnemy(cars[i]);
        }
    }
    
    private abstract class Turret extends KillableEntity {
        private double width;
        private double height;
        protected double playerDir;
        
        protected Sprite bodySprite;
        protected Sprite gunSprite;
        
        public Turret(double x, double y, double width, double height, double hp) {
            super(x, y, generateSquareHitbox(width, height), hp, 70);
            this.width = width;
            this.height = height;
            playerDir = 0;
            bodySprite = null;
            gunSprite = null;
        }
        
        public abstract void shoot(double timePassed, ArizonaAdventure game);
        
        public void draw(Graphics2D g) {
            gunSprite.draw(g, x, y, playerDir);
            bodySprite.draw(g, x, y, 0);
        }
        
        public void update(double timePassed, ArizonaAdventure game) {
            Player p = game.getPlayer();
            playerDir = new Vector2D(p.x - x, p.y - y).getAngle();
            shoot(timePassed, game);
        }
    }
    
    private class MissileTurret extends Turret {
        private CooldownTimer fire;
        
        
        public MissileTurret() {
            super(0, 0, 40, 50, 200);
            fire = new CooldownTimer(0.43);
            fire.randomize();
            bodySprite = missileBody;
            gunSprite = missileTurret;
        }
    
        public void shoot(double timePassed, ArizonaAdventure game) {
            fire.updateTimer(timePassed);
            if(fire.tryToFire()) {
                Player player = game.getPlayer();
                Vector2D dir = new Vector2D(player.x - x, player.y - y);
                game.addNewProjectile(new EnemyRocket(x, y, dir.getAngle(), 25));
            }
        }
    }
    
    private class BasicTurret extends Turret {
        private CooldownTimer fire;
        
        public BasicTurret() {
            super(0, 0, 30, 50, 200);
            fire = new CooldownTimer(0.95);
            fire.randomize();
            gunSprite = basicTurret;
            bodySprite = basicBody;
            
        }
        
        public void shoot(double timePassed, ArizonaAdventure game) {
            fire.updateTimer(timePassed);
            if(fire.tryToFire()) {
                Player player = game.getPlayer();
                Vector2D velocity = new Vector2D(player.x - x, player.y - y).getUnitVector().scale(250);
                game.addNewProjectile(new BasicEnemyBullet(x, y, velocity, basicBullet));
            }
        }
    }
    
    private class LobTurret extends Turret {
        private CooldownTimer fire;
        private double radius = 22;
        
        public LobTurret() {
            super(0, 0, 30, 50, 200);
            fire = new CooldownTimer(0.95);
            fire.randomize();
            gunSprite = lobTurret;
            bodySprite = lobBody;
        }
        
        private class TeaProjectile extends LobProjectile {
            
            public TeaProjectile(double x, double y, double vx, double vy) {
                super(x, y, new Vector2D(vx, vy), 15, 15, 25, 25, false);
            }
            
            public void draw(Graphics2D g) {
                lobBullet.draw(g, x, y, orientation);
            }
        }
        
        public void shoot(double timePassed, ArizonaAdventure game) {
            fire.updateTimer(timePassed);
            if(fire.tryToFire()) {
                Player p = game.getPlayer();
                double air = 120;
                double yH = p.y - air - y;
                if(yH > 0) {
                    yH = 0;
                }
                double xH = p.x - x;
                double g = LobProjectile.getGravity();
                double vY = -Math.sqrt(-2 * g * yH);
                double t = Math.sqrt(2 * air / g) - vY / g;
                double vX = xH/t;
                game.addNewProjectile(new TeaProjectile(-Math.cos(orientation + Math.PI/6) * radius + x, -Math.sin(orientation + Math.PI/6) * radius + y, vX, vY));
            }
        }
    }
    
    private class BurstTurret extends Turret {
        private CooldownTimer burst;
        private CooldownTimer fire;
        private final int burstSize = 25;
        private int burstCount = 0;
        private double radius = 75;
        
        public BurstTurret() {
            super(0, 0, 60, 40, 350);
            burst = new CooldownTimer(0.18);
            fire = new CooldownTimer(10);
            bodySprite = burstBody;
            gunSprite = burstGun;
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
                    game.addNewProjectile(new BasicEnemyBullet(x + radius * Math.cos(playerDir),
                            y + radius * Math.sin(playerDir), velocity, burstBullet));
                }
            }
        }
    }
    
    private class TrainCar extends KillableEntity {
        private ArrayList<Turret> turrets;
        private double width, height;
        protected Sprite carSprite;
        private int nExplosions;
        private CooldownTimer explosionTimer;
        
        public TrainCar(double x, double y, double width, double height, Sprite sprite) {
            super(x, y, generateSquareHitbox(width, height), 700, 250);
            this.width = width;
            this.height = height;
            turrets = new ArrayList();
            carSprite = sprite;
            nExplosions = 20;
            explosionTimer = new CooldownTimer(10);
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
                    case 3:
                        t = new LobTurret();
                        break;
                }
                double xPos = x - width/2.0 + (i + 1) * width/(nTurrets + 1);
                double yPos = y - height/2.0 - t.height/2.0;
                t.moveEntity(xPos, yPos, 0);
                game.addNewEnemy(t);
                turrets.add(t);
            }
        }
        
        private boolean exploding() {
            return super.isDead();
        }
        
        public boolean isDead() {
            return exploding() && nExplosions <= 0;
        }
        
        public void takeDamage(double damage) {
            super.takeDamage(damage);
            if(super.isDead()) {
                for(Turret t : turrets) {
                    t.kill();
                }
            }
        }
        
        public void update(double timePassed, ArizonaAdventure game) {
            if(exploding() && nExplosions > 0) {
                explosionTimer.updateTimer(timePassed);
                if(explosionTimer.tryToFire()) {
                    double xC = x - width/2.0 + Math.random() * width;
                    double yC = y - height/2.0 + Math.random() * height;
                    game.addExplosion(new ExplosionEffect(xC, yC, (int) (100 + Math.random() * 60), 0.35));
                    nExplosions--;
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
            //super.draw(g);
            carSprite.draw(g, x, y, orientation);
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
        else if(!cars[currentCar].exploding()) {
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
