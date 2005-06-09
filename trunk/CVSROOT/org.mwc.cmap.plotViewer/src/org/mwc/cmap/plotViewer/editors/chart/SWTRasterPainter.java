/**
 * 
 */
package org.mwc.cmap.plotViewer.editors.chart;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;

import MWC.GUI.CanvasType;
import MWC.GUI.Chart.Painters.SpatialRasterPainter;
import MWC.GUI.Chart.Painters.SpatialRasterPainter.PainterComponent;

/**
 * SWT-specific raster painter component - to handle SWT-type images
 * 
 * @author ian.mayo
 */
public class SWTRasterPainter extends PainterComponent
{
	/**
	 * the image we plot
	 */
	private ImageData _myImageBuffer;

	public static int toSWTColor(int r, int g, int b)
	{
		int res = b * 256 * 256 + g * 256 + r;
		return res;
	}

	private void test()
	{

		PaletteData palette = new PaletteData(0xFF, 0xFF00, 0xFF0000);
		ImageData imageData = new ImageData(48, 48, 24, palette);

		for (int x = 0; x < 48; x++)
		{
			for (int y = 0; y < 48; y++)
			{
				if (y > 11 && y < 35 && x > 11 && x < 35)
				{
					imageData.setPixel(x, y, toSWTColor(255, 0, 0)); // Set the center to
					// red
				}
				else
				{
					imageData.setPixel(x, y, toSWTColor(0, 255, 0)); // Set the outside
					// to green
				}
			}
		}
		;
		Image image = new Image(Display.getCurrent(), imageData);

	}

	/**
	 * check if we need to create or update our image
	 * 
	 * @param width
	 * @param height
	 */
	protected void checkImageValid(final int width, final int height)
	{
		if ((_myImageBuffer == null)
				|| ((_myImageBuffer.width != width) || (_myImageBuffer.height != height)))
		{
			PaletteData palette = new PaletteData(0xFF, 0xFF00, 0xFF0000);
			_myImageBuffer = new ImageData(width, height, 24, palette);
		}
	}

	/**
	 * ok, do the actual (system-specific) paint operation
	 * 
	 * @param dest
	 * @param width
	 * @param height
	 */
	protected void paintTheImage(final CanvasType dest, final int width,
			final int height)
	{
		Image image = new Image(Display.getCurrent(), _myImageBuffer);
		
		if(dest instanceof SWTCanvasAdapter)
		{
			SWTCanvasAdapter canvas = (SWTCanvasAdapter) dest;
			canvas.drawImage(image, 0, 0, width, height);
		}
	}

	/**
	 * set this pixel to the correct color
	 * 
	 * @param width
	 * @param thisValue
	 * @param x_coord
	 * @param y_coord
	 */
	protected void assignPixel(final int width, final int thisValue,
			final int x_coord, final int y_coord)
	{
		final int idx = (y_coord) * width + (x_coord);

		// put this elevation into our array
		_myImageBuffer.setPixel(x_coord, y_coord, thisValue);
	}

	/**
	 * pass through the array - switching the depth value to it's colour-coded
	 * equivalent
	 * 
	 * @param parent
	 * @param width
	 * @param height
	 * @param min_height
	 * @param max_height
	 */
	protected void updatePixelColors(SpatialRasterPainter parent,
			final int width, final int height, int min_height, int max_height)
	{
		// do a second pass to set the actual colours
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				int thisP = _myImageBuffer.getPixel(j, i);
				final int thisCol = parent.getColor(thisP, min_height, max_height);
				_myImageBuffer.setPixel(j, i, thisCol);
			}
		}
	}
}
