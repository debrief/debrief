/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
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
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.geotools.xml.styling.SLDParser;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.FilterFactory;

public class NaturalearthUtil {
	static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
	static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();

	public static void addLabelStyle(final Style sld, final NEFeatureStyle st) {
		// "labelPoint" feature type style
		final StyleBuilder sb = new StyleBuilder();
		// FilterFactory2 ff = sb.getFilterFactory();

		// creation of the TextSymbolizer
		final AnchorPoint anchorPoint = sb.createAnchorPoint(sb.attributeExpression("X"), sb.attributeExpression("Y"));
		final PointPlacement pointPlacement = sb.createPointPlacement(anchorPoint, null, sb.literalExpression(0));
		final TextSymbolizer textSymbolizer = sb.createTextSymbolizer(sb.createFill(Color.BLACK),
				new org.geotools.styling.Font[] { sb.createFont("Lucida Sans", 10), sb.createFont("Arial", 10) },
				sb.createHalo(), sb.attributeExpression("name"), pointPlacement, null);

		// creation of the Point symbolizer
		final Mark circle = sb.createMark(StyleBuilder.MARK_CIRCLE, Color.RED);
		final Graphic graph2 = sb.createGraphic(null, circle, null, 1, 4, 0);
		final PointSymbolizer pointSymbolizer = sb.createPointSymbolizer(graph2);

		// creation of the style

		final FeatureTypeStyle featureTypeStyle = sb.createFeatureTypeStyle("labelPoint",
				new Symbolizer[] { textSymbolizer, pointSymbolizer });
		sld.featureTypeStyles().add(featureTypeStyle);
	}

	public static ImageData awtToSwt(final BufferedImage bufferedImage, final int width, final int height) {
		final int[] awtPixels = new int[width * height];
		final PaletteData PALETTE_DATA = new PaletteData(0xFF0000, 0xFF00, 0xFF);
		final int TRANSPARENT_COLOR = 0x123456;
		final ImageData swtImageData = new ImageData(width, height, 24, PALETTE_DATA);
		swtImageData.transparentPixel = TRANSPARENT_COLOR;
		final int step = swtImageData.depth / 8;
		final byte[] data = swtImageData.data;
		bufferedImage.getRGB(0, 0, width, height, awtPixels, 0, width);
		for (int i = 0; i < height; i++) {
			int idx = (0 + i) * swtImageData.bytesPerLine + 0 * step;
			for (int j = 0; j < width; j++) {
				final int rgb = awtPixels[j + i * width];
				for (int k = swtImageData.depth - 8; k >= 0; k -= 8) {
					data[idx++] = (byte) ((rgb >> k) & 0xFF);
				}
			}
		}

		return swtImageData;
	}

