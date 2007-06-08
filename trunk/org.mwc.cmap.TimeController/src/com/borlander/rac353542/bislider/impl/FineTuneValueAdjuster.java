package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import com.borlander.rac353542.bislider.*;

public class FineTuneValueAdjuster {

    private static final int MAX_ADJUSTMENT_STEPS = 100;
    private static final int MIN_ADJUSTMENT_STEPS = 3;
    private final BiSliderImpl myBiSlider;
    private final BiSliderDataModel myDataModel;
    private final boolean myIsForMinimumPointer;
    private AdjustmentControl myAdjustmentControl;

    public FineTuneValueAdjuster(BiSliderImpl biSlider, boolean isForMinimumPointer) {
        myBiSlider = biSlider;
        myDataModel = biSlider.getDataModel();
        myIsForMinimumPointer = isForMinimumPointer;
    }

    public void showAdjustmentControl() {
        if (myAdjustmentControl != null) {
            return;
        }
        Scale2ValueConverter converter = createScale2ValueConverter();
        if (converter == null) {
            return;
        }
        myAdjustmentControl = createAdjustmentComposite(myBiSlider, converter);
        if (myAdjustmentControl == null) {
            return;
        }
        positionAdjustmentControl(myAdjustmentControl);
        myAdjustmentControl.setVisible(true);
        myAdjustmentControl.getScale().setFocus();
        myAdjustmentControl.getScale().addFocusListener(new FocusAdapter() {

            public void focusLost(FocusEvent e) {
                disposeAdjustmentControl(false);
            }
        });
        myAdjustmentControl.getScale().addKeyListener(new KeyListener() {

            public void keyReleased(KeyEvent e) {
                if (e.character == SWT.ESC || e.character == SWT.CR) {
                    disposeAdjustmentControl(e.character == SWT.CR);
                }
            }

            public void keyPressed(KeyEvent e) {
                //
            }
        });
    }

    /**
     * NOTE: Inside tis method, "width" and "X" are used to denote the size and
     * value for <b>tangential</b> component of coordinate sets defined by
     * axis, and "height" and "Y" used to denote the size and value for
     * <b>normal</b> component. That is, if Bislider iyself is Vertical, than
     * "width" means the screen height, and vise versa
     */
    private void positionAdjustmentControl(Control control) {
        /*
         * NOTE: Inside tis method, "width" and "X" are used to denote the size
         * and value for <b>tangential</b> component of coordinate sets defined
         * by axis, and "height" and "Y" used to denote the size and value for
         * <b>normal</b> component. That is, if Bislider iyself is Vertical,
         * than "width" means the screen height, and vise versa.
         */
        Point prefSize = myAdjustmentControl.getPreferredSize();
        CoordinateMapper coordinateMapper = myBiSlider.getCoordinateMapper();
        Axis axis = coordinateMapper.getAxis();
        Rectangle drawArea = coordinateMapper.getDrawArea();
        boolean atMinumumEdge = !myIsForMinimumPointer;
        Point basePoint = coordinateMapper.value2pixel(getRoughValue(), atMinumumEdge);
        
        int controlCenterX = axis.get(basePoint);
        int controlWidth = axis.get(prefSize);
        int drawAreaMinX = axis.getMin(drawArea);
        int drawAreaMaxX = axis.getMax(drawArea);

        int controlHalfWidth = controlWidth / 2;
        if (controlCenterX - controlHalfWidth < drawAreaMinX) {
            controlCenterX = drawAreaMinX + controlHalfWidth;
        }
        if (controlCenterX + controlHalfWidth > drawAreaMaxX) {
            controlCenterX = drawAreaMaxX - controlHalfWidth;
        }
        
        axis.set(basePoint, controlCenterX - controlHalfWidth);

        //nornal component
        Rectangle fullControlBounds = coordinateMapper.getFullBounds();
        int baseY = axis.getNormal(basePoint);
        int controlHeight = axis.getNormal(prefSize);
        int minVisibleY = axis.getNormalMin(fullControlBounds);
        int maxVisibleY = axis.getNormalMax(fullControlBounds);
        if (atMinumumEdge) {
            int pointToSeeAtBottom = maxVisibleY - controlHeight;
            if (pointToSeeAtBottom < minVisibleY){
                pointToSeeAtBottom = minVisibleY;
            }
            axis.setNormal(basePoint, Math.min(baseY + 10, pointToSeeAtBottom));
        } else {
            int pointToSeeAtTop = minVisibleY;
            axis.setNormal(basePoint, Math.max(baseY -10 - controlHeight, pointToSeeAtTop));
        }
        control.setBounds(basePoint.x, basePoint.y, prefSize.x, prefSize.y);
    }

    public void disposeAdjustmentControl(boolean acceptValue) {
        if (myAdjustmentControl != null && !myAdjustmentControl.isDisposed()) {
            if (acceptValue) {
                double adjustedValue = myAdjustmentControl.getSelectedValue();
                if (myIsForMinimumPointer) {
                    myBiSlider.getWritableDataModel().setUserMinimum(adjustedValue);
                } else {
                    myBiSlider.getWritableDataModel().setUserMaximum(adjustedValue);
                }
            }
            myAdjustmentControl.dispose();
            myAdjustmentControl = null;
        }
    }

