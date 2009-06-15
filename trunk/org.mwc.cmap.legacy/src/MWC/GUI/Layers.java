// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: Layers.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.14 $
// $Log: Layers.java,v $
// Revision 1.14  2006/10/03 07:56:51  Ian.Mayo
// Switch to Java 5.  Use better compareTo methods
//
// Revision 1.13  2006/06/12 09:17:35  Ian.Mayo
// Better layer messaging
//
// Revision 1.12  2006/05/25 14:10:40  Ian.Mayo
// Make plottables comparable
//
// Revision 1.11  2006/01/13 15:25:52  Ian.Mayo
// Eclipse refactoring, add method to provide layers in GUI sorted order.
//
// Revision 1.10  2006/01/05 11:49:59  Ian.Mayo
// Fire correct event
//
// Revision 1.9  2005/09/08 08:57:09  Ian.Mayo
// Refactor name of chart features layer
//
// Revision 1.8  2005/09/06 10:23:50  Ian.Mayo
// Refactor getting origin
//
// Revision 1.7  2005/06/30 11:30:57  Ian.Mayo
// Improve passing modified items
//
// Revision 1.6  2005/06/30 10:35:27  Ian.Mayo
// Add more useful DataListener that passes new item with extended call
//
// Revision 1.5  2005/05/12 13:24:19  Ian.Mayo
// Allow app to stop Layers from firing dataExtended messages
//
// Revision 1.4 2004/10/07 14:23:21 Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.3 2004/08/31 09:38:23 Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing
// more consistent
//
// Revision 1.2 2004/05/11 13:27:33 Ian.Mayo
// Better digit formatting (to support Clover)
//
// Revision 1.1.1.1 2003/07/17 10:07:03 Ian.Mayo
// Initial import
//
// Revision 1.9 2003-07-04 11:00:49+01 ian_mayo
// Reflect name change of parent editor test
//
// Revision 1.8 2002-11-25 11:11:46+00 ian_mayo
// Refer to Plottable & Plottables
//
// Revision 1.7 2002-10-30 16:27:01+00 ian_mayo
// tidy (shorten) up display names for editables
//
// Revision 1.6 2002-10-28 09:23:28+00 ian_mayo
// support line widths
//
// Revision 1.5 2002-07-08 11:48:06+01 ian_mayo
// <>
//
// Revision 1.4 2002-06-05 12:56:31+01 ian_mayo
// unnecessarily loaded
//
// Revision 1.3 2002-05-31 09:54:08+01 ian_mayo
// Provide extra method to add layer without resize
//
// Revision 1.2 2002-05-28 09:25:32+01 ian_mayo
// after switch to new system
//
// Revision 1.2 2002-05-28 09:16:01+01 ian_mayo
// Minor tidying
//
// Revision 1.1 2002-05-28 09:15:12+01 ian_mayo
// Initial revision
//
// Revision 1.1 2002-04-11 14:02:28+01 ian_mayo
// Initial revision
//
// Revision 1.6 2002-02-26 10:01:47+00 administrator
// Return area centered on Fort Blockhouse when no geo-referenced layers present
//
// Revision 1.5 2002-01-24 14:22:34+00 administrator
// Reflect fact that Layers events for reformat and modified take a Layer
// parameter (which is possibly null). These changes are a step towards
// implementing per-layer graphics updates
//
// Revision 1.4 2001-08-29 19:23:30+01 administrator
// Improve tidying up as we close.
//
// Revision 1.3 2001-08-24 12:38:13+01 administrator
// Store the Layer editors inside the object itself
//
// Revision 1.2 2001-08-21 12:10:08+01 administrator
// remove static data objects, and Replace anonymous listeners with named class
// (to remove final objects)
//
// Revision 1.1 2001-08-17 07:55:34+01 administrator
// minor tidying
//
// Revision 1.0 2001-07-17 08:46:36+01 administrator
// Initial revision
//
// Revision 1.4 2001-01-22 12:29:29+00 novatech
// added JUnit testing code
//
// Revision 1.3 2001-01-16 19:27:57+00 novatech
// only paint if the projection has a valid screen area
//
// Revision 1.2 2001-01-05 09:11:06+00 novatech
// add fresh "AddLayer" method, which allows layer duplication
//
// Revision 1.1 2001-01-03 13:43:07+00 novatech
// Initial revision
//
// Revision 1.1.1.1 2000/12/12 21:42:52 ianmayo
// initial version
//
// Revision 1.16 2000-11-24 11:52:03+00 ian_mayo
// removing unnecessary comments
//
// Revision 1.15 2000-11-22 10:36:51+00 ian_mayo
// check if a layer exists before we add it, so that we can do an append instead
//
// Revision 1.14 2000-11-02 16:44:38+00 ian_mayo
// changing Layer into Interface, replaced by BaseLayer
//
// Revision 1.13 2000-10-31 15:43:12+00 ian_mayo
// perform tidying up to keep JBuilder happy
//
// Revision 1.12 2000-09-21 09:06:45+01 ian_mayo
// make Editable.EditorType a transient parameter, to prevent it being written
// to file
//
// Revision 1.11 2000-08-18 13:36:04+01 ian_mayo
// implement singleton of Editable.EditorType
//
// Revision 1.10 2000-08-17 10:21:28+01 ian_mayo
// improve comments
//
// Revision 1.9 2000-08-17 10:04:15+01 ian_mayo
// replace specification of our custom editor class in BeanInfo
//
// Revision 1.8 2000-08-11 08:42:02+01 ian_mayo
// tidy beaninfo
//
// Revision 1.7 2000-08-09 16:03:12+01 ian_mayo
// remove stray semi-colons
//
// Revision 1.6 2000-04-19 11:39:13+01 ian_mayo
// implement Close method, clear local storage
//
// Revision 1.5 2000-03-08 14:28:10+00 ian_mayo
// check canvas is valid before starting redraw
//
// Revision 1.4 2000-03-07 10:13:34+00 ian_mayo
// add serialVersionUID
//
// Revision 1.3 2000-01-21 12:04:36+00 ian_mayo
// inserted methods to delete layers
//
// Revision 1.2 1999-11-26 15:45:06+00 ian_mayo
// adding toString method
//
// Revision 1.1 1999-10-12 15:37:08+01 ian_mayo
// Initial revision
//
// Revision 1.3 1999-08-17 08:12:30+01 administrator
// correct adding layer to another (remove duplicates)
//
// Revision 1.1 1999-07-27 10:50:50+01 administrator
// Initial revision
//

