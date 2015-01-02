/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.gt2plot.proj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.runtime.Platform;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.renderer.label.LabelCacheImpl;
import org.geotools.renderer.lite.LabelCache;
import org.geotools.renderer.lite.StreamingRenderer;

/**
 * helper class that produces an image from a GeoTools map
 * 
 * @author ian
 * 
 */
public class GeoToolsPainter
{

	/** utlity function to produce a Java image from the supplied layers
	 * 
	 * @param width width of required image
	 * @param height height of reuqired image
	 * @param proj projection containing the MapContent 
	 * @param bkColor (optional) background color to fill the image
	 * @return
	 */
	public static BufferedImage drawAwtImage(final int width, final int height,
			final GtProjection proj, final Color bkColor)
	{
		final MapContent map = proj.getMapContent();
		final StreamingRenderer renderer = new StreamingRenderer();
		renderer.setMapContent(map);
		// fix/workaround for "Pole 90% exception"
		org.geotools.util.logging.Logging.getLogger("org.geotools.rendering").setLevel(Level.OFF);

		if (!Platform.OS_LINUX.equals(Platform.getOS()))
		{
			final RenderingHints hints = new RenderingHints(
					RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			renderer.setJava2DHints(hints);
		}
		final Map<String, Object> rendererParams = new HashMap<String, Object>();
		rendererParams.put("optimizedDataLoadingEnabled", new Boolean(true));
		LabelCache labelCache = new LabelCacheImpl();
		rendererParams.put(StreamingRenderer.LABEL_CACHE_KEY, labelCache);
		renderer.setRendererHints(rendererParams);

		final BufferedImage baseImage = new BufferedImage(width + 1, height + 1,
				BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g2d = baseImage.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
		if (Platform.OS_LINUX.equals(Platform.getOS()))
		{
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 250);
			g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		}
		// do we need to set the background color?
		if (bkColor != null && !Platform.OS_LINUX.equals(Platform.getOS()))
		{
			// fill in the background color
			g2d.setPaint(bkColor);
			g2d.fillRect(0, 0, baseImage.getWidth(), baseImage.getHeight());
		}
		
		// renderer.setContext(context);
		final java.awt.Rectangle awtRectangle = new Rectangle(0, 0, width, height);
		final ReferencedEnvelope mapAOI = map.getViewport().getBounds();
		final AffineTransform worldToScreen = map.getViewport().getWorldToScreen();
		renderer.paint(g2d, awtRectangle, mapAOI, worldToScreen);
		// swtImage.dispose();

		return baseImage;
	}

}
