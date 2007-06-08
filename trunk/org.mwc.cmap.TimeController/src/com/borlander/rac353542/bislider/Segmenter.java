package com.borlander.rac353542.bislider;

public class Segmenter implements Disposable {
    private final BiSliderDataModel myDataModel;
    private final BiSliderUIModel myUiModel;
    private ColorInterpolation myInterpolation;
    private double myCachedTotalMin;
    private double myCachedTotalMax;
    private double myCachedSegmentSize;
    private ColoredSegment[] mySegments;
    private final BiSliderDataModel.Listener myDataModelListener;
    
    public Segmenter(BiSliderDataModel dataModel, BiSliderUIModel uiModel, ColorInterpolation interpolation){
        myDataModel = dataModel;
        myUiModel = uiModel;
        cacheDataModelValues();
        setInterpolation(interpolation != null ? interpolation : new ColorInterpolation.INTERPOLATE_RGB());
        myDataModelListener = new BiSliderDataModel.Listener(){
            public void dataModelChanged(BiSliderDataModel model) {
                onDataModelChanged();
            }
        };
        myDataModel.addListener(myDataModelListener);
    }
    
    public void freeResources() {
        myDataModel.removeListener(myDataModelListener);
        disposeSegments();
    }
    
    public ColoredSegmentEnumeration allSegments(){
        return new SegmentsArrayAsEnumeration(mySegments);
    }
    
    public ColoredSegmentEnumeration segments(double minValue, double maxValue){
        minValue = Math.max(minValue, myCachedTotalMin);
        maxValue = Math.min(maxValue, myCachedTotalMax);
        
        double segmentSize = myDataModel.getSegmentLength();
        int startIndex = (int)Math.floor((minValue - myCachedTotalMin) / segmentSize);
        int lastIndex = (int)Math.ceil((maxValue - myCachedTotalMin) / segmentSize);
        return new SegmentsArrayAsEnumeration(mySegments, startIndex, lastIndex);
    }
    
    private void computeSegments() {
        double segmentSize = myDataModel.getSegmentLength();
        int segmentsCount = myDataModel.getSegmentsCount();
        mySegments = new ColoredSegment[segmentsCount];
        double nextSegmentStart = myDataModel.getTotalMinimum();
        for (int i = 0; i < segmentsCount; i++, nextSegmentStart += segmentSize){
            ColorDescriptor descriptor = new ColorDescriptor(myInterpolation.interpolateRGB(nextSegmentStart));
            mySegments[i] = new ColoredSegment(nextSegmentStart, nextSegmentStart + segmentSize, descriptor);
        }
    }

    private void cacheDataModelValues(){
        myCachedTotalMax = myDataModel.getTotalMaximum();
        myCachedTotalMin = myDataModel.getTotalMinimum();
        myCachedSegmentSize = myDataModel.getSegmentLength();
    }
    
    private boolean isDataModelChanged(){
        return myCachedSegmentSize != myDataModel.getSegmentLength() ||
                myCachedTotalMax != myDataModel.getTotalMaximum() || 
                myCachedTotalMin != myDataModel.getTotalMinimum();
    }
    
    private void disposeSegments(){
        if (mySegments != null){
            for (int i = 0; i < mySegments.length; i++){
                mySegments[i].getColorDescriptor().freeResources();
            }
            mySegments = null;
        }
    }
    
    private void setInterpolation(ColorInterpolation interpolation){
        if (interpolation == null){
            return;
        }
        if (myInterpolation != null && myInterpolation.isSameInterpolationMode(interpolation)){
            return;
        }
        myInterpolation = interpolation;
        myInterpolation.setContext(myUiModel.getMinimumRGB(), myUiModel.getMaximumRGB(), myDataModel.getTotalMinimum(), myDataModel.getTotalMaximum());
        disposeSegments();
        computeSegments();
    }
    
    private void onDataModelChanged() {
        if (isDataModelChanged()){
            disposeSegments();
            computeSegments();
        }
        cacheDataModelValues();        
    }
    
    private static class SegmentsArrayAsEnumeration implements ColoredSegmentEnumeration {
        private int myNextIndex;
        private final ColoredSegment[] myArray;
        private final int myLastIndexExclusive;
        
        public SegmentsArrayAsEnumeration(ColoredSegment[] segments){
            this(segments, 0, segments.length);
        }
        
        public SegmentsArrayAsEnumeration(ColoredSegment[] segments, int startIndexInclusive, int lastIndexExclusive){
            myNextIndex = startIndexInclusive;
            myArray = segments;
            myLastIndexExclusive = lastIndexExclusive;
        }
        
        public boolean hasMoreElements() {
            return myNextIndex < myLastIndexExclusive;
        }
        
        public Object nextElement() {
            return next();
        }
        
        public ColoredSegment next() {
            return myArray[myNextIndex++];
        }
    }
    
}
