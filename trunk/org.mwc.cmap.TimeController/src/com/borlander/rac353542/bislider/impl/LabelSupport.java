package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import com.borlander.rac353542.bislider.*;

public class LabelSupport {
	public static final int LABEL_GAP_NORMAL = 5;
	private final BiSliderImpl myBiSlider;
	private Font myBoldFont;
	
    public LabelSupport(BiSliderImpl biSlider) {
		myBiSlider = biSlider;
	}

	public Font getLabelFont(GC gc) {
        if (myBoldFont == null) {
            myBoldFont = Util.deriveBold(gc.getFont());
        }
        return myBoldFont;
    }
	
	public int getPrefferedLabelInsets(GC gc){
		BiSliderLabelProvider labelProvider = getUIModel().getLabelProvider();
		String minLabel = labelProvider.getLabel(getDataModel().getTotalMinimum());
		String maxLabel = labelProvider.getLabel(getDataModel().getTotalMaximum());
		
		Point minLabelSize = getTextSize(gc, minLabel);
		Point maxLabelSize = getTextSize(gc, maxLabel);
		
		int size;
		if (getUIModel().isVertical()){
			size = Math.max(minLabelSize.x, maxLabelSize.x);
		} else {
			size = Math.max(minLabelSize.y, maxLabelSize.y);
		}
		return size + LABEL_GAP_NORMAL;
	}
	
    public Point getTextSize(GC gc, String label) {
    	Font oldFont = gc.getFont();
    	gc.setFont(getLabelFont(gc));
        Point result = gc.stringExtent(label);
        if (getUIModel().isVerticalLabels()){
            int temp = result.x;
            result.x = result.y;
            result.y = temp;
        }
        gc.setFont(oldFont);
        return result;
    }
    
    public void freeResources() {
        if (myBoldFont != null) {
            myBoldFont.dispose();
            myBoldFont = null;
        }
    }
    
    private BiSliderUIModel getUIModel(){
    	return myBiSlider.getUIModel();
    }
    
    private BiSliderDataModel getDataModel(){
    	return myBiSlider.getDataModel();
    }

}
