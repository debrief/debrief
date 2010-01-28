package ASSET.GUI.Workbench.Plotters;

import java.awt.Point;
import java.util.Enumeration;
import java.util.Vector;

import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import ASSET.Participants.Status;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * ******************************************************************** class to
 * handle listening to participants
 * *********************************************************************
 */
public class ScenarioParticipantWrapper implements
		ASSET.Participants.ParticipantMovedListener,
		ASSET.Participants.ParticipantDetectedListener, Layer

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
	 * the scenario layer which contains us (we access the show
	 * symbol,name,activity from this layer)
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
	 * utility to represent us as a vector
	 */
	private Vector<Editable> _theElements;

	/**
	 * sort out if we have created sensor fit in our elements array
	 */
	private boolean _noSensorFit = true;

	/**
	 * sort out if we have created decision model in our elements array
	 */
	private boolean _noDecisionModel = true;

	/**
	 * sort out if we have created rad chars in our elements array
	 */
	private boolean _noRadChars = true;

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */
	public ScenarioParticipantWrapper(final ASSET.ParticipantType part,
			final ScenarioLayer parent)
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
	 * ************************************************************ methods
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
	public void restart(ScenarioType scenario)
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
	
	public boolean hasOrderedChildren()
	{
		return false;
	}
	
	public static java.awt.Color getColorFor(String force)
	{
		if (force.equals(ASSET.Participants.Category.Force.BLUE))
			return java.awt.Color.blue;
		else if (force.equals(
				ASSET.Participants.Category.Force.GREEN))
			return java.awt.Color.green;
		else
			return java.awt.Color.red;
		
	}
	
	public java.awt.Color getColor()
	{
		return getColorFor(_myPart.getCategory().getForce());
	}

	public String toString()
	{
		return _myPart.getName();
	}

	// ////////////////////////////////////////////////
	// layer support
	// ////////////////////////////////////////////////

	// ////////////////////////////////////////////////
	// accessors
	// ////////////////////////////////////////////////

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
						final WorldVector wv = new WorldVector(MWC.Algorithms.Conversions
								.Degs2Rads(brg.floatValue()), rng.getValueIn(WorldDistance.YARDS), 0);
						final WorldLocation other_end = _curLocation.add(wv);
						// take copy of original location, since we don't want to over-write
						// it
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

				if (_myParent.getShowSymbol())
				{

					// see if we can remember this symbol
					PlainSymbol sym = (PlainSymbol) _myParent.getSymbolRegister().get(
							_myPart.getCategory().getType());
					if (sym == null)
					{
						// bugger. we haven't had this one before. retrieve it the long way
						sym = MWC.GUI.Shapes.Symbols.SymbolFactory.createSymbol(_myPart.getCategory()
								.getType());

						// ok, and remember it
						_myParent.getSymbolRegister().put(_myPart.getCategory().getType(), sym);
					}

					if (sym != null)
					{
						sym.setColor(getColor());
						sym.setScaleVal(MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor.LARGE);
						sym.paint(dest, loc, MWC.Algorithms.Conversions.Degs2Rads(_myPart.getStatus()
								.getCourse()));
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
				if (_myParent.getShowActivity())
				{
					final String act = _myPart.getActivity();
					final int ht = dest.getStringHeight(null);
					dest.drawText(act, pt.x + 16, pt.y - ht / 2);
				}

				// and the name
				if (_myParent.getShowName())
				{
					final String nm = _myPart.getName();
					final int wid = dest.getStringWidth(null, nm);
					final int ht = dest.getStringHeight(null);
					dest.drawText(nm, pt.x - wid / 2, pt.y + ht - 2);
				}

				// and the name
				if (_myParent.getShowStatus())
				{
					final Status theStat = _myPart.getStatus();
					final String statString = theStat.statusString();
					final int wid = dest.getStringWidth(null, statString);
					dest.drawText(statString, pt.x - wid / 2, pt.y - 22);
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
	 * how far away are we from this point. Return null if it can't be calculated
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
		return _myPart.getName();
		// + ":" + _myPart.getActivity() + " " + _myPart.getStatus().statusString();
	}

	public String getParticipantName()
	{
		return _myPart.getName();
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 * 
	 * @return yes/no
	 */
	public boolean hasEditor()
	{
		return true;
	}

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

	// //////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing the projection
	// //////////////////////////////////////////////////////////////////////////
	/**
	 * the definition of what is editable about this object
	 */
	public class ParticipantInfo extends MWC.GUI.Editable.EditorType
	{

		/**
		 * constructor for editable details of a set of Layers
		 * 
		 * @param data
		 *          the Layers themselves
		 */
		public ParticipantInfo(final ScenarioParticipantWrapper data)
		{
			super(data, data.getParticipantName(), "");
		}

		/**
		 * return a description of this bean, also specifies the custom editor we
		 * use
		 * 
		 * @return the BeanDescriptor
		 */
		public java.beans.BeanDescriptor getBeanDescriptor()
		{
			final java.beans.BeanDescriptor bp = new java.beans.BeanDescriptor(
					ScenarioParticipantWrapper.class, ASSET.GUI.Editors.VesselPane.class);
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
						prop("Name", "name of this participant"), };
				return res;
			}
			catch (java.beans.IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 * do the comparison
	 * 
	 * @param arg0
	 * @return
	 */
	public int compareTo(Plottable arg0)
	{
		ScenarioParticipantWrapper other = (ScenarioParticipantWrapper) arg0;
		String otherName = other.getName();
		return getName().compareTo(otherName);
	}

	public void add(Editable point)
	{
	}

	public void append(Layer other)
	{
	}

	public Enumeration<Editable> elements()
	{
		if (_theElements == null)
		{
			_theElements = new Vector<Editable>(3, 1);

			// ok add the movement chars
			Editable moveChars = new MoveCharsPlottable(_myPart.getMovementChars(), _myParent);
			_theElements.add(moveChars);
		}

		// sensors
		if (_noSensorFit)
			if (_myPart.getSensorFit() != null)
			{
				Editable sensors = new SensorsPlottable(_myPart.getSensorFit(), _myParent);
				_theElements.add(sensors);
				_noSensorFit = false;
			}

		// decision model
		if (_noDecisionModel)
			if (_myPart.getDecisionModel() != null)
			{
				Editable behaviours = new BehavioursPlottable(_myPart.getDecisionModel(),
						_myParent);
				_theElements.add(behaviours);
				_noDecisionModel = false;
			}

		// rad noise model
		if (_noRadChars)
			if (_myPart.getRadiatedChars() != null)
			{
				RadiatedCharacteristics chars = _myPart.getRadiatedChars();
				Editable wrappedChart = new RadCharsPlottable(chars, _myParent);
				_theElements.add(wrappedChart);
				_noRadChars = false;
			}

		// ok, wrap our items
		return _theElements.elements();
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
		_myPart.setName(val);
	}

}