package MWC.GUI;

import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import MWC.GUI.Layer.BackgroundLayer;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * Plain implementation of layer manager. In addition to managing a set of
 * layers this class provides additional (GUI-independent) support for GUI
 * classes which handle layers
 */
public class Layers implements Serializable, Editable, Plottable, PlottablesType
{

	// ////////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////////

	/**
	 * the name of the chart-features layer
	 */
	public static final String CHART_FEATURES = "Chart Features";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the layer data
	 */
	private Vector<Editable> _theLayers;

	/**
	 * the set of callbacks for when data has been modified
	 */
	transient private Vector<DataListener> _dataModifiedListeners;

	/**
	 * the set of callbacks for when the data on this layer has been added to or
	 * removed
	 */
	transient private Vector<DataListener> _dataExtendedListeners;

	/**
	 * the set of callbacks for when formatting details of data on this layer have
	 * been modified
	 */
	transient private Vector<DataListener> _dataReformattedListeners;

	/**
	 * the editors for these layers
	 */
	transient private MWC.GUI.Tools.Chart.RightClickEdit _myEditor;

	/**
	 * whether these layers are visible
	 */
	private boolean _isVisible = true;

	/**
	 * flag to track if we should allow the fireExtended() method to fire or not.
	 */
	private boolean _suspendFiringExtended;

	// ////////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////////

	/**
	 * a list of layers
	 */
	public Layers()
	{
		_theLayers = new Vector<Editable>(0, 1);

		produceLists();
	}

