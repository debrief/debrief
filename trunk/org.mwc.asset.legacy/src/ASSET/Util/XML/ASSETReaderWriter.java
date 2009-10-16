/*
 * XMLReaderWriter.java
 *
 * Created on 04 October 2000, 11:32
 */

package ASSET.Util.XML;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import ASSET.ScenarioType;
import ASSET.Models.SensorType;
import ASSET.Models.Sensor.SensorList;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.Control.StandaloneObserverListHandler;
import ASSET.Util.XML.Control.Observers.ScenarioControllerHandler;
import ASSET.Util.XML.Decisions.WaterfallHandler;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.Utilities.ReaderWriter.XML.LayersHandler;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * @author IAN MAYO
 */
public class ASSETReaderWriter extends MWC.Utilities.ReaderWriter.XML.MWCXMLReaderWriter
{


  ScenarioHandler _theScenarioHandler;

  /**
   * Creates new XMLReaderWriter
   */
  public ASSETReaderWriter()
  {
  }


  static public ASSET.ParticipantType readThis(final java.io.File file)
  {
    ASSET.ParticipantType res = null;
    try
    {
      final java.io.FileInputStream str = new java.io.FileInputStream(file);

      res = importParticipant("Scrap", str);
    }
    catch (java.io.IOException e)
    {
      e.printStackTrace();
    }

    return res;
  }

  /////////////////////////////////////////////////////////////////////
  //
  //////////////////////////////////////////////////////////////////////


  /**
   * handle the import of XML data into an existing session
   */
  public void importThis(String fName,
                         final java.io.InputStream is,
                         final MWC.GUI.Layers theData)
  {
    final MWCXMLReader handler = new LayersHandler(theData);

    // import the datafile into this set of layers
    doImport(new InputSource(is), handler);

    //
    theData.fireModified(null);
  }


  /**
   * handle the import of XML data, creating a new session for it
   */
  static public void importThis(final ASSET.Scenario.CoreScenario theScenario,
                                final String fName,
                                final java.io.InputStream is)
  {
    // create the new handler
    final ASSETReaderWriter xr = new ASSETReaderWriter();

    final MWCXMLReader handler = new ScenarioHandler(theScenario);

    // import the datafile into this set of layers
    xr.doImport(new InputSource(is), handler);
  }
  
  /**
   * handle the import of XML data, creating a new session for it
   */
  static public void importThis(final ASSET.Scenario.CoreScenario theScenario,
  															final Layers theLayers,
                                final String fName,
                                final java.io.InputStream is)
  {
    // create the new handler
    final ASSETReaderWriter xr = new ASSETReaderWriter();

    final MWCXMLReader handler = new ScenarioHandler(theScenario, theLayers);

    // import the datafile into this set of layers
    xr.doImport(new InputSource(is), handler);
  }
  

  /**
   * handle the import of XML data, creating a new session for it
   *
   * @param fName the filename to read from (largely ignored)
   * @param is    an input stream to read from
   * @return the output directory to write, if applicable
   */
	static public ResultsContainer importThisControlFile(final String fName,
                                                       final java.io.InputStream is)
  {
    // create the new handler
    final ASSETReaderWriter xr = new ASSETReaderWriter();

    final Vector<ResultsContainer> resultsHolder = new Vector<ResultsContainer>(0, 1);

    final MWCXMLReader handler = new ScenarioControllerHandler()
    {
      public void setResults(ResultsContainer results)
      {
        resultsHolder.add(results);
      }
    };

    // import the datafile into this set of layers
    InputSource inputStream = new InputSource(is);
       
		xr.doImport(inputStream, handler);

    ResultsContainer results = null;

    // did we get an output holder?
  //  File outputDirectory = null;
    if (resultsHolder.size() > 0)
    {
      results =
        (ResultsContainer) resultsHolder.firstElement();
    }

    return results;

  }

  /**
   * handle the import of XML data, creating a new session for it
   */
  static public Vector<ScenarioObserver> importThisObserverList(final String fName,
                                              final java.io.InputStream is)
  {
    // create the new handler
    final ASSETReaderWriter xr = new ASSETReaderWriter();

    final Vector<ScenarioObserver> res = new Vector<ScenarioObserver>(0, 1);

    final MWCXMLReader handler = new StandaloneObserverListHandler()
    {
      public void setObserverList(Vector<ScenarioObserver> list)
      {
        res.addAll(list);
      }
    };

    // import the datafile into this set of layers
    xr.doImport(new InputSource(is), handler);

    return res;
  }

