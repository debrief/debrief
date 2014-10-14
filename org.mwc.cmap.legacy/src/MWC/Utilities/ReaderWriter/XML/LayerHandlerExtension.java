/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.Utilities.ReaderWriter.XML;

import MWC.GUI.Layer;
import MWC.GUI.Layers;

public interface LayerHandlerExtension
{
	/**
	 * store the layers object that we're working on
	 * 
	 * @param theLayers
	 */
	public void setLayers(Layers theLayers);

	/**
	 * indicate if this handler can export objects of this type
	 * 
	 * @param subject
	 * @return
	 */
	public boolean canExportThis(Layer subject);

	/**
	 * actually export this object
	 * 
	 * @param theLayer
	 * @param parent
	 * @param doc
	 */
	public void exportThis(Layer theLayer, org.w3c.dom.Element parent,
			org.w3c.dom.Document doc);

}
