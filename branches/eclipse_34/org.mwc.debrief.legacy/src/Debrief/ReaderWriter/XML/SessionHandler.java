package Debrief.ReaderWriter.XML;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */


abstract public class SessionHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private final Debrief.GUI.Frames.Application _parent;
  private Debrief.GUI.Frames.Session _session;
  //  private MWC.Algorithms.PlainProjection _projection;

  public SessionHandler(Debrief.GUI.Frames.Application theDestination,
                        Debrief.GUI.Frames.Session theSession,
                        String fileName)
  {
    // inform our parent what type of class we are
    super("session");

    MWC.GUI.Layers _theLayers = null;

    _parent = theDestination;

    // check if we are creating a fresh session
    if (theSession == null)
      _session = new Debrief.GUI.Frames.Swing.SwingSession(_parent, _parent.getClipboard(), null);
    else
      _session = theSession;

    // do we know the fileName?
    if (fileName != null)
    {
      // has it already been set?
      _session.setFileName(fileName);
    }

    // and get the layers object for the session
    _theLayers = _session.getData();

    // define our handlers
    addHandler(new ProjectionHandler(_session));
    addHandler(new GUIHandler(_session));
    addHandler(new DebriefLayersHandler(_theLayers));

  }

  public final void elementClosed()
  {
    // session is complete
    addSession(_session);

    _session = null;

  }

  abstract public void addSession(Debrief.GUI.Frames.Session data);

  public static void exportThis(Debrief.GUI.Frames.Session session, org.w3c.dom.Element parent,
                                org.w3c.dom.Document doc)
  {
    org.w3c.dom.Element eSession = doc.createElement("session");

    // now the Layers
    DebriefLayersHandler.exportThis(session, eSession, doc);

    // now the projection
    Debrief.GUI.Views.PlainView pl = session.getCurrentView();
    if (pl instanceof Debrief.GUI.Views.AnalysisView)
    {
      Debrief.GUI.Views.AnalysisView av = (Debrief.GUI.Views.AnalysisView) pl;
      ProjectionHandler.exportProjection(av.getChart().getCanvas().getProjection(), eSession, doc);
    }

    // now the GUI
    GUIHandler.exportThis(session, eSession, doc);

    // send out the data
    parent.appendChild(eSession);
  }

}