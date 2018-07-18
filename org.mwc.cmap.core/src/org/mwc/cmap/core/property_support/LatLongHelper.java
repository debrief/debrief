/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.core.property_support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mwc.cmap.core.CorePlugin;

import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.BriefFormatLocation;
import MWC.Utilities.TextFormatting.DebriefFormatLocation;

public class LatLongHelper extends EditorHelper
{

	/**
	 * remember how to format items on line
	 */
	protected static DecimalFormat _floatFormat = new DecimalFormat("0.0000", new DecimalFormatSymbols(Locale.UK));
	private HashMap<WorldLocation, LatLongPropertySource> _myLocations;

	/**
	 * constructor. just declare our object type
	 */
	public LatLongHelper()
	{
		super(WorldLocation.class);
	}

	/**
	 * we define a custom cell editor just to get the "Paste" button
	 * 
	 * @param parent
	 * @return
	 */
	public CellEditor getCellEditorFor(final Composite cellParent)
	{
		final DialogCellEditor res = new PasteLocationDialogCellEditor(cellParent);

		return res;
	}

	@SuppressWarnings("rawtypes")
	public boolean editsThis(final Class target)
	{
		return (target == WorldLocation.class);
	}

  public Object translateToSWT(final Object orig)
	{
		final WorldLocation value = (WorldLocation) orig;
    
    // do we have a list?
    if (_myLocations == null)
      _myLocations = new HashMap<WorldLocation, LatLongPropertySource>();

		LatLongPropertySource res = null;

		// do we know this one already?
		res = _myLocations.get(value);
		if (res == null)
		{
			res = new LatLongPropertySource((WorldLocation) value);
			_myLocations.put(value, res);
		}
		
		// ok, refresh the extracted values
		res.refresh();

		// ok, we've received a location. Return our new property source
		// representing a
		// DTG
		return res;
	}

	public Object translateFromSWT(final Object value)
	{
		final LatLongPropertySource res = (LatLongPropertySource) value;
		return res.getValue();
	}

	public ILabelProvider getLabelFor(final Object currentValue)
	{
		final ILabelProvider label1 = new LabelProvider()
		{
			public String getText(final Object element)
			{
				final LatLongPropertySource val = (LatLongPropertySource) element;
				return val.toString();
			}

			public Image getImage(final Object element)
			{
				return null;
			}

		};
		return label1;
	}

	/**
	 * @param loc
	 */
	public static class LatLongPropertySource implements IPropertySource2
	{

		private final PropertyChangeSupport _pSupport;

		/**
		 * the working values
		 */
		private String _latDeg;

		private String _latMin;

		private String _latSec;

		private String _latHem;

		private String _longDeg;

		private String _longMin;

		private String _longSec;

		private String _longHem;

		private WorldDistance _depth;

		/**
		 * the original values
		 */

		private String _origLatDeg;

		private String _origLatMin;

		private String _origLatSec;

		private String _origLatHem;

		private String _origLongDeg;

		private String _origLongMin;

		private String _origLongSec;

		private String _origLongHem;

		private WorldDistance _origDepth;

		final private WorldLocation _originalLocation;

		/**
		 * name for the lat & long properties
		 */
		public static String ID_LAT_DEG = "LAT_DEG";

		public static String ID_LAT_MIN = "LAT_MIN";

		public static String ID_LAT_SEC = "LAT_SEC";

		public static String ID_LAT_HEM = "LAT_HEM";

		public static String ID_LONG_DEG = "LONG_DEG";

		public static String ID_LONG_MIN = "LONG_MIN";

		public static String ID_LONG_SEC = "LONG_SEC";

		public static String ID_LONG_HEM = "LONG_HEM";

		public static String ID_DEPTH = "DEPTH";

		protected static IPropertyDescriptor[] descriptors;

		protected static String[] _latCats;

		protected static String[] _longCats;

