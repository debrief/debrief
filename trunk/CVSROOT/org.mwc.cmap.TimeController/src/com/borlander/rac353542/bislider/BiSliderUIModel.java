package com.borlander.rac353542.bislider;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;


public interface BiSliderUIModel {
    public boolean isVertical();
    public int getArcRadius();
    public Rectangle getDrawArea(Rectangle fullBounds);
    
    public boolean hasLabelsAboveOrLeft();
    public boolean hasLabelsBelowOrRight();
    
    public int getLabelInsets();
    public int getNonLabelInsets();
    
    public RGB getMinimumRGB();
    public RGB getMaximumRGB();
    
    public RGB getBiSliderForegroundRGB();
}
