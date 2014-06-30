/*
 * XMLReaderWriter.java
 *
 * Created on 04 October 2000, 11:32
 */

package MWC.Utilities.ReaderWriter.XML;

import java.io.InputStream;

import org.xml.sax.AttributeList;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;

import MWC.GUI.Plottable;

/**
 *
 * @author  IAN MAYO
 * @version 1
 */
@SuppressWarnings("deprecation")
public class XMLReaderWriter extends XMLHandler implements MWC.Utilities.ReaderWriter.PlainImporter
{


  /** Creates new XMLReaderWriter */
  public XMLReaderWriter() {
    super("");
  }

  public void importThis(final String fName,
                         final InputStream is)
  {
    // null implementation!
  }

  public boolean canHandleThis(final String type)
  {
    // hey! we can't really handle anything!
    return false;
  }

  protected void handleOurselves(final String name, final AttributeList atts)
  {
    // stuff it
  }


  private void doImport(final org.xml.sax.InputSource is,
                        final XMLHandler theHandler)
  {

    try{
      // Create SAX 2 parser...
      final Parser xr = org.xml.sax.helpers.ParserFactory.makeParser("com.sun.xml.parser.Parser");

      // put our plot handler into the chain
      theHandler.handleThis(xr, this);

      // start parsing
      xr.parse(is);
    }
    catch(final java.lang.InstantiationException ie)
    {
      MWC.Utilities.Errors.Trace.trace(ie, "Could not create XML parser");
    }
    catch(final org.xml.sax.SAXParseException se)
    {
      final int line = se.getLineNumber();
      final int col = se.getColumnNumber();
      final String msg = "Trouble reading input file at line:" + line +", column:" + col;
      MWC.Utilities.Errors.Trace.trace(se, msg);
      MWC.GUI.Dialogs.DialogFactory.showMessage("Open Debrief file", msg);
    }
    catch(final org.xml.sax.SAXException se)
    {
      MWC.Utilities.Errors.Trace.trace(se, "Unknown trouble with SAX handling");
    }
    catch(final Exception e)
    {
      MWC.Utilities.Errors.Trace.trace(e, "Errors parsing XML document");
    }
  }

  /////////////////////////////////////////////////////////////////////
  //
  //////////////////////////////////////////////////////////////////////

  /** do an import using the indicated handler
   *
   */
  static public void importThis(final XMLHandler theHandler,
                         final java.io.InputStream is)
  {
    final XMLReaderWriter xr = new XMLReaderWriter();

    xr.doImport(new InputSource(is), theHandler);
  }


  /** handle the import of XML data into an existing session
   */
  public void importThis(final String fName,
                         final java.io.InputStream is,
                         final MWC.GUI.Layers theData)
  {
    if(theData == null)
    {
      importThis(fName, is);
    }
    else
    {
//      XMLHandler handler = new LayersHandler(theData);
//
//      // create progress monitor for this stream
//      javax.swing.ProgressMonitorInputStream po = new javax.swing.ProgressMonitorInputStream(null, "Opening " + fName, is);
//
//      // import the datafile into this set of layers
//      doImport(new InputSource(po), handler);
//
//      //
//      theData.fireModified();

    }
  }


  /** read in this whole file
   */
  public boolean canImportThisFile(final String theFile)
  {
    boolean res = false;
    String theSuffix=null;
    final int pos = theFile.lastIndexOf(".");
    theSuffix = theFile.substring(pos, theFile.length()).toUpperCase();

    if(theSuffix.equals(".XML"))
      res = true;

    return res;
  }

  /** export this item using this format
   */
  public void exportThis(final MWC.GUI.Plottable item)
  {
  	
  }

  /** export this item using this format
   */
  public void exportThis(final String comment)
  {

  }


  /** signal problem importing data
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
