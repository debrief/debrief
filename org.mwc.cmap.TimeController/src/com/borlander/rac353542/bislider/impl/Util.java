package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;

class Util {

    private Util() {
        // do not instantiate
    }

    public static Rectangle cloneRectangle(Rectangle rectangle) {
        return new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    /**
     * Always create a new instance with SWT.BOLD flag "on".
     */
    public static Font deriveBold(Font font) {
        FontData[] fontData = font.getFontData();
        if (fontData.length != 1) {
            // fallback, newer with Windows
            // create separate instance to simplify disposing
            return new Font(Display.getCurrent(), fontData);
        }
        FontData theOnly = fontData[0];
//        return new Font(Display.getCurrent(), theOnly.getName(), theOnly.getHeight(), theOnly.getStyle() | SWT.BOLD);
        return new Font(Display.getCurrent(), theOnly.getName(), 8, theOnly.getStyle());
    }
}