	// ////////////////////////////////////////////////////
	// member functions
	// ////////////////////////////////////////////////////

	/**
	 * store editor to go with this set of layers. Ideally we would like to store
	 * this editor in the LayerManager, but since the LayerManager is an editor
	 * itself it doesn't have a constructor and there's no way of setting this
	 * data (other than via static data, but it needs to be unique for each
	 * operation).
	 */
	public void setEditor(MWC.GUI.Tools.Chart.RightClickEdit editor)
	{
		_myEditor = editor;
	}

	/**
	 * Retrieve actions to go with this set of layers.
	 */
	public MWC.GUI.Tools.Chart.RightClickEdit getEditor()
	{
		return _myEditor;
	}

	private void produceLists()
	{
		_dataModifiedListeners = new Vector<DataListener>(0, 1);
		_dataExtendedListeners = new Vector<DataListener>(0, 1);
		_dataReformattedListeners = new Vector<DataListener>(0, 1);
	}

	/**
	 * get the bounds of this set of layers (return our SPECIAL area in absence of
	 * bounds)
	 */
	public WorldArea getBounds()
	{
		WorldArea res = null;

		Iterator<Editable> it = _theLayers.iterator();
		while (it.hasNext())
		{
			Layer thisL = (Layer) it.next();
			WorldArea newBounds = thisL.getBounds();
			if (newBounds != null)
			{
				if (res == null)
					res = newBounds;
				else
					res.extend(newBounds);
			}
		}

		// did we find anything?
		if (res == null)
		{
			res = getDebriefOrigin();
		}

		return res;

	}

	/**
	 * if we don't have any data, we still want to be able to centre on a
	 * geographic location. use HMS Dolphin.
	 * 
	 * @return WorldArea to provide default origin
	 */
	public static final WorldArea getDebriefOrigin()
	{
		// no, return the origin of Debrief (Fort Blockhouse, HMS Dolphin)
		return new WorldArea(new WorldLocation(51, 12, 8.27, 'N', 001, 58, 7.62, 'W', 0),
				new WorldLocation(50, 30, 26.99, 'N', 0, 42, 56.58, 'W', 0));
	}

	/**
	 * add the other layers to ours (perform shallow copy)
	 * 
	 * @param theOther
	 *          layer to add to us
	 */
	public void addThis(Layers theOther)
	{
		//
		Enumeration<Editable> other = theOther._theLayers.elements();
		while (other.hasMoreElements())
		{
			Layer thisL = (Layer) other.nextElement();
			// see if we are storing this layer already
			Layer current = findLayer(thisL.getName());
			if (current == null)
				_theLayers.addElement(thisL);
			else
				current.append(thisL);
		}

	}

	/**
	 * get the current number of layers
	 * 
	 * @return current number of layers
	 */
	public int size()
	{
		return _theLayers.size();
	}

	public Enumeration<Editable> elements()
	{
		return _theLayers.elements();
	}
	
	/** retrieve the layers.
	 * Right, we do some SPECIAL PROCESSING HERE.
	 * 
	 * We want to ensure that we return
	 * 
	 * @return
	 */
	public Enumeration<Layer> sortedElements()
	{
		// have a got a creating a sorted set of layers
		Vector<Layer> res = new Vector<Layer>(0, 1);
		Enumeration<Editable> numer = _theLayers.elements();

		Vector<Layer> _backgrounds = new Vector<Layer>(0, 1);
		Vector<Layer> _buffered = new Vector<Layer>(0, 1);
		Vector<Layer> _nonBuffered = new Vector<Layer>(0, 1);
		while (numer.hasMoreElements())
		{
			boolean inserted = false;
			Layer thisLayer = (Layer) numer.nextElement();
			if (thisLayer instanceof BackgroundLayer)
			{
				_backgrounds.add(thisLayer);
				inserted = true;
			}
			else if (thisLayer instanceof BaseLayer)
			{
				BaseLayer bl = (BaseLayer) thisLayer;
				if (bl.isBuffered())
				{
					_buffered.add(thisLayer);
					inserted = true;
				}
			}

			if (!inserted)
				_nonBuffered.add(thisLayer);
		}
		
		res.addAll(_backgrounds);
		res.addAll(_buffered);
		res.addAll(_nonBuffered);
		return res.elements();
	}

