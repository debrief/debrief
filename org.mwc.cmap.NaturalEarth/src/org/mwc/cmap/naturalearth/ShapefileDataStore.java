package org.mwc.cmap.naturalearth;

import java.io.File;
import java.util.HashMap;

import org.mwc.cmap.naturalearth.wrapper.CachedShapefile;

public class ShapefileDataStore
{
	// our cached data
	private HashMap<String, CachedShapefile> _store = new HashMap<String, CachedShapefile>();
	
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
	public CachedShapefile get(String fName)
	{
		// construct the path to the file
		String path = _path + File.pathSeparator + fName;
		
		CachedShapefile res = _store.get(path);
		
		// do we still need to load it?
		if(res == null)
		{
			// ok, better load it
			res = new CachedShapefile(fName);
			
			// and remember it.
			_store.put(fName, res);
		}
		
		return res;
	}
}
