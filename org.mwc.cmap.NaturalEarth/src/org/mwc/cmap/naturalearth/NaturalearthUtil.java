package org.mwc.cmap.naturalearth;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.AnchorPoint;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Font;
import org.geotools.styling.Graphic;
import org.geotools.styling.Halo;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointPlacement;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.FilterFactory;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class NaturalearthUtil
{
	static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
  static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();

	public static void addLabelStyle(Style sld, NEFeatureStyle st)
	{
		// "labelPoint" feature type style
		StyleBuilder sb = new StyleBuilder();
		//FilterFactory2 ff = sb.getFilterFactory();

		// creation of the TextSymbolizer
		AnchorPoint anchorPoint = sb.createAnchorPoint(sb.attributeExpression("X"),
				sb.attributeExpression("Y"));
		PointPlacement pointPlacement = sb.createPointPlacement(anchorPoint, null,
				sb.literalExpression(0));
		TextSymbolizer textSymbolizer = sb.createTextSymbolizer(
				sb.createFill(Color.BLACK), new org.geotools.styling.Font[]
				{ sb.createFont("Lucida Sans", 10), sb.createFont("Arial", 10) },
				sb.createHalo(), sb.attributeExpression("name"), pointPlacement, null);

		// creation of the Point symbolizer
		Mark circle = sb.createMark(StyleBuilder.MARK_CIRCLE, Color.RED);
		Graphic graph2 = sb.createGraphic(null, circle, null, 1, 4, 0);
		PointSymbolizer pointSymbolizer = sb.createPointSymbolizer(graph2);

		// creation of the style

		FeatureTypeStyle featureTypeStyle = sb.createFeatureTypeStyle("labelPoint",
				new Symbolizer[]
				{ textSymbolizer, pointSymbolizer });
		sld.featureTypeStyles().add(featureTypeStyle);
	}

	@SuppressWarnings("rawtypes")
	public static Style createStyle2(FeatureSource featureSource)
	{
		SimpleFeatureType schema = (SimpleFeatureType) featureSource.getSchema();
		Class geomType = schema.getGeometryDescriptor().getType().getBinding();

		if (Polygon.class.isAssignableFrom(geomType)
				|| MultiPolygon.class.isAssignableFrom(geomType))
		{
			return createPolygonStyle(featureSource);

		}
		else if (LineString.class.isAssignableFrom(geomType)
				|| MultiLineString.class.isAssignableFrom(geomType))
		{
			return createLineStyle(featureSource);

		}
		else
		{
			return createPointStyle(featureSource);
		}
	}

	/**
	 * Create a Style to draw polygon features 
	 * fill
	 * @param featureSource 
	 */
	@SuppressWarnings("rawtypes")
	public static Style createPolygonStyle(FeatureSource featureSource)
	{

		// create a partially opaque outline stroke
		Color lineColor = null;
		Stroke stroke = null;
		
		stroke = styleFactory.createStroke(
					filterFactory.literal(lineColor), filterFactory.literal(1),
					filterFactory.literal(0.5));

		// create a partial opaque fill
		Fill fill = styleFactory
				.createFill(filterFactory.literal(Color.GRAY),
						filterFactory.literal(0.5));

		/*
		 * Setting the geometryPropertyName arg to null signals that we want to draw
		 * the default geomettry of features
		 */
		PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill,
				null);

		Rule rule = styleFactory.createRule();
		rule.symbolizers().add(sym);
		FeatureTypeStyle fts = null;
		PropertyDescriptor propertyDescriptor = featureSource.getSchema()
				.getDescriptor("name");
		if (propertyDescriptor != null)
		{
			// return
			// loadStyle("/home/snpe/Workspaces/workspace-debrief/tutorial/polygon_polygonwithstyledlabel.sld");
			StyleBuilder sb = new StyleBuilder();
			AnchorPoint anchorPoint = sb.createAnchorPoint(0.5, 0.5);
			PointPlacement pointPlacement = sb.createPointPlacement(anchorPoint,
					null, sb.literalExpression(0));
			Rule textRule = createTextRule(sb, pointPlacement);
			fts = styleFactory.createFeatureTypeStyle(new Rule[]
			{ rule, textRule });
		}
		if (fts == null)
		{
			fts = styleFactory.createFeatureTypeStyle(new Rule[] { rule });
		}
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);

		return style;
	}

	private static Rule createTextRule(StyleBuilder sb,
			PointPlacement pointPlacement)
	{
		int txtHeight = 10;
		boolean italic = false;
		boolean bold = false;
		Font font = sb.createFont("Arial", italic, bold,
				txtHeight);
		Halo halo = null; // sb.createHalo();
		Fill fill = styleFactory.createFill(filterFactory.literal(Color.BLACK),
					filterFactory.literal(0.5));
		TextSymbolizer textSymbolizer = sb.createTextSymbolizer(
						fill, 
						new org.geotools.styling.Font[] { font },
						halo, sb.attributeExpression("name"),
						pointPlacement, null);
		textSymbolizer.getOptions().put("maxDisplacement", "150");
		textSymbolizer.getOptions().put("autoWrap", "60");
		Rule rule = styleFactory.createRule();
		rule.symbolizers().add(textSymbolizer);
		return rule;
	}

	/**
	 * Create a Style to draw line features 
	 * @param featureSource 
	 */
	@SuppressWarnings("rawtypes")
	public static Style createLineStyle(FeatureSource featureSource)
	{
		Stroke stroke = styleFactory.createStroke(
				filterFactory.literal(Color.LIGHT_GRAY), filterFactory.literal(1));

		/*
		 * Setting the geometryPropertyName arg to null signals that we want to draw
		 * the default geomettry of features
		 */
		LineSymbolizer sym = styleFactory.createLineSymbolizer(stroke, null);

		Rule rule = styleFactory.createRule();
		rule.symbolizers().add(sym);
		FeatureTypeStyle fts = null;
		PropertyDescriptor propertyDescriptor = featureSource.getSchema()
				.getDescriptor("name");
		if (propertyDescriptor != null)
		{
			StyleBuilder sb = new StyleBuilder();
			AnchorPoint anchorPoint = sb.createAnchorPoint(0.5, 0.5);
			PointPlacement pointPlacement = sb.createPointPlacement(anchorPoint,
					null, sb.literalExpression(0));
			Rule textRule = createTextRule(sb, pointPlacement);
			fts = styleFactory.createFeatureTypeStyle(new Rule[]
			{ rule, textRule });
		}
		if (fts == null)
		{
			fts = styleFactory.createFeatureTypeStyle(new Rule[] { rule });
		}
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);

		return style;
	}

	/**
	 * Create a Style to draw point features as circles with blue outlines and
	 * cyan fill
	 * @param featureSource 
	 */
	@SuppressWarnings("rawtypes")
	public static Style createPointStyle(FeatureSource featureSource)
	{
		Graphic gr = styleFactory.createDefaultGraphic();
		Mark mark = styleFactory.getCircleMark();
		mark.setStroke(styleFactory.createStroke(
				filterFactory.literal(Color.BLUE), filterFactory.literal(1)));
		mark.setFill(styleFactory.createFill(filterFactory.literal(Color.CYAN)));

		gr.graphicalSymbols().clear();
		gr.graphicalSymbols().add(mark);
		gr.setSize(filterFactory.literal(5));

		PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, null);

		Rule rule = styleFactory.createRule();
		rule.symbolizers().add(sym);
		FeatureTypeStyle fts = null;
		PropertyDescriptor propertyDescriptor = featureSource.getSchema()
				.getDescriptor("name");
		if (propertyDescriptor != null)
		{
			StyleBuilder sb = new StyleBuilder();
			AnchorPoint anchorPoint = sb.createAnchorPoint(0.5, 0.5);
			PointPlacement pointPlacement = sb.createPointPlacement(anchorPoint,
					null, sb.literalExpression(0));
			Rule textRule = createTextRule(sb, pointPlacement);
			fts = styleFactory.createFeatureTypeStyle(new Rule[]
			{ rule, textRule });
		}
		if (fts == null)
		{
			fts = styleFactory.createFeatureTypeStyle(new Rule[] { rule });
		}
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);
		return style;
	}

	public static ImageData awtToSwt(BufferedImage bufferedImage, int width, int height)
	{
		final int[] awtPixels = new int[width * height];
		final PaletteData PALETTE_DATA = new PaletteData(0xFF0000, 0xFF00, 0xFF);
		final int TRANSPARENT_COLOR = 0x123456;
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

	public static ImageData createImageData(RenderedImage image,
			boolean transparent)
	{
		ImageData swtdata = null;
		int width = image.getWidth();
		int height = image.getHeight();
		PaletteData palette;
		int depth;

		depth = 24;
		palette = new PaletteData(0xFF, 0xFF00, 0xFF0000);
		swtdata = new ImageData(width, height, depth, palette);
		Raster raster = image.getData();
		int numbands = raster.getNumBands();
		int[] awtdata = raster.getPixels(0, 0, width, height, new int[width
				* height * numbands]);
		int step = swtdata.depth / 8;

		byte[] data = swtdata.data;
		swtdata.transparentPixel = -1;
		int baseindex = 0;
		for (int y = 0; y < height; y++)
		{
			int idx = ((0 + y) * swtdata.bytesPerLine) + (0 * step);

			for (int x = 0; x < width; x++)
			{
				int pixel = (x + (y * width));
				baseindex = pixel * numbands;

				data[idx++] = (byte) awtdata[baseindex + 2];
				data[idx++] = (byte) awtdata[baseindex + 1];
				data[idx++] = (byte) awtdata[baseindex];
				if (numbands == 4 && transparent)
				{
					swtdata.setAlpha(x, y, awtdata[baseindex + 3]);
				}
			}
		}
		return swtdata;
	}

	public static ImageData convertToSWT(BufferedImage bufferedImage)
	{
		if (bufferedImage.getColorModel() instanceof DirectColorModel)
		{
			DirectColorModel colorModel = (DirectColorModel) bufferedImage
					.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(),
					colorModel.getGreenMask(), colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(),
					bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			for (int y = 0; y < data.height; y++)
			{
				for (int x = 0; x < data.width; x++)
				{
					int rgb = bufferedImage.getRGB(x, y);
					int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF,
							(rgb >> 8) & 0xFF, rgb & 0xFF));
					data.setPixel(x, y, pixel);
					if (colorModel.hasAlpha())
					{
						data.setAlpha(x, y, (rgb >> 24) & 0xFF);
					}
				}
			}
			return data;
		}
		else if (bufferedImage.getColorModel() instanceof IndexColorModel)
		{
			IndexColorModel colorModel = (IndexColorModel) bufferedImage
					.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++)
			{
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}
			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth(),
					bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
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

	public static Style loadStyle(String fileName) {
		File file = new File(fileName);
		SLDParser stylereader;
		try
		{
			stylereader = new SLDParser(styleFactory, file.toURI().toURL());
			Style[] style = stylereader.readXML();
	    return style[0];
		}
		catch (MalformedURLException e)
		{
			Activator.logError(IStatus.WARNING, "Invalid file: " + fileName, e);
		}
		catch (IOException e)
		{
			Activator.logError(IStatus.WARNING, "Invalid file: " + fileName, e);
		}
    return null;
	}
}