	public boolean getVisible()
	{
		return _isVisible;
	}

	public void setVisible(boolean visible)
	{
		this._isVisible = visible;
	}

	public double rangeFrom(WorldLocation other)
	{
		return -1;
	}

	/**
	 * see if we can find this layer
	 * 
	 * @param theLayerName
	 *          the name of the layer to look for
	 * @return the layer
	 */
	public Layer findLayer(String theLayerName)
	{
		Layer res = null;
		// step through our layers
		Enumeration<Editable> enumer = _theLayers.elements();
		while (enumer.hasMoreElements())
		{
			Layer thisL = (Layer) enumer.nextElement();
			if (thisL.getName().equalsIgnoreCase(theLayerName))
			{
				res = thisL;
				break;
			}
		}
		//
		return res;
	}

	public void addThisLayerDoNotResize(Layer theLayer)
	{
		// see if we are already storing this layer
		Layer res = findLayer(theLayer.getName());

		if (res == null)
		{
			// no, we know nothing about it, create a fresh one

			// is this a layer which wants to go at the back?
			if (theLayer instanceof Layer.BackgroundLayer)
			{
				_theLayers.insertElementAt(theLayer, 0);
			}
			else
			{
				// no, just stick it on the end
				_theLayers.addElement(theLayer);
			}
		}
		else
		{
			// we know about it already, copy the new one into our existing one
			res.append(theLayer);
		}
	}

	/**
	 * add this layer to our list
	 * 
	 * @param theLayer
	 *          the layer to add
	 */
	public void addThisLayer(Layer theLayer)
	{
		addThisLayerDoNotResize(theLayer);

		// and fire the extended event
		fireExtended(null, theLayer);
	}

	/**
	 * add this layer to our list, even if there is already one with the same name
	 * 
	 * @param theLayer
	 *          the layer to add
	 */
	public void addThisLayerAllowDuplication(Layer theLayer)
	{

		// no, we know nothing about it, create a fresh one
		_theLayers.addElement(theLayer);

		// and fire the extended event
		fireExtended();
	}

	/**
	 * remove a layer from our list
	 * 
	 * @param theLayer
	 *          the layer to remove
	 */
	public void removeThisLayer(Layer theLayer)
	{
		// first remove the layer
		_theLayers.removeElement(theLayer);

		// and fire the modified event
		fireExtended();
	}

	/**
	 * return the layer at this index
	 * 
	 * @param index
	 *          the index to look at
	 * @return the Layer, or null
	 */
	public Layer elementAt(int index)
	{
		return (Layer) _theLayers.elementAt(index);
	}

	/**
	 * paint all of the layers in this canvas
	 * 
	 * @param dest
	 *          destination for the plotting
	 */
	public void paint(CanvasType dest)
	{
		// check that we have a valid canvas (that the sizes are set)
		java.awt.Dimension sArea = dest.getProjection().getScreenArea();
		if (sArea != null)
		{
			if (sArea.width > 0)
			{
				Enumeration<Editable> enumer = _theLayers.elements();
				while (enumer.hasMoreElements())
				{
					Layer thisLayer = (Layer) enumer.nextElement();

					// set the line width
					float oldWid = dest.getLineWidth();
					dest.setLineWidth(thisLayer.getLineThickness());
					thisLayer.paint(dest);
					dest.setLineWidth((int) oldWid);
				}
			}
		}
	}

	/**
	 * create an empty layer, and return it
	 * 
	 * @return a blank layer
	 */
	public Layer cleanLayer()
	{
		Layer res = new BaseLayer();
		return res;
	}

	// ////////////////////////////////////////////////////
	// callback management
	// ////////////////////////////////////////////////////