		public static class CategorisedDescriptor extends TextPropertyDescriptor
		{
			public CategorisedDescriptor(final String id, final String title, final String cat)
			{
				super(id, title);
				super.setCategory(cat);
			}
		}

		static
		{
			_latCats = new String[]
			{ "N", "S" };
			_longCats = new String[]
			{ "E", "W" };

			descriptors = new IPropertyDescriptor[]
			{
					new CategorisedDescriptor(ID_LAT_DEG, "1. Lat Degrees", "Lat"),
					new CategorisedDescriptor(ID_LAT_MIN, "2. Lat Minutes", "Lat"),
					new CategorisedDescriptor(ID_LAT_SEC, "3. Lat Seconds", "Lat"),
					new ComboBoxPropertyDescriptor(ID_LAT_HEM, "4. Lat Hemisphere",
							_latCats),
					new CategorisedDescriptor(ID_LONG_DEG, "5. Long Degrees", "Long"),
					new CategorisedDescriptor(ID_LONG_MIN, "6. Long Minutes", "Long"),
					new CategorisedDescriptor(ID_LONG_SEC, "7. Long Seconds", "Long"),
					new ComboBoxPropertyDescriptor(ID_LONG_HEM, "8. Long Hemisphere",
							_longCats),
					new CategorisedDescriptor(ID_DEPTH, "9. Depth", "Depth")
					{
						public CellEditor createPropertyEditor(final Composite parent)
						{
							return new WorldDistanceHelper.WorldDistanceCellEditor(parent);
						}
					},

			};
		}

		public LatLongPropertySource(final WorldLocation location)
		{
			_originalLocation = location;

			// extract the fields
			refresh();
			
			// sort out property change support
			_pSupport = new PropertyChangeSupport(this);
		}

		/** convenience function, to initialise (or refresh) the child fields
		 * 
		 */
		public void refresh()
		{
		  final DebriefFormatLocation.brokenDown bLat = new DebriefFormatLocation.brokenDown(
		      _originalLocation.getLat(), true);
      final DebriefFormatLocation.brokenDown bLong = new DebriefFormatLocation.brokenDown(
          _originalLocation.getLong(), false);

      _latDeg = _origLatDeg = "" + bLat.deg;
      _latMin = _origLatMin = "" + bLat.min;
      _latSec = _origLatSec = _floatFormat.format(bLat.sec);
      _latHem = _origLatHem = "" + bLat.hem;

      _longDeg = _origLongDeg = "" + bLong.deg;
      _longMin = _origLongMin = "" + bLong.min;
      _longSec = _origLongSec = _floatFormat.format(bLong.sec);
      _longHem = _origLongHem = "" + bLong.hem;

      _depth = _origDepth = new WorldDistance(_originalLocation.getDepth(),
          WorldDistance.METRES);
		}
		

		public void addPropertyChangeListener(final PropertyChangeListener listener)
		{
			_pSupport.addPropertyChangeListener(listener);
		}

		public void removePropertyChangeListener(final PropertyChangeListener listener)
		{
			_pSupport.removePropertyChangeListener(listener);
		}

		private void firePropertyChanged(final String type)
		{
			final PropertyChangeEvent event = new PropertyChangeEvent(this, type,
					_originalLocation, getLocation());
			_pSupport.firePropertyChange(event);
		}

		public Object getEditableValue()
		{
			return this;
		}

		public IPropertyDescriptor[] getPropertyDescriptors()
		{
			return descriptors;
		}

		public Object getPropertyValue(final Object propName)
		{
			Object res = "";
			if (ID_LAT_DEG.equals(propName))
				res = _latDeg;
			else if (ID_LAT_MIN.equals(propName))
				res = _latMin;
			else if (ID_LAT_SEC.equals(propName))
				res = _latSec;
			else if (ID_LAT_HEM.equals(propName))
			{
				if (_latHem.equals("N"))
					res = new Integer(0);
				else
					res = new Integer(1);
			}
			else if (ID_LONG_DEG.equals(propName))
				res = _longDeg;
			else if (ID_LONG_MIN.equals(propName))
				res = _longMin;
			else if (ID_LONG_SEC.equals(propName))
				res = _longSec;
			else if (ID_LONG_HEM.equals(propName))
			{
				if (_longHem.equals("E"))
					res = new Integer(0);
				else
					res = new Integer(1);
			}
			else if (ID_DEPTH.equals(propName))
				res = _depth;

			return res;
		}

