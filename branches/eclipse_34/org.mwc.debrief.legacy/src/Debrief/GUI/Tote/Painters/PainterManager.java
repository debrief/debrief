package Debrief.GUI.Tote.Painters;

import Debrief.GUI.Tote.*;
import MWC.GUI.Editable;
import MWC.GUI.StepperListener;
import MWC.GenericData.HiResDate;

import java.util.*;
import java.beans.*;

public final class PainterManager implements StepperListener, Editable
{
  ///////////////////////////////////
  // member variables
  //////////////////////////////////
	private static Vector _thePainters;
	private StepperListener _current;
  private java.beans.PropertyChangeSupport _pSupport;
  transient private MWC.GUI.Editable.EditorType  _myEditor = null;
  ///////////////////////////////////
  // constructor
  //////////////////////////////////
	public PainterManager(StepControl stepper)
	{
    _thePainters = new Vector(0,1);

    _pSupport = new PropertyChangeSupport(this);

		// register with the stepper
		stepper.addStepperListener(this);
	}


  ///////////////////////////////////
  // member functions
  //////////////////////////////////

  /** provide method to clear out local data
   *
   */
  public final void closeMe()
  {
    _current = null;
    _pSupport = null;
    _thePainters.removeAllElements();
    _myEditor = null;
  }

  public final void addPropertyChangeListener(java.beans.PropertyChangeListener listener)
  {
    _pSupport.addPropertyChangeListener(listener);
  }

  public final void removePropertyChangeListener(java.beans.PropertyChangeListener listener)
  {
    _pSupport.removePropertyChangeListener(listener);
  }

  private void firePropertyChange()
  {
    _pSupport.firePropertyChange("Painter Change", null, null);
  }

	public final void addPainter(StepperListener listener)
	{
		_thePainters.addElement(listener);
    firePropertyChange();
	}

	public final void removePainter(final StepperListener listener)
	{
		_thePainters.removeElement(listener);
    firePropertyChange();
	}

	public final void setCurrentListener(final StepperListener listener)
	{
		// tell the current one it is now 'off'
		if(_current != null)
			_current.steppingModeChanged(false);

		// and assign the new one
		_current = listener;

		// tell the new one it is now on
		_current.steppingModeChanged(true);

    firePropertyChange();

	}

	public final void setDisplay(final String listener)
	{
		final Enumeration iter = _thePainters.elements();
		while(iter.hasMoreElements())
		{
			final StepperListener l = (StepperListener) iter.nextElement();
			if(l.toString().equals(listener))
			{
				setCurrentListener(l);
				break;
			}
		}
	}

	public final StepperListener getCurrentPainterObject()
	{
		return _current;
	}

	public final String getDisplay()
	{
		final String res;
		if(_current != null)
		{
			res = _current.toString();
		}
		else
			res = null;

		return res;
	}

	public static String[] getListeners()
	{
		String[] strings = null;
		final Vector res = new Vector(0,1);
		final Enumeration iter = _thePainters.elements();
		while(iter.hasMoreElements())
		{
			final StepperListener l = (StepperListener) iter.nextElement();
			res.addElement(l.toString());
		}

		// are there any results?
		if(res.size()>0)
		{
			strings = new String[res.size()];
			res.copyInto(strings);
		}

		return strings;
	}

  ///////////////////////////////////
  // stepper listener classes
  //////////////////////////////////
	public final void steppingModeChanged(final boolean on)
	{
		if(_current != null)
			_current.steppingModeChanged(on);
	}

	public final void newTime(final HiResDate oldDTG, final HiResDate newDTG, final MWC.GUI.CanvasType canvas)
	{
		if(_current != null)
			_current.newTime(oldDTG, newDTG, canvas);
	}

	public final String toString()
	{
		return "Painter Manager";
	}


  ////////////////////////////////////////////////////////////
  // 'editable' methods
  ////////////////////////////////////////////////////////////


	public final String getName()
	{
		return "Painter Manager";
	}

	public final boolean hasEditor()
	{
		return true;
	}

	public final MWC.GUI.Editable.EditorType getInfo()
	{
    if(_myEditor == null)
      _myEditor = new PainterManagerInfo(this);

    return _myEditor;
	}

  ////////////////////////////////////////////////////////////
  // nested class describing how to edit this class
  ////////////////////////////////////////////////////////////
  public static final class PainterManagerInfo extends Editable.EditorType
  {

    public PainterManagerInfo(final PainterManager data)
    {
      super(data, "Tote Painter", "");
    }

    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try{
        final PropertyDescriptor[] res={
          longProp("Display", "the current display mode", TagListEditor.class),
        };
        return res;
      }
      catch(Exception e)
      {
        e.printStackTrace();
        return super.getPropertyDescriptors();
      }

    }

	}

  ////////////////////////////////////////////////////////////
  // property editor to return the painters as a combo box
  ////////////////////////////////////////////////////////////

	public static final class TagListEditor extends PropertyEditorSupport
	{
		// the working copy we are editing
    String current;

		/** return a tag list of the current editors
		 */
	  public final String[] getTags()
	  {
			return getListeners();
	  }

	  public final Object getValue()
	  {
	    return current;
	  }

		public final void setValue(final Object p1)
		{
		  if(p1 instanceof String)
		  {
		    final String val = (String) p1;
		    setAsText(val);
		  }
		}

	  public final void setAsText(final String p1)
	  {
			current = p1;
	  }

	  public final String getAsText()
	  {
			return current;
	  }
	}

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testMe extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE  = "UNIT";
    public testMe(final String val)
    {
      super(val);
    }
    public final void testMyParams()
    {
      final StepControl stepper = new Debrief.GUI.Tote.Swing.SwingStepControl(null,null,null,null, null, null);
      Editable ed = new PainterManager(stepper);
      Editable.editableTesterSupport.testParams(ed, this);
      ed = null;
    }
  }
}
