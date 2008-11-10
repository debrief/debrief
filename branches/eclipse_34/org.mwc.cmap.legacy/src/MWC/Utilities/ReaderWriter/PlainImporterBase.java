// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: PlainImporterBase.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.7 $
// $Log: PlainImporterBase.java,v $
// Revision 1.7  2007/06/01 13:46:06  ian.mayo
// Improve performance of export text to clipboard
//
// Revision 1.6  2006/05/24 14:46:02  Ian.Mayo
// Reflect change in exportThis method (return string exported)
//
// Revision 1.5  2005/05/18 09:13:51  Ian.Mayo
// Tidy javadoc
//
// Revision 1.4  2005/05/12 09:47:10  Ian.Mayo
// Refactor, to make it easier to over-ride
//
// Revision 1.3  2005/05/12 08:12:46  Ian.Mayo
// Lots of eclipse-style tidying, add handler for not able to find file in order to count lines
//
// Revision 1.2  2004/05/24 16:24:38  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:27  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:51  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:06+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:13:56+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:25+01  ian_mayo
// Initial revision
//
// Revision 1.3  2001-08-17 07:55:46+01  administrator
// Clear up memory leak
//
// Revision 1.2  2001-08-06 12:43:26+01  administrator
// Add method to count lines in file (used in support of progress monitor)
//
// Revision 1.1  2001-08-01 20:09:03+01  administrator
// Create object to return list of layers - hey, we may want to use it!
//
// Revision 1.0  2001-07-17 08:42:46+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:35+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:52:10  ianmayo
// initial version
//

package MWC.Utilities.ReaderWriter;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import MWC.GUI.*;

/**
 * class to provide general (non file-type specific) for importing a whole file.
 * The class performs layers management tasks
 */
public abstract class PlainImporterBase implements PlainImporter
{

	// //////////////////////////////////////
	// member variables
	// //////////////////////////////////////

	/**
	 * the layers object being created/extended in this process
	 */
	private Layers _theLayers;

	/**
	 * the suffixes which we import with this import manager (set by the
	 * instantiation)
	 */
	protected String[] _myTypes;

	/** the block of text we're collating
	 * 
	 */
	private StringBuffer _beingExported;

	// //////////////////////////////////////
	// member methods
	// //////////////////////////////////////

	/**
	 * general command used to import a whole file of a specific type
	 */
	public void importThis(String fName, java.io.InputStream is, Layers theData)
	{
		_theLayers = theData;
		importThis(fName, is);

		// ok, forget about the layers object now that we're finished
		_theLayers = null;
	}

	abstract public void importThis(String fName, java.io.InputStream is);

	/**
	 * create a new layer in the data using this name
	 * 
	 * @return the new layer
	 */
	public Layer createLayer(String theName)
	{
		Layer res = new BaseLayer();
		res.setName(theName);
		return res;
	}

	/**
	 * add the specified layer to our data
	 */
	public void addLayer(Layer theLayer)
	{
		// add it to the manager
		_theLayers.addThisLayer(theLayer);
	}

	/**
	 * retrieve the layer of the given name (and create it if necessary)
	 * 
	 * @return the requested layer, or null if not found
	 */
	public Layer getLayerFor(String theName)
	{
		Layer theLayer = _theLayers.findLayer(theName);

		return theLayer;
	}

	/**
	 * add the provided data item to the indicated layer, creating the layer if
	 * necessary
	 */
	public void addToLayer(Plottable theItem, Layer theLayer)
	{
		// add this item to the layer
		theLayer.add(theItem);
	}

	/**
	 * signal problem importing data
	 */
	public void readError(String fName, int line, String msg, String thisLine)
	{
		String res = "Problem reading in " + fName + " at line " + line;
		res = res + ", " + msg + ":" + thisLine;
		MWC.GUI.Dialogs.DialogFactory.showMessage("Import Error", res);
	}

	/**
	 * provide setter function for the layers object
	 */
	public void setLayers(Layers theData)
	{
		_theLayers = theData;
	}

	/**
	 * get the layers object we are editing
	 */
	protected Layers getLayers()
	{
		return _theLayers;
	}

	public int countLinesFor(String fName)
	{
		int counter = 0;
		try
		{

			// see if we can find the required file
			File findIt = new File(fName);
			if (findIt.exists())
			{
				java.io.InputStream is = new java.io.FileInputStream(fName);
				counter = countLinesInStream(is);
				is.close();
			}
			else
			{
				System.err.println("Can't find input file:" + fName);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return counter;
	}

	/**
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public int countLinesInStream(java.io.InputStream is) throws IOException
	{
		int counter = 0;
		java.io.InputStreamReader ir = new java.io.InputStreamReader(is);
		java.io.BufferedReader br = new java.io.BufferedReader(ir);
		String line = br.readLine();
		while (line != null)
		{
			counter++;
			line = br.readLine();
		}
		br.close();
		ir.close();
		return counter;
	}

	/** get everything ready for the export
	 * 
	 * @param item
	 */
	public void startExport(Plottable item)
	{
		java.awt.datatransfer.Clipboard cl = java.awt.Toolkit.getDefaultToolkit()
				.getSystemClipboard();
	
		// see if there is currently text on the cliboard
		// since we will 'add' rather than 'replace' it if so.
		java.awt.datatransfer.Transferable tr = cl.getContents(this);
	
		// was there anything on the clipboard?
		if (tr != null)
		{
			if (tr.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor))
			{
				try
				{
					// get the current contents
					Object oj = tr.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
					_beingExported = new StringBuffer((String) oj);
				}
				catch (Exception e)
				{
					MWC.Utilities.Errors.Trace.trace(e);
				}
			}
			else
			{
				// don't bother, lets just put the string on the clipboard
			}
		}
	}

	/**
	 * ok, we've build up our string to export. put it on the clipboard
	 * 
	 * @param item
	 */
	public void endExport(Plottable item)
	{
		// get the clipboard;
		java.awt.datatransfer.Clipboard cl = Toolkit.getDefaultToolkit().getSystemClipboard();
	
		// create the string to write
		java.awt.datatransfer.StringSelection ss = new java.awt.datatransfer.StringSelection(
				_beingExported.toString());
		
		// dump it on there.
		cl.setContents(ss, ss);
	}

	/** append this line to what we're building up
	 * 
	 * @param txt
	 */
	protected void addThisToExport(String txt)
	{	
		// initialise the export string?
		if(_beingExported == null)
			_beingExported = new StringBuffer();
		else
			_beingExported.append('\n');
		
		_beingExported.append(txt);
	}
}
