package com.borlander.rac353542.bislider.impl;

import java.util.HashMap;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;


class ColorManager {
    private final HashMap myColors;
    private static ColorManager ourInstance;
    
    private ColorManager(){
        myColors = new HashMap();
    }
    
    public static ColorManager getInstance(){
        if (ourInstance == null){
            ourInstance = new ColorManager();
        }
        return ourInstance;
    }
    
    public Color getColor(RGB rgb){
        ColorWithCounter holder = (ColorWithCounter)myColors.get(rgb);
        if (holder == null){
            holder = new ColorWithCounter(rgb);
            myColors.put(rgb, holder);
        }
        return holder.allocateColor();
    }
    
    public Color getColor(int red, int green, int blue){
        return getColor(getKey(red, green, blue));
    }
    
    public void releaseColor(Color color){
        if (color.isDisposed()){
            //we can not ask disposed color for components.
            //but most probably application is about to close and we may skip disposing
            return;
        }
        Object key = getKey(color.getRed(), color.getGreen(), color.getBlue());
        ColorWithCounter holder = (ColorWithCounter)myColors.get(key);
        //intentionally do not check null
        holder.disposeColor();
    }
    
    private RGB getKey(int red, int green, int blue){
        return new RGB(red, green, blue);
    }
    
    private static class ColorWithCounter {
        private Color myColor;
        private int myCounter;
        private final int myRed;
        private final int myGreen;
        private final int myBlue;
        
        public ColorWithCounter(RGB rgb){
            this(rgb.red, rgb.green, rgb.blue);
        }
        
        public ColorWithCounter(int red, int green, int blue){
            myRed = red;
            myGreen = green;
            myBlue = blue;
            myCounter = 0;
        }
        
        public Color allocateColor(){
            if (myColor != null && myColor.isDisposed()){
                myColor = null;
                myCounter = 0;
            }
            myCounter++;
            if (myColor == null){
                myColor = new Color(Display.getCurrent(), myRed, myGreen, myBlue);
            }
            return myColor;
        }
        
        public void disposeColor(){
            myCounter--;
            if (myCounter <= 0){
                if (myColor != null && !myColor.isDisposed()){
                    myColor.dispose();
                }
                myColor = null;
                myCounter = 0;
            }
        }
        
        public boolean equals(Object obj) {
            if (obj == this){
                return true;
            }
            if (obj instanceof ColorWithCounter){
                ColorWithCounter that = (ColorWithCounter)obj;
                return this.myRed == that.myRed &&
                        this.myGreen == that.myGreen &&
                        this.myBlue == that.myBlue;
            }
            return false;
        }
        
        public int hashCode() {
            return myRed << 16 + myGreen << 8 + myBlue;
        }
        
        public String toString() {
            return "SafeColor: [" + myRed + ", " + myGreen + ", " + myBlue + "]";
        }
        
    }
    
}