	/**
	 * inform listeners that we have been extended
	 */
	public void fireExtended()
	{
		if (!_suspendFiringExtended)
		{
			Enumeration<DataListener> enumer = _dataExtendedListeners.elements();
			while (enumer.hasMoreElements())
			{
				DataListener thisOne = (DataListener) enumer.nextElement();
				thisOne.dataExtended(this);
			}
		}
	}

	/**
	 * inform listeners that we have been extended
	 */
	public void fireExtended(Plottable newItem, Layer parent)
	{
		if (!_suspendFiringExtended)
		{
			Enumeration<DataListener> enumer = _dataExtendedListeners.elements();
			while (enumer.hasMoreElements())
			{
				DataListener thisOne = (DataListener) enumer.nextElement();

				// just see if this is a special listener
				if (thisOne instanceof DataListener2)
				{
					// yes, indicate which is the new item
					DataListener2 d2 = (DataListener2) thisOne;
					d2.dataExtended(this, newItem, parent);
				}
				else
					thisOne.dataExtended(this);
			}
		}
	}

	/**
	 * inform listeners that we have been modified
	 */
	public void fireModified(Layer changedLayer)
	{
		Enumeration<DataListener> enumer = _dataModifiedListeners.elements();
		while (enumer.hasMoreElements())
		{
			DataListener thisOne = (DataListener) enumer.nextElement();
			thisOne.dataModified(this, changedLayer);
		}
	}

	/**
	 * inform listeners that we have had a formatting change
	 */
	public void fireReformatted(Layer changedLayer)
	{
		Enumeration<DataListener> enumer = _dataReformattedListeners.elements();
		while (enumer.hasMoreElements())
		{
			DataListener thisOne = (DataListener) enumer.nextElement();
			thisOne.dataReformatted(this, changedLayer);
		}
	}

	/**
	 * add this listener
	 * 
	 * @param theListener
	 *          the listener to add
	 */
	public void addDataModifiedListener(DataListener theListener)
	{
		_dataModifiedListeners.addElement(theListener);
	}

	/**
	 * add this listener
	 * 
	 * @param theListener
	 *          the listener to add
	 */
	public void addDataExtendedListener(DataListener theListener)
	{
		_dataExtendedListeners.addElement(theListener);
	}

	/**
	 * add this listener
	 * 
	 * @param theListener
	 *          the listener to add
	 */
	public void addDataReformattedListener(DataListener theListener)
	{
		_dataReformattedListeners.addElement(theListener);
	}

	/**
	 * remove this listener
	 * 
	 * @param theListener
	 *          the listener to remove
	 */
	public void removeDataModifiedListener(DataListener theListener)
	{
		_dataModifiedListeners.removeElement(theListener);
	}

	/**
	 * remove this listener
	 * 
	 * @param theListener
	 *          the listener to remove
	 */
	public void removeDataExtendedListener(DataListener theListener)
	{
		_dataExtendedListeners.removeElement(theListener);
	}

