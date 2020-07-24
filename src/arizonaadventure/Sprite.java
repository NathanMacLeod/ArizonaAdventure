/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author macle
 */
public class Sprite {
    protected BufferedImage sprite;
    
    public Sprite(String name, int size) {
        sprite = fetchSprite(name, size);
    }
    
    public Sprite(Sprite copyFrom) {
        sprite = new BufferedImage(copyFrom.sprite.getWidth() , copyFrom.sprite.getHeight(), BufferedImage.TYPE_INT_ARGB);
        sprite.getGraphics().drawImage(copyFrom.sprite, 0, 0, null);
    }
    
    protected BufferedImage fetchSprite(String name, int size) {
        BufferedImage sprite = null;
        try {
            BufferedImage spriteImage = ImageIO.read(new File("./sprites/" + name));
            sprite = new BufferedImage((int)(size), (int)(size), BufferedImage.TYPE_INT_ARGB);
            Graphics imageGraphics = sprite.getGraphics();
            imageGraphics.drawImage(spriteImage, 0, 0, (int)(size), (int)(size), 0, 0, spriteImage.getWidth(), spriteImage.getHeight(), null);
        }
        catch(IOException e) {
            System.out.println(e);
        }
        return sprite;
    }
    
    public void draw(Graphics2D g, double x, double y, double orientation) {
        AffineTransform transformation = new AffineTransform();
        transformation.translate(x, y);
        transformation.rotate(orientation);
        transformation.translate(-sprite.getWidth()/2.0, - sprite.getHeight()/2.0);
        g.drawImage(sprite, transformation, null);
    }
}
