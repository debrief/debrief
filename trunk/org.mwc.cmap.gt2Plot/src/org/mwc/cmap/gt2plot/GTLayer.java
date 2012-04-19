package org.mwc.cmap.gt2plot;

import org.eclipse.swt.graphics.Image;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvasAdapter;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;

public class GTLayer extends BaseLayer
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void paint(CanvasType dest)
	{
		if (dest instanceof SWTCanvasAdapter)
		{
			// right, I presume this is a SWT canvas
			SWTCanvasAdapter swt = (SWTCanvasAdapter) dest;

			// sort out the current dimensions
			int width = dest.getSize().width;
			int height = dest.getSize().height;

			// and what's the current perspective?
			PlainProjection proj = dest.getProjection();

			// right, is this a GeoTools compliant one?

			// draw ourselves to an image

			// and draw the image to the canvas
			Image theImage = null;
			if (theImage != null)
			{
				swt.drawSWTImage(theImage, 0, 0, width, height);
			}

		}
	}

}