	public static ImageData convertToSWT(final BufferedImage bufferedImage) {
		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			final DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
			final PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(),
					colorModel.getBlueMask());
			final ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					final int rgb = bufferedImage.getRGB(x, y);
					final int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF));
					data.setPixel(x, y, pixel);
					if (colorModel.hasAlpha()) {
						data.setAlpha(x, y, (rgb >> 24) & 0xFF);
					}
				}
			}
			return data;
		} else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
			final IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
			final int size = colorModel.getMapSize();
			final byte[] reds = new byte[size];
			final byte[] greens = new byte[size];
			final byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			final RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}
			final PaletteData palette = new PaletteData(rgbs);
			final ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			final WritableRaster raster = bufferedImage.getRaster();
			final int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		}
		return null;
	}

	public static ImageData createImageData(final RenderedImage image, final boolean transparent) {
		ImageData swtdata = null;
		final int width = image.getWidth();
		final int height = image.getHeight();
		PaletteData palette;
		int depth;

		depth = 24;
		palette = new PaletteData(0xFF, 0xFF00, 0xFF0000);
		swtdata = new ImageData(width, height, depth, palette);
		final Raster raster = image.getData();
		final int numbands = raster.getNumBands();
		final int[] awtdata = raster.getPixels(0, 0, width, height, new int[width * height * numbands]);
		final int step = swtdata.depth / 8;

		final byte[] data = swtdata.data;
		swtdata.transparentPixel = -1;
		int baseindex = 0;
		for (int y = 0; y < height; y++) {
			int idx = ((0 + y) * swtdata.bytesPerLine) + (0 * step);

			for (int x = 0; x < width; x++) {
				final int pixel = (x + (y * width));
				baseindex = pixel * numbands;

				data[idx++] = (byte) awtdata[baseindex + 2];
				data[idx++] = (byte) awtdata[baseindex + 1];
				data[idx++] = (byte) awtdata[baseindex];
				if (numbands == 4 && transparent) {
					swtdata.setAlpha(x, y, awtdata[baseindex + 3]);
				}
			}
		}
		return swtdata;
	}

	/**
	 * Create a Style to draw line features
	 *
	 * @param featureSource
	 */
	@SuppressWarnings("rawtypes")
	public static Style createLineStyle(final FeatureSource featureSource) {
		final Stroke stroke = styleFactory.createStroke(filterFactory.literal(Color.LIGHT_GRAY),
				filterFactory.literal(1));

		/*
		 * Setting the geometryPropertyName arg to null signals that we want to draw the
		 * default geomettry of features
		 */
		final LineSymbolizer sym = styleFactory.createLineSymbolizer(stroke, null);

		final Rule rule = styleFactory.createRule();
		rule.symbolizers().add(sym);
		FeatureTypeStyle fts = null;
		final PropertyDescriptor propertyDescriptor = featureSource.getSchema().getDescriptor("name");
		if (propertyDescriptor != null) {
			final StyleBuilder sb = new StyleBuilder();
			final AnchorPoint anchorPoint = sb.createAnchorPoint(0.5, 0.5);
			final PointPlacement pointPlacement = sb.createPointPlacement(anchorPoint, null, sb.literalExpression(0));
			final Rule textRule = createTextRule(sb, pointPlacement);
			fts = styleFactory.createFeatureTypeStyle(new Rule[] { rule, textRule });
		}
		if (fts == null) {
			fts = styleFactory.createFeatureTypeStyle(new Rule[] { rule });
		}
		final Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);

		return style;
	}

	/**
	 * Create a Style to draw point features as circles with blue outlines and cyan
	 * fill
	 *
	 * @param featureSource
	 */
	@SuppressWarnings("rawtypes")
	public static Style createPointStyle(final FeatureSource featureSource) {
		final Graphic gr = styleFactory.createDefaultGraphic();
		final Mark mark = styleFactory.getCircleMark();
		mark.setStroke(styleFactory.createStroke(filterFactory.literal(Color.BLUE), filterFactory.literal(1)));
		mark.setFill(styleFactory.createFill(filterFactory.literal(Color.CYAN)));

		gr.graphicalSymbols().clear();
		gr.graphicalSymbols().add(mark);
		gr.setSize(filterFactory.literal(5));

		final PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, null);

		final Rule rule = styleFactory.createRule();
		rule.symbolizers().add(sym);
		FeatureTypeStyle fts = null;
		final PropertyDescriptor propertyDescriptor = featureSource.getSchema().getDescriptor("name");
		if (propertyDescriptor != null) {
			final StyleBuilder sb = new StyleBuilder();
			final AnchorPoint anchorPoint = sb.createAnchorPoint(0.5, 0.5);
			final PointPlacement pointPlacement = sb.createPointPlacement(anchorPoint, null, sb.literalExpression(0));
			final Rule textRule = createTextRule(sb, pointPlacement);
			fts = styleFactory.createFeatureTypeStyle(new Rule[] { rule, textRule });
		}
		if (fts == null) {
			fts = styleFactory.createFeatureTypeStyle(new Rule[] { rule });
		}
		final Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);
		return style;
	}

	/**
	 * Create a Style to draw polygon features fill
	 *
	 * @param featureSource
	 */
	@SuppressWarnings("rawtypes")
	public static Style createPolygonStyle(final FeatureSource featureSource) {

		// create a partially opaque outline stroke
		final Color lineColor = null;
		Stroke stroke = null;

		stroke = styleFactory.createStroke(filterFactory.literal(lineColor), filterFactory.literal(1),
				filterFactory.literal(0.5));

		// create a partial opaque fill
		final Fill fill = styleFactory.createFill(filterFactory.literal(Color.GRAY), filterFactory.literal(0.5));

		/*
		 * Setting the geometryPropertyName arg to null signals that we want to draw the
		 * default geomettry of features
		 */
		final PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);

		final Rule rule = styleFactory.createRule();
		rule.symbolizers().add(sym);
		FeatureTypeStyle fts = null;
		final PropertyDescriptor propertyDescriptor = featureSource.getSchema().getDescriptor("name");
		if (propertyDescriptor != null) {
			// return
			// loadStyle("/home/snpe/Workspaces/workspace-debrief/tutorial/polygon_polygonwithstyledlabel.sld");
			final StyleBuilder sb = new StyleBuilder();
			final AnchorPoint anchorPoint = sb.createAnchorPoint(0.5, 0.5);
			final PointPlacement pointPlacement = sb.createPointPlacement(anchorPoint, null, sb.literalExpression(0));
			final Rule textRule = createTextRule(sb, pointPlacement);
			fts = styleFactory.createFeatureTypeStyle(new Rule[] { rule, textRule });
		}
		if (fts == null) {
			fts = styleFactory.createFeatureTypeStyle(new Rule[] { rule });
		}
		final Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);

		return style;
	}

	@SuppressWarnings("rawtypes")
	public static Style createStyle2(final FeatureSource featureSource) {
		final SimpleFeatureType schema = (SimpleFeatureType) featureSource.getSchema();
		final Class geomType = schema.getGeometryDescriptor().getType().getBinding();

		if (Polygon.class.isAssignableFrom(geomType) || MultiPolygon.class.isAssignableFrom(geomType)) {
			return createPolygonStyle(featureSource);

		} else if (LineString.class.isAssignableFrom(geomType) || MultiLineString.class.isAssignableFrom(geomType)) {
			return createLineStyle(featureSource);

		} else {
			return createPointStyle(featureSource);
		}
	}

	private static Rule createTextRule(final StyleBuilder sb, final PointPlacement pointPlacement) {
		final int txtHeight = 10;
		final boolean italic = false;
		final boolean bold = false;
		final Font font = sb.createFont("Arial", italic, bold, txtHeight);
		final Halo halo = null; // sb.createHalo();
		final Fill fill = styleFactory.createFill(filterFactory.literal(Color.BLACK), filterFactory.literal(0.5));
		final TextSymbolizer textSymbolizer = sb.createTextSymbolizer(fill, new org.geotools.styling.Font[] { font },
				halo, sb.attributeExpression("name"), pointPlacement, null);
		textSymbolizer.getOptions().put("maxDisplacement", "150");
		textSymbolizer.getOptions().put("autoWrap", "60");
		final Rule rule = styleFactory.createRule();
		rule.symbolizers().add(textSymbolizer);
		return rule;
	}

	public static Style loadStyle(final String fileName) {
		final File file = new File(fileName);
		SLDParser stylereader;
		try {
			stylereader = new SLDParser(styleFactory, file.toURI().toURL());
			final Style[] style = stylereader.readXML();
			return style[0];
		} catch (final MalformedURLException e) {
			Activator.logError(IStatus.WARNING, "Invalid file: " + fileName, e);
		} catch (final IOException e) {
			Activator.logError(IStatus.WARNING, "Invalid file: " + fileName, e);
		}
		return null;
	}
}
