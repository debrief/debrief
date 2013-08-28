package org.mwc.cmap.gt2plot.views;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.gce.image.WorldImageFormat;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactoryImpl;
import org.geotools.swt.utils.Utils;
import org.mwc.cmap.gt2plot.GtActivator;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterValue;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class ViewTest extends ViewPart
{
	private static final PaletteData PALETTE_DATA = new PaletteData(0xFF0000,
			0xFF00, 0xFF);

	/** RGB value to use as transparent color */
	private static final int TRANSPARENT_COLOR = 0x123456;
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.mwc.cmap.gt2plot.views.SampleView";

	private Action action1;

	private final MapContent map;

	private AffineTransform worldToScreen;

	private Canvas canvas;

	private Image swtImage;

	/**
	 * The constructor.
	 */
	public ViewTest()
	{
		// Create a map content and add our shapefile to it
		map = new MapContent();
		map.setTitle("simple map content");

		// hey, try for an image aswell
		final String path = "/Users/ian/Desktop/ukrasterchart/2_BRITISH_ISLES.tif";
		final File chartFile = new File(path);
		if (!chartFile.exists())
			System.err.println("CANNOT FILE THE CHART FILE!!!");

		final WorldImageFormat format = new WorldImageFormat();
		final AbstractGridCoverage2DReader tiffReader = format.getReader(chartFile);
		if (tiffReader != null)
		{
			final StyleFactoryImpl sf = new StyleFactoryImpl();
			final RasterSymbolizer symbolizer = sf.getDefaultRasterSymbolizer();
			final Style defaultStyle = SLD.wrapSymbolizers(symbolizer);

			final GeneralParameterValue[] params = null;

			final GridReaderLayer res = new GridReaderLayer(tiffReader, defaultStyle,
					params);
			map.addLayer(res);
		}

		try
		{
			final URL url = GtActivator.getDefault().getBundle()
					.getEntry("data/50m_admin_0_countries.shp");
			final String filePath = FileLocator.resolve(url).getFile();
			final File file = new File(filePath);
			if (!file.exists())
				System.err.println("can't find file!!!");
			final FileDataStore store = FileDataStoreFinder.getDataStore(file);
			if (store != null)
			{
				final SimpleFeatureSource featureSource = store.getFeatureSource();

				final Style style = SLD.createSimpleStyle(featureSource.getSchema());
				final Layer layer = new FeatureLayer(featureSource, style);
				map.addLayer(layer);
			}

		}
		catch (final IOException e)
		{

		}
		//
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(final Composite parent)
	{
		canvas = new Canvas(parent, SWT.NONE);
		canvas.addPaintListener(new PaintListener()
		{

			public void paintControl(final PaintEvent e)
			{
				paintMe(e);
			}
		});

		makeActions();
		contributeToActionBars();
	}

	@SuppressWarnings("unchecked")
	private void paintMe(final PaintEvent e)
	{
		final int width = e.gc.getClipping().width;
		final int height = e.gc.getClipping().height;

		if (map != null)
		{
			// sort out the transforms
			final org.eclipse.swt.graphics.Rectangle paintArea = new org.eclipse.swt.graphics.Rectangle(
					0, 0, width, height);
			final ReferencedEnvelope mapArea = map.getViewport().getBounds();
			setTransforms(mapArea, paintArea);

			final StreamingRenderer renderer = new StreamingRenderer();
			renderer.setMapContent(map);

			final RenderingHints hints = new RenderingHints(
					RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			renderer.setJava2DHints(hints);

			@SuppressWarnings("rawtypes")
			final
			Map rendererParams = new HashMap();
			rendererParams.put("optimizedDataLoadingEnabled", new Boolean(true));

			renderer.setRendererHints(rendererParams);

			final org.eclipse.swt.graphics.Rectangle curPaintArea = e.gc.getClipping();
			final BufferedImage baseImage = new BufferedImage(curPaintArea.width + 1,
					curPaintArea.height + 1, BufferedImage.TYPE_INT_ARGB);
			final Graphics2D g2d = baseImage.createGraphics();
			g2d.fillRect(0, 0, curPaintArea.width + 1, curPaintArea.height + 1);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			// renderer.setContext(context);
			final java.awt.Rectangle awtRectangle = Utils.toAwtRectangle(curPaintArea);
			final ReferencedEnvelope mapAOI = map.getViewport().getBounds();
			renderer.paint(g2d, awtRectangle, mapAOI, getWorldToScreenTransform());
			// swtImage.dispose();

			if (swtImage != null && !swtImage.isDisposed())
			{
				swtImage.dispose();
				swtImage = null;
			}
			swtImage = new Image(canvas.getDisplay(), awtToSwt(baseImage,
					curPaintArea.width + 1, curPaintArea.height + 1));

			// org.eclipse.swt.graphics.Image image = new
			// org.eclipse.swt.graphics.Image(
			// e.display, convertToSWT(tmpImage));
			e.gc.drawImage(swtImage, 0, 0);
		}

		final double y2 = Math.random() * 120d;
		e.gc.drawLine(20, 40, 80, (int) y2);
	};

	/**
	 * Transform a java2d bufferedimage to a swt image.
	 * 
	 * @param bufferedImage
	 *          the image to trasform.
	 * @param width
	 *          the image width.
	 * @param height
	 *          the image height.
	 * @return swt image.
	 */
	private ImageData awtToSwt(final BufferedImage bufferedImage, final int width, final int height)
	{
		final int[] awtPixels = new int[width * height];
		final ImageData swtImageData = new ImageData(width, height, 24, PALETTE_DATA);
		swtImageData.transparentPixel = TRANSPARENT_COLOR;
		final int step = swtImageData.depth / 8;
		final byte[] data = swtImageData.data;
		bufferedImage.getRGB(0, 0, width, height, awtPixels, 0, width);
		for (int i = 0; i < height; i++)
		{
			int idx = (0 + i) * swtImageData.bytesPerLine + 0 * step;
			for (int j = 0; j < width; j++)
			{
				final int rgb = awtPixels[j + i * width];
				for (int k = swtImageData.depth - 8; k >= 0; k -= 8)
				{
					data[idx++] = (byte) ((rgb >> k) & 0xFF);
				}
			}
		}

		return swtImageData;
	}

	private ReferencedEnvelope worldEnvelope()
	{
		return new ReferencedEnvelope(-180, 180, -90, 90,
				DefaultGeographicCRS.WGS84);
	}

	/**
	 * Calculate the affine transforms used to convert between world and pixel
	 * coordinates. The calculations here are very basic and assume a cartesian
	 * reference system.
	 * <p>
	 * Tne transform is calculated such that {@code envelope} will be centred in
	 * the display
	 * 
	 * @param envelope
	 *          the current map extent (world coordinates)
	 * @param paintArea
	 *          the current map pane extent (screen units)
	 */
	private void setTransforms(final Envelope envelope,
			final org.eclipse.swt.graphics.Rectangle paintArea)
	{
		ReferencedEnvelope refEnv = null;
		if (envelope != null)
		{
			refEnv = new ReferencedEnvelope(envelope);
		}
		else
		{
			refEnv = worldEnvelope();
			// FIXME content.setCoordinateReferenceSystem(DefaultGeographicCRS.WGS84);
		}

		final java.awt.Rectangle awtPaintArea = Utils.toAwtRectangle(paintArea);
		final double xscale = awtPaintArea.getWidth() / refEnv.getWidth();
		final double yscale = awtPaintArea.getHeight() / refEnv.getHeight();

		final double scale = Math.min(xscale, yscale);

		final double xoff = refEnv.getMedian(0) * scale - awtPaintArea.getCenterX();
		final double yoff = refEnv.getMedian(1) * scale + awtPaintArea.getCenterY();

		worldToScreen = new AffineTransform(scale, 0, 0, -scale, -xoff, yoff);
		try
		{
			worldToScreen.createInverse();

		}
		catch (final NoninvertibleTransformException ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * Get a (copy of) the world to screen coordinate transform being used by this
	 * map pane. This method can be used to determine the current drawing scale...
	 * 
	 * <pre>
	 * {
	 * 	&#064;code
	 * 	double scale = mapPane.getWorldToScreenTransform().getScaleX();
	 * }
	 * </pre>
	 * 
	 * @return a copy of the world to screen coordinate transform
	 */
	private AffineTransform getWorldToScreenTransform()
	{
		if (worldToScreen != null)
		{
			return new AffineTransform(worldToScreen);
		}
		else
		{
			return null;
		}
	}

	/**
	 * keep a cached copy of the image - to reduce replotting time
	 */
	protected transient ImageData _myImageTemplate = null;

	protected org.eclipse.swt.graphics.Image createSWTImage(
			final ImageData myImageTemplate)
	{
		_myImageTemplate.transparentPixel = _myImageTemplate.palette
				.getPixel(new RGB(255, 255, 255));
		final org.eclipse.swt.graphics.Image image = new org.eclipse.swt.graphics.Image(
				Display.getCurrent(), _myImageTemplate);
		return image;
	}

	static ImageData convertToSWT(final BufferedImage bufferedImage)
	{
		if (bufferedImage.getColorModel() instanceof DirectColorModel)
		{
			final DirectColorModel colorModel = (DirectColorModel) bufferedImage
					.getColorModel();
			final PaletteData palette = new PaletteData(colorModel.getRedMask(),
					colorModel.getGreenMask(), colorModel.getBlueMask());
			final ImageData data = new ImageData(bufferedImage.getWidth(),
					bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			final WritableRaster raster = bufferedImage.getRaster();
			final int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++)
			{
				for (int x = 0; x < data.width; x++)
				{
					raster.getPixel(x, y, pixelArray);
					final int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1],
							pixelArray[2]));
					data.setPixel(x, y, pixel);
				}
			}
			return data;
		}
		else if (bufferedImage.getColorModel() instanceof IndexColorModel)
		{
			final IndexColorModel colorModel = (IndexColorModel) bufferedImage
					.getColorModel();
			final int size = colorModel.getMapSize();
			final byte[] reds = new byte[size];
			final byte[] greens = new byte[size];
			final byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			final RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++)
			{
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}
			final PaletteData palette = new PaletteData(rgbs);
			final ImageData data = new ImageData(bufferedImage.getWidth(),
					bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			final WritableRaster raster = bufferedImage.getRaster();
			final int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++)
			{
				for (int x = 0; x < data.width; x++)
				{
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		}
		return null;
	}

	private void contributeToActionBars()
	{
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(final IMenuManager manager)
	{
		manager.add(action1);
	}

	private void fillLocalToolBar(final IToolBarManager manager)
	{
		manager.add(action1);
	}

	private void makeActions()
	{
		action1 = new Action()
		{
			public void run()
			{
				// _chart.rescale();
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
	}
}