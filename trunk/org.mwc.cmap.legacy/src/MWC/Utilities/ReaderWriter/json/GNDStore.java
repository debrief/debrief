package MWC.Utilities.ReaderWriter.json;

import java.net.URL;

public class GNDStore
{
	public GNDStore(URL url, String databaseName)
	{
	}
	
	protected void checkConnected()
	{
		// connect to the store
		
		// connect to the database
		
	}
	
	public void close()
	{
		// close teh connection
	}
	
	public void put(GNDDocHandler.GNDDoc doc)
	{
		// are we connected?
		checkConnected();
		
		// do the put
	}
	
	public GNDDocHandler.GNDDoc get(String name)
	{
		// are we connected?
		checkConnected();
		
		// do the get
		return null;
	}
}
