package org.mwc.cmap.naturalearth;

import java.io.File;
import java.util.HashMap;

import org.mwc.cmap.naturalearth.data.CachedNaturalEarthFile;

public class ShapefileDataStore
{
	// our cached data
	private HashMap<String, CachedNaturalEarthFile> _store = new HashMap<String, CachedNaturalEarthFile>();
	
	// where to find our data
	private String _path;
	
	
	/** assign the current data folder
	 * 
	 * @param path
	 */
	public void setPath(final String path)
	{
		if(path != _path)
		{
			// ok, clear out the store - we've got a new data root
			_store.clear();
		}
		
		_path = path;
	}


	/** return the cached data from the named file (loading it if necessary)
	 * 
	 * @param fName
	 * @return
	 */
	public CachedNaturalEarthFile get(String fName)
	{
		// construct the path to the file
		String path = _path + File.separator + fName + File.separator + fName + ".shp";
		
		CachedNaturalEarthFile res = _store.get(path);
		
		// do we still need to load it?
		if(res == null)
		{
			// ok, better load it
			res = new CachedNaturalEarthFile(path);
			
			// and remember it.
			_store.put(fName, res);
		}
		
		return res;
	}
}
