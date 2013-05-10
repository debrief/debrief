package org.mwc.debrief.core.loaders.xml_handlers;

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
