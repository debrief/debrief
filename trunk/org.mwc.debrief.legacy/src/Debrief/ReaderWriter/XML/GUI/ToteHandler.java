package Debrief.ReaderWriter.XML.GUI;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 *
 * @author Ian Mayo
 * @version 1.0
 */


public final class ToteHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private final Debrief.GUI.Tote.AnalysisTote _myTote;
  private final MWC.GUI.Layers _theData;

  public ToteHandler(Debrief.GUI.Tote.AnalysisTote theTote, MWC.GUI.Layers theData)
  {
    // inform our parent what type of class we are
    super("tote");

    _myTote = theTote;

    if (_myTote == null)
      System.err.println("Tote information missing from Tote handler");

    _theData = theData;

    addHandler(new PrimarySecondaryHandler("primary")
    {
      public void setTrack(String name)
      {
        Debrief.Tools.Tote.WatchableList wa = getTrack(name);
        if (wa != null)
        {
          if (_myTote != null)
          {
            // set this as primary
            _myTote.setPrimary(wa);
          }
        }
      }
    });

    addHandler(new PrimarySecondaryHandler("secondary")
    {
      public void setTrack(String name)
      {
        Debrief.Tools.Tote.WatchableList wa = getTrack(name);
        if (wa != null)
        {
          if (_myTote != null)
          {
            // set this as primary
            _myTote.setSecondary(wa);
          }
        }
      }
    });

  }

  private Debrief.Tools.Tote.WatchableList getTrack(String name)
  {
    Debrief.Tools.Tote.WatchableList res = null;

    // look at the data
    MWC.GUI.Plottable ly = _theData.findLayer(name);

    if (ly == null)
    {
      // no, this isn't a top level layer, maybe it's an element

      // find the nearest editable item
      int num = _theData.size();
      for (int i = 0; i < num; i++)
      {
        MWC.GUI.Layer thisL = _theData.elementAt(i);
        // go through this layer
        java.util.Enumeration iter = thisL.elements();
        while (iter.hasMoreElements())
        {
          MWC.GUI.Plottable p = (MWC.GUI.Plottable) iter.nextElement();
          String nm = p.getName();
          if (nm.equals(name))
          {
            ly = p;
            break;
          }
        }
      }

    }

    if (ly instanceof Debrief.Tools.Tote.WatchableList)
    {
      res = (Debrief.Tools.Tote.WatchableList) ly;
    }

    return res;
  }

  public static void exportTote(Debrief.GUI.Frames.Session session, org.w3c.dom.Element parent,
                                org.w3c.dom.Document doc)
  {
    // create the element to put it in
    org.w3c.dom.Element tote = doc.createElement("tote");
    Debrief.GUI.Views.PlainView pv = session.getCurrentView();
    Debrief.GUI.Views.AnalysisView av = null;
    if (pv instanceof Debrief.GUI.Views.AnalysisView)
    {
      av = (Debrief.GUI.Views.AnalysisView) pv;
    }

    if (av == null)
      return;

    // get the tote itself
    Debrief.GUI.Tote.AnalysisTote _theTote = av.getTote();


    // now output the parts of the tote
    // find the primary
    Debrief.Tools.Tote.WatchableList primary = _theTote.getPrimary();
    java.util.Vector secondaries = _theTote.getSecondary();

    if (primary != null)
    {
      org.w3c.dom.Element pri = doc.createElement("primary");
      pri.setAttribute("Name", primary.getName());
      tote.appendChild(pri);
    }

    if (secondaries != null)
    {
      if (secondaries.size() > 0)
      {
        java.util.Enumeration iter = secondaries.elements();
        while (iter.hasMoreElements())
        {
          Debrief.Tools.Tote.WatchableList was = (Debrief.Tools.Tote.WatchableList) iter.nextElement();
          org.w3c.dom.Element sec = doc.createElement("secondary");
          sec.setAttribute("Name", was.getName());
          tote.appendChild(sec);
        }
      }
    }

    //////////////////////////////
    // and finally add ourselves to the parent
    parent.appendChild(tote);
  }

}