	/**
	 * remove this listener
	 * 
	 * @param theListener
	 *          the listener to remove
	 */
	public void removeDataReformattedListener(DataListener theListener)
	{
		_dataReformattedListeners.removeElement(theListener);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException
	{

		in.defaultReadObject();

		produceLists();
	}

	/**
	 * the name of this object
	 * 
	 * @return name
	 */
	public String toString()
	{
		return getName();
	}

	/**
	 * the Name of this object
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return "The Layers";
	}

	/**
	 * duff editable, not used
	 * 
	 * @param val
	 *          largely ignored by the people of New Guinea
	 */
	public void setName(String val)
	{
		//
	}

	/**
	 * whether this item has an editor (yes, of course)
	 * 
	 * @return yes/no flag
	 */
	public boolean hasEditor()
	{
		return true;
	}

	/**
	 * get the editable information for this object
	 * 
	 * @return details needed for editing
	 */
	public Editable.EditorType getInfo()
	{
		return new LayersInfo(this);
	}

	public int compareTo(Plottable arg0)
	{
		final int res;
		Plottable other = (Plottable) arg0;
		int myCode = hashCode();
		int otherCode = other.hashCode();
		if(myCode < otherCode)
			res = -1;
		else if(myCode > otherCode)
			res = 1;
		else 
			res = 0;
		return res;
	}	
	
	/**
	 * finalise function, removes all references to layers
	 */
	public void close()
	{
		// first, get the layers to close themselves
		Enumeration<Editable> enumer = _theLayers.elements();
		while (enumer.hasMoreElements())
		{
			Object layer = enumer.nextElement();
			if (layer instanceof PlainWrapper)
			{
				PlainWrapper pw = (PlainWrapper) layer;
				pw.closeMe();
			}
		}

		// and now empty the object itself
		_theLayers.removeAllElements();
		_theLayers = null;

		// also tidy up the listeners
		_dataExtendedListeners.clear();
		_dataModifiedListeners.clear();
		_dataReformattedListeners.clear();
	}

	// ////////////////////////////////////////////////////////////////
	// interface definition
	// ////////////////////////////////////////////////////////////////
	/**
	 * interface to be implemented by classes intending to watch the full set of
	 * data for this scenarion
	 */
	public interface DataListener
	{
		/**
		 * some part of the data has been modified (not necessarily formatting
		 * though)
		 * 
		 * @param theData
		 *          the Layers containing the item of data which has been modified
		 * @param changedLayer
		 *          a layer which has changed (or null if not known)
		 */
		public void dataModified(Layers theData, Layer changedLayer);

		/**
		 * a new piece of data has been edited
		 * 
		 * @param theData
		 *          the Layers which have had something edited
		 */
		public void dataExtended(Layers theData);

		/**
		 * some kind of formatting has been applied
		 * 
		 * @param theData
		 *          the Layers containing the data which has been reformatted
		 * @param changedLayer
		 *          a layer which has changed (or null if not known)
		 */
		public void dataReformatted(Layers theData, Layer changedLayer);

	}

	/**
	 * extended interface which indicates the new item
	 */
	public interface DataListener2 extends DataListener
	{
		/**
		 * a new piece of data has been edited
		 * 
		 * @param theData
		 *          the Layers which have had something edited
		 * @param newItem
		 *          the item which has been added
		 * @param parent
		 *          the layer containing the item
		 */
		public void dataExtended(Layers theData, Plottable newItem, Layer parent);
	}

	// //////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing this layer
	// //////////////////////////////////////////////////////////////////////////
	/**
	 * the definition of what is editable about this object
	 */
	public class LayersInfo extends Editable.EditorType
	{

		/**
		 * constructor for editable details of a set of Layers
		 * 
		 * @param data
		 *          the Layers themselves
		 */
		public LayersInfo(Layers data)
		{
			super(data, data.getName(), "");
		}

		/**
		 * return a description of this bean, also specifies the custom editor we
		 * use
		 * 
		 * @return the BeanDescriptor
		 */
		public BeanDescriptor getBeanDescriptor()
		{
			BeanDescriptor bp = new BeanDescriptor(Layers.class,
					MWC.GUI.LayerManager.Swing.SwingLayerManager.class);
			bp.setDisplayName("Layer Manager");
			return bp;
		}

		/**
		 * The things about these Layers which are editable. We don't really use
		 * this list, since we have our own custom editor anyway
		 * 
		 * @return property descriptions
		 */
		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				PropertyDescriptor[] res = { prop("Name", "the name for these layers"), };

				return res;
			}
			catch (IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class LayersTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public LayersTest(String val)
		{
			super(val);
		}

		public void testMyParams()
		{
			MWC.GUI.Editable ed = new Layers();
			Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}
	}

	/**
	 * class to stop the layer manager from firing "extended" messages -
	 * particularly when we're loading a lot of data
	 * 
	 * @param suspendFiring
	 */
	public void suspendFiringExtended(boolean suspendFiring)
	{
		_suspendFiringExtended = suspendFiring;
	}
}
