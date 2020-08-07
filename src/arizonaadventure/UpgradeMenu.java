/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author macle
 */
public class UpgradeMenu extends ImagePanel {
    
    public UpgradeMenu(ArizonaAdventure game) {  
        super();
        BufferedImage button = null;
        BufferedImage buttonHover = null;
        BufferedImage returnB = null;
        BufferedImage returnBHover = null;
        try {
            button = ImageIO.read(new File("./sprites/buyButton.png"));
            buttonHover = ImageIO.read(new File("./sprites/buyButtonHighlighted.png"));
            returnB = ImageIO.read(new File("./sprites/returnButton.png"));
            returnBHover = ImageIO.read(new File("./sprites/returnButtonHighlighted.png"));
            this.image = ImageIO.read(new File("./sprites/upgradeBG.png"));
        } catch(IOException e) {
            System.out.println("Unable to load level select sprites");
        }
        
        Button returnButton = new Button(returnB, returnBHover, 0, 0, 190, 70) {
            public void doAction(ArizonaAdventure game) {
                game.loadMenu(MenuState.Levels);
            }
        };
        
        UpgradeChoice fireRate = new UpgradeChoice(button, buttonHover, 100, 150, 70, 70, 1, "Fire rate", "Increase fire rate by 0.33/s") {
            public void doAction(ArizonaAdventure game) {
                if(canAfford(game)) {
                    game.getUpgrades().fireRate++;
                    game.subTokens(getCost());
                }
            }
        };
        UpgradeChoice missiles = new UpgradeChoice(button, buttonHover, 100, 250, 70, 70, 3, "Missiles", "Periodically fire guided missiles"){
            public void doAction(ArizonaAdventure game) {
                if(canAfford(game) && !game.getUpgrades().missiles) {
                    game.getUpgrades().missiles = true;
                    game.subTokens(getCost());
                }
            }
        };
        UpgradeChoice health = new UpgradeChoice(button, buttonHover, 100, 350, 70, 70, 1, "Health", "Increase health by 25%"){
            public void doAction(ArizonaAdventure game) {
                if(canAfford(game)) {
                    game.getUpgrades().health++;
                    game.subTokens(getCost());
                }
            }
        };
        UpgradeChoice miniship = new UpgradeChoice(button, buttonHover, 100, 450, 70, 70, 2, "Mini Ship", "You have a decreased hitbox, but 70% HP"){
            public void doAction(ArizonaAdventure game) {
                if(canAfford(game) && !game.getUpgrades().miniShip) {
                    game.getUpgrades().miniShip = true;
                    game.subTokens(getCost());
                }
            }
        };
        UpgradeChoice fireVolume = new UpgradeChoice(button, buttonHover, 500, 250, 70, 70, 5, "Fire Volume", "Fire an additional bullet"){
            public void doAction(ArizonaAdventure game) {
                if(canAfford(game)) {
                    game.getUpgrades().fireVolume++;
                    game.subTokens(getCost());
                }
            }
        };
        UpgradeChoice refund = new UpgradeChoice(button, buttonHover, 500, 150, 70, 70, 0, "Refund", "refund all upgrades"){
            public void doAction(ArizonaAdventure game) {
                game.refundUpgrades();
            }
        };
        
        buttons.add(returnButton);
        buttons.add(fireRate);
        buttons.add(missiles);
        buttons.add(health);
        buttons.add(refund);
        buttons.add(miniship);
        buttons.add(fireVolume);
    }
    
    private class UpgradeChoice extends Button {
        private String name;
        private String description;
        private int cost;
        
        public UpgradeChoice(BufferedImage image, BufferedImage hover, int x, int y, int width, int height, int cost, String name, String description) {
            super(image, hover, x, y, width, height);
            this.cost = cost;
            this.name = name;
            this.description = description;
        }

        public boolean canAfford(ArizonaAdventure game) {
            return game.getTokens() >= cost;
        }
        
        public int getCost() {
            return cost;
        }
        
        public void setCost(int newCost) {
            newCost = cost;
        }
        
        public void draw(Graphics2D g) {
            super.draw(g);
            g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.7f));
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            g.drawString(name, x + width, y + height/3);
            g.drawString(description, x + width, y + (height * 2)/3);
            g.drawString("Cost: " + cost, x + width, y + height);
        }
    }
    
}
