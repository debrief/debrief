package ASSET.GUI.SuperSearch.Plotters;

import java.util.Enumeration;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Scenario.MultiForceScenario;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldSpeed;

/**
 * Title:  SSGuiSupport
 * Description: Class collating progress of SuperSearch runs, ready for GUI plotting (General plotting)
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author Ian Mayo
 * @version 1.0
 */

public class SSGuiSupport extends MWC.GUI.BaseLayer implements ASSET.Scenario.ParticipantsChangedListener
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * ********************************************************************
   * member variables
   * *********************************************************************
   */
  private ASSET.Scenario.MultiForceScenario _myScenario = null;

  /**
   * the editor for this object
   */
  private Editable.EditorType _myEditor = null;

  /**
   * whether to plot a course/speed vector for targets
   */
  boolean _showVector = true;


  /**
   * whether to plot the name of targets
   */
  boolean _showNames = false;

  /**
   * whether to plot the activities of targets
   */
  boolean _showActivities = false;


  /**
   * ********************************************************************
   * constructor
   * *********************************************************************
   */
  public SSGuiSupport()
  {
    super.setName("SuperSearch plotting");
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

    _myScenario = (ASSET.Scenario.MultiForceScenario) scenario;
    _myScenario.addParticipantsChangedListener(this);
    _myScenario.addBlueParticipantsChangedListener(new ASSET.Scenario.ParticipantsChangedListener()
    {
      public void newParticipant(final int index)
      {
        newBlueParticipant(index);
      }

      public void participantRemoved(final int index)
      {
        blueParticipantRemoved(index);
      }

      public void restart(ScenarioType scenario)
      {
        ;
      }

    });
  }


  /**
   * accessor to wrap the supplied participant
   *
   * @param part the participant to wrap
   * @return the wrapped participant
   */
  public ParticipantListener getNewParticipantListener(ParticipantType part)
  {
    return new ParticipantListener(part, this);
  }

  void newBlueParticipant(final int index)
  {
    newParticipant(index);
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
   * the indicated participant has been removed from the scenario
   */
  public void participantRemoved(final int index)
  {
    final java.util.Enumeration<Editable> enumer = super.elements();
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
   * the indicated participant has been removed from the scenario
   */
  void blueParticipantRemoved(final int index)
  {
    participantRemoved(index);
  }

  /**
   * the scenario has restarted, process it
   */
  public void restart(ScenarioType scenario)
  {
    // pass through our elements, resetting them
    final Enumeration<Editable> it = super.elements();
    while (it.hasMoreElements())
    {
      final ParticipantListener pl = (ParticipantListener) it.nextElement();
      pl.restart(scenario);
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
      _myEditor = new GUIPlotterInfo(this);

    return _myEditor;
  }

  /**
   * whether to plot a course/speed vector for targets
   */
  public boolean getShowVector()
  {
    return _showVector;
  }

  /**
   * whether to plot a course/speed vector for targets
   */
  public void setShowVector(final boolean val)
  {
    _showVector = val;
  }

  /**
   * whether to show the name of the targets
   *
   * @return yes/no
   */
  public boolean getShowNames()
  {
    return _showNames;
  }

  /**
   * whether to show the name of the targets
   *
   * @param showNames yes/no
   */
  public void setShowNames(boolean showNames)
  {
    this._showNames = showNames;
  }

  /**
   * whether to show the Activities of the targets
   *
   * @return yes/no
   */
  public boolean getShowActivities()
  {
    return _showActivities;
  }

  /**
   * whether to show the Activities of the targets
   *
   * @param showActivities yes/no
   */
  public void setShowActivities(boolean showActivities)
  {
    this._showActivities = showActivities;
  }

  public void setStepTime(Duration duration)
  {
    _myScenario.setStepTime((int) duration.getValueIn(Duration.MILLISECONDS));
  }

  public Duration getStepTime()
  {
    return new Duration(_myScenario.getStepTime(), Duration.MILLISECONDS);
  }

  public void setScenarioStepTime(Duration duration)
  {
    _myScenario.setScenarioStepTime((int) duration.getValueIn(Duration.MILLISECONDS));
  }

  public Duration getScenarioStepTime()
  {
    return new Duration(_myScenario.getScenarioStepTime(), Duration.MILLISECONDS);
  }

  ////////////////////////////////////////////////////////////////////////////
  //  embedded class, used for editing the projection
  ////////////////////////////////////////////////////////////////////////////
  /**
   * the definition of what is editable about this object
   */
  public class GUIPlotterInfo extends Editable.EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public GUIPlotterInfo(final SSGuiSupport data)
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
          prop("ShowVector", "show course/speed vector for participants"),
          prop("ShowNames", "show participant names"),
          prop("StepTime", "time interval between auto steps"),
          prop("ShowActivities", "show participant activities"),
          prop("ScenarioStepTime", "model time step interval"),
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
  					MWC.GUI.Plottable
  {

    SSGuiSupport _myParent;

    private MWC.GenericData.WorldLocation _curLocation = null;

    private ASSET.ParticipantType _myPart = null;

    /**
     * the editor for this object
     */
    private Editable.EditorType _myEditor = null;

    public ParticipantListener(final ASSET.ParticipantType part, SSGuiSupport parent)
    {
      _myPart = part;
      _myParent = parent;
    }

    public void startListen()
    {
      _myPart.addParticipantMovedListener(this);
    }

    public void stopListen()
    {
      _myPart.removeParticipantMovedListener(this);
    }

    public int getId()
    {
      return _myPart.getId();
    }

    public ASSET.ParticipantType getParticipant()
    {
      return _myPart;
    }

    /**
     * this participant has moved
     */
    public void moved(final ASSET.Participants.Status newStatus)
    {
      _curLocation = newStatus.getLocation();
    }

    /**
     * the scenario has restarted
     */
    public void restart(ScenarioType scenario)
    {
      _curLocation = _myPart.getStatus().getLocation();
    }

    public MWC.GenericData.WorldLocation getLocation()
    {
      return _curLocation;
    }

    public java.awt.Color getColor()
    {
      if (_myPart.getCategory().getForce().equals(ASSET.Participants.Category.Force.BLUE))
        return java.awt.Color.blue;
      else
        return java.awt.Color.red;
    }

    /***********************************************************************
     *
     ***********************************************************************/

    /**
     * paint this object to the specified canvas
     */
    public void paint(final MWC.GUI.CanvasType dest)
    {
      final MWC.GenericData.WorldLocation loc = getLocation();
      if (loc != null)
      {
        dest.setColor(getColor());
        final java.awt.Point pt = dest.toScreen(loc);

        if (_myParent._showVector)
        {
          dest.drawRect(pt.x - 1, pt.y - 1, 2, 2);

          double crse = _myPart.getStatus().getCourse();
          crse = MWC.Algorithms.Conversions.Degs2Rads(crse);
          final double dx = Math.sin(crse) * _myPart.getStatus().getSpeed().getValueIn(WorldSpeed.M_sec);
          final double dy = Math.cos(crse) * 5d;
          dest.drawLine(pt.x, pt.y, pt.x + (int) dx, pt.y - (int) dy);
        }
        else
        {
          dest.drawRect(pt.x, pt.y, 1, 1);
        }

        if (_myParent._showNames)
        {
          dest.drawText(_myPart.getName(), pt.x + 1, pt.y - 1);
        }
        if (_myParent._showActivities)
        {
          dest.drawText(_myPart.getActivity(), pt.x + 1, pt.y + 4);
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
      return true;
    }

    /**
     * set the visibility of this item
     */
    public void setVisible(boolean val)
    {
      // ignore
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
    };

    public String toString()
    {
      return getName();
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
    public Editable.EditorType getInfo()
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
    public class ParticipantInfo extends Editable.EditorType
    {

      /**
       * constructor for editable details of a set of Layers
       *
       * @param data the Layers themselves
       */
      public ParticipantInfo(final ParticipantListener data)
      {
        super(data, data.getName(), "Edit");
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
    }

    /** implement comparison, use their names
     * 
     * @param arg0
     * @return
     */
		public int compareTo(Plottable arg0)
		{
			ParticipantListener other = (ParticipantListener) arg0;
			String otherName = other.getName();
			return this.getName().compareTo(otherName);
		}


  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public static class GuiSupportTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public GuiSupportTest(final String val)
    {
      super(val);
    }

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      SSGuiSupport ed = new SSGuiSupport();
      ed.setScenario(new MultiForceScenario());
      return ed;
    }
  }

  public static class PartInfoTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public PartInfoTest(final String val)
    {
      super(val);
    }

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      SSGuiSupport ed = new SSGuiSupport();
      ed.setScenario(new MultiForceScenario());
      return ed;
    }
  }

}