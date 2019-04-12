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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import MWC.GUI.Layer.BackgroundLayer;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * Plain implementation of layer manager. In addition to managing a set of layers this class
 * provides additional (GUI-independent) support for GUI classes which handle layers
 */
public class Layers implements Serializable, Plottable, PlottablesType
{
  /**
   * interface to be implemented by classes intending to watch the full set of data for this
   * scenarion
   */
  public interface DataListener
  {
    /**
     * a new piece of data has been edited
     *
     * @param theData
     *          the Layers which have had something edited
     */
    public void dataExtended(Layers theData);

    /**
     * some part of the data has been modified (not necessarily formatting though)
     *
     * @param theData
     *          the Layers containing the item of data which has been modified
     * @param changedLayer
     *          a layer which has changed (or null if not known)
     */
    public void dataModified(Layers theData, Layer changedLayer);

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
     *          the item that has been added/edited/deleted (if just one)
     * @param parent
     *          the layer containing the item
     */
    public void dataExtended(Layers theData, Plottable newItem,
        HasEditables parent);
  }

  // ////////////////////////////////////////////////////
  // member variables
  // ////////////////////////////////////////////////////

  /**
   * interface for classes that want to know about new items being added
   *
   * @author ian
   *
   */
  public interface INewItemListener extends ExcludeFromRightClickEdit
  {
    /**
     * a new layer, or a new item has been added
     *
     * @param parent
     *          the layer that has a new item
     * @param item
     *          the new item (null if this is actually just a new layer)
     * @param theSymbology
     */
    void newItem(Layer parent, Editable item, String theSymbology);

    /**
     * the data has been reloaded, forget any existing state
     *
     */
    void reset();
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
    public LayersInfo(final Layers data)
    {
      super(data, data.getName(), "");
    }

    /**
     * return a description of this bean, also specifies the custom editor we use
     *
     * @return the BeanDescriptor
     */
    @Override
    public BeanDescriptor getBeanDescriptor()
    {
      final BeanDescriptor bp = new BeanDescriptor(Layers.class,
          MWC.GUI.LayerManager.Swing.SwingLayerManager.class);
      bp.setDisplayName("Layer Manager");
      return bp;
    }

    /**
     * The things about these Layers which are editable. We don't really use this list, since we
     * have our own custom editor anyway
     *
     * @return property descriptions
     */
    @Override
    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
        {prop("Name", "the name for these layers"),};

        return res;
      }
      catch (final IntrospectionException e)
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

    public LayersTest(final String val)
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
   * marker for objects that want to know about the parent layer (prob because they can create other
   * layers)
   */
  public static interface NeedsToKnowAboutLayers
  {
    public void setLayers(Layers parent);
  }

  /**
   * marker for objects that must be wrapped before they are pasted into the layer manager
   *
   */
  public static interface NeedsWrappingInLayerManager
  {
    public Layer wrapMe(Layers layers);
  }

  public static interface OperateFunction
  {
    void operateOn(Editable item);
  }