  /**
   * handle the import of XML data, creating a new session for it
   */
  static public void importThis(final MWC.GUI.Layers layers,
                                final java.io.InputStream is)
  {
    // create the new handler
    final ASSETReaderWriter xr = new ASSETReaderWriter();

    final MWCXMLReader handler = new MWC.Utilities.ReaderWriter.XML.LayersHandler(layers);

    // import the datafile into this set of layers
    xr.doImport(new InputSource(is), handler);
  }


  static protected ASSET.ParticipantType _tempPart;

  static public ASSET.ParticipantType importParticipant(String fName,
                                                        final java.io.InputStream is)
  {
    final ASSETReaderWriter xr = new ASSETReaderWriter();

    _tempPart = null;

    final GeneralImporter gi = new GeneralImporter();
    gi.addHandler(new ASSET.Util.XML.Vessels.SSKHandler()
    {
      public void addThis(final ASSET.ParticipantType part, boolean isMonteCarlo)
      {
        _tempPart = part;
      }
    });
    gi.addHandler(new ASSET.Util.XML.Vessels.SSNHandler()
    {
      public void addThis(final ASSET.ParticipantType part, boolean isMonteCarlo)
      {
        _tempPart = part;
      }
    });
    gi.addHandler(new ASSET.Util.XML.Vessels.SurfaceHandler()
    {
      public void addThis(final ASSET.ParticipantType part, boolean isMonteCarlo)
      {
        _tempPart = part;
      }
    });
    gi.addHandler(new ASSET.Util.XML.Vessels.HeloHandler()
    {
      public void addThis(final ASSET.ParticipantType part, boolean isMonteCarlo)
      {
        _tempPart = part;
      }
    });
    gi.addHandler(new ASSET.Util.XML.Vessels.FixedWingHandler()
    {
      public void addThis(final ASSET.ParticipantType part, boolean isMonteCarlo)
      {
        _tempPart = part;
      }
    });
    gi.addHandler(new ASSET.Util.XML.Vessels.TorpedoHandler()
    {
      public void addThis(final ASSET.ParticipantType part, boolean isMonteCarlo)
      {
        _tempPart = part;
      }
    });


    // import the datafile into this set of layers
    xr.doImport(new InputSource(is), gi);

    return _tempPart;
  }


  static public void importThis(final ASSET.Models.Decision.BehaviourList theChain,
                                String fName,
                                final java.io.InputStream is)
  {
    // create the new handler
    final ASSETReaderWriter xr = new ASSETReaderWriter();

    final ASSET.Models.Decision.BehaviourList thisChain = theChain;

    final GeneralImporter gi = new GeneralImporter();
    gi.addHandler(new ASSET.Util.XML.Decisions.WaterfallHandler(WaterfallHandler.MAX_CHAIN_DEPTH)
    {
      public void setModel(final ASSET.Models.DecisionType dec)
      {
        thisChain.insertAtFoot(dec);
      }
    });
    gi.addHandler(new ASSET.Util.XML.Decisions.EvadeHandler()
    {
      public void setModel(final ASSET.Models.DecisionType dec)
      {
        thisChain.insertAtFoot(dec);
      }
    });
    gi.addHandler(new ASSET.Util.XML.Decisions.SSKRechargeHandler()
    {
      public void setModel(final ASSET.Models.DecisionType dec)
      {
        thisChain.insertAtFoot(dec);
      }
    });
    gi.addHandler(new ASSET.Util.XML.Decisions.SternArcClearanceHandler()
    {
      public void setModel(final ASSET.Models.DecisionType dec)
      {
        thisChain.insertAtFoot(dec);
      }
    });
    gi.addHandler(new ASSET.Util.XML.Decisions.BearingTrailHandler()
    {
      public void setModel(final ASSET.Models.DecisionType dec)
      {
        thisChain.insertAtFoot(dec);
      }
    });
    gi.addHandler(new ASSET.Util.XML.Decisions.TrailHandler()
    {
      public void setModel(final ASSET.Models.DecisionType dec)
      {
        thisChain.insertAtFoot(dec);
      }
    });
    gi.addHandler(new ASSET.Util.XML.Decisions.TransitHandler()
    {
      public void setModel(final ASSET.Models.DecisionType dec)
      {
        thisChain.insertAtFoot(dec);
      }
    });


    // import the datafile into this set of layers
    xr.doImport(new InputSource(is), gi);
  }


