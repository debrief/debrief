package com.borlander.rac353542.bislider;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class BiSliderContents extends BiSliderComponentBase implements Disposable {

    private final BiSliderContentsDataProvider myContentsDataProvider;
    private Color mySegmentForeground;
    private Color myNotColoredSegmentsBackground;
    private Segmenter mySegmenter;

    public BiSliderContents(BiSlider biSlider) {
        this(biSlider, new ColorInterpolation.INTERPOLATE_HSB(), BiSliderContentsDataProvider.NORMAL_DISTRIBUTION);
    }

    public BiSliderContents(BiSlider biSlider, ColorInterpolation colorInterpolation, BiSliderContentsDataProvider contentsDataProvider) {
        super(biSlider);
        myContentsDataProvider = contentsDataProvider;
        mySegmenter = new Segmenter(getDataModel(), getUIModel(), colorInterpolation);
        mySegmentForeground = ColorManager.getInstance().getColor(0, 0, 0);
        myNotColoredSegmentsBackground = ColorManager.getInstance().getColor(255, 255, 255);
    }

    public void freeResources() {
        if (mySegmenter != null) {
            mySegmenter.freeResources();
            mySegmenter = null;
        }
        if (mySegmentForeground != null) {
            ColorManager.getInstance().releaseColor(mySegmentForeground);
            mySegmentForeground = null;
        }
        if (myNotColoredSegmentsBackground != null) {
            ColorManager.getInstance().releaseColor(myNotColoredSegmentsBackground);
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
                gc.setBackground(myNotColoredSegmentsBackground);
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
                    gc.setBackground(myNotColoredSegmentsBackground);
                    gc.fillRectangle(leftArea);
                }
                if (segmentMax > userMax) {
                    Rectangle rightArea = mapper.segment2rectangle(userMax, segmentMax, 1.0, true);
                    gc.setBackground(myNotColoredSegmentsBackground);
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
            gc.setForeground(mySegmentForeground);
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
}
