package ASSET.GUI.Workbench.Plotters;

import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.GUI.*;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

import java.awt.*;
import java.util.*;

/**
 * class providing scenario plotting, together with scenario controls
 */

public class ScenarioLayer extends MWC.GUI.BaseLayer implements ASSET.Scenario.ParticipantsChangedListener
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/***********************************************************************
   * member variables
   ***********************************************************************/
  /**
   * the scenario we are plotting
   */
  private ASSET.ScenarioType _myScenario = null;

  /**
   * whether to plot symbols
   */
  private boolean _plotSymbol = true;

  /**
   * whether to plot the current behaviour
   */
  private boolean _plotBehaviour = true;

  /**
   * whether to plot the name of the participant
   */
  private boolean _plotName = false;

  /**
   * whether to plot the current status
   */
  private boolean _plotStatus = false;


  /**
   * keep our own little register of symbols for participant types
   * - the method to retreive the symbol for a participant type is a compleicated one
   */
  static private HashMap _mySymbolRegister = new HashMap();

  /**
   * ********************************************************************
   * constructor
   * *********************************************************************
   */
  public ScenarioLayer()
  {
    super.setName("Scenario");
  }


  /**
   * ********************************************************************
   * member variables
   * *********************************************************************
   */
  public void setScenario(final ASSET.ScenarioType scenario)
  {
    // do we cancel listening to old scenario?
    if (_myScenario != null)
    {
      _myScenario.removeParticipantsChangedListener(this);
    }

    _myScenario = scenario;
    _myScenario.addParticipantsChangedListener(this);
  }

  /**
   * the indicated participant has been added to the scenario
   */
  public void newParticipant(final int index)
  {
    final ParticipantListener pl = new ParticipantListener(_myScenario.getThisParticipant(index), this);
    super.add(pl);
    pl.startListen();
  }

  /**
   * the scenario has restarted
   */
  public void restart()
  {
    // reset our plottables
    final Enumeration it = super.elements();
    while (it.hasMoreElements())
    {
      final ParticipantListener pl = (ParticipantListener) it.nextElement();
      pl.restart();
    }
  }

  /**
   * the indicated participant has been removed from the scenario
   */
  public void participantRemoved(final int index)
  {
    final java.util.Enumeration enumer = super.elements();
    while (enumer.hasMoreElements())
    {
      final ParticipantListener pl = (ParticipantListener) enumer.nextElement();
      if (pl.getId() == index)
      {
        super.removeElement(pl);
        pl.stopListen();
      }
    }
  }

  /**
   * ********************************************************************
   * BaseLayer support
   * *********************************************************************
   */
  public boolean hasEditor()
  {
    return true;
  }

  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new ScenarioPlotterInfo(this);

    return _myEditor;
  }

  public boolean getShowName()
  {
    return _plotName;
  }

  public boolean getShowSymbol()
  {
    return _plotSymbol;
  }

  public boolean getShowActivity()
  {
    return _plotBehaviour;
  }

  public void setShowName(final boolean val)
  {
    _plotName = val;
  }

  public void setShowSymbol(final boolean val)
  {
    _plotSymbol = val;
  }

  public void setShowActivity(final boolean val)
  {
    _plotBehaviour = val;
  }

  public boolean getShowStatus()
  {
    return _plotStatus;
  }

  public void setShowStatus(boolean plotStatus)
  {
    this._plotStatus = plotStatus;
  }

  public void setScenarioStepTime(Duration val)
  {
    _myScenario.setScenarioStepTime((int) val.getValueIn(Duration.MILLISECONDS));
  }

  public void setStepTime(Duration val)
  {
    _myScenario.setStepTime((int) val.getValueIn(Duration.MILLISECONDS));
  }

  public Duration getScenarioStepTime()
  {
    return new Duration(_myScenario.getScenarioStepTime(), Duration.MILLISECONDS);
  }

  public Duration getStepTime()
  {
    return new Duration(_myScenario.getStepTime(), Duration.MILLISECONDS);
  }


  /***************************************************************
   *  editable data for this plotter
   ***************************************************************/
  ////////////////////////////////////////////////////////////////////////////
  //  embedded class, used for editing the plotter
  ////////////////////////////////////////////////////////////////////////////
  /**
   * the definition of what is editable about this object
   */
  public class ScenarioPlotterInfo extends Editable.EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public ScenarioPlotterInfo(final ScenarioLayer data)
    {
      super(data, data.getName(), "Edit");
    }

    /**
     * editable GUI properties for our participant
     *
     * @return property descriptions
     */
    public java.beans.PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final java.beans.PropertyDescriptor[] res = {
          prop("ShowSymbol", "show symbol for participants"),
          prop("ShowName", "show name for participants"),
          prop("ShowStatus", "show the current vessel status"),
          prop("ShowActivity", "show current activity for participants"),
          prop("StepTime", "time interval between auto steps"),
          prop("ScenarioStepTime", "model step time"),
        };
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }

  }


  /**
   * ********************************************************************
   * class to handle listening to participants
   * *********************************************************************
   */
  public static class ParticipantListener implements ASSET.Participants.ParticipantMovedListener,
    MWC.GUI.Plottable,
    ASSET.Participants.ParticipantDetectedListener,
    Layer

  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
     * the last location we received for this participant
     */
    private MWC.GenericData.WorldLocation _curLocation = null;

    /**
     * the participant we are watching
     */
    private ASSET.ParticipantType _myPart = null;

    /**
     * the editor we store
     */
    transient private MWC.GUI.Editable.EditorType _myEditor = null;

    /**
     * the scenario layer which contains us (we access the
     * show symbol,name,activity from this layer)
     */
    private ScenarioLayer _myParent = null;

    /**
     * the most recent list of detections
     */
    private DetectionList _myDetections;

    /**
     * whether to plot detections or not
     */
    private boolean _showDetections = false;

    /**
     * whether we're visible
     */
    private boolean _visible = true;

    /**
     * ************************************************************
     * constructor
     * *************************************************************
     */
    public ParticipantListener(final ASSET.ParticipantType part, final ScenarioLayer parent)
    {
      // remember the participant
      _myPart = part;

      // try to get the initial location
      final ASSET.Participants.Status stat = part.getStatus();
      if (stat != null)
        _curLocation = stat.getLocation();

      _myParent = parent;
    }

    /**
     * ************************************************************
     * methods
     * *************************************************************
     */
    public void startListen()
    {
      _myPart.addParticipantMovedListener(this);
      _myPart.addParticipantDetectedListener(this);
    }

    public void stopListen()
    {
      _myPart.removeParticipantMovedListener(this);
      _myPart.removeParticipantDetectedListener(this);
    }

    public int getId()
    {
      return _myPart.getId();
    }


    /**
     * the scenario has restarted
     */
    public void restart()
    {
      _curLocation = _myPart.getStatus().getLocation();
    }


    /**
     * this participant has moved
     */
    public void moved(final ASSET.Participants.Status newStatus)
    {
      _curLocation = newStatus.getLocation();
    }

    /**
     * pass on the list of new detections
     */
    public void newDetections(final DetectionList detections)
    {
      _myDetections = detections;
    }


    public MWC.GenericData.WorldLocation getLocation()
    {
      return _myPart.getStatus().getLocation();
    }

    public java.awt.Color getColor()
    {
      if (_myPart.getCategory().getForce().equals(ASSET.Participants.Category.Force.BLUE))
        return java.awt.Color.blue;
      else if (_myPart.getCategory().getForce().equals(ASSET.Participants.Category.Force.GREEN))
        return java.awt.Color.green;
      else
        return java.awt.Color.red;
    }

    public String toString()
    {
      return _myPart.getName();
    }


    //////////////////////////////////////////////////
    // layer support
    //////////////////////////////////////////////////

    //////////////////////////////////////////////////
    // accessors
    //////////////////////////////////////////////////

    /**
     * accessor to get the participant
     */
    public ASSET.ParticipantType getParticipant()
    {
      return _myPart;
    }

    /**
     * the detections part of the painting
     */
    private void paintDetections(final MWC.GUI.CanvasType dest, Point pt)
    {

      if (_myDetections != null)
      {
        final int len = _myDetections.size();
        for (int i = 0; i < len; i++)
        {
          final DetectionEvent de = _myDetections.getDetection(i);

          // do we have bearing?
          final Float brg = de.getBearing();
          if (brg != null)
          {
            // do we have range?
            final WorldDistance rng = de.getRange();
            if (rng != null)
            {
              // hey, plot it!
              final WorldVector wv = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(brg.floatValue()), rng.getValueIn(WorldDistance.YARDS), 0);
              final WorldLocation other_end = _curLocation.add(wv);
              // take copy of original location, since we don't want to over-write it
              pt = new Point(pt);
              final Point pt2 = dest.toScreen(other_end);
              dest.drawLine(pt.x, pt.y, pt2.x, pt2.y);
            }
          }

        }
      }
    }

    /**
     * paint this object to the specified canvas
     */
    public void paint(final MWC.GUI.CanvasType dest)
    {
      if (getVisible())
      {
        final MWC.GenericData.WorldLocation loc = getLocation();
        if (loc != null)
        {
          final java.awt.Point pt = dest.toScreen(loc);

          if (_myParent._plotSymbol)
          {

            // see if we can remember this symbol
            PlainSymbol sym = (PlainSymbol) _mySymbolRegister.get(_myPart.getCategory().getType());
            if (sym == null)
            {
              // bugger. we haven't had this one before. retrieve it the long way
              sym = MWC.GUI.Shapes.Symbols.SymbolFactory.createSymbol(_myPart.getCategory().getType());

              // ok, and remember it
              _mySymbolRegister.put(_myPart.getCategory().getType(), sym);
            }


            if (sym != null)
            {
              sym.setColor(getColor());
              sym.setScaleVal(MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor.LARGE);
              sym.paint(dest, loc, MWC.Algorithms.Conversions.Degs2Rads(_myPart.getStatus().getCourse()));
            }
            else
            {
              dest.setColor(getColor());
              dest.drawRect(pt.x, pt.y, 1, 1);
            }
          }
          else
          {
            dest.setColor(getColor());

            dest.drawRect(pt.x, pt.y, 1, 1);
          }

          // now for the activity
          if (_myParent._plotBehaviour)
          {
            final String act = _myPart.getActivity();
            dest.drawText(act, pt.x + 8, pt.y + 2);
          }

          // and the name
          if (_myParent._plotName)
          {
            final String nm = _myPart.getName();
            final int wid = dest.getStringWidth(null, nm);
            dest.drawText(nm, pt.x - wid / 2, pt.y + 16);
          }

          // and the name
          if (_myParent._plotStatus)
          {
            final Status theStat = _myPart.getStatus();
            final String statString = theStat.statusString();
            final int wid = dest.getStringWidth(null, statString);
            dest.drawText(statString, pt.x - wid / 2, pt.y - 12);
          }

          // lastly the detections
          if (_showDetections)
          {
            paintDetections(dest, pt);
          }
        }
      }
    }

    /**
     * find the data area occupied by this item
     */
    public MWC.GenericData.WorldArea getBounds()
    {
      MWC.GenericData.WorldArea res = null;
      if (_curLocation != null)
        res = new MWC.GenericData.WorldArea(_curLocation, _curLocation);

      return res;
    }

    /**
     * it this item currently visible?
     */
    public boolean getVisible()
    {
      return _visible;
    }

    /**
     * set the visibility of this item
     */
    public void setVisible(boolean val)
    {
      _visible = val;
    }

    /**
     * whether to plot contacts
     */
    public void setShowContacts(boolean val)
    {
      _showDetections = true;
    }

    /**
     * whether to plot contacts
     */
    public boolean getShowContacts()
    {
      return _showDetections;
    }


    /**
     * how far away are we from this point.
     * Return null if it can't be calculated
     */
    public double rangeFrom(final MWC.GenericData.WorldLocation other)
    {
      double res = INVALID_RANGE;
      if (_curLocation != null)
        res = _curLocation.rangeFrom(other);
      return res;
    }

    /**
     * the name of this object
     *
     * @return the name of this editable object
     */
    public String getName()
    {
      return _myPart.getName() + ":" + _myPart.getActivity() + " " + _myPart.getStatus().statusString();
    }

    public String getParticipantName()
    {
      return _myPart.getName();
    }

    /**
     * whether there is any edit information for this item
     * this is a convenience function to save creating the EditorType data
     * first
     *
     * @return yes/no
     */
    public boolean hasEditor()
    {
      return true;
    };

    /**
     * get the editor for this item
     *
     * @return the BeanInfo data for this editable object
     */
    public MWC.GUI.Editable.EditorType getInfo()
    {
      if (_myEditor == null)
        _myEditor = new ParticipantInfo(this);

      return _myEditor;
    }


    ////////////////////////////////////////////////////////////////////////////
    //  embedded class, used for editing the projection
    ////////////////////////////////////////////////////////////////////////////
    /**
     * the definition of what is editable about this object
     */
    public class ParticipantInfo extends MWC.GUI.Editable.EditorType
    {

      /**
       * constructor for editable details of a set of Layers
       *
       * @param data the Layers themselves
       */
      public ParticipantInfo(final ParticipantListener data)
      {
        super(data, data.getParticipantName(), "");
      }


      /**
       * return a description of this bean, also specifies the custom editor we use
       *
       * @return the BeanDescriptor
       */
      public java.beans.BeanDescriptor getBeanDescriptor()
      {
        final java.beans.BeanDescriptor bp = new java.beans.BeanDescriptor(ParticipantListener.class,
                                                                           ASSET.GUI.Editors.VesselPane.class);
        bp.setDisplayName(super.getData().toString());
        return bp;
      }

      /**
       * editable GUI properties for our participant
       *
       * @return property descriptions
       */
      public java.beans.PropertyDescriptor[] getPropertyDescriptors()
      {
        try
        {
          final java.beans.PropertyDescriptor[] res = {
            prop("ShowContacts", "show contacts for this participant"),
            prop("Visible", "whether to show this participant"),
          };
          return res;
        }
        catch (java.beans.IntrospectionException e)
        {
          return super.getPropertyDescriptors();
        }
      }
    }


    /** do the comparison
     * 
     * @param arg0
     * @return
     */
		public int compareTo(Object arg0)
		{
			ParticipantListener other = (ParticipantListener) arg0;
			String otherName = other.getName();
			return getName().compareTo(otherName);
		}

		public void add(Editable point)
		{
		}

		public void append(Layer other)
		{
		}

		public Enumeration elements()
		{
			Vector theElements = new Vector(3,1);

			// ok, sort out our child elements
			Editable performance = new PerformancePlottable(_myPart.getMovementChars());
			theElements.add(performance);

			if(_myPart.getSensorFit() != null)
			{
				Editable sensors = new SensorsPlottable(_myPart.getSensorFit());
				theElements.add(sensors);
			}
			if(_myPart.getDecisionModel() != null)
			{
				Editable behaviours = new BehavioursPlottable(_myPart.getDecisionModel());
				theElements.add(behaviours);
			}
			
			
			// ok, wrap our items
			return theElements.elements();
		}

		public void exportShape()
		{
			}

		public int getLineThickness()
		{
			// TODO Auto-generated method stub
			return 1;
		}

		public void removeElement(Editable point)
		{
		}

		public void setName(String val)
		{
		}

  }

  //////////////////////////////////////////////////
  // testing support
  //////////////////////////////////////////////////
  public static class LayerTest extends SupportTesting.EditableTesting
  {
    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      ScenarioLayer layer = new ScenarioLayer();
      CoreScenario cs = new CoreScenario();
      layer.setScenario(cs);
      layer.setStepTime(new Duration(12, Duration.SECONDS));
      layer.setScenarioStepTime(new Duration(12, Duration.SECONDS));
      return layer;
    }
  }

  public static class LayerListenerTest extends SupportTesting.EditableTesting
  {
    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      SSN ssn = new SSN(12);
      Editable listener = new ParticipantListener(ssn, null);
      return listener;
    }
  }

}