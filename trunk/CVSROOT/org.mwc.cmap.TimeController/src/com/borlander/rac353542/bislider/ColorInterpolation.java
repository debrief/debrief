package com.borlander.rac353542.bislider;

import org.eclipse.swt.graphics.RGB;

public abstract class ColorInterpolation {
    private double myMaxValue;
    private double myMinValue;
    private RGB myMinRGB;
    private RGB myMaxRGB;
    private boolean myIsTrivial;

    protected abstract RGB computeRGB(double rate);
    
    public boolean isSameInterpolationMode(ColorInterpolation other){
        return other != null && this.getClass().equals(other.getClass());
    }

    public final RGB interpolateRGB(double value) {
        if (myIsTrivial) {
            return myMinRGB;
        }
        double rate = getRate(value);
        return computeRGB(rate);
    }

    public void setContext(RGB minRGB, RGB maxRGB, double minValue, double maxValue) {
        if (minValue == maxValue) {
            throw new IllegalArgumentException("I can not accept zero range");
        }
        if (minValue > maxValue) {
            double temp = minValue;
            minValue = maxValue;
            maxValue = temp;
        }
        myMinRGB = minRGB;
        myMaxRGB = maxRGB;
        myMinValue = minValue;
        myMaxValue = maxValue;
        myIsTrivial = myMinRGB.equals(myMaxRGB);
    }

    protected final RGB getMinRGB() {
        return myMinRGB;
    }

    protected final RGB getMaxRGB() {
        return myMaxRGB;
    }

    protected double getRate(double value) {
        if (value <= myMinValue) {
            return 0;
        }
        if (value > myMaxValue) {
            return 1;
        }
        return (value - myMinValue) / (myMaxValue - myMinValue);
    }

    protected final RGB createRGB(double red, double green, double blue) {
        return new RGB((int) red, (int) green, (int) blue);
    }

    public static class INTERPOLATE_RGB extends ColorInterpolation {

        protected RGB computeRGB(double rate) {
            RGB min = getMinRGB();
            RGB max = getMaxRGB();
            return createRGB( //
                    min.red + rate * (max.red - min.red), //
                    min.green + rate * (max.green - min.green), //
                    min.blue + rate * (max.blue - min.blue));
        }
    }

    public static class INTERPOLATE_HSB extends ColorInterpolation {

        protected RGB computeRGB(double rate) {
            RGB min = getMinRGB();
            RGB max = getMaxRGB();
            float hsbMin[] = AWTColorUtil.RGBtoHSB(min.red, min.green, min.blue, null);
            float hsbMax[] = AWTColorUtil.RGBtoHSB(max.red, max.green, max.blue, null);
            double h = (hsbMax[0] - hsbMin[0]) * rate + hsbMin[0];
            double s = (hsbMax[1] - hsbMin[1]) * rate + hsbMin[1];
            double b = (hsbMax[2] - hsbMin[2]) * rate + hsbMin[2];
            return AWTColorUtil.HSBtoRGBObject(h, s, b);
        }
    }

    public static class INTERPOLATE_CENTRAL_BLACK extends ColorInterpolation {

        private static final RGB BLACK = new RGB(0, 0, 0);
        private final ColorInterpolation myLeftInterpolation;
        private final ColorInterpolation myRightInterpolation;

        public INTERPOLATE_CENTRAL_BLACK() {
            myLeftInterpolation = new INTERPOLATE_RGB();
            myRightInterpolation = new INTERPOLATE_RGB();
        }

        public void setContext(RGB minRGB, RGB maxRGB, double minValue, double maxValue) {
            super.setContext(minRGB, maxRGB, minValue, maxValue);
            myLeftInterpolation.setContext(minRGB, BLACK, 0, 0.5);
            myRightInterpolation.setContext(minRGB, BLACK, 0.5, 1);
        }

        protected RGB computeRGB(double rate) {
            return rate <= 0.5 ? myLeftInterpolation.interpolateRGB(rate) : myRightInterpolation.interpolateRGB(rate);
        }
    }
}
