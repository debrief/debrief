/*
 * XMLReaderWriter.java
 *
 * Created on 04 October 2000, 11:32
 */

package org.mwc.debrief.core.loaders.xml_handlers;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.debrief.core.editors.PlotEditor;
import org.w3c.dom.Document;

import MWC.GUI.Layers;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * @author IAN MAYO
 * @version 1
 */
public final class DebriefEclipseXMLReaderWriter extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReaderWriter
{

	/**
	 * Creates new XMLReaderWriter
	 */
	public DebriefEclipseXMLReaderWriter()
	{
	}

	// ///////////////////////////////////////////////////////////////////
	//
	// ////////////////////////////////////////////////////////////////////

	/**
	 * handle the import of XML data, creating a new session for it
	 */
	public final void importThis(final String fName,
			final java.io.InputStream is, Layers destination,
			IControllableViewport view, PlotEditor plot)
	{
		// create the handler for this type of data
		final MWCXMLReader handler = new PlotHandler(fName, destination, view, plot);

		// import the datafile into this set of layers
		importThis(fName, is, handler);
	}

	/**
	 * exporting the session
	 * 
	 * @param version
	 *          the version number of Debrief that's doing the export
	 */
	static public void exportThis(final PlotEditor thePlot,
			final java.io.OutputStream os, String version)
	{
		// first put the plot into an XML document
		try
		{
			final Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			final org.w3c.dom.Element plot = PlotHandler.exportPlot(thePlot, doc,
					version);
			doc.appendChild(plot);

			outputContent(os, doc);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * exporting the session
	 */
	static public void exportThis(final Layers theLayers,
			final java.io.OutputStream os)
	{
		// first put the plot into an XML document
		try
		{
			final Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			final org.w3c.dom.Element plot = PlotHandler.exportPlot(theLayers, doc);
			doc.appendChild(plot);

			outputContent(os, doc);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * ok - we've got our output in a doc, write it to the specified stream
	 * 
	 * @param os -
	 *          where we're writing to
	 * @param doc -
	 *          the content we're outputting
	 */
	private static void outputContent(final java.io.OutputStream os,
			final Document doc)
	{
		// and now export it.
		// this way of exporting the dom came from sample code in the Xerces 2.6.2
		// download
		try
		{
			TransformerFactory tF = TransformerFactory.newInstance();
			Transformer tr = tF.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(os);

			tr.transform(source, result);
			//
			// final OutputFormat format = new OutputFormat(doc, "UTF-8", true); //
			// Serialize
			// // DOM
			// format.setLineSeparator(System.getProperty("line.separator")); // use
			// // windows
			// // line
			// // separator
			// format.setLineWidth(0); // don't wrap any lines
			// format.setIndent(2); // only use a small indentation for
			// pretty-printing
			// final XMLSerializer serial = new XMLSerializer(os, format);
			// serial.asDOMSerializer(); // As a DOM Serializer
			// serial.serialize(doc.getDocumentElement());
		}
		catch (TransformerException e)
		{
			MWC.Utilities.Errors.Trace
					.trace(
							"Debrief failed to save this file correctly.  Please investigate the trace file",
							true);
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////

	// Don't bother with this way of testing. It relies on opening up a Debrief
	// session & where it compares output file with reference
	// version it's too sensitive.

	// static public final class testImport extends junit.framework.TestCase
	// {
	//
	// // todo put in test to handle when a plot-file is dragged into existing
	// plot
	//
	// // todo re-instate these tests (change name back to testImport)
	//
	// static public final String TEST_ALL_TEST_TYPE = "UNT";
	// private static String fileName;// = "C:/D3/DebriefApp/test.xml";
	// private static String outputFilename;
	//
	// public testImport(String val)
	// {
	// super(val);
	//
	// fileName = System.getProperty("dataDir") + "/test.xml";
	// outputFilename = System.getProperty("dataDir") + "/test_output.xml";
	// }
	//
	// public final void testReadXML()
	// {
	// java.io.File testFile = null;
	//
	// // can we load it directly
	// testFile = new java.io.File(fileName);
	//
	// if (!testFile.exists())
	// {
	//
	// // first try to get the URL of the image
	// java.lang.ClassLoader loader = getClass().getClassLoader();
	// if (loader != null)
	// {
	// java.net.URL imLoc = loader.getResource(fileName);
	// if (imLoc != null)
	// {
	// testFile = new java.io.File(imLoc.getFile());
	// }
	// }
	// else
	// {
	// fail("Failed to find class loader");
	// }
	// }
	//
	// // did we find it?
	// assertTrue("Failed to find file:" + fileName, testFile.exists());
	//
	//
	// // ok, now try to read it in
	// MWC.GUI.Layers _theLayers = new MWC.GUI.Layers();
	// java.io.File[] _theFiles = new java.io.File[]{testFile};
	//
	// Debrief.GUI.Frames.Swing.SwingApplication application = new
	// Debrief.GUI.Frames.Swing.SwingApplication();
	//
	// // add the XML importer
	// MWC.Utilities.ReaderWriter.ImportManager.addImporter(new
	// Debrief.ReaderWriter.XML.DebriefXMLReaderWriter(application, null));
	//
	// // get our thread to import this
	// MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller reader =
	// new MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller(_theFiles,
	// null)
	// {
	// // handle the completion of each file
	// public void fileFinished(java.io.File fName, MWC.GUI.Layers newData)
	// {
	// // MWC.Utilities.Errors.Trace.trace("file finished received for:" +
	// fName.getPath());
	// }
	//
	// // handle completion of the full import process
	// public void allFilesFinished(java.io.File[] fNames, MWC.GUI.Layers newData)
	// {
	// // MWC.Utilities.Errors.Trace.trace("XML Reader:all files finished
	// received!");
	// }
	// };
	//
	// // and start it running
	// reader.start();
	//
	// // wait for the results
	// while (reader.isAlive())
	// {
	// try
	// {
	// Thread.currentThread().sleep(100);
	// }
	// catch (java.lang.InterruptedException e)
	// {
	// }
	// }
	//
	// // get the current session
	// Debrief.GUI.Frames.Session sess = application.getCurrentSession();
	//
	// // we've now got to get the layers
	// _theLayers = sess.getData();
	// assertEquals("Count of layers", _theLayers.size(), 2);
	//
	// // area of coverage
	// MWC.GenericData.WorldArea area = _theLayers.elementAt(1).getBounds();
	//
	// super.assertEquals("tl lat of first layer", area.getTopLeft().getLat(),
	// 12.142258, 0.001);
	// super.assertEquals("tl long of first layer", area.getTopLeft().getLong(),
	// -11.7342556, 0.00001);
	// super.assertEquals("tl depth of first layer", area.getTopLeft().getDepth(),
	// 0, 0.00001);
	//
	// super.assertEquals("br lat of first layer", area.getBottomRight().getLat(),
	// 11.89421, 0.001);
	// super.assertEquals("br long of first layer",
	// area.getBottomRight().getLong(), -11.59376, 0.00001);
	// super.assertEquals("br depth of first layer",
	// area.getBottomRight().getDepth(), 0, 0.00001);
	//
	// // area of projection
	// Debrief.GUI.Views.AnalysisView av = (Debrief.GUI.Views.AnalysisView)
	// sess.getCurrentView();
	// // System.out.println("area:" +
	// av.getChart().getCanvas().getProjection().getDataArea());
	//
	//
	// try
	// {
	// // now we have to save the session - check they're the same!
	// java.io.OutputStream os = new java.io.FileOutputStream(outputFilename);
	//
	// // inform the session of it's filename
	// sess.setFileName(outputFilename);
	//
	// // pass all of this to the XML exporter
	// Debrief.ReaderWriter.XML.DebriefXMLReaderWriter.exportThis(sess, os);
	//
	// os.close();
	// }
	// catch (Exception e)
	// {
	// fail("Failed whilst outputing our session!");
	// }
	//
	// // now try to compare the two files
	// String first = getFile(fileName);
	// String second = getFile(outputFilename);
	//
	// // check the contents of the files
	// assertEquals("Output matches input", first, second);
	//
	//
	// }
	//
	// private String getFile(String fName)
	// {
	// String res = "";
	// String thisL;
	// java.io.BufferedReader in = null;
	//
	// try
	// {
	// in = new java.io.BufferedReader(new java.io.FileReader(fName));
	// }
	// catch (Exception e)
	// {
	// fail("Failed to read file:" + fName);
	// }
	//
	// try
	// {
	// do
	// {
	// thisL = in.readLine();
	// res += thisL;
	// }
	// while (thisL != null);
	// }
	// catch (Exception e)
	// {
	// fail("Whilst reading in file:" + fName);
	// }
	// return res;
	// }
	//
	// }
	//
	// public static void main(String[] args)
	// {
	// testImport ti = new testImport("some name");
	// ti.testReadXML();
	// System.exit(0);
	// }
	//

}
