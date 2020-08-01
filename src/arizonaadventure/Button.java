/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.Graphics2D; 
/**
 *
 * @author macle
 */
public class Button implements Drawable {
    
    private BufferedImage image;
    private BufferedImage hoverImage;
    private boolean hovered;
    protected int x, y, width, height;
    
    public Button(BufferedImage image, BufferedImage hoverImage, int x, int y, int width, int height) {
        this.image = image;
        this.hoverImage = hoverImage;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void doAction(ArizonaAdventure game) {}
    
    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }
    
    public void setHovered(boolean bool) {
        hovered = bool;
    }
    
    public void draw(Graphics2D g) {
        BufferedImage bImage = (hovered)? hoverImage : image;
        g.drawImage(bImage, x, y, width, height, null);
    }
}
