package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import com.borlander.rac353542.bislider.BiSliderDataModel;

/**
 * Selects a single segment on double click
 */
class SegmentSelector extends MouseAdapter {

    private final BiSliderImpl myBiSlider;
    private final Segmenter mySegmenter;

    public SegmentSelector(BiSliderImpl biSlider, Segmenter segmenter) {
        myBiSlider = biSlider;
        mySegmenter = segmenter;
    }

    public void mouseDoubleClick(MouseEvent e) {
        if (myBiSlider.isInsidePointer(e.x, e.y)){
            return;
        }
        CoordinateMapper mapper = myBiSlider.getCoordinateMapper();
        Rectangle drawArea = mapper.getDrawArea();
        if (drawArea.contains(e.x, e.y)) {
            double value = mapper.pixel2value(e.x, e.y);
            ColoredSegment segment = mySegmenter.getSegment(value);
            if (segment != null) {
                BiSliderDataModel.Writable dataModel = myBiSlider.getWritableDataModel();
                dataModel.setUserRange(segment.getMinValue(), segment.getMaxValue());
            }
        }
    }
}
