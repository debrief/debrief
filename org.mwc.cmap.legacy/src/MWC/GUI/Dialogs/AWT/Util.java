/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Dialogs.AWT;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;

public class Util {
    public static void waitForImage(final Component component, 
                                    final Image image) {
        final MediaTracker tracker = new MediaTracker(component);
        try {
            tracker.addImage(image, 0);
            tracker.waitForID(0);
        }
        catch(final InterruptedException e) { MWC.Utilities.Errors.Trace.trace(e); }
    }
    public static void wallPaper(final Component component, 
                            final Graphics  g, 
                            final Image     image) {
        final Dimension compsize = component.getSize();
        Util.waitForImage(component, image);

        final int patchW = image.getWidth(component);
        final int patchH = image.getHeight(component);

        for(int r=0; r < compsize.width; r += patchW) {
            for(int c=0; c < compsize.height; c += patchH) {
				g.drawImage(image, r, c, component);
			}
        }
    }
	public static Frame getFrame(final Component comp) {
		Component c = comp;
        if(c instanceof Frame)
            return (Frame)c;

        while((c = c.getParent()) != null) {
            if(c instanceof Frame)
                return (Frame)c;
        }
        return null;
    }
}
