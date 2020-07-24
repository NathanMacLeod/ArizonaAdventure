/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author macle
 */
public class MoveingEntity implements Drawable, Updatable {
    protected double x, y;
    private ArrayList<Vector2D> hitbox;
    private ArrayList<Vector2D> hitboxWorldRel;
    protected double orientation;
    private double sizeRadius;
    
    
    public MoveingEntity(double x, double y, ArrayList<Vector2D> hitbox, double orientation) {
        this.x = x;
        this.y = y;
        this.hitbox = hitbox;
        this.orientation = orientation;
        generateWorldHitbox();
        sizeRadius = determineRadius();
    }
    
    protected static ArrayList<Vector2D> generateSquareHitbox(double width, double height) {
        
        double wr = width/2.0;
        double hr = height/2.0;
        
        ArrayList<Vector2D> points = new ArrayList();
        points.add(new Vector2D(-wr, -hr));
        points.add(new Vector2D(wr, -hr));
        points.add(new Vector2D(wr, hr));
        points.add(new Vector2D(-wr, hr));
        return points;
    }
    
    private void generateWorldHitbox() {
        hitboxWorldRel = new ArrayList();
        double cos = Math.cos(orientation);
        double sin = Math.sin(orientation);
        for(Vector2D v : hitbox) {
            double worldX = v.x * cos - v.y * sin + x;
            double worldY = v.x * sin + v.y * cos + y;
            hitboxWorldRel.add(new Vector2D(worldX, worldY));
        }
    }
    
    private double determineRadius() {
        double max = 0;
        for(Vector2D p : hitbox) {
            double r = Math.sqrt(p.x * p.x + p.y * p.y);
            if(r > max) {
                max = r;
            }
        }
        return max;
    }
    
    public double getRadius() {
        return sizeRadius;
    }
    
    public boolean entityOutOfBounds(ArizonaAdventure game) {
        return y + sizeRadius < 0 || y - sizeRadius > game.getGameHeight() 
                || x + sizeRadius < 0 || x - sizeRadius > game.getGameWidth();
    }
    
    public boolean entityOffLeft() {
        return x + sizeRadius < 0;
    }
    
    public void moveEntity(double dx, double dy, double rotation) {
        x += dx;
        y += dy;
        
        if(rotation != 0) {
            orientation += rotation;
            generateWorldHitbox();
        }
        else {
            for(Vector2D v: hitboxWorldRel) {
                v.x += dx;
                v.y += dy;
            }
        }
    }
    
    private boolean isAxisSeparating(Vector2D axis, MoveingEntity entity) {
        boolean first = true;
        double min = 0, max = 0;
        
        for(Vector2D p: hitboxWorldRel) {
            double val = p.dot(axis);
            if(first) {
                first = false;
                min = val;
                max = val;
            }
            else if(val < min)
                min = val;
            else if(val > max)
                max = val;
        }
        
        first = true;
        double eMin = 0, eMax = 0;
        for(Vector2D p: entity.hitboxWorldRel) {
            double val = p.dot(axis);
            if(first) {
                first = false;
                eMin = val;
                eMax = val;
            }
            else if(val < eMin)
                eMin = val;
            else if(val > eMax)
                eMax = val;
        }
        
        return eMin > max || eMax < min;
    }
    
    private boolean checkOwnAxises(MoveingEntity entity) {
        for(int i = 0; i < hitboxWorldRel.size(); i++) {
            int j = (i == hitboxWorldRel.size() - 1)? 0 : i + 1;
            Vector2D norm = hitboxWorldRel.get(j).sub(hitboxWorldRel.get(i));
            if (isAxisSeparating(norm, entity)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hitboxesIntersecting(MoveingEntity entity) {
        //Separating Axis theorem
        return !checkOwnAxises(entity) && !entity.checkOwnAxises(this);
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public void draw(Graphics2D g) {
        int nPoints = hitboxWorldRel.size();
        int[] xPoints = new int[nPoints];
        int[] yPoints = new int[nPoints];
        
        for(int i = 0; i < hitboxWorldRel.size(); i++) {
            Vector2D p = hitboxWorldRel.get(i);
            xPoints[i] = (int) p.x;
            yPoints[i] = (int) p.y;
        }
        
        g.setColor(Color.green);
        g.fillPolygon(xPoints, yPoints, nPoints);
        
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        
    }
}
