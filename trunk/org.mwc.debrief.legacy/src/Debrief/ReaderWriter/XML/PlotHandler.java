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

  public PlotHandler(Debrief.GUI.Frames.Application theDestination,
                     Debrief.GUI.Frames.Session theSession,
                     String fileName)
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
      public void addSession(Debrief.GUI.Frames.Session data)
      {
        addThisSession(data);
      }
    });
    addHandler(new DetailsHandler(null));


    super.addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(String name, String val)
      {
        System.out.println("Name of Plot is " + val);
      }
    });
    super.addAttributeHandler(new HandleAttribute("Created")
    {
      public void setValue(String name, String val)
      {
        System.out.println("Plot was created on " + val);
      }
    });
  }

  private void addThisSession(Debrief.GUI.Frames.Session data)
  {
    // tidy up the session, and add it to the application
    _parent.newSession(data);
  }

  public static org.w3c.dom.Element exportPlot(Debrief.GUI.Frames.Session session, org.w3c.dom.Document doc)
  {
    org.w3c.dom.Element plt = doc.createElement("plot");
    plt.setAttribute("Created", new java.util.Date().toString());
    plt.setAttribute("Name", "Debrief Plot");
    String details = "Saved with Debrief version dated " + Debrief.GUI.VersionInfo.getVersion();
    SessionHandler.exportThis(session, plt, doc);
    DetailsHandler.exportPlot(details, plt, doc);
    return plt;
  }


}