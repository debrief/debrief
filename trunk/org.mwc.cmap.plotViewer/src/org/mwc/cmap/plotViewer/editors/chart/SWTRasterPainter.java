/**
 * 
 */
package org.mwc.cmap.plotViewer.editors.chart;

import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

import javax.swing.JButton;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;

import MWC.GUI.CanvasType;
import MWC.GUI.Canvas.MetafileCanvas;
import MWC.GUI.Chart.Painters.SpatialRasterPainter;
import MWC.GUI.Chart.Painters.SpatialRasterPainter.*;

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

	/**
	 * keep a list of depths (since we can't insert the depth into the image
	 * buffer
	 */
	private int[][] _depthData;

	private int[] _metafileBuffer;

	/**
	 * convert the color
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static int toSWTColor(int r, int g, int b)
	{
		int res = b * 256 * 256 + g * 256 + r;
		return res;
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
			
			// also create a buffer for generating a metafile - so that we 
			// write metafiles all the quicker.
			_metafileBuffer = new int[width * height];

			// also create the double bugger
			_depthData = new int[width][height];
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
		if(dest instanceof SWTCanvasAdapter)
		{			
			// cast the canvas - so that we can do an SWT draw image operation
			SWTCanvasAdapter canvas = (SWTCanvasAdapter) dest;

			// create our new image
			Image image = new Image(Display.getCurrent(), _myImageBuffer);

			// and draw it to the canvas
			canvas.drawImage(image, 0, 0, width, height);
			
			// we've created SWT image - we'd better delete it!
			image.dispose();
			image = null;
		}
		else if(dest instanceof MetafileCanvas)
		{
			// cast the canvas - so that we can do an SWT draw image operation
			MetafileCanvas canvas = (MetafileCanvas) dest;
			
			final MemoryImageSource mis = new MemoryImageSource(width, height,
					_metafileBuffer, 0, width);
			final java.awt.Image im = Toolkit.getDefaultToolkit().createImage(mis);

			// ok, actually draw the image
			canvas.drawImage(im, 0, 0, width, height, new JButton());		
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
		// put this depth datum into our depths list
		_depthData[x_coord][y_coord] = thisValue;			
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
			final int width, final int height, int min_height, int max_height, CanvasType dest)
	{
		ColorConverter theConverter;
		
		// decide which color converter to use.
		if(dest instanceof MetafileCanvas)
		{
			theConverter = new SwingColorConverter();
		}
		else
		{
			theConverter = this;
		}
		
		// do a second pass to set the actual colours
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
			{
				// retrieve this depth
				int thisD = _depthData[j][i];
				
				// convert the color
				int thisCol = parent.getColor(thisD, min_height, max_height, theConverter);
				
				// and place into the image
				_myImageBuffer.setPixel(j, i, thisCol);
				
				final int idx = i * width + j;				
				_metafileBuffer[idx] = thisCol;
			}
	}

	/** convert the three shades to an SWT color version
	 * 
	 * @param red 
	 * @param green
	 * @param blue
	 * @return SWT integer value for our color
	 */
	public int convertColor(int red, int green, int blue)
	{
		return toSWTColor(red, green, blue);
	}

}
