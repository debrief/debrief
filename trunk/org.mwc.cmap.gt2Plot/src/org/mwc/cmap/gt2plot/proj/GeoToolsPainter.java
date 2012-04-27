package org.mwc.cmap.gt2plot.proj;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;

/** helper class that produces an image from a GeoTools map
 * 
 * @author ian
 *
 */
public class GeoToolsPainter
{

	public static BufferedImage drawAwtImage(int width, int height, GtProjection proj)
	{
		MapContent map = proj.getMapContent();
		StreamingRenderer renderer = new StreamingRenderer();
		renderer.setMapContent(map);

		RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		renderer.setJava2DHints(hints);

		Map<String, Object> rendererParams = new HashMap<String, Object>();
		rendererParams.put("optimizedDataLoadingEnabled", new Boolean(true));

		renderer.setRendererHints(rendererParams);

		BufferedImage baseImage = new BufferedImage(width + 1, height + 1,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = baseImage.createGraphics();
	//	g2d.fillRect(0, 0, width + 1, height + 1);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// renderer.setContext(context);
		java.awt.Rectangle awtRectangle = new Rectangle(0, 0, width, height);
		final ReferencedEnvelope mapAOI = map.getViewport().getBounds();
		AffineTransform worldToScreen = map.getViewport().getWorldToScreen();
		renderer.paint(g2d, awtRectangle, mapAOI, worldToScreen);
		// swtImage.dispose();

		return baseImage;
	}

}
