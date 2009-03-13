package ASSET.GUI.Workbench.Plotters;

import java.util.*;

import ASSET.ScenarioType;
import ASSET.Models.Vessels.SSN;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.Duration;

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
  static private HashMap<String, PlainSymbol> _mySymbolRegister = new HashMap<String, PlainSymbol>();

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
    final ScenarioParticipantWrapper pl = new ScenarioParticipantWrapper(_myScenario.getThisParticipant(index), this);
    super.add(pl);
    pl.startListen();
  }

  /**
   * the scenario has restarted
   */
  public void restart()
  {
    // reset our plottables
    final Enumeration<Editable> it = super.elements();
    while (it.hasMoreElements())
    {
      final ScenarioParticipantWrapper pl = (ScenarioParticipantWrapper) it.nextElement();
      pl.restart();
    }
  }

  /**
   * the indicated participant has been removed from the scenario
   */
  public void participantRemoved(final int index)
  {
    final java.util.Enumeration<Editable> enumer = super.elements();
    while (enumer.hasMoreElements())
    {
      final ScenarioParticipantWrapper pl = (ScenarioParticipantWrapper) enumer.nextElement();
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

  public ScenarioType getScenario()
  {
  	return _myScenario;
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
      Editable listener = new ScenarioParticipantWrapper(ssn, null);
      return listener;
    }
  }

	/**
	 * @return the _mySymbolRegister
	 */
	public static final HashMap<String, PlainSymbol> get_mySymbolRegister()
	{
		return _mySymbolRegister;
	}


	/**
	 * @param symbolRegister the _mySymbolRegister to set
	 */
	public static final void set_mySymbolRegister(HashMap<String, PlainSymbol> symbolRegister)
	{
		_mySymbolRegister = symbolRegister;
	}


	public HashMap<String, PlainSymbol> getSymbolRegister()
	{
		return _mySymbolRegister;
	}



}