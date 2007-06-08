/*
 * Desciption:
 * User: administrator
 * Date: Nov 8, 2001
 * Time: 1:19:14 PM
 */
package ASSET.GUI.SuperSearch;

import MWC.GUI.DragDrop.FileDropSupport;
import MWC.GUI.*;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

import java.util.Vector;
import java.util.Iterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import ASSET.ParticipantType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.Control.Observers.ObserverListHandler;
import ASSET.Util.XML.ASSETReaderWriter;
import org.xml.sax.SAXException;

public class SSBuilder
{
  /***************************************************************
   *  member variables
   ***************************************************************/

  /** name of the observers layer
   *
   */
  private final static String OBSERVER_NAME = "Observers";

  /** the template for our SSKs
   *
   */
  String _templateFile = null;

  /** the control file for generating the scenario
   *
   */
  String _controlFile = null;

  /** the file dropper for the template file
   *
   */
  MWC.GUI.DragDrop.FileDropSupport _templateDropper = null;

  /** the file dropper for the control file
   *
   */
  MWC.GUI.DragDrop.FileDropSupport _controlDropper = null;

  /** the file dropper for the blue files
   *
   */
  MWC.GUI.DragDrop.FileDropSupport _blueDropper = null;

  /** the scenario we're managing
   *
   */
  ASSET.Scenario.MultiForceScenario _myScenario = null;

  /** to handle our listener
   *
   */
  private java.beans.PropertyChangeListener _listener;

  /** the layers we are editing
   *
   */
  private Layers _theData;

  /***************************************************************
   *  constructor
   ***************************************************************/
  public SSBuilder(final ASSET.Scenario.MultiForceScenario scenario, final Layers theData)
  {
    _myScenario = scenario;
    _theData = theData;

    // listen out for templates being dropped
    _templateDropper = new FileDropSupport();
    _templateDropper.setFileDropListener(new FileDropSupport.FileDropListener()
    {
      public void FilesReceived(final Vector files)
      {
        final File fl = (File)files.firstElement();
        setTemplateFile(fl.getPath());
      }
    }, ".XML");

    // listen out for templates being dropped
    _controlDropper = new FileDropSupport();
    _controlDropper.setFileDropListener(new FileDropSupport.FileDropListener()
    {
      public void FilesReceived(final Vector files)
      {
        // extract the first file
        final File fl = (File)files.firstElement();

        // extract the list of observers
        createObservers(fl.getPath());

        // pass the control details on to the scenario builder
        setControlFile(fl.getPath());
      }
    }, ".XML");

    // listen out for blue vessels being dropped
    _blueDropper = new FileDropSupport();
    _blueDropper.setFileDropListener(new FileDropSupport.FileDropListener()
    {
      public void FilesReceived(final Vector files)
      {
        final Iterator it = files.iterator();
        while (it.hasNext())
        {
          final File thisFile = (File) it.next();
          addBlueFile(thisFile.getPath());
        }
      }
    }, ".XML");



  }

  /***************************************************************
   *  member methods
   ***************************************************************/

  /** sort out the list of observers
   *
   */
  private void createObservers(final String file)
  {
    // get the list of observers
    final MWCXMLReader mxr = new ObserverListHandler()
    {
      public void setObserverList(final Vector list)
      {
        // add these observers, initialising them as we go
        final Iterator ii = list.iterator();
        while (ii.hasNext())
        {
          final ScenarioObserver observer = (ScenarioObserver) ii.next();
          observer.setup(_myScenario);

          // is this observer a Editable?
          if(observer instanceof Plottable)
          {
            final Plottable thisEditor = (Plottable)observer;

            // add to the layers
            Layer observerLayer = _theData.findLayer(OBSERVER_NAME);

            // do we have the layer?
            if(observerLayer == null)
            {
              // no, create it
              observerLayer = new BaseLayer();
              observerLayer.setName(OBSERVER_NAME);
              _theData.addThisLayer(observerLayer);
            }

            // and add this observer
            observerLayer.add(thisEditor);

          }
        }

      }
    };

    try
    {
      mxr.reportNotHandledErrors(false);
      ASSETReaderWriter.importThis(mxr, file, new java.io.FileInputStream(file));
    }
    catch (SAXException e)
    {
      e.printStackTrace();
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
  }

  /** record the template file
   *
   */
  private void setTemplateFile(final String val)
  {
    _templateFile = val;

    // inform the listener
    _listener.propertyChange(new PropertyChangeEvent(this, "template", null, null));
  }

  /** record the control file
   *
   */
  private void setControlFile(final String val)
  {
    _controlFile = val;

    // inform the listener
    _listener.propertyChange(new PropertyChangeEvent(this, "control", null, null));
  }

  /** add a blue participant
   *
   */
  private void addBlueFile(final String val)
  {
    // read in this participant
    try
    {
      final ParticipantType newPart = ASSET.Util.XML.ASSETReaderWriter.importParticipant(val, new java.io.FileInputStream(val));

      if(newPart != null)
      {
        // update the time to match the scenario
        newPart.getStatus().setTime(_myScenario.getTime());

        // and add to the scenario
        _myScenario.addBlueParticipant(0, newPart);
      }
    }
    catch(java.io.FileNotFoundException fe)
    {
      fe.printStackTrace();
    }

    // inform the listener
    _listener.propertyChange(new PropertyChangeEvent(this, "blue", null, null));
  }

  /** do the build operatoin
   *
   */
  void doBuild()
  {
    if((_templateFile == null) || (_controlFile == null))
    {
      MWC.Utilities.Errors.Trace.trace("Control or template file not set");
      return;
    }

    _myScenario.createRedForce(_templateFile, _controlFile);
  }

  /** assign our listener
   *
   */
  public void setListener(final PropertyChangeListener listener)
  {
    _listener = listener;
  }
}