  /**
   * make the property listener class into an embedded class - because we were experiencing some
   * not-serializable problems
   *
   * @author ian
   *
   */
  private class ReformatListener implements PropertyChangeListener, Serializable
  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void propertyChange(final PropertyChangeEvent evt)
    {
      final Layer layer = (Layer) evt.getSource();
      fireReformatted(layer);
    }

  }

  public static final String NEW_LAYER_COMMAND = "[Add new layer...]";

  public static final String DEFAULT_TARGET_LAYER = "Misc";

  /**
   * the name of the formatters layer
   */
  private static final String FORMATTERS = "Formatters";

  /**
   * the name of the chart-features layer
   */
  public static final String CHART_FEATURES = "Chart Features";

  /**
   * the name of the chart-features layer
   */
  public static final String DYNAMIC_FEATURES = "Dynamic Features";

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private static Layer checkLayer(final Layer layer, final String name)
  {
    /**
     * if it's a monster layer, we won't bother searching it
     *
     */
    final long MAX_TO_SEARCH = 1000;

    Layer res = null;
    // don't search the layer if it's a monster
    if (layer instanceof BaseLayer)
    {
      final BaseLayer bl = (BaseLayer) layer;
      if (bl.size() > MAX_TO_SEARCH)
      {
        return null;
      }
    }

    // ok, continue with normal processing
    final Enumeration<Editable> iter = layer.elements();
    while (iter.hasMoreElements() && res == null)
    {
      final Editable ele = iter.nextElement();
      if (ele instanceof Layer)
      {
        final Layer l = (Layer) ele;
        if (l.getName() != null && l.getName().equals(name))
        {
          return l;
        }
        else
        {
          res = checkLayer(l, name);
        }
      }
    }
    return res;
  }

  // ////////////////////////////////////////////////////
  // constructor
  // ////////////////////////////////////////////////////

  private static TimePeriod extend(final TimePeriod period,
      final HiResDate date)
  {
    final TimePeriod result;
    // have we received a date?
    if (date != null)
    {
      if (period == null)
      {
        result = new TimePeriod.BaseTimePeriod(date, date);
      }
      else
      {
        result = period;
        result.extend(date);
      }
    }
    else
    {
      result = null;
    }

    return result;
  }

  /**
   * if we don't have any data, we still want to be able to centre on a geographic location. use HMS
   * Dolphin.
   *
   * @return WorldArea to provide default origin
   */
  public static final WorldArea getDebriefOrigin()
  {
    // no, return the origin of Debrief (Fort Blockhouse, HMS Dolphin)
    return new WorldArea(new WorldLocation(51, 12, 8.27, 'N', 001, 58, 7.62,
        'W', 0), new WorldLocation(50, 30, 26.99, 'N', 0, 42, 56.58, 'W', 0));
  }

  // ////////////////////////////////////////////////////
  // member functions
  // ////////////////////////////////////////////////////

  /**
   * the layer data
   */
  private final Vector<Editable> _theLayers = new Vector<Editable>(0, 1);

  /**
   * the set of callbacks for when data has been modified
   */
  transient private Vector<DataListener> _dataModifiedListeners;

  /**
   * the set of callbacks for when the data on this layer has been added to or removed
   */
  transient private Vector<DataListener> _dataExtendedListeners;

  /**
   * the set of callbacks for when formatting details of data on this layer have been modified
   */
  transient private Vector<DataListener> _dataReformattedListeners;

  /**
   * classes that want to know about new items
   *
   */
  transient private List<INewItemListener> _newItemListeners;

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

  /**
   * handler for if the formatting of a layer hcanges
   *
   */
  private final PropertyChangeListener _formatListener;

  /**
   * handler for if the size (length) of a layer changes
   *
   */
  private final PropertyChangeListener _extendedListener;

  /**
   * a list of layers
   */
  public Layers()
  {
    _formatListener = new ReformatListener();
    _extendedListener = new PropertyChangeListener()
    {
      @Override
      public void propertyChange(final PropertyChangeEvent arg0)
      {
        fireExtended();
      }
    };

    produceLists();

  }

  /**
   * add this listener
   *
   * @param theListener
   *          the listener to add
   */
  public void addDataExtendedListener(final DataListener theListener)
  {
    if (!_dataExtendedListeners.contains(theListener))
    {
      _dataExtendedListeners.addElement(theListener);
    }
  }

  /**
   * add this listener
   *
   * @param theListener
   *          the listener to add
   */
  public void addDataModifiedListener(final DataListener theListener)
  {
    if (!_dataModifiedListeners.contains(theListener))
    {
      _dataModifiedListeners.addElement(theListener);
    }
  }

  /**
   * add this listener
   *
   * @param theListener
   *          the listener to add
   */
  public void addDataReformattedListener(final DataListener theListener)
  {
    if (!_dataReformattedListeners.contains(theListener))
    {
      _dataReformattedListeners.addElement(theListener);
    }
  }

  /**
   * add this listener
   *
   * @param theListener
   *          the listener to add
   */
  public void addNewItemListener(final INewItemListener theListener)
  {
    if (!_newItemListeners.contains(theListener))
    {
      _newItemListeners.add(theListener);

      // and store it
      storeFormatter(theListener);
    }

  }

  /**
   * add the other layers to ours (perform shallow copy)
   *
   * @param theOther
   *          layer to add to us
   */
  public void addThis(final Layers theOther)
  {
    //
    final Enumeration<Editable> other = theOther._theLayers.elements();
    while (other.hasMoreElements())
    {
      final Layer thisL = (Layer) other.nextElement();
      // see if we are storing this layer already
      final Layer current = findLayer(thisL.getName());
      if (current == null)
      {
        // ok, now we can add it
        _theLayers.addElement(thisL);
      }
      else
      {
        current.append(thisL);
      }
    }

  }

  /**
   * add this layer to our list
   *
   * @param theLayer
   *          the layer to add
   */
  public void addThisLayer(final Layer theLayer)
  {
    Layer layer = theLayer;
    // right, see if it's one to be wrapped
    if (layer instanceof NeedsWrappingInLayerManager)
    {
      // right, does it already exist at the top level
      final Layer exists = this.findLayer(layer.getName());
      if (exists != null)
      {
        // better rename it then
        layer.setName(layer.getName() + "_" + (int) (Math.random() * 1000));
      }

      // now wrap it
      final NeedsWrappingInLayerManager nl =
          (NeedsWrappingInLayerManager) layer;
      layer = nl.wrapMe(this);
    }

    // does it need to know about the layers, or that it has been added to the layers?
    if (layer instanceof NeedsToKnowAboutLayers)
    {
      final NeedsToKnowAboutLayers need = (NeedsToKnowAboutLayers) layer;
      need.setLayers(this);
    }

    addThisLayerDoNotResize(layer);

    // and fire the extended event
    fireExtended(null, layer);
  }

  /**
   * add this layer to our list, even if there is already one with the same name
   *
   * @param theLayer
   *          the layer to add
   */
  public void addThisLayerAllowDuplication(final Layer theLayer)
  {
    // right, does this layer name already exist?
    final Iterator<Editable> iter = _theLayers.iterator();
    while (iter.hasNext())
    {
      final Layer thisLayer = (Layer) iter.next();
      if (thisLayer.getName().equals(theLayer.getName()))
      {
        // right, we've got to subtlely change the new layer name
        theLayer.setName(theLayer.getName() + "_1");
      }
    }

    // ok, now we can add it - we've changed the name if that's necessary.
    _theLayers.add(theLayer);

    // and fire the extended event
    fireExtended();
  }

  public void addThisLayerDoNotResize(final Layer theLayer)
  {
    // see if we are already storing this layer
    final Layer res = findLayer(theLayer.getName());

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
        _theLayers.add(theLayer);
      }
    }
    else
    {
      // we know about it already, copy the new one into our existing one
      res.append(theLayer);
    }

    if (theLayer instanceof SupportsPropertyListeners)
    {
      final SupportsPropertyListeners pr = (SupportsPropertyListeners) theLayer;
      pr.addPropertyChangeListener(SupportsPropertyListeners.FORMAT,
          _formatListener);
      pr.addPropertyChangeListener(SupportsPropertyListeners.EXTENDED,
          _extendedListener);
    }

  }

  /**
   * create an empty layer, and return it
   *
   * @return a blank layer
   */
  public Layer cleanLayer()
  {
    final Layer res = new BaseLayer();
    return res;
  }

  /**
   * clear out all layers
   *
   */
  public void clear()
  {
    _theLayers.clear();

    // and fire the extended event
    fireExtended(null, null);

  }

  /**
   * finalise function, removes all references to layers
   */
  public void close()
  {
    // first, get the layers to close themselves
    final Enumeration<Editable> enumer = _theLayers.elements();
    while (enumer.hasMoreElements())
    {
      final Object layer = enumer.nextElement();
      if (layer instanceof PlainWrapper)
      {
        final PlainWrapper pw = (PlainWrapper) layer;
        pw.closeMe();
      }
    }

    // and now empty the object itself
    _theLayers.removeAllElements();

    // also tidy up the listeners
    _dataExtendedListeners.clear();
    _dataModifiedListeners.clear();
    _dataReformattedListeners.clear();
  }

  @Override
  public int compareTo(final Plottable arg0)
  {
    final int res;
    final Plottable other = arg0;
    final int myCode = hashCode();
    final int otherCode = other.hashCode();
    if (myCode < otherCode)
      res = -1;
    else if (myCode > otherCode)
      res = 1;
    else
      res = 0;
    return res;
  }

  // ////////////////////////////////////////////////////
  // callback management
  // ////////////////////////////////////////////////////

  /**
   * create a version of the supplied layer name that doesn't exist, by appending a counter
   *
   * @param prefix
   * @return
   */
  public String createUniqueLayerName(final String prefix)
  {
    String res = prefix;

    if (findLayer(prefix) != null)
    {
      // ok, that one is taken
      // we'll have to increment it
      int ctr = 1;
      while (findLayer(prefix + "_" + ctr) != null)
      {
        // nope, taken. increment
        ctr++;
      }

      // ok, we now have a name to use
      res += "_" + ctr;
    }

    return res;
  }

  /**
   * return the layer at this index
   *
   * @param index
   *          the index to look at
   * @return the Layer, or null
   */
  public Layer elementAt(final int index)
  {
    return (Layer) _theLayers.elementAt(index);
  }

  @Override
  public Enumeration<Editable> elements()
  {
    return _theLayers.elements();
  }

  /**
   * see if we can find this layer
   *
   * @param theLayerName
   *          the name of the layer to look for
   * @return the layer
   */
  public Layer findLayer(final String theLayerName)
  {
    Layer res = null;
    // step through our layers
    final Enumeration<Editable> enumer = _theLayers.elements();
    while (enumer.hasMoreElements())
    {
      final Layer thisL = (Layer) enumer.nextElement();
      final String layerName = thisL.getName();
      if (layerName != null)
        if (layerName.equalsIgnoreCase(theLayerName))
        {
          res = thisL;
          break;
        }
    }
    //
    return res;
  }

  public Layer findLayer(final String theName, final boolean recursive)
  {
    Layer res = null;

    if (recursive)
    {
      // step through our layers
      final Enumeration<Editable> enumer = _theLayers.elements();
      while (enumer.hasMoreElements() && res == null)
      {
        final Layer thisL = (Layer) enumer.nextElement();
        final String layerName = thisL.getName();
        if (layerName != null && layerName.equalsIgnoreCase(theName))
        {
          res = thisL;
          break;
        }
        else
        {
          res = checkLayer(thisL, theName);
        }
      }
      //
      return res;

    }
    else
    {
      res = findLayer(theName);
    }

    return res;
  }

  /**
   * inform listeners that we have been extended
   */
  public void fireExtended()
  {
    if (!_suspendFiringExtended)
    {
      final Enumeration<DataListener> enumer = _dataExtendedListeners
          .elements();
      while (enumer.hasMoreElements())
      {
        final DataListener thisOne = enumer.nextElement();
        thisOne.dataExtended(this);
      }
    }
  }

  /**
   * inform listeners that we have been extended
   */
  public void fireExtended(final Plottable newItem, final HasEditables parent)
  {
    if (!_suspendFiringExtended)
    {
      final Enumeration<DataListener> enumer = _dataExtendedListeners
          .elements();
      while (enumer.hasMoreElements())
      {
        final DataListener thisOne = enumer.nextElement();

        // just see if this is a special listener
        if (thisOne instanceof DataListener2)
        {
          // yes, indicate which is the new item
          final DataListener2 d2 = (DataListener2) thisOne;
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
  public void fireModified(final Layer changedLayer)
  {
    final Enumeration<DataListener> enumer = _dataModifiedListeners.elements();
    while (enumer.hasMoreElements())
    {
      final DataListener thisOne = enumer.nextElement();
      thisOne.dataModified(this, changedLayer);
    }
  }

  /**
   * inform listeners that we have had a formatting change
   */
  public void fireReformatted(final Layer changedLayer)
  {
    final Enumeration<DataListener> enumer = _dataReformattedListeners
        .elements();
    while (enumer.hasMoreElements())
    {
      final DataListener thisOne = enumer.nextElement();
      thisOne.dataReformatted(this, changedLayer);
    }
  }

  /**
   * get the bounds of this set of layers (return our SPECIAL area in absence of bounds)
   */
  @Override
  public WorldArea getBounds()
  {
    WorldArea res = getRawBounds();

    // did we find anything?
    if (res == null)
    {
      res = getDebriefOrigin();
    }
    else if (res.getHeight() == 0 && res.getWidth() == 0)
    {
      // ok, expand it, so we've got some coverage
      final double diagExtendNm = 1d;
      final WorldLocation centre = res.getCentreAtSurface();

      // create the new corners
      final WorldLocation newTL = centre.add(new WorldVector(
          MWC.Algorithms.Conversions.Degs2Rads(315), new WorldDistance(
              diagExtendNm, WorldDistance.NM).getValueIn(WorldDistance.DEGS),
          0));
      final WorldLocation newBR = centre.add(new WorldVector(
          MWC.Algorithms.Conversions.Degs2Rads(135), new WorldDistance(
              diagExtendNm, WorldDistance.NM).getValueIn(WorldDistance.DEGS),
          0));

      // and extend the area to include the new corners
      res.extend(newTL);
      res.extend(newBR);
    }

    return res;

  }

  /**
   * Retrieve actions to go with this set of layers.
   */
  public MWC.GUI.Tools.Chart.RightClickEdit getEditor()
  {
    return _myEditor;
  }

  /**
   * get the editable information for this object
   *
   * @return details needed for editing
   */
  @Override
  public Editable.EditorType getInfo()
  {
    return new LayersInfo(this);
  }

  /**
   * the Name of this object
   *
   * @return the name
   */
  @Override
  public String getName()
  {
    return "The Layers";
  }

  /**
   * get the new item listeners
   *
   * @return
   */
  public List<INewItemListener> getNewItemListeners()
  {
    // do we need to rescan the listeners?
    if (_newItemListeners.size() == 0)
    {
      final Layer fLayer = findLayer(FORMATTERS);
      if (fLayer != null)
      {
        final Enumeration<Editable> fEnum = fLayer.elements();
        while (fEnum.hasMoreElements())
        {
          final Editable thisE = fEnum.nextElement();
          if (thisE instanceof INewItemListener)
          {
            _newItemListeners.add((INewItemListener) thisE);
          }
        }
      }
    }

    return _newItemListeners;
  }

  public WorldArea getRawBounds()
  {
    WorldArea res = null;

    final Iterator<Editable> it = _theLayers.iterator();
    while (it.hasNext())
    {
      final Layer thisL = (Layer) it.next();
      final WorldArea newBounds = thisL.getBounds();
      if (newBounds != null)
      {
        if (res == null)
          res = new WorldArea(newBounds);
        else
          res.extend(newBounds);
      }
    }

    return res;
  }

  public TimePeriod getTimePeriod()
  {
    TimePeriod res = null;

    for (final Enumeration<Editable> iter = elements(); iter.hasMoreElements();)
    {
      final Layer thisLayer = (Layer) iter.nextElement();

      // and through this layer
      if (thisLayer instanceof WatchableList)
      {
        final WatchableList thisT = (WatchableList) thisLayer;
        res = extend(res, thisT.getStartDTG());
        res = extend(res, thisT.getEndDTG());
      }
      else if (thisLayer instanceof BaseLayer)
      {
        final Enumeration<Editable> elements = thisLayer.elements();
        while (elements.hasMoreElements())
        {
          final Plottable nextP = (Plottable) elements.nextElement();
          if (nextP instanceof Watchable)
          {
            final Watchable wrapped = (Watchable) nextP;
            final HiResDate dtg = wrapped.getTime();
            if (dtg != null)
            {
              res = extend(res, dtg);

              // also see if it this data type an end time
              if (wrapped instanceof WatchableList)
              {
                // ok, make sure we also handle the end time
                final WatchableList wl = (WatchableList) wrapped;
                final HiResDate endD = wl.getEndDTG();
                if (endD != null)
                {
                  res = extend(res, endD);
                }
              }
            }
          }
          else if (nextP instanceof WatchableList)
          {
            final WatchableList wl = (WatchableList) nextP;
            res = extend(res, wl.getStartDTG());
            res = extend(res, wl.getEndDTG());
          }
        }
      }
    }

    return res;
  }

  @Override
  public boolean getVisible()
  {
    return _isVisible;
  }

  /**
   * whether this item has an editor (yes, of course)
   *
   * @return yes/no flag
   */
  @Override
  public boolean hasEditor()
  {
    return true;
  }

  /**
   * paint all of the layers in this canvas
   *
   * @param dest
   *          destination for the plotting
   */
  @Override
  public void paint(final CanvasType dest)
  {
    // check that we have a valid canvas (that the sizes are set)
    final java.awt.Dimension sArea = dest.getProjection().getScreenArea();
    if (sArea != null)
    {
      if (sArea.width > 0)
      {
        final Enumeration<Editable> enumer = _theLayers.elements();
        while (enumer.hasMoreElements())
        {
          final Layer thisLayer = (Layer) enumer.nextElement();

          // set the line width
          final float oldWid = dest.getLineWidth();
          dest.setLineWidth(thisLayer.getLineThickness());
          thisLayer.paint(dest);
          dest.setLineWidth((int) oldWid);
        }
      }
    }
  }

  private void produceLists()
  {
    _dataModifiedListeners = new Vector<DataListener>(0, 1);
    _dataExtendedListeners = new Vector<DataListener>(0, 1);
    _dataReformattedListeners = new Vector<DataListener>(0, 1);
    _newItemListeners = new ArrayList<INewItemListener>();
  }

  @Override
  public double rangeFrom(final WorldLocation other)
  {
    return -1;
  }

  // ////////////////////////////////////////////////////////////////
  // interface definition
  // ////////////////////////////////////////////////////////////////

  private void readObject(final java.io.ObjectInputStream in)
      throws IOException, ClassNotFoundException
  {

    in.defaultReadObject();

    produceLists();
  }

  /**
   * remove this listener
   *
   * @param theListener
   *          the listener to remove
   */
  public void removeDataExtendedListener(final DataListener theListener)
  {
    _dataExtendedListeners.removeElement(theListener);
  }

  /**
   * remove this listener
   *
   * @param theListener
   *          the listener to remove
   */
  public void removeDataModifiedListener(final DataListener theListener)
  {
    _dataModifiedListeners.removeElement(theListener);
  }

  /**
   * remove this listener
   *
   * @param theListener
   *          the listener to remove
   */
  public void removeDataReformattedListener(final DataListener theListener)
  {
    _dataReformattedListeners.removeElement(theListener);
  }

  /**
   * walk the object tree, to find this item. Then delete it
   *
   * @param item
   */
  public boolean removeThisEditable(final Layer parent, final Editable item)
  {
    boolean found = false;

    if (parent != null)
    {
      // ok, search inside this layer
      final Enumeration<Editable> iter = parent.elements();
      while (iter.hasMoreElements() && found == false)
      {
        final Editable editable = iter.nextElement();
        if (item.equals(editable))
        {
          parent.removeElement(item);
          return true;
        }
        else if (editable instanceof Layer)
        {
          final Layer thisP = (Layer) editable;
          found = removeThisEditable(thisP, item);
        }
      }
    }
    else
    {
      // ok, walk the layers
      // step through our layers
      final Enumeration<Editable> enumer = elements();
      while (enumer.hasMoreElements() && found == false)
      {
        final Layer thisL = (Layer) enumer.nextElement();
        if (item.equals(thisL))
        {
          removeThisLayer(thisL);
          return true;
        }
        else
        {
          found = removeThisEditable(thisL, item);
        }
      }
    }
    return found;
  }

  /**
   * remove a layer from our list
   *
   * @param theLayer
   *          the layer to remove
   */
  public void removeThisLayer(final Layer theLayer)
  {
    // first remove the layer
    _theLayers.removeElement(theLayer);

    if (theLayer instanceof SupportsPropertyListeners)
    {
      final SupportsPropertyListeners pr = (SupportsPropertyListeners) theLayer;
      pr.removePropertyChangeListener(SupportsPropertyListeners.FORMAT,
          _formatListener);
      pr.removePropertyChangeListener(SupportsPropertyListeners.EXTENDED,
          _extendedListener);
    }

    // do we need to tell it that it's being removed?
    if (theLayer instanceof NeedsToBeInformedOfRemove)
    {
      final NeedsToBeInformedOfRemove rem =
          (NeedsToBeInformedOfRemove) theLayer;
      rem.beingRemoved();
    }

    // and fire the modified event
    fireExtended(null, null);
  }

  /**
   * store editor to go with this set of layers. Ideally we would like to store this editor in the
   * LayerManager, but since the LayerManager is an editor itself it doesn't have a constructor and
   * there's no way of setting this data (other than via static data, but it needs to be unique for
   * each operation).
   */
  public void setEditor(final MWC.GUI.Tools.Chart.RightClickEdit editor)
  {
    _myEditor = editor;
  }

  /**
   * duff editable, not used
   *
   * @param val
   *          largely ignored by the people of New Guinea
   */
  public void setName(final String val)
  {
    //
  }

  @Override
  public void setVisible(final boolean visible)
  {
    this._isVisible = visible;
  }

  //
  /**
   * get the current number of layers
   *
   * @return current number of layers
   */
  @Override
  public int size()
  {
    return _theLayers.size();
  }

  /**
   * retrieve the layers. Right, we do some SPECIAL PROCESSING HERE.
   *
   * We want to ensure that we return
   *
   * @return
   */
  public Enumeration<Layer> sortedElements()
  {
    // have a got a creating a sorted set of layers
    final Vector<Layer> res = new Vector<Layer>(0, 1);
    final Enumeration<Editable> numer = _theLayers.elements();

    final Vector<Layer> _backgrounds = new Vector<Layer>(0, 1);
    final Vector<Layer> _buffered = new Vector<Layer>(0, 1);
    final Vector<Layer> _nonBuffered = new Vector<Layer>(0, 1);
    while (numer.hasMoreElements())
    {
      boolean inserted = false;
      final Layer thisLayer = (Layer) numer.nextElement();
      if (thisLayer instanceof BackgroundLayer)
      {
        _backgrounds.add(thisLayer);
        inserted = true;
      }
      else if (thisLayer instanceof BaseLayer)
      {
        final BaseLayer bl = (BaseLayer) thisLayer;
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

  /**
   * store this formatter, if it's sutiable
   *
   * @param theListener
   */
  private void storeFormatter(final Object theListener)
  {
    // if it's an editable, we can add it to the formatters
    if (theListener instanceof Editable)
    {
      Layer formatL = this.findLayer(FORMATTERS);
      if (formatL == null)
      {
        formatL = new BaseLayer(false);
        formatL.setName(FORMATTERS);
        this.addThisLayer(formatL);
      }
      formatL.add((Editable) theListener);

      // ok, clear out the lists
      _newItemListeners.clear();
    }

  }

  /**
   * class to stop the layer manager from firing "extended" messages - particularly when we're
   * loading a lot of data
   *
   * @param suspendFiring
   */
  public void suspendFiringExtended(final boolean suspendFiring)
  {
    _suspendFiringExtended = suspendFiring;
  }

  /**
   * the name of this object
   *
   * @return name
   */
  @Override
  public String toString()
  {
    return getName();
  }

  public String[] trimmedLayers()
  {
    final Vector<String> res = new Vector<String>(0, 1);
    final Enumeration<Editable> enumer = elements();
    while (enumer.hasMoreElements())
    {
      final Layer thisLayer = (Layer) enumer.nextElement();
      if (thisLayer instanceof BaseLayer)
      {
        final BaseLayer bl = (BaseLayer) thisLayer;
        if (bl.canTakeShapes())
          res.add(thisLayer.getName());
      }
    }

    res.add(NEW_LAYER_COMMAND);

    final String[] sampleArray = new String[]
    {"aa"};
    return res.toArray(sampleArray);
  }

  private void walkThis(final BaseLayer layer, final Class<?> mustMatch,
      final OperateFunction function)
  {
    // walk the tree
    final Enumeration<Editable> lIter = layer.elements();
    while (lIter.hasMoreElements())
    {
      final Plottable item = (Plottable) lIter.nextElement();
      if (item.getVisible())
      {
        if (mustMatch.isAssignableFrom(item.getClass()))
        {
          function.operateOn(item);
        }
        else
        {
          if (item instanceof BaseLayer)
          {
            walkThis((BaseLayer) item, mustMatch, function);
          }
        }
      }
    }
  }

  public void walkVisibleItems(final Class<?> mustMatch,
      final OperateFunction function)
  {
    // walk the tree
    final Enumeration<Editable> lIter = elements();
    while (lIter.hasMoreElements())
    {
      final Layer layer = (Layer) lIter.nextElement();
      if (layer.getVisible())
      {
        if (mustMatch.isAssignableFrom(layer.getClass()))
        {
          function.operateOn(layer);
        }
        else
        {
          if (layer instanceof BaseLayer)
          {
            walkThis((BaseLayer) layer, mustMatch, function);
          }
        }
      }

    }
  }

}
