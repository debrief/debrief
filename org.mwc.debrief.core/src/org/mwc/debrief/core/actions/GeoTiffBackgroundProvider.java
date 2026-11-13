/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2026, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.core.actions;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.PlanarImage;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import Debrief.ReaderWriter.GeoPDF.AbstractGeoPDFBuilder.GeoPDFConfiguration.BackgroundProvider;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFRaster;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * Prepares a GeoTIFF background chart for a GeoPDF: reprojects it to WGS84
 * lat/long and saves it as a PNG. This does what
 * {@code gdalwarp -t_srs EPSG:4326 -r near} did when GeoPDF export used GDAL:
 * square lat/long pixels over the chart's footprint, each taking the nearest
 * chart pixel, and 0 (black) outside the chart.
 */
public class GeoTiffBackgroundProvider implements BackgroundProvider {

	/** points per edge used when transforming the chart's outline to lat/long */
	private static final int EDGE_DENSIFY_POINTS = 20;

	/**
	 * reproject an image. Exposed for testing.
	 *
	 * @param source      the chart image
	 * @param gridToCrs   pixel corner to chart coordinates
	 * @param sourceCrs   the chart's coordinate system
	 * @param result      receives the lat/long bounds of the image returned
	 * @return the image on a lat/long grid
	 */
	static BufferedImage reproject(final BufferedImage source, final AffineTransform gridToCrs,
			final CoordinateReferenceSystem sourceCrs, final double[] result)
			throws FactoryException, TransformException, NoninvertibleTransformException {
		final int sw = source.getWidth();
		final int sh = source.getHeight();

		// the chart's footprint in lat/long, following its (densified) outline
		final ReferencedEnvelope footprint = new ReferencedEnvelope(
				gridToCrs.createTransformedShape(new java.awt.Rectangle(0, 0, sw, sh)).getBounds2D(), sourceCrs)
						.transform(DefaultGeographicCRS.WGS84, true, EDGE_DENSIFY_POINTS);
		final double lonMin = footprint.getMinX();
		final double latMax = footprint.getMaxY();

		// square pixels, with about as many as the chart has, as gdalwarp chooses
		final double res = Math.sqrt(footprint.getWidth() * footprint.getHeight() / ((double) sw * sh));
		final int tw = Math.max(1, (int) Math.round(footprint.getWidth() / res));
		final int th = Math.max(1, (int) Math.round(footprint.getHeight() / res));

		final MathTransform toChart = CRS.findMathTransform(DefaultGeographicCRS.WGS84, sourceCrs, true);
		final AffineTransform crsToGrid = gridToCrs.createInverse();

		final ColorModel cm = source.getColorModel();
		final WritableRaster target = cm.createCompatibleWritableRaster(tw, th);
		final Raster src = source.getRaster();
		final double[] row = new double[tw * 2];
		final double[] pixel = new double[2];
		Object element = null;
		for (int y = 0; y < th; y++) {
			final double lat = latMax - (y + 0.5) * res;
			for (int x = 0; x < tw; x++) {
				row[2 * x] = lonMin + (x + 0.5) * res;
				row[2 * x + 1] = lat;
			}
			toChart.transform(row, 0, row, 0, tw);
			for (int x = 0; x < tw; x++) {
				crsToGrid.transform(row, 2 * x, pixel, 0, 1);
				final int sx = (int) Math.floor(pixel[0]);
				final int sy = (int) Math.floor(pixel[1]);
				if (sx >= 0 && sy >= 0 && sx < sw && sy < sh) {
					element = src.getDataElements(sx, sy, element);
					target.setDataElements(x, y, element);
				}
			}
		}
		result[0] = lonMin;
		result[1] = latMax - th * res;
		result[2] = lonMin + tw * res;
		result[3] = latMax;
		return new BufferedImage(cm, target, cm.isAlphaPremultiplied(), null);
	}

	@Override
	public GeoPDFRaster prepare(final String backgroundFile, final File tempFolder) throws IOException {
		final File sourceFile = new File(backgroundFile);
		final GeoTiffReader reader = new GeoTiffReader(sourceFile);
		try {
			final GridCoverage2D coverage = reader.read(null);
			final RenderedImage rendered = coverage.getRenderedImage();
			final BufferedImage source = rendered instanceof BufferedImage ? (BufferedImage) rendered
					: PlanarImage.wrapRenderedImage(rendered).getAsBufferedImage();
			final AffineTransform gridToCrs = (AffineTransform) coverage.getGridGeometry()
					.getGridToCRS(PixelInCell.CELL_CORNER);

			final double[] bounds = new double[4];
			final BufferedImage geographic = reproject(source, gridToCrs, coverage.getCoordinateReferenceSystem2D(),
					bounds);

			tempFolder.mkdirs();
			final String name = sourceFile.getName().replaceAll("\\.[^.]*$", "");
			final File png = File.createTempFile(name + "_wgs84_", ".png", tempFolder);
			if (!ImageIO.write(geographic, "png", png)) {
				throw new IOException("Can't write background image " + png);
			}
			final WorldArea area = new WorldArea(new WorldLocation(bounds[1], bounds[0], 0),
					new WorldLocation(bounds[3], bounds[2], 0));
			return new GeoPDFRaster(png, area);
		} catch (FactoryException | TransformException | NoninvertibleTransformException e) {
			throw new IOException("Can't reproject background " + backgroundFile + ": " + e.getMessage(), e);
		} finally {
			reader.dispose();
		}
	}
}
