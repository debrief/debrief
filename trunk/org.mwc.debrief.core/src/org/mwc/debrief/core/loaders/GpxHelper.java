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
	 * @param gpxStream
	 *          stream representing the gpx xml.
	 * @return aggregate root
	 */
	Layers unmarshall(InputStream gpxStream);

	/**
	 * @param from
	 *          aggregate root
	 * @param saveToGpx
	 *          GPX file to save to.
	 */
	void marshall(Layers from, File saveToGpx);
}