		public WorldLocation getValue()
		{
			WorldLocation res = _originalLocation;
			try
			{
				res = getLocation();
			}
			catch (final Exception e)
			{
				// fall back on the original value
				CorePlugin.logError(Status.ERROR, "Failed to produce location", e);
				res = _originalLocation;
			}
			return res;
		}

		public WorldLocation getOriginalValue()
		{
			return _originalLocation;
		}

		/**
		 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(Object)
		 */
		public boolean isPropertySet(final Object propName)
		{
			boolean res = false;
			if (ID_LAT_DEG.equals(propName))
				res = !_latDeg.equals(_origLatDeg);
			else if (ID_LAT_MIN.equals(propName))
				res = !_latMin.equals(_origLatMin);
			else if (ID_LAT_SEC.equals(propName))
				res = !_latSec.equals(_origLatSec);
			else if (ID_LAT_HEM.equals(propName))
				res = !_latHem.equals(_origLatHem);
			else if (ID_LONG_DEG.equals(propName))
				res = !_longDeg.equals(_origLongDeg);
			else if (ID_LONG_MIN.equals(propName))
				res = !_longMin.equals(_origLongMin);
			else if (ID_LONG_SEC.equals(propName))
				res = !_longSec.equals(_origLongSec);
			else if (ID_LONG_HEM.equals(propName))
				res = !_longHem.equals(_origLongHem);
			else if (ID_DEPTH.equals(propName))
				res = !_depth.equals(_origDepth);

			return res;
		}

		public void resetPropertyValue(final Object propName)
		{
			if (ID_LAT_DEG.equals(propName))
				_latDeg = new String(_origLatDeg);
			else if (ID_LAT_MIN.equals(propName))
				_latMin = new String(_origLatMin);
			else if (ID_LAT_SEC.equals(propName))
				_latSec = new String(_origLatSec);
			else if (ID_LAT_HEM.equals(propName))
				_latHem = new String(_origLatHem);
			else if (ID_LONG_DEG.equals(propName))
				_longDeg = new String(_origLongDeg);
			else if (ID_LONG_MIN.equals(propName))
				_longMin = new String(_origLongMin);
			else if (ID_LONG_SEC.equals(propName))
				_longSec = new String(_origLongSec);
			else if (ID_LONG_HEM.equals(propName))
				_longHem = _origLongHem;
			else if (ID_DEPTH.equals(propName))
				_depth = new WorldDistance(_origDepth);
		}

		public void setPropertyValue(final Object propName, final Object value)
		{
			if (ID_LAT_DEG.equals(propName))
				_latDeg = new String((String) value);
			else if (ID_LAT_MIN.equals(propName))
				_latMin = new String((String) value);
			else if (ID_LAT_SEC.equals(propName))
				_latSec = new String((String) value);
			else if (ID_LAT_HEM.equals(propName))
			{
				final int index = ((Integer) value).intValue();
				_latHem = _latCats[index];
			}
			else if (ID_LONG_DEG.equals(propName))
				_longDeg = new String((String) value);
			else if (ID_LONG_MIN.equals(propName))
				_longMin = new String((String) value);
			else if (ID_LONG_SEC.equals(propName))
				_longSec = new String((String) value);
			else if (ID_LONG_HEM.equals(propName))
			{
				final int index = ((Integer) value).intValue();
				_longHem = _longCats[index];
			}
			else if (ID_DEPTH.equals(propName))
				_depth = new WorldDistance((WorldDistance) value);

			firePropertyChanged((String) propName);
		}
	  

