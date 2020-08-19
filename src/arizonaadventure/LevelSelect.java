/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 *
 * @author macle
 */
public class LevelSelect extends ImagePanel {
    
    public LevelSelect(int maxLevel) {
        super();
        BufferedImage button = null;
        BufferedImage buttonHover = null;
        BufferedImage upgrade = null;
        BufferedImage upgradeHover = null;
        try {
            button = ImageIO.read(new File("./sprites/levelButton.png"));
            buttonHover = ImageIO.read(new File("./sprites/levelButtonSelected.png"));
            upgrade = ImageIO.read(new File("./sprites/upgradeMenu.png"));
            upgradeHover = ImageIO.read(new File("./sprites/upgradeMenuHighlighted.png"));
            this.image = ImageIO.read(new File("./sprites/mapwvolcano.png"));
        } catch(IOException e) {
            System.out.println("Unable to load level select sprites");
        }
        
        Button level1 = new Button(button, buttonHover, 298, 280, 60, 36) {
            
            public void doAction(ArizonaAdventure game) {
                game.loadLevel(1);
            }
            
        };
        Button level2 = new Button(button, buttonHover, 530, 335, 60, 36) {
            
            public void doAction(ArizonaAdventure game) {
                game.loadLevel(2);
            }

        };
        
        Button level3 = new Button(button, buttonHover, 456, 86, 60, 36) {
            
            public void doAction(ArizonaAdventure game) {
                game.loadLevel(3);
            }

        };
        
        Button upgradeButton = new Button(upgrade, upgradeHover, 0, 530, 190, 70) {
            public void doAction(ArizonaAdventure game) {
                game.loadMenu(MenuState.Upgrades);
            }
        };
        
        buttons.add(upgradeButton);
        if(maxLevel >= 1) {
            buttons.add(level1);
        }
        if(maxLevel >= 2) {
            buttons.add(level2);
        }
        if(maxLevel >= 3) {
            buttons.add(level3);
        }
    }
    
}
