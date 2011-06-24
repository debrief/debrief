package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import com.borlander.rac353542.bislider.BiSliderContentsDataProvider;
import com.borlander.rac353542.bislider.BiSliderDataModel;
import com.borlander.rac353542.bislider.BiSliderUIModel;

class BiSliderContents extends BiSliderComponentBase {
    private BiSliderContentsDataProvider myContentsDataProvider;
    private ColorDescriptor mySegmentForeground;
    private ColorDescriptor myNotColoredSegmentsBackground;
    private Segmenter mySegmenter;
    private BiSliderUIModel.Listener myConfigListener;

    public BiSliderContents(BiSliderImpl biSlider, Segmenter segmenter) {
        super(biSlider);
        mySegmenter = segmenter;
        reloadConfig();
        myConfigListener = new BiSliderUIModel.Listener(){
            public void uiModelChanged(BiSliderUIModel uiModel) {
                reloadConfig();
            }
        };
        getUIModel().addListener(myConfigListener);
    }

    public void freeResources() {
        if (myConfigListener != null){
            getUIModel().removeListener(myConfigListener);
            myConfigListener = null;
        }
        if (mySegmentForeground != null) {
            mySegmentForeground.freeResources();
            mySegmentForeground = null;
        }
        if (myNotColoredSegmentsBackground != null) {
            myNotColoredSegmentsBackground.freeResources();            
            myNotColoredSegmentsBackground = null;
        }
    }

    public void paintContents(GC gc) {
        CoordinateMapper mapper = getMapper();
        BiSliderDataModel dataModel = getDataModel();
        double userMin = dataModel.getUserMinimum();
        double userMax = dataModel.getUserMaximum();
        for (ColoredSegmentEnumeration segments = getSegments(); segments.hasMoreElements();) {
            ColoredSegment nextSegment = segments.next();
            double segmentMin = nextSegment.getMinValue();
            double segmentMax = nextSegment.getMaxValue();
            double normalValue = getValueAt(nextSegment);
            if (userMin == userMax || segmentMax < userMin || segmentMin > userMax) {
                // completely outside user range
                Rectangle segmentInnerArea = mapper.segment2rectangle(segmentMin, segmentMax, 1.0, true);
                gc.setBackground(myNotColoredSegmentsBackground.getColor());
                gc.fillRectangle(segmentInnerArea);
            } else if (segmentMin >= userMin && segmentMax <= userMin) {
                // completely inside user range
                Rectangle segmentInnerArea = mapper.segment2rectangle(segmentMin, segmentMax, normalValue, true);
                gc.setBackground(nextSegment.getColorDescriptor().getColor());
                gc.fillRectangle(segmentInnerArea);
            } else {
                // at least one (may be both!!!) borders should be drawn in this
                // segment
                if (segmentMin < userMin) {
                    Rectangle leftArea = mapper.segment2rectangle(segmentMin, userMin, 1.0, true);
                    gc.setBackground(myNotColoredSegmentsBackground.getColor());
                    gc.fillRectangle(leftArea);
                }
                if (segmentMax > userMax) {
                    Rectangle rightArea = mapper.segment2rectangle(userMax, segmentMax, 1.0, true);
                    gc.setBackground(myNotColoredSegmentsBackground.getColor());
                    gc.fillRectangle(rightArea);
                }
                double leftMostColored = Math.max(segmentMin, userMin);
                double rightMostColored = Math.min(segmentMax, userMax);
                Rectangle coloredArea = mapper.segment2rectangle(leftMostColored, rightMostColored, normalValue, true);
                gc.setBackground(nextSegment.getColorDescriptor().getColor());
                gc.fillRectangle(coloredArea);
            }
        }
        // outline should be drawn after color fill
        for (ColoredSegmentEnumeration segments = getSegments(); segments.hasMoreElements();) {
            ColoredSegment nextSegment = segments.next();
            double segmentMin = nextSegment.getMinValue();
            double segmentMax = nextSegment.getMaxValue();
            double normalValue = getValueAt(nextSegment);
            Rectangle segmentOutline = mapper.segment2rectangle(segmentMin, segmentMax, normalValue, false);
            gc.setForeground(mySegmentForeground.getColor());
            gc.drawRectangle(segmentOutline);
        }
    }

    private ColoredSegmentEnumeration getSegments() {
        // BiSliderDataModel dataModel = getDataModel();
        // return mySegmenter.segments(dataModel.getUserMinimum(),
        // dataModel.getUserMaximum());
        return mySegmenter.allSegments();
    }

    private double getValueAt(ColoredSegment segment) {
        BiSliderDataModel dataModel = getDataModel();
        return myContentsDataProvider.getNormalValueAt( //
                dataModel.getTotalMinimum(), dataModel.getTotalMaximum(), //  
                segment.getMinValue(), segment.getMaxValue());
    }

    
    void reloadConfig(){
        BiSliderUIModel uiModel = getUIModel();
        myContentsDataProvider = uiModel.getContentsDataProvider();
        mySegmentForeground = updateColorDescriptor( //
                mySegmentForeground, uiModel.getBiSliderForegroundRGB());
        myNotColoredSegmentsBackground = updateColorDescriptor( //
                myNotColoredSegmentsBackground, uiModel.getNotColoredSegmentRGB());
    }

}
