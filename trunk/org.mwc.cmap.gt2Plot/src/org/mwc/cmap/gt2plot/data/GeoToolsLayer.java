package org.mwc.cmap.gt2plot.data;

import java.io.File;

import org.geotools.map.Layer;
import org.geotools.map.MapContent;

import MWC.GUI.ExternallyManagedDataLayer;

public abstract class GeoToolsLayer extends ExternallyManagedDataLayer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** the map where we display ourselves
	 * 
	 */
	protected transient MapContent _myMap;
	
	/** the GeoTools representation of this object
	 * 
	 */
	protected transient Layer _myLayer;

	public GeoToolsLayer(String dataType, String layerName, String fileName)
	{
		super(dataType, layerName, fileName);
	}
	
	
	
	@Override
	protected void finalize() throws Throwable
	{
		// ok, ditch the layer
		if(_myLayer != null)
			_myLayer.dispose();
		
		// and let the parent do it's stuff
		super.finalize();
	}



	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		
		if(_myLayer != null)
			_myLayer.setVisible(visible);
	}



	/** forget about any existing map/layer
	 * 
	 */
	public void clearMap()
	{
		if (_myLayer != null)
		{
			// remove ourselves from the map
			if (_myMap != null)
			{
				_myMap.removeLayer(_myLayer);
			}
			_myLayer.dispose();
			_myLayer = null;
		}
		_myMap = null;
	}

	/** tell this layer where to plot itself
	 * 
	 * @param map
	 */
	public void setMap(MapContent map)
	{
		clearMap();

		// remember the map
		_myMap = map;

		// read ourselves in
		File openFile = new File(super.getFilename());
		if (openFile != null && openFile.exists())
		{
			_myLayer = loadLayer(openFile);
		}

		// ok, add ourselves to the map
		if (_myLayer != null)
		{
			// sort out the visibility
			_myLayer.setVisible(this.getVisible());

			_myLayer.setTitle(super.getName());
			_myMap.addLayer(_myLayer);
			
		}
	}

	public Layer getLayer()
	{
		return _myLayer;
	}
	
	/** load data from the specific file, return it as a GeoTools layer
	 * 
	 * @param openFile the file to open
	 * @return the layer to load into a GeoTools object
	 */
	abstract protected Layer loadLayer(File openFile);

}
