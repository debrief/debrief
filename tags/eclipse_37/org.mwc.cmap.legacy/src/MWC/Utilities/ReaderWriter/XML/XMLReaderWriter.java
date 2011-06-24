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

  public void importThis(String fName,
                         InputStream is)
  {
    // null implementation!
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


  private void doImport(org.xml.sax.InputSource is,
                        XMLHandler theHandler)
  {

    try{
      // Create SAX 2 parser...
      Parser xr = org.xml.sax.helpers.ParserFactory.makeParser("com.sun.xml.parser.Parser");

      // put our plot handler into the chain
      theHandler.handleThis(xr, this);

      // start parsing
      xr.parse(is);
    }
    catch(java.lang.InstantiationException ie)
    {
      MWC.Utilities.Errors.Trace.trace(ie, "Could not create XML parser");
    }
    catch(org.xml.sax.SAXParseException se)
    {
      int line = se.getLineNumber();
      int col = se.getColumnNumber();
      String msg = "Trouble reading input file at line:" + line +", column:" + col;
      MWC.Utilities.Errors.Trace.trace(se, msg);
      MWC.GUI.Dialogs.DialogFactory.showMessage("Open Debrief file", msg);
    }
    catch(org.xml.sax.SAXException se)
    {
      MWC.Utilities.Errors.Trace.trace(se, "Unknown trouble with SAX handling");
    }
    catch(Exception e)
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
  static public void importThis(XMLHandler theHandler,
                         java.io.InputStream is)
  {
    XMLReaderWriter xr = new XMLReaderWriter();

    xr.doImport(new InputSource(is), theHandler);
  }


  /** handle the import of XML data into an existing session
   */
  public void importThis(String fName,
                         java.io.InputStream is,
                         MWC.GUI.Layers theData)
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
  public boolean canImportThisFile(String theFile)
  {
    boolean res = false;
    String theSuffix=null;
    int pos = theFile.lastIndexOf(".");
    theSuffix = theFile.substring(pos, theFile.length()).toUpperCase();

    if(theSuffix.equals(".XML"))
      res = true;

    return res;
  }

  /** export this item using this format
   */
  public void exportThis(MWC.GUI.Plottable item)
  {
  	
  }

  /** export this item using this format
   */
  public void exportThis(String comment)
  {

  }


  /** signal problem importing data
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
