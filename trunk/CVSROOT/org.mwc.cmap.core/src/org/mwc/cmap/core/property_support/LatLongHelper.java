/**
 * 
 */
package org.mwc.cmap.core.property_support;

import java.text.DecimalFormat;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.*;
import org.mwc.cmap.core.CorePlugin;

import MWC.GenericData.*;
import MWC.Utilities.TextFormatting.*;

public class LatLongHelper extends EditorHelper
{

	protected static DecimalFormat _floatFormat = new DecimalFormat("0.0000");
	
	public static class LatLongPropertySource implements IPropertySource2
	{
		
		/** the working values
		 * 
		 */
		private String _latDeg;
		private String _latMin;
		private String _latSec;
		private String _latHem;
		private String _longDeg;
		private String _longMin;
		private String _longSec;
		private String _longHem;
		
		/** the original values
		 * 
		 */
		
		private String _origLatDeg;
		private String _origLatMin;
		private String _origLatSec;
		private String _origLatHem;
		private String _origLongDeg;
		private String _origLongMin;
		private String _origLongSec;
		private String _origLongHem;
		private WorldLocation _originalLocation;
		
		

		/** name for the lat & long properties
		 * 
		 */
		public static String ID_LAT_DEG = "LAT_DEG";
		public static String ID_LAT_MIN = "LAT_MIN";
		public static String ID_LAT_SEC = "LAT_SEC";
		public static String ID_LAT_HEM = "LAT_HEM";
		public static String ID_LONG_DEG = "LONG_DEG";
		public static String ID_LONG_MIN = "LONG_MIN";
		public static String ID_LONG_SEC = "LONG_SEC";
		public static String ID_LONG_HEM = "LONG_HEM";


		protected static IPropertyDescriptor[] descriptors;

		public static class CategorisedDescriptor extends TextPropertyDescriptor
		{
			public CategorisedDescriptor(String id, String title, String cat)
			{
				super(id, title);
				super.setCategory(cat);
			}
		}
		
		static
		{
			descriptors = new IPropertyDescriptor[] {
					new CategorisedDescriptor(ID_LAT_DEG, "1. Lat Degrees", "Lat"),
					new CategorisedDescriptor(ID_LAT_MIN, "2. Lat Minutes", "Lat"),
					new CategorisedDescriptor(ID_LAT_SEC, "3. Lat Seconds", "Lat"),
					new ComboBoxPropertyDescriptor(ID_LAT_HEM, "4. Lat Hemisphere", new String[] {"N", "S"}),
					new CategorisedDescriptor(ID_LONG_DEG, "5. Long Degrees", "Long"),
					new CategorisedDescriptor(ID_LONG_MIN, "6. Long Minutes", "Long"),
					new CategorisedDescriptor(ID_LONG_SEC, "7. Long Seconds", "Long"),
					new ComboBoxPropertyDescriptor(ID_LONG_HEM, "8. Long Hemisphere", new String[] {"E", "W"})
					};
		}

		public LatLongPropertySource(WorldLocation location)
		{
			_originalLocation = new WorldLocation(location);
			
			DebriefFormatLocation.brokenDown bLat = new DebriefFormatLocation.brokenDown(location.getLat(), true);
			DebriefFormatLocation.brokenDown bLong = new DebriefFormatLocation.brokenDown(location.getLong(), false);
			
			
			_latDeg = _origLatDeg = "" + bLat.deg;
			_latMin = _origLatMin = "" + bLat.min;
			_latSec = _origLatSec = _floatFormat.format(bLat.sec);
			_latHem = _origLatHem = "" + bLat.hem;
			
			_longDeg = _origLongDeg = "" + bLong.deg;
			_longMin = _origLongMin = "" + bLong.min;
			_longSec = _origLongSec = _floatFormat.format(bLong.sec);
			_longHem = _origLongHem = "" + bLong.hem;
			
			
//
//
//			if (dtg == null)
//			{
//				_originalVal = null;
//				_date = UNSET;
//				_time = UNSET;
//			}
//			else
//			{
//				_originalVal = new HiResDate(dtg);
//				_date = _dateFormat.format(dtg.getDate());
//				_time = _timeFormat.format(dtg.getDate());
//			}
//			
//			_originalDate = new String( _date);
//			_originalTime = new String(_time);
		}

		protected void firePropertyChanged(String propName)
		{
			// Control ctl = (Control)element.getControl();
			//			
			// if (ctl == null) {
			// // the GUIView is probably hidden in this case
			// return;
			// }
			// ctl.setSize(_dtg);
		}

		public Object getEditableValue()
		{
			return this;
		}

		public IPropertyDescriptor[] getPropertyDescriptors()
		{
			return descriptors;
		}

		public Object getPropertyValue(Object propName)
		{
			Object res = "";
			if (ID_LAT_DEG.equals(propName))
				res = _latDeg;
			else if(ID_LAT_MIN.equals(propName))
				res = _latMin;
			else if(ID_LAT_SEC.equals(propName))
				res = _latSec;
			else if(ID_LAT_HEM.equals(propName))
			{
				if(_latHem.equals("N"))
					res = new Integer(0);
				else
					res = new Integer(1);
			}
			else if (ID_LONG_DEG.equals(propName))
				res = _longDeg;
			else if(ID_LONG_MIN.equals(propName))
				res = _longMin;
			else if(ID_LONG_SEC.equals(propName))
				res = _longSec;
			else if(ID_LONG_HEM.equals(propName))
			{
				if(_latHem.equals("E"))
					res = new Integer(0);
				else
					res = new Integer(1);
			}				
			
			return res;
		}

