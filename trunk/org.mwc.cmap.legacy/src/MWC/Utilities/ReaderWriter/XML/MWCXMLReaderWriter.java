/*
 * XMLReaderWriter.java
 *
 * Created on 04 October 2000, 11:32
 */

package MWC.Utilities.ReaderWriter.XML;

import java.io.InputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.awt.*;

import org.xml.sax.*;
import org.apache.xerces.parsers.SAXParser;

import MWC.GUI.Plottable;

import javax.swing.*;

/**
 * @author IAN MAYO
 * @version 1
 */
public class MWCXMLReaderWriter extends MWCXMLReader implements
		MWC.Utilities.ReaderWriter.PlainImporter
{
	/**
	 * flag which gets set when the user has cancelled the import process
	 */
	protected boolean _importCancelled;

	/** Creates new XMLReaderWriter */
	public MWCXMLReaderWriter()
	{
		super("");
	}

	public void importThis(String fName, InputStream is)
	{
		// null implementation!
		throw new RuntimeException("importThis method not implemented!");
	}

	public boolean canHandleThis(String type)
	{
		// hey! we can't really handle anything!
		return false;
	}

	protected void handleOurselves(String name, AttributeList atts)
	{
		// stuff it
	}

	/**
	 * utility class to create & configure our SAXParser for us. We configure the
	 * parser by telling it not to check against a specific DTD, since ASSET was
	 * repeatedly falling over when unable to find the indicated DTD
	 * 
	 * @return a configured parser
	 */
	static protected SAXParser getConfiguredParser()
	{
		SAXParser res = null;

		try
		{
			res = new SAXParser();
			res.setFeature("http://xml.org/sax/features/validation", false);
			res.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
					false);
		}
		catch (SAXException e)
		{
			System.err.println("could not set parser feature");
		}

		return res;

	}

	protected void doImport(InputSource is, MWCXMLReader theHandler)
	{

		try
		{

			// Create SAX 2 parser...
			SAXParser spf = getConfiguredParser();

			// put our plot handler into the chain
			theHandler.handleThis(spf, this);

			// start parsing
			spf.parse(is);
		}
		catch (SAXParseException se)
		{
			if (_importCancelled == true)
			{
				System.out.println("CANCELLED");
			}
			else
			{
				int line = se.getLineNumber();
				int col = se.getColumnNumber();
				String msg = "Trouble reading input file at line:" + line + ", column:" + col;
				MWC.Utilities.Errors.Trace.trace(se, msg);
				MWC.GUI.Dialogs.DialogFactory.showMessage("Open Debrief file", msg);
			}
		}
		catch (SAXNotRecognizedException sre)
		{
			MWC.Utilities.Errors.Trace.trace(sre,
					"Unknown trouble with SAX parsing (not recognised):" + sre.getMessage());
		}
		catch (SAXNotSupportedException spe)
		{
			MWC.Utilities.Errors.Trace.trace(spe,
					"Unknown trouble with SAX parsing (not supported)");
		}
		catch (SAXException se)
		{
			throw new RuntimeException(se.getMessage(), se);
		}
		catch (IOException e)
		{
			MWC.Utilities.Errors.Trace.trace(e, "Errors parsing XML document");
		}
	}

	// ///////////////////////////////////////////////////////////////////
	//
	// ////////////////////////////////////////////////////////////////////

	/**
	 * do an import using the indicated handler
	 */
	static public void importThis(MWCXMLReader theHandler, String name, InputStream is)
			throws SAXException
	{
		MWCXMLReaderWriter xr = new MWCXMLReaderWriter();

		xr.importThis(name, is, theHandler);
	}

	/**
	 * handle the import of XML data into an existing session
	 */
	public void importThis(String fName, InputStream is, MWCXMLReader reader)
	{

		// create progress monitor for this stream
		javax.swing.ProgressMonitorInputStream po = new ModifiedProgressMonitorInputStream(
				null, "Opening " + fName, is);

		// initialise cancelled flag
		_importCancelled = false;

		// import the datafile into this set of layers
		doImport(new InputSource(po), reader);

	}

	/**
	 * handle the import of XML data into an existing session
	 */
	public void importThis(String fName, InputStream is, MWC.GUI.Layers theData)
	{
		if (theData == null)
		{
			importThis(fName, is);
		}
		else
		{
			// create a handler
			MWCXMLReader handler = new LayersHandler(theData);

			// do the import
			importThis(fName, is, handler);

			//
			theData.fireModified(null);

		}
	}

	protected class ModifiedProgressMonitorInputStream extends ProgressMonitorInputStream
	{
		private int override_nread = 0;

		public ModifiedProgressMonitorInputStream(Component parentComponent, Object message,
				InputStream in)
		{
			super(parentComponent, message, in);
		}

		public int read(byte b[], int off, int len) throws IOException
		{
			int nr = in.read(b, off, len);
			if (nr > 0)
				getProgressMonitor().setProgress(override_nread += nr);
			if (getProgressMonitor().isCanceled())
			{
				_importCancelled = true;
				InterruptedIOException exc = new InterruptedIOException("progress");
				throw exc;
			}
			return nr;
		}
	};

	/**
	 * read in this whole file
	 */
	public boolean canImportThisFile(String theFile)
	{
		boolean res = false;
		String theSuffix = null;
		int pos = theFile.lastIndexOf(".");
		theSuffix = theFile.substring(pos, theFile.length()).toUpperCase();

		if (theSuffix.equals(".XML"))
			res = true;

		return res;
	}

	/**
	 * export this item using this format
	 */
	public void exportThis(MWC.GUI.Plottable item)
	{
	}

	/**
	 * export this item using this format
	 */
	public void exportThis(String comment)
	{

	}

	/**
	 * signal problem importing data
	 */
	public void readError(String fName, int line, String msg, String thisLine)
	{

	}

	public void endExport(Plottable item)
	{
		// TODO Auto-generated method stub
		
	}

	public void startExport(Plottable item)
	{
		// TODO Auto-generated method stub
		
	}

}
