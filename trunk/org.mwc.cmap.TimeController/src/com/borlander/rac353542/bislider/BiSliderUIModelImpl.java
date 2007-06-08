package com.borlander.rac353542.bislider;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;


public class BiSliderUIModelImpl implements BiSliderUIModel {
    private static final int BORDER = 10;
    private RGB myMaxRGB = new RGB(0, 255, 0);
    private RGB myMinRGB = new RGB(0, 0, 255);
    private RGB myBiSliderForeground = new RGB(134, 56, 234);
    
    //[MG]
    public int getArcRadius() {
        return 0;
    }
    
    public boolean isVertical(){
        return false;
    }
    
    public int getLabelInsets() {
        return 40;
    }
    
    public int getNonLabelInsets(){
        return 40;
    }
    
    public Rectangle getDrawArea(Rectangle fullBounds) {
        int width = fullBounds.width;
        int height = fullBounds.height;
        
        int labelInsets = getLabelInsets();
        
        //alloacte space for labels but only if there is enough space
        if (isVertical()){
            if (hasLabelsAboveOrLeft() && width > labelInsets){
                width -= labelInsets;
            }
            if (hasLabelsBelowOrRight() && width > labelInsets){
                width -= labelInsets;
            }
            if (height > 2 * getNonLabelInsets()){
                height -= 2 * getNonLabelInsets();
            }
            
        } else {
            if (hasLabelsAboveOrLeft() && height > labelInsets){
                height -= labelInsets;
            }
            if (hasLabelsBelowOrRight() && height > labelInsets){
                height -= labelInsets;
            }
            if (width > 2 * getNonLabelInsets()){
                width -= 2 * getNonLabelInsets();
            }
        }
        
//        //[MG]SEVERE: only horizontal style supproted now
//        int normalSize = fullBounds.height;
//        if (isVertical()){
//            if (hasLabelsAboveOrLeft()){
//                normalSize
//            }
//            
//        }
//        if (normal < NORMAL_SIZE){
//            return Util.cloneRectangle(fullBounds);
//        }
//        
//        int safeInsetsX = Math.min(getInsetsWidth(), (fullBounds.width - MINIMUM_TANGENTIAL_SIZE) / 2);
//        safeInsetsX = Math.max(safeInsetsX, 0);
//        
//        int safeInsetsY = Math.min(getInsetsHeight(), (fullBounds.height - MINIMUM_NORMAL_SIZE) / 2);
//        safeInsetsY = Math.max(safeInsetsY, 0);
//        
//        int totalHeight = safeInsetsY + getDisplayAreaHeight();
//        
        int centerY = fullBounds.y + fullBounds.height / 2;
        int centerX = fullBounds.x + fullBounds.width / 2;
        return new Rectangle(centerX - width / 2, centerY - height / 2, width, height); 
    }
    
    public boolean hasLabelsAboveOrLeft() {
        return true;
    }
    
    public boolean hasLabelsBelowOrRight() {
        return false;
    }
    
    public RGB getBiSliderForegroundRGB() {
        return myBiSliderForeground;
    }

    private Axis getAxis(){
        return Axis.getAxis(isVertical());
    }
    
    public RGB getMaximumRGB() {
        return myMaxRGB;
    }
    
    public RGB getMinimumRGB() {
        return myMinRGB;
    }

}