		public WorldLocation getValue()
		{
			WorldLocation res = _originalLocation;
			try
			{
				res = getLocation();
				
//				
//				// see if they have been set yet
//				if ((!_date.equals(UNSET)) && (!_time.equals(UNSET)))
//				{
//					Date date = _dateFormat.parse(_date);
//					Date time = _timeFormat.parse(_time);
//					res = new HiResDate(date.getTime() + time.getTime(), 0);
//				}
			} catch (Exception e)
			{
				// fall back on the original value
				CorePlugin.logError(Status.ERROR, "Failed to produce location", e);
				res = _originalLocation;
			}
			return res;
		}

		/**
		 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(Object)
		 */
		public boolean isPropertySet(Object propName)
		{
			boolean res = false;
			if (ID_LAT_DEG.equals(propName))
				res =  !  _latDeg.equals(_origLatDeg);
			else if (ID_LAT_MIN.equals(propName))
				res =  !  _latMin.equals(_origLatMin);
			else if (ID_LAT_SEC.equals(propName))
				res =  !  _latSec.equals(_origLatSec);
			else if (ID_LAT_HEM.equals(propName))
				res =  !  _latHem.equals(_origLatHem);
			else if (ID_LONG_DEG.equals(propName))
				res =  !  _longDeg.equals(_origLongDeg);
			else if (ID_LONG_MIN.equals(propName))
				res =  !  _longMin.equals(_origLongMin);
			else if (ID_LONG_SEC.equals(propName))
				res =  !  _longSec.equals(_origLongSec);
			else if (ID_LONG_HEM.equals(propName))
				res =  !  _longHem.equals(_origLongHem);

			
			return res;
		}

		public void resetPropertyValue(Object propName)
		{
			if (ID_LAT_DEG.equals(propName))
				 _latDeg = new String(_origLatDeg);
			else if (ID_LAT_MIN.equals(propName))
				 _latMin = new String(_origLatMin);
			else if (ID_LAT_SEC.equals(propName))
				 _latSec = new String(_origLatSec);
			else if (ID_LAT_HEM.equals(propName))
				 _latHem = new String( _origLatHem);
			else if (ID_LONG_DEG.equals(propName))
				 _longDeg = new String( _origLongDeg);
			else if (ID_LONG_MIN.equals(propName))
				 _longMin = new String( _origLongMin);
			else if (ID_LONG_SEC.equals(propName))
				 _longSec = new String( _origLongSec);
			else if (ID_LONG_HEM.equals(propName))
				 _longHem = new String( _origLongHem);
		}

		public void setPropertyValue(Object propName, Object value)
		{
			if (ID_LAT_DEG.equals(propName))
				 _latDeg = new String((String)value);
			else if (ID_LAT_MIN.equals(propName))
				 _latMin = new String((String)value);
			else if (ID_LAT_SEC.equals(propName))
				 _latSec = new String((String)value);
			else if (ID_LAT_HEM.equals(propName))
				 _latHem = new String( (String)value);
			else if (ID_LONG_DEG.equals(propName))
				 _longDeg = new String( (String)value);
			else if (ID_LONG_MIN.equals(propName))
				 _longMin = new String( (String)value);
			else if (ID_LONG_SEC.equals(propName))
				 _longSec = new String( (String)value);
			else if (ID_LONG_HEM.equals(propName))
				 _longHem = new String( (String)value);

			firePropertyChanged((String) propName);
		}

		public String toString()
		{
			String res;
			
			res = BriefFormatLocation.toString(getLocation());

			return res;
		}

		private WorldLocation getLocation()
		{
			// produce a new location from our data values
			WorldLocation res = new WorldLocation((int)Double.parseDouble(_latDeg),
					(int)Double.parseDouble(_latMin) + (Double.parseDouble(_latSec)/60), _latHem.charAt(0),
					(int)Double.parseDouble(_longDeg),
					(int)Double.parseDouble(_longMin) + (Double.parseDouble(_longSec)/60), _longHem.charAt(0), 0);
					
			return res;
		}

		public boolean isPropertyResettable(Object id)
		{
			// both parameters are resettable. cool.
			return true;
		}

	}

	public LatLongHelper()
	{
		super(WorldLocation.class);
	}

	public CellEditor getEditorFor(Composite parent)
	{
		return null;
	}

	public boolean editsThis(Class target)
	{
		return (target == WorldLocation.class);
	}

	public Object translateToSWT(Object value)
	{
		// ok, we've received a location. Return our new property source representing a
		// DTG
		return new LatLongPropertySource((WorldLocation) value);
	}

	public Object translateFromSWT(Object value)
	{
		LatLongPropertySource res = (LatLongPropertySource) value;
		return res.getValue();
	}

	public ILabelProvider getLabelFor(Object currentValue)
	{
		ILabelProvider label1 = new LabelProvider()
		{
			public String getText(Object element)
			{
				LatLongPropertySource val = (LatLongPropertySource) element;
				return val.toString();
			}

			public Image getImage(Object element)
			{
				return null;
			}

		};
		return label1;
	}
}