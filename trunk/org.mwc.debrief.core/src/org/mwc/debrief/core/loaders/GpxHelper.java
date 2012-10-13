package org.mwc.debrief.core.loaders;

import java.io.File;
import java.io.InputStream;

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
	 *          aggregate root
	 * @param saveToGpx
	 *          GPX file to save to.
	 */
	void marshall(Layers from, File saveToGpx);
}