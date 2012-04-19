package org.mwc.cmap.gt2plot.proj;

import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;

import org.eclipse.swt.graphics.Rectangle;
import org.geotools.map.MapContent;
import org.geotools.swt.event.MapPaneListener;
import org.opengis.geometry.Envelope;

/** isolate the interface that the SwtMapPane provides which goes beyond a plain Canvas control
 * 
 * @author ian
 *
 */
public interface IDebriefMapPane
{
	public MapContent getMapContent();

	public void setDisplayArea(Envelope env);

	public RenderedImage getBaseImage();

	public Rectangle getBounds();

	public void addMapPaneListener(MapPaneListener mapPaneListener);

	/** provide easy access to the screen -> world transform
	 * 
	 * @return
	 */
	public AffineTransform getScreenToWorldTransform();
	
	/** provide easy access to the world -> screen transform
	 * 
	 * @return
	 */
	public AffineTransform getWorldToScreenTransform();


}
