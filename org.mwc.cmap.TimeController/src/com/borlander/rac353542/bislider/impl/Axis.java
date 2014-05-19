package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

abstract class Axis {
    public abstract int get(Point point);
    public abstract int getNormal(Point point);
    public abstract void set(Point point, int value);
    public abstract void setNormal(Point point, int value);
    
    public abstract double getAsDouble(Point point);
    public abstract int getMin(Rectangle rectangle);
    public abstract int getMax(Rectangle rectangle);
    public abstract int getNormalMin(Rectangle rectangle);
    public abstract int getNormalMax(Rectangle rectangle);
    public abstract int getNormalDelta(Rectangle rectangle);
    
    public abstract int getCenter(Rectangle rectangle);
    public abstract int getNormalCenter(Rectangle rectangle);
    
    public abstract void advance(Point p, int delta);
    public abstract void advanceNormal(Point p, int delta);
    
    public abstract Rectangle createRectangle(int x, int y, int size, int normalSize);
    
    public final Rectangle createRectangle(Point point, int size, int normalSize){
        return createRectangle(point.x, point.y, size, normalSize);
    }
    
    //[MG]public abstract Point set(Point point, int value);

    public final int getDelta(Rectangle rectangle){
        return getMax(rectangle) - getMin(rectangle);
    }

    public static Axis getAxis(boolean verticalNotHorizontal){
        return verticalNotHorizontal ? AXIS_Y : AXIS_X;
    }
    
    private static final Axis AXIS_X = new Axis() {
        public double getAsDouble(Point point) {
            return get(point);
        }

        public int get(Point point) {
            return point.x;
        }
        
        public void set(Point point, int value) {
            point.x = value;
        }
        
        public void setNormal(Point point, int value) {
            point.y = value;
        }
        
        public int getNormal(Point point) {
            return point.y;
        }

        public int getMax(Rectangle rectangle) {
            return rectangle.x + rectangle.width;
        }

        public int getMin(Rectangle rectangle) {
            return rectangle.x;
        }
        
        public int getNormalMax(Rectangle rectangle) {
            return rectangle.y + rectangle.height;
        }
        
        public int getNormalMin(Rectangle rectangle) {
            return rectangle.y;
        }
        
        public int getNormalDelta(Rectangle rectangle){ 
            return rectangle.height;
        }
        
        public int getNormalCenter(Rectangle rectangle) {
            return rectangle.y + rectangle.height / 2;
        }
        
        public int getCenter(Rectangle rectangle) {
            return rectangle.x + rectangle.width / 2;
        }
        
        public Rectangle createRectangle(int x, int y, int size, int normalSize) {
            return new Rectangle(x, y, size, normalSize);
        }
        
        public void advance(Point p, int delta) {
            p.x += delta;
        }
        
        public void advanceNormal(Point p, int delta) {
            p.y += delta;
        }
    };
    
    private static final Axis AXIS_Y = new Axis() {
        public double getAsDouble(Point point) {
            return get(point);
        }

        public int get(Point point) {
            return point.y;
        }
        
        public int getNormal(Point point) {
            return point.x;
        }

        public void set(Point point, int value) {
            point.y = value;
        }
        
        public void setNormal(Point point, int value) {
            point.x = value;
        }
        
        public int getMax(Rectangle rectangle) {
            return rectangle.y + rectangle.height;
        }

        public int getMin(Rectangle rectangle) {
            return rectangle.y;
        }

        public int getNormalMax(Rectangle rectangle) {
            return rectangle.x + rectangle.width;
        }
        
        public int getNormalMin(Rectangle rectangle) {
            return rectangle.x;
        }

        public int getNormalDelta(Rectangle rectangle){ 
            return rectangle.width;
        }

        public int getNormalCenter(Rectangle rectangle) {
            return rectangle.x + rectangle.width / 2;
        }
        
        public int getCenter(Rectangle rectangle) {
            return rectangle.y + rectangle.height / 2;
        }

        public Rectangle createRectangle(int x, int y, int size, int normalSize) {
            return new Rectangle(x, y, normalSize, size);
        }
        
        public void advance(Point p, int delta) {
            p.y += delta;
        }
        
        public void advanceNormal(Point p, int delta) {
            p.x += delta;
        }
    };
    
}