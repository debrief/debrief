package org.mwc.debrief.core.loaders.xml_handlers;

import java.util.Vector;

import org.eclipse.core.runtime.IAdaptable;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.interfaces.*;
import org.mwc.debrief.core.editors.PlotEditor;

import Debrief.ReaderWriter.XML.DebriefLayersHandler;
import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.Layers;
import MWC.GenericData.WorldArea;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */


public class SessionHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  public SessionHandler(Layers _theLayers, 
  		final IControllableViewport  view, PlotEditor plot)
  {
    // inform our parent what type of class we are
    super("session");

    // define our handlers
    addHandler(new ProjectionHandler()
    		{
					public void setProjection(PlainProjection proj)
					{
						view.setProjection(proj);
					}
    		});
    addHandler(new SWTGUIHandler(plot)
    		{
					public void assignTracks(String primaryTrack, Vector secondaryTracks)
					{
						// see if we have our track data listener
						if(view instanceof IAdaptable)
						{
							IAdaptable ad = (IAdaptable) view;
							Object adaptee = ad.getAdapter(org.mwc.cmap.core.DataTypes.TrackData.TrackManager.class);
							if(adaptee != null)
							{
								TrackManager tl = (TrackManager) adaptee;
								tl.assignTracks(primaryTrack, secondaryTracks);
							}
						}
					}
    		});
    
    addHandler(new DebriefLayersHandler(_theLayers));

  }

  public final void elementClosed()
  { 	
  	// and the GUI details
  //	setGUIDetails(null);
  }

  public static void exportThis(PlotEditor thePlot, org.w3c.dom.Element parent,
                                org.w3c.dom.Document doc)
  {
    // ok, get the layers
    Layers theLayers = (Layers) thePlot.getAdapter(Layers.class);

    exportTheseLayers(theLayers, thePlot, parent, doc);
  }

  public static void exportTheseLayers(Layers theLayers, PlotEditor thePlot, org.w3c.dom.Element parent,
      org.w3c.dom.Document doc)
  {
    org.w3c.dom.Element eSession = doc.createElement("session");
    
    // now the Layers
    DebriefLayersHandler.exportThis(theLayers, eSession, doc);

    // now the projection
    final PlainProjection proj;
    if(thePlot != null)
    {
    	proj =  (PlainProjection) thePlot.getAdapter(PlainProjection.class);
    }
    else
    {    
    	proj = new FlatProjection()
    	{
    		/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public WorldArea getDataArea()
    		{
    			// TODO Auto-generated method stub
    			return Layers.getDebriefOrigin();
    		}

				/**
				 * @return
				 */
				public double getDataBorder()
				{
					return 1.1;
				}

				/**
				 * @return
				 */
				public boolean getRelativePlot()
				{
					return false;
				}
    		
    	};
    }
    	
    ProjectionHandler.exportProjection(proj, eSession, doc);

    // now the GUI
    // do we have a gui?
    if(thePlot != null)
    	SWTGUIHandler.exportThis(thePlot, eSession, doc);

    // send out the data
    parent.appendChild(eSession);
  }
  
}