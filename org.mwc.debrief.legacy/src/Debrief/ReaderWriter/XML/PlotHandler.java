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
package Debrief.ReaderWriter.XML;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 *
 * @author Ian Mayo
 * @version 1.0
 */

final public class PlotHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private final Debrief.GUI.Frames.Application _parent;

  public PlotHandler(final Debrief.GUI.Frames.Application theDestination,
                     final Debrief.GUI.Frames.Session theSession,
                     final String fileName)
  {
    // inform our parent what type of class we are
    super("plot");

    if (theDestination == null)
    {
      _parent = new Debrief.GUI.Frames.Swing.SwingApplication();
    }
    else
    {
      _parent = theDestination;
    }

    // sort out the handlers
    addHandler(new SessionHandler(_parent, theSession, fileName)
    {
      public void addSession(final Debrief.GUI.Frames.Session data)
      {
        addThisSession(data);
      }
    });
    addHandler(new DetailsHandler(null));


    super.addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(final String name, final String val)
      {
        System.out.println("Name of Plot is " + val);
      }
    });
    super.addAttributeHandler(new HandleAttribute("Created")
    {
      public void setValue(final String name, final String val)
      {
        System.out.println("Plot was created on " + val);
      }
    });
  }

  void addThisSession(final Debrief.GUI.Frames.Session data)
  {
    // tidy up the session, and add it to the application
    _parent.newSession(data);
  }

  public static org.w3c.dom.Element exportPlot(final Debrief.GUI.Frames.Session session, final org.w3c.dom.Document doc)
  {
    final org.w3c.dom.Element plt = doc.createElement("plot");
    plt.setAttribute("Created", new java.util.Date().toString());
    plt.setAttribute("Name", "Debrief Plot");
    final String details = "Saved with Debrief version dated " + Debrief.GUI.VersionInfo.getVersion();
    SessionHandler.exportThis(session, plt, doc);
    DetailsHandler.exportPlot(details, plt, doc);
    return plt;
  }


}