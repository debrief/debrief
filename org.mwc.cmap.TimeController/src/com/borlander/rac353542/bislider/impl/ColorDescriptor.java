package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;


class ColorDescriptor implements Disposable {
    private final RGB myRGB;
    private Color myColor;

    public ColorDescriptor(RGB rgb){
        myRGB = rgb;
    }
    
    public ColorDescriptor(int red, int green, int blue){
        this(new RGB(red, green, blue));
    }
    
    public Color getColor(){
        if (myColor == null){
            myColor = ColorManager.getInstance().getColor(myRGB);
        }
        return myColor;
    }
    
    public void freeResources() {
        if (myColor != null){
            ColorManager.getInstance().releaseColor(myColor);
            myColor = null;
        }
    }
    
    public RGB getRGB() {
        return myRGB;
    }
    
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (obj instanceof ColorDescriptor){
            ColorDescriptor that = (ColorDescriptor)obj;
            return that.myRGB == this.myRGB;
        }
        return false;
    }
    
    public int hashCode() {
        return myRGB.hashCode();
    }
    
}