	  @Override
	  public boolean equals(Object obj)
	  {
      final boolean res;
	    if(obj instanceof LatLongPropertySource)
	    {
	      LatLongPropertySource o = (LatLongPropertySource) obj;

	      // compare locations
	      res = this.getValue().equals(o.getValue());
	    }
	    else
	    {
	      res = false;
	    }
	    return res;
	  }
		
		public String toString()
		{
			String res;

			res = BriefFormatLocation.toString(getLocation());

			return res;
		}

		private WorldLocation getLocation()
		{
			double depth = 0d;

			// hold on, do we have a depth?
			if (_depth != null)
				depth = _depth.getValueIn(WorldDistance.METRES);
			WorldLocation res = null;
			try
			{
				// produce a new location from our data values
				final double dLatDeg =  MWCXMLReader.readThisDouble(_latDeg);
				final double dLatMin =  MWCXMLReader.readThisDouble(_latMin);
				final double dLatSec =  MWCXMLReader.readThisDouble(_latSec);
				final double dLongDeg = MWCXMLReader.readThisDouble(_longDeg);
				final double dLongMin = MWCXMLReader.readThisDouble(_longMin);
				final double dLongSec = MWCXMLReader.readThisDouble(_longSec);

				res = new WorldLocation(dLatDeg, dLatMin, dLatSec, _latHem.charAt(0),
						dLongDeg, dLongMin, dLongSec, _longHem.charAt(0), depth);

			}
			catch (final ParseException ee)
			{
				CorePlugin.logError(Status.ERROR,
						"Failed to correctly parse double in lat helper", ee);
				res = _originalLocation;
			}

			return res;
		}

		public boolean isPropertyResettable(final Object id)
		{
			// both parameters are resettable. cool.
			return true;
		}
	}

	/**
	 * custom cell editor which re-purposes button used to open dialog as a paste
	 * button
	 * 
	 * @author ian.mayo
	 */
	private static class PasteLocationDialogCellEditor extends DialogCellEditor
	{
		/**
		 * constructor - just pass on to parent
		 * 
		 * @param cellParent
		 */
		public PasteLocationDialogCellEditor(final Composite cellParent)
		{
			super(cellParent);
		}

		/**
		 * override operation triggered when button pressed. We should strictly be
		 * opening a new dialog, instead we're looking for a valid location on the
		 * clipboard. If one is there, we paste it.
		 * 
		 * @param cellEditorWindow
		 *          the parent control we belong to
		 * @return
		 */
		protected Object openDialogBox(final Control cellEditorWindow)
		{
			LatLongPropertySource output = null;

			// right, see what's on the clipboard
			// right, copy the location to the clipboard
			final Clipboard clip = CorePlugin.getDefault().getClipboard();
			final Object val = clip.getContents(TextTransfer.getInstance());
			if (val != null)
			{
				final String txt = (String) val;
				// cool, get the text
				final WorldLocation loc = CorePlugin.fromClipboard(txt);

				if (loc != null)
				{

					// create the output value
					output = new LatLongPropertySource(loc);
				}
				else
				{
					CorePlugin
							.showMessage(
									"Paste location",
									"Sorry the clipboard text is not in the right format (two doubles, separated by a space)."
											+ "\nContents:" + txt);
				}
			}
			else
			{
				CorePlugin.showMessage("Paste location",
						"Sorry, there is no suitable text on the clipboard");
			}

			return output;
		}

		/**
		 * Creates the button for this cell editor under the given parent control.
		 * <p>
		 * The default implementation of this framework method creates the button
		 * display on the right hand side of the dialog cell editor. Subclasses may
		 * extend or reimplement.
		 * </p>
		 * 
		 * @param parent
		 *          the parent control
		 * @return the new button control
		 */
		protected Button createButton(final Composite parent)
		{
			final Button result = super.createButton(parent);
			result.setText("Paste");
			return result;
		}
	}
}