    private AdjustmentControl createAdjustmentComposite(Composite parent, Scale2ValueConverter converter) {
        return new AdjustmentControl(parent, myBiSlider.getUIModel().isVertical(), converter, myBiSlider.getUIModel().getLabelProvider());
    }

    private Scale2ValueConverter createScale2ValueConverter() {
        CoordinateMapper mapper = myBiSlider.getCoordinateMapper();
        double roughValue = getRoughValue();
        double onePixelDelta = mapper.getOnePixelValueDelta(roughValue);
        // allow to adjust values for +/- 2 screen pixels
        double scaleDelta = onePixelDelta * 2;
        double safeMin = makeSafe(roughValue - scaleDelta);
        double safeMax = makeSafe(roughValue + scaleDelta);
        double precision = myDataModel.getPrecision();
        if (safeMax - safeMin <= precision * MIN_ADJUSTMENT_STEPS) {
            return null;
        }
        int adjustmentSteps = (precision == 0) ? MAX_ADJUSTMENT_STEPS : Math.min(MAX_ADJUSTMENT_STEPS, (int) ((safeMax - safeMin) / precision));
        if (adjustmentSteps < MIN_ADJUSTMENT_STEPS) {
            return null;
        }
        int stepForRoughValue = (int) Math.round(adjustmentSteps * (roughValue - safeMin) / (safeMax - safeMin));
        return new Scale2ValueConverter(roughValue, stepForRoughValue, safeMax - safeMin, adjustmentSteps);
    }

    private double makeSafe(double someDouble) {
        return (myIsForMinimumPointer) ? ensureInRange(someDouble, myDataModel.getTotalMinimum(), myDataModel.getUserMaximum()) : ensureInRange(someDouble, myDataModel.getUserMinimum(), myDataModel
                .getTotalMaximum());
    }

    private static double ensureInRange(double value, double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("Requested min: " + min + ", requested max: " + max);
        }
        value = Math.min(value, max);
        value = Math.max(value, min);
        return value;
    }

    private double getRoughValue() {
        return myIsForMinimumPointer ? myDataModel.getUserMinimum() : myDataModel.getUserMaximum();
    }

    private static class Scale2ValueConverter {

        private final double myFixedValue;
        private final int myStepForFixedValue;
        private final int myTotalSteps;
        private final double myStepIncrement;

        /**
         * Creates converter which will map double range into [0, totalSteps]
         * integer range suitable for Scale control. In the context of this
         * mapping the given <code>fixedValue</code> should be mapped into
         * <code>stepForFixedValue</code>.
         */
        public Scale2ValueConverter(double fixedValue, int stepForFixedValue, double totalDelta, int maxStep) {
            myFixedValue = fixedValue;
            myStepForFixedValue = stepForFixedValue;
            myTotalSteps = maxStep;
            myStepIncrement = totalDelta / maxStep;
        }

        public int getTotalSteps() {
            return myTotalSteps;
        }

        public int getStepForFixedValue() {
            return myStepForFixedValue;
        }

        public double scale2value(int step) {
            int delta = step - myStepForFixedValue;
            return myFixedValue + delta * myStepIncrement;
        }
    }

    private static class AdjustmentControl extends Composite {

        private final BiSliderLabelProvider myLabelProvider;
        private final Scale2ValueConverter myConverter;
        private final boolean myIsVertical;
        private Scale myScale;
        private Label myLabel;

        public AdjustmentControl(Composite parent, boolean isVertical, Scale2ValueConverter converter, BiSliderLabelProvider labelProvider) {
            super(parent, SWT.BORDER);
            myLabelProvider = labelProvider;
            myIsVertical = isVertical;
            myConverter = converter;
            createContents();
            myScale.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {
                    updateLabel();
                }

                public void widgetSelected(SelectionEvent e) {
                    updateLabel();
                }
            });
            updateLabel();
        }

        private void updateLabel() {
            int selectedScale = myScale.getSelection();
            double selectedValue = myConverter.scale2value(selectedScale);
            String label = myLabelProvider.getLabel(selectedValue);
            if (label == null) {
                label = "";
            }
            myLabel.setText(label);
        }

        public Point getPreferredSize() {
            return this.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        }

        public Scale getScale() {
            return myScale;
        }

        public double getSelectedValue() {
            return myConverter.scale2value(myScale.getSelection());
        }

        private void createContents() {
            setLayout(new GridLayout(1, true));
            myScale = new Scale(this, myIsVertical ? SWT.VERTICAL : SWT.HORIZONTAL);
            myScale.setMinimum(0);
            myScale.setMaximum(myConverter.getTotalSteps());
            myScale.setIncrement(1);
            myScale.setPageIncrement(Math.max(1, myConverter.getTotalSteps() / 4));
            myScale.setSelection(myConverter.getStepForFixedValue());
            myScale.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            myLabel = new Label(this, SWT.CENTER);
            myLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        }
    }
}
