package org.mwc.cmap.gt2plot.data;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.mwc.cmap.core.ui_support.swt.SWTCanvasAdapter;
import org.mwc.cmap.gt2plot.GtActivator;
import org.mwc.cmap.gt2plot.proj.GtProjection;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Layer.BackgroundLayer;

public class GTLayer extends BaseLayer implements BackgroundLayer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final PaletteData PALETTE_DATA = new PaletteData(0xFF0000,
			0xFF00, 0xFF);

	/** RGB value to use as transparent color */
	private static final int TRANSPARENT_COLOR = 0x123456;

	private transient FeatureLayer _countries;
	private transient Image swtImage;

	private transient WorldImageLayer _ukImage;

	public GTLayer()
	{
		setName("GT test");
		
		try
		{
			URL url = GtActivator.getDefault().getBundle()
					.getEntry("data/50m_admin_0_countries.shp");
			String filePath = FileLocator.resolve(url).getFile();
			File file = new File(filePath);
			if (!file.exists())
				System.err.println("can't find file!!!");
			FileDataStore store = FileDataStoreFinder.getDataStore(file);
			if (store != null)
			{
				SimpleFeatureSource featureSource = store.getFeatureSource();
				Style style = SLD.createSimpleStyle(featureSource.getSchema());
				_countries = new FeatureLayer(featureSource, style);
			}

			String path = "/Users/ian/Desktop/ukrasterchart/2_BRITISH_ISLES.tif";
			File chartFile = new File(path);
			if(!chartFile.exists())
				System.err.println("CANNOT FILE THE CHART FILE!!!");
			
			// also have a go at loading the UK
			_ukImage = new WorldImageLayer("uk", path );
			_ukImage.setVisible(true);
			
		}
		catch (IOException e)
		{

		}
	}

	@Override
	public void paint(CanvasType dest)
	{
		int width = dest.getSize().width;
		int height = dest.getSize().height;

		// is this a loverly GT map
		if (dest.getProjection() instanceof GtProjection)
		{

			GtProjection gp = (GtProjection) dest.getProjection();

			MapContent map = gp.getMapContent();

			// right, does it already store us?
			boolean found = false;
			Iterator<Layer> iter = map.layers().iterator();
			while (iter.hasNext())
			{
				Layer layer = (Layer) iter.next();
				if (layer == _countries)
					found = true;
			}

			if (!found)
			{
				map.addLayer(_countries);
				map.addLayer(_ukImage.getLayer());
				_ukImage.setMap(map);
			}

			StreamingRenderer renderer = new StreamingRenderer();
			renderer.setMapContent(map);

			RenderingHints hints = new RenderingHints(
					RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			renderer.setJava2DHints(hints);

			Map<String, Object> rendererParams = new HashMap<String, Object>();
			rendererParams.put("optimizedDataLoadingEnabled", new Boolean(true));

			renderer.setRendererHints(rendererParams);

			BufferedImage baseImage = new BufferedImage(width + 1, height + 1,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = baseImage.createGraphics();
			g2d.fillRect(0, 0, width + 1, height + 1);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			// renderer.setContext(context);
			java.awt.Rectangle awtRectangle = new Rectangle(0, 0, width, height);
			final ReferencedEnvelope mapAOI = map.getViewport().getBounds();
			AffineTransform worldToScreen = map.getViewport().getWorldToScreen();
			renderer.paint(g2d, awtRectangle, mapAOI, worldToScreen);
			// swtImage.dispose();

			if (swtImage != null && !swtImage.isDisposed())
			{
				swtImage.dispose();
				swtImage = null;
			}
			swtImage = new Image(Display.getCurrent(), awtToSwt(baseImage, width + 1,
					height + 1));

			// org.eclipse.swt.graphics.Image image = new
			// org.eclipse.swt.graphics.Image(
			// e.display, convertToSWT(tmpImage));
			if (dest instanceof SWTCanvasAdapter)
			{
				SWTCanvasAdapter swtC = (SWTCanvasAdapter) dest;
				swtC.drawSWTImage(swtImage, 0, 0, width, height);
			}
		}

		double y2 = Math.random() * 120d;
		dest.setColor(Color.red);
		dest.drawLine(20, 40, 80, (int) y2);
	}

	private ImageData awtToSwt(BufferedImage bufferedImage, int width, int height)
	{
		final int[] awtPixels = new int[width * height];
		ImageData swtImageData = new ImageData(width, height, 24, PALETTE_DATA);
		swtImageData.transparentPixel = TRANSPARENT_COLOR;
		int step = swtImageData.depth / 8;
		final byte[] data = swtImageData.data;
		bufferedImage.getRGB(0, 0, width, height, awtPixels, 0, width);
		for (int i = 0; i < height; i++)
		{
			int idx = (0 + i) * swtImageData.bytesPerLine + 0 * step;
			for (int j = 0; j < width; j++)
			{
				int rgb = awtPixels[j + i * width];
				for (int k = swtImageData.depth - 8; k >= 0; k -= 8)
				{
					data[idx++] = (byte) ((rgb >> k) & 0xFF);
				}
			}
		}

		return swtImageData;
	}

}