  static public void importThis(final SensorList theSensorFit,
                                String fName,
                                final java.io.InputStream is)
  {
    // create the new handler
    final ASSETReaderWriter xr = new ASSETReaderWriter();

    final SensorList thisList = theSensorFit;

    final GeneralImporter gi = new GeneralImporter();
    // first the normal handlers
    gi.addHandler(new ASSET.Util.XML.Sensors.ActiveBroadbandHandler()
    {			public void addSensor(SensorType sensor)
			{				thisList.add(sensor);			}    });
    gi.addHandler(new ASSET.Util.XML.Sensors.ActiveInterceptHandler()
    {			public void addSensor(SensorType sensor)
			{				thisList.add(sensor);			}    });
    gi.addHandler(new ASSET.Util.XML.Sensors.BroadbandHandler()
    {			public void addSensor(SensorType sensor)
			{				thisList.add(sensor);			}    });
    gi.addHandler(new ASSET.Util.XML.Sensors.DippingActiveBroadbandHandler()
    {			public void addSensor(SensorType sensor)
			{				thisList.add(sensor);			}    });
    gi.addHandler(new ASSET.Util.XML.Sensors.NarrowbandHandler()
    {			public void addSensor(SensorType sensor)
			{				thisList.add(sensor);			}    });
    gi.addHandler(new ASSET.Util.XML.Sensors.OpticSensorHandler()
    {			public void addSensor(SensorType sensor)
			{				thisList.add(sensor);			}    });
    
    
    
    // now the lookup handlers
    gi.addHandler(new ASSET.Util.XML.Sensors.Lookup.OpticLookupSensorHandler()
    {			public void addSensor(SensorType sensor)
			{				thisList.add(sensor);			}    });   
    gi.addHandler(new ASSET.Util.XML.Sensors.Lookup.MADLookupSensorHandler()
    {			public void addSensor(SensorType sensor)
			{				thisList.add(sensor);			}    });   
    gi.addHandler(new ASSET.Util.XML.Sensors.Lookup.RadarLookupSensorHandler()
    {			public void addSensor(SensorType sensor)
			{				thisList.add(sensor);			}    });   


    // import the datafile into this set of layers
    xr.doImport(new InputSource(is), gi);
  }
  
  
  static public class GeneralImporter extends MWCXMLReader
  {
    MWCXMLReader _currentHandler;

    public GeneralImporter()
    {
      super("General");
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

    if (theSuffix.equals(".ASF"))
      res = true;

    return res;
  }


  /**
   * export this item using this format
   */
  public void exportThis(String comment)
  {

  }

  /**
   * exporting the session
   * @param theDecorations a layer of graphic backdrops to export
   */
  static public void exportThis(final ScenarioType scenario, Layer theDecorations, final java.io.OutputStream os)
  {
    // output the XML header stuff
    // output the plot
//    final Document doc2 = new DocumentImpl();
    
    try
		{
			DOMImplementation domI = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
			Document doc = domI.createDocument(null,null,null);
			

	    final org.w3c.dom.Element plot = ScenarioHandler.exportScenario(scenario, theDecorations, doc);
	    doc.appendChild(plot);
	    
	    // and now export it.
	    // this way of exporting the dom came from sample code in the Xerces 2.6.2 download
	    try
	    {
	      final OutputFormat format = new OutputFormat(doc,"UTF-8", true);   //Serialize DOM
	      format.setLineSeparator(System.getProperty("line.separator")); // use windows line separator
	      format.setLineWidth(0); // don't wrap any lines
	      format.setIndent(2); // only use a small indentation for pretty-printing
	      final XMLSerializer serial = new XMLSerializer(os, format);
	      serial.asDOMSerializer();                            // As a DOM Serializer
	      serial.serialize(doc.getDocumentElement());
	    }
	    catch (IOException e)
	    {
	      MWC.Utilities.Errors.Trace.trace("Debrief failed to save this file correctly.  Please investigate the trace file", true);
	      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	    }			
			
		}
		catch (ParserConfigurationException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    
    
    
//    
//    doc.changeNodeOwner(plot);
//    doc.setSystemId("ASSET XML Version 1.0");
//
//    // ok, we should be done now
//    try
//    {
//      doc.write(os);
//    }
//    catch (java.io.IOException e)
//    {
//      e.printStackTrace();
//    }
  }


  /**
   * signal problem importing data
   */
  public void readError(String fName, int line, String msg, String thisLine)
  {

  }


  /**
   * handy container to let us pass the complex scenario controller structure around
   */
  public static class ResultsContainer
  {
    public Vector<ScenarioObserver> observerList;
    public File outputDirectory;
    public Integer randomSeed;
  }

}
