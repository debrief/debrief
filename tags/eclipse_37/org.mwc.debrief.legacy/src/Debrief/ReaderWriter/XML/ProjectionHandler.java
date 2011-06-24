package Debrief.ReaderWriter.XML;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.Element;

import MWC.Algorithms.PlainProjection;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;


final class ProjectionHandler extends  MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  MWC.GenericData.WorldLocation _tl;
  MWC.GenericData.WorldLocation _br;
  String _type;
  double _border;
  boolean _relative;
  private final Debrief.GUI.Frames.Session _session;
  private Debrief.GUI.Tote.AnalysisTote _theTote = null;

  public ProjectionHandler(Debrief.GUI.Frames.Session destination)
  {
    // inform our parent what type of class we are
    super("projection");

    // store the session
    _session = destination;

    Debrief.GUI.Views.PlainView pv = _session.getCurrentView();
    if(pv instanceof Debrief.GUI.Views.AnalysisView)
    {
      Debrief.GUI.Views.AnalysisView av = (Debrief.GUI.Views.AnalysisView)pv;
      _theTote = av.getTote();
    }

    // handlers for the corners
    addHandler(new LocationHandler("tl"){
      public void setLocation(MWC.GenericData.WorldLocation res)
      {
        _tl = res;
      }
    });
    addHandler(new LocationHandler("br"){
      public void setLocation(MWC.GenericData.WorldLocation res)
      {
        _br = res;
      }
    });

    addAttributeHandler(new HandleAttribute("Type"){
      public void setValue(String name, String val)
      {
        _type = val;
      }
    });
    addAttributeHandler(new HandleAttribute("Border"){
      public void setValue(String name, String val)
      {
        try{
          _border = readThisDouble(val);
        }
        catch(java.text.ParseException pe)
        {
          MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in border size for projection:" + val);
        }
      }
    });
    addAttributeHandler(new HandleBooleanAttribute("Relative"){
      public void setValue(String name, boolean value)
      {
        _relative = value;
      }});


  }



  public final void elementClosed()
  {
    MWC.Algorithms.PlainProjection newProj=null;
    if(_type.equals("Flat"))
    {
      newProj = new MWC.Algorithms.Projections.FlatProjection();
      newProj.setDataBorder(_border);
      newProj.setDataArea(new MWC.GenericData.WorldArea(_tl, _br));
      newProj.setPrimaryOriented(_relative);
      if(_theTote != null)
        newProj.setRelativeProjectionParent(_theTote);
    }

    if(newProj != null)
    {
      Debrief.GUI.Views.PlainView pv = _session.getCurrentView();
      if(pv instanceof Debrief.GUI.Views.AnalysisView)
      {
        Debrief.GUI.Views.AnalysisView av = (Debrief.GUI.Views.AnalysisView)pv;

        // get any listeners for the current projection
        PlainProjection oldProj = av.getChart().getCanvas().getProjection();

        // assign the new projection
        av.getChart().getCanvas().setProjection(newProj);

        // fire the updated event
        oldProj.firePropertyChange(PlainProjection.REPLACED_EVENT, oldProj, newProj);

      }
    }

  }

  public static void exportProjection(MWC.Algorithms.PlainProjection projection, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {

/*
<!ELEMENT projection (tl, br)>
<!ATTLIST projection
  type CDATA #REQUIRED
  border CDATA "1.0"
  relative (TRUE|FALSE) "FALSE"
>
*/

    Element proj = doc.createElement("projection");

    // see which type of projection it is
    if(projection instanceof MWC.Algorithms.Projections.FlatProjection)
    {
      MWC.Algorithms.Projections.FlatProjection flat = (MWC.Algorithms.Projections.FlatProjection)projection;

      // first the attributes for the projection
      proj.setAttribute("Type", "Flat");
      proj.setAttribute("Border", writeThis(flat.getDataBorder()));
      proj.setAttribute("Relative", writeThis(flat.getPrimaryOriented()));

      // and now the corners
      MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(flat.getDataArea().getTopLeft(), "tl", proj, doc);
      MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(flat.getDataArea().getBottomRight(), "br", proj, doc);

      parent.appendChild(proj);
    }
    else
    {
      // Hmm, we don't really know how tor handle this.
      java.lang.RuntimeException duffer = new java.lang.RuntimeException("Unable to store this projection type");
      throw duffer;
    }



  }


}