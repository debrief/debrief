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

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

import javax.swing.ProgressMonitorInputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

import MWC.GUI.Plottable;

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

	public void importThis(final String fName, final InputStream is)
	{
		// null implementation!
		throw new RuntimeException("importThis method not implemented!");
	}

	@Override
	public boolean canHandleThis(final String type)
	{
		// hey! we can't really handle anything!
		return false;
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
			res = SAXParserFactory.newInstance().newSAXParser();
			// res.setProperty("http://xml.org/sax/features/validation", false);
			// res.setFeature(
			// "http://apache.org/xml/features/nonvalidating/load-external-dtd",
			// false);
		}
		catch (final SAXException e)
		{
			System.err.println("could not set parser feature");
		}
		catch (final ParserConfigurationException e)
		{
			e.printStackTrace();
		}

		return res;

	}

	protected void doImport(final InputSource is, final MWCXMLReader theHandler)
	{

		try
		{

			// Create SAX 2 parser...
			final SAXParser spf = getConfiguredParser();

			// put our plot handler into the chain
			theHandler.handleThis(spf.getXMLReader(), this);

			// start parsing
			spf.parse(is, theHandler);
		}
		catch (final SAXParseException se)
		{
			if (_importCancelled == true)
			{
				System.out.println("CANCELLED");
			}
			else
			{
				final int line = se.getLineNumber();
				final int col = se.getColumnNumber();
				final String msg = "Trouble reading input file at line:" + line + ", column:"
						+ col;
				MWC.Utilities.Errors.Trace.trace(se, msg);
				MWC.GUI.Dialogs.DialogFactory.showMessage("Open Debrief file", msg);
			}
		}
		catch (final SAXNotRecognizedException sre)
		{
			MWC.Utilities.Errors.Trace.trace(sre,
					"Unknown trouble with SAX parsing (not recognised):"
							+ sre.getMessage());
		}
		catch (final SAXNotSupportedException spe)
		{
			MWC.Utilities.Errors.Trace.trace(spe,
					"Unknown trouble with SAX parsing (not supported)");
		}
		catch (final SAXException se)
		{
			throw new RuntimeException(se.getMessage(), se);
		}
		catch (final IOException e)
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
	static public void importThis(final MWCXMLReader theHandler, final String name,
			final InputStream is) throws SAXException
	{
		final MWCXMLReaderWriter xr = new MWCXMLReaderWriter();

		xr.importThis(name, is, theHandler);
	}

	/**
	 * handle the import of XML data into an existing session
	 */
	public void importThis(final String fName, final InputStream is, final MWCXMLReader reader)
	{

		// create progress monitor for this stream
		final javax.swing.ProgressMonitorInputStream po = new ModifiedProgressMonitorInputStream(
				null, "Opening " + fName, is);

		// initialise cancelled flag
		_importCancelled = false;

		// import the datafile into this set of layers
		doImport(new InputSource(po), reader);

	}

	/**
	 * handle the import of XML data into an existing session
	 */
	public void importThis(final String fName, final InputStream is, final MWC.GUI.Layers theData)
	{
		if (theData == null)
		{
			importThis(fName, is);
		}
		else
		{
			// create a handler
			final MWCXMLReader handler = new LayersHandler(theData);

			// do the import
			importThis(fName, is, handler);

			//
			theData.fireModified(null);

		}
	}

	protected class ModifiedProgressMonitorInputStream extends
			ProgressMonitorInputStream
	{
		private int override_nread = 0;

		public ModifiedProgressMonitorInputStream(final Component parentComponent,
				final Object message, final InputStream in)
		{
			super(parentComponent, message, in);
		}

		@Override
		public int read(final byte b[], final int off, final int len) throws IOException
		{
			final int nr = in.read(b, off, len);
			if (nr > 0)
				getProgressMonitor().setProgress(override_nread += nr);
			if (getProgressMonitor().isCanceled())
			{
				_importCancelled = true;
				final InterruptedIOException exc = new InterruptedIOException("progress");
				throw exc;
			}
			return nr;
		}
	}

	/**
	 * read in this whole file
	 */
	public boolean canImportThisFile(final String theFile)
	{
		boolean res = false;
		String theSuffix = null;
		final int pos = theFile.lastIndexOf(".");
		theSuffix = theFile.substring(pos, theFile.length()).toUpperCase();

		if (theSuffix.equals(".XML"))
			res = true;

		return res;
	}

	/**
	 * export this item using this format
	 */
	public void exportThis(final MWC.GUI.Plottable item)
	{
	}

	/**
	 * export this item using this format
	 */
	public void exportThis(final String comment)
	{

	}

	/**
	 * signal problem importing data
	 */
	public void readError(final String fName, final int line, final String msg, final String thisLine)
	{

	}

	public void endExport(final Plottable item)
	{

	}

	public void startExport(final Plottable item)
	{

	}

}
