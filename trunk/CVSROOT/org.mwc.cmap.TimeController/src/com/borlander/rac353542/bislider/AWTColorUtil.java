package com.borlander.rac353542.bislider;

import org.eclipse.swt.graphics.RGB;

/**
 * This class contains methods copy-pasetd from java.awt.Color Intention of this
 * copy paste was to avoid any dependency to awt which is known source of
 * problems in SWT-implementation for some platforms.
 * <p>
 * If you think that it is OK to import AWT classes, please feel free to replace
 * these methods with direct calls to <code>java.awt.Color</code>
 */
public class AWTColorUtil {

    /**
     * Converts the components of a color, as specified by the HSB model, to an
     * equivalent set of values for the default RGB model packed into
     * <code>org.eclipse.swt.graphics.RGB</code> object.
     */
    public static RGB HSBtoRGBObject(double hue, double saturation, double brightness) {
        int encoded = HSBtoRGB((float)hue, (float)saturation, (float)brightness);
        int red = (encoded >> 16) & 0xFF;
        int green = (encoded >> 8) & 0xFF;
        int blue = (encoded >> 0) & 0xFF;
        return new RGB(red, green, blue);
    }

    /**
     * Converts the components of a color, as specified by the default RGB
     * model, to an equivalent set of values for hue, saturation, and brightness
     * that are the three components of the HSB model.
     * <p>
     * If the <code>hsbvals</code> argument is <code>null</code>, then a
     * new array is allocated to return the result. Otherwise, the method
     * returns the array <code>hsbvals</code>, with the values put into that
     * array.
     * 
     * @param r
     *            the red component of the color
     * @param g
     *            the green component of the color
     * @param b
     *            the blue component of the color
     * @param hsbvals
     *            the array used to return the three HSB values, or
     *            <code>null</code>
     * @return an array of three elements containing the hue, saturation, and
     *         brightness (in that order), of the color with the indicated red,
     *         green, and blue components.
     * @see java.awt.Color#getRGB()
     * @see java.awt.Color#Color(int)
     * @see java.awt.image.ColorModel#getRGBdefault()
     * @since JDK1.0
     */
    public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
        float hue, saturation, brightness;
        if (hsbvals == null) {
            hsbvals = new float[3];
        }
        int cmax = (r > g) ? r : g;
        if (b > cmax)
            cmax = b;
        int cmin = (r < g) ? r : g;
        if (b < cmin)
            cmin = b;
        brightness = cmax / 255.0f;
        if (cmax != 0)
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        else
            saturation = 0;
        if (saturation == 0)
            hue = 0;
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0f + redc - bluec;
            else
                hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0)
                hue = hue + 1.0f;
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }

    /**
     * Converts the components of a color, as specified by the HSB model, to an
     * equivalent set of values for the default RGB model.
     * <p>
     * The <code>saturation</code> and <code>brightness</code> components
     * should be floating-point values between zero and one (numbers in the
     * range 0.0-1.0). The <code>hue</code> component can be any
     * floating-point number. The floor of this number is subtracted from it to
     * create a fraction between 0 and 1. This fractional number is then
     * multiplied by 360 to produce the hue angle in the HSB color model.
     * <p>
     * The integer that is returned by <code>HSBtoRGB</code> encodes the value
     * of a color in bits 0-23 of an integer value that is the same format used
     * by the method {@link #getRGB() <code>getRGB</code>}. This integer can be
     * supplied as an argument to the <code>Color</code> constructor that
     * takes a single integer argument.
     * 
     * @param hue
     *            the hue component of the color
     * @param saturation
     *            the saturation of the color
     * @param brightness
     *            the brightness of the color
     * @return the RGB value of the color with the indicated hue, saturation,
     *         and brightness.
     * @see java.awt.Color#getRGB()
     * @see java.awt.Color#Color(int)
     * @see java.awt.image.ColorModel#getRGBdefault()
     * @since JDK1.0
     */
    private static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) java.lang.Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
            case 0:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (t * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 1:
                r = (int) (q * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 2:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (t * 255.0f + 0.5f);
                break;
            case 3:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (q * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 4:
                r = (int) (t * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 5:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (q * 255.0f + 0.5f);
                break;
            }
        }
        return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
    }
}
