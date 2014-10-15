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
package org.mwc.debrief.core.loaders;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;

/**
 * Helper service for marshalling and unmarshalling the data to GPX format.
 * Debrief specific data not supported out of the box by GPX XSD will be saved
 * as part of <b>extensions</b> element.
 * 
 * @author Aravind R. Yarram <yaravind@gmail.com>
 * @date August 21, 2012
 * @category gpx
 */
public interface GpxHelper
{
	/**
	 * Can unmarshall gpx 1.0 and 1.1 versions
	 * 
	 * @param gpxStream
	 *          stream representing the gpx xml.
	 * @param theLayers
	 *          layers to which the tracks are added. A new Layers object will be
	 *          created if <code>theLayers</code> is null.
	 * @return aggregate root
	 */
	Layers unmarshall(InputStream gpxStream, Layers theLayers);

	/**
	 * Will marshall in gpx 1.0 version only
	 * 
	 * @param from
	 *          tracks to export
	 * @param saveToGpx
	 *          GPX file to save to.
	 */
	void marshall(List<TrackWrapper> tracks, File saveToGpx);
}