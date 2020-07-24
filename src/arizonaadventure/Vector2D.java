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
public class Vector2D {
    public double x;
    public double y;
    
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector2D add(Vector2D b) {
        return new Vector2D(x + b.x, y + b.y);
    }
    
    public Vector2D sub(Vector2D b) {
        return new Vector2D(x - b.x, y - b.y);
    }
    
    public Vector2D scale(double scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }
    
    public Vector2D getNorm() {
        return new Vector2D(y, -x);
    }
    
    public Vector2D getUnitVector() {
        double mag = Math.sqrt(getMagnitudeSquared());
        return new Vector2D(x / mag, y / mag);
    }
    
    public double getMagnitudeSquared() {
        return x * x + y * y;
    }
    
    public double dot(Vector2D b) {
        return x * b.x + y * b.y;
    }
    
    public boolean clockwiseOrientation(Vector2D b) {
        return x * b.y - y * b.x < 0;
    }
    
    public double getAngle() {
        double mag = Math.sqrt(getMagnitudeSquared());
        double ang = Math.asin(y / mag);
        if(x < 0) {
            ang = Math.PI - ang;
        }
        return ang;
    }
}
