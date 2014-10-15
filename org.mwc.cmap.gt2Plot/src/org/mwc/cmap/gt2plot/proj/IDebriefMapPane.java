/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
