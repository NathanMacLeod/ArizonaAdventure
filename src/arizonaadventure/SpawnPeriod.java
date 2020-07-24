/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

/**
 *
 * @author macle
 */
public class SpawnPeriod implements Updatable {
    
    private final int[] nToSpawn = {
        1,
        4,
        1,
        1
    };
    
    private double life;
    private double levelDuration;
    private double difficultyCurve;
    private int[] enemies;
    private double initialSpawnRate;
    private int spawnTally = 0;
    
    public SpawnPeriod(double duration, double difficultyCurve, int[] enemies) {
        this.enemies = enemies;
        this.difficultyCurve = difficultyCurve;
        this.levelDuration = duration;
        life = 0;
        
        double avgSpawnRate = getToSpawnCount() / levelDuration;
        initialSpawnRate = avgSpawnRate - avgSpawnRate * (difficultyCurve - 1)/ (difficultyCurve + 1);
    }
    
    private int getToSpawnCount() {
        int total = 0;
        for (int count: enemies) {
            total += count;
        }
        return total;
    }
    
    private void spawnRandomEnemy(ArizonaAdventure game) {
        int total = getToSpawnCount();
        int chosen = (int) (Math.random() * total);
        int type = 0;
        for (int i = 0; i < enemies.length; i++) {
            chosen -= enemies[i];
            if(chosen < 0) {
                type = i;
                break;
            }
        }
        
        double y = -1;
        
        enemies[type] -= 1;
        
        for(int i = 0; i < nToSpawn[type]; i++) {  
            KillableEntity enemy = null;
            switch(type) {
                case 0: //shooty enemy
                    enemy = new BasicEnemy(0, 0);
                    break;
                case 1:
                    enemy = new FodderEnemy(0, 0);
                    break;
                case 2:
                    enemy = new RocketEnemy(0, 0);
                    break;
                case 3:
                    enemy = new LaserEnemy(0, 0);
            }
            if(y == -1) {
                y = enemy.getRadius() + Math.random() * (game.getGameHeight() - 2 * enemy.getRadius());
            }
            enemy.moveEntity(game.getGameWidth() + enemy.getRadius() + (enemy.getRadius() * 2.5 * i), y, 0);
            game.addNewEnemy(enemy);
        }
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        life += timePassed;
        if (getToSpawnCount() > 0) {
            int totalSpawn = (int) (initialSpawnRate * (life + life * life * (difficultyCurve - 1) * 0.5 / levelDuration));
            for (int i = 0; i < totalSpawn - spawnTally; i++) {
                spawnRandomEnemy(game);
            }
            spawnTally = totalSpawn;
        }
    } 
    
    
}
