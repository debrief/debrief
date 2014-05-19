package MWC.GUI.Tools.Chart;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: RightClickEdit.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $st
// $Log: RightClickEdit.java,v $
// Revision 1.7  2005/12/05 11:02:19  Ian.Mayo
// Improve what's shown in right-click edit
//
// Revision 1.6  2004/10/07 14:23:14  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.5  2004/09/06 14:04:36  Ian.Mayo
// Switch to supporting editables in Layer Manager, and showing icon for any editables which have one
//
// Revision 1.4  2004/09/03 15:13:27  Ian.Mayo
// Reflect refactored plottable getElements
//
// Revision 1.3  2004/08/31 08:05:12  Ian.Mayo
// Rename/remove old tests, so that we don't have non-testing classes whose named ends with Test (in support of Maven integration)
//
// Revision 1.2  2004/05/25 15:43:54  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:43  Ian.Mayo
// Initial import
//
// Revision 1.4  2003-02-07 09:49:26+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.3  2002-11-25 14:39:31+00  ian_mayo
// Tidy comments
//
// Revision 1.2  2002-05-28 09:25:59+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:09+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:43+01  ian_mayo
// Initial revision
//
// Revision 1.10  2002-03-19 11:07:03+00  administrator
// Switch to Swing menus, and allow additional bean items to be plotted with base bean items (according to new Editable property)
//
// Revision 1.8  2002-01-25 13:31:37+00  administrator
// Pass around the "top-layer" objects so that we can update just the required layer following move
//
// Revision 1.7  2002-01-24 14:22:29+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.6  2001-11-14 19:51:43+00  administrator
// make ObjectConstruct support class public
//
// Revision 1.5  2001-08-29 19:36:32+01  administrator
// Remove commented out methods
//
// Revision 1.4  2001-08-24 12:38:34+01  administrator
// Provide an accessor to get at the current set of plottable extras
//
// Revision 1.3  2001-08-21 12:09:14+01  administrator
// Replace anonymous listeners with named class (to remove final objects)
//
// Revision 1.2  2001-08-17 07:56:18+01  administrator
// make data local not static, to clear up memory leaks
//
// Revision 1.1  2001-08-13 12:49:15+01  administrator
// Recursively pass through layered data
//
// Revision 1.0  2001-07-17 08:42:58+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-26 11:19:54+00  novatech
// provided indicator of current value in drop-down selection lists
//
// Revision 1.2  2001-01-21 21:36:49+00  novatech
// pass the layers to the menu creators
//
// Revision 1.1  2001-01-03 13:41:52+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:38  ianmayo
// initial version
//
// Revision 1.20  2000-11-08 11:51:52+00  ian_mayo
// correct years_old problem with creating hashtables for new Sessions (we were creating a fresh one each time, not adding a new one to our list
//
// Revision 1.19  2000-11-02 16:44:34+00  ian_mayo
// changing Layer into Interface, replaced by BaseLayer
//
// Revision 1.18  2000-10-09 13:35:45+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.17  2000-09-26 09:49:40+01  ian_mayo
// Create embedded class which provides skeleton for creating menu of properties for non-plottable objects
//
// Revision 1.16  2000-09-21 09:06:42+01  ian_mayo
// make Editable.EditorType a transient parameter, to prevent it being written to file
//
// Revision 1.15  2000-08-29 10:56:04+01  ian_mayo
// inform the Editable that we've changed it after calling one of it's methods
//
// Revision 1.14  2000-08-21 09:49:40+01  ian_mayo
// create editor menu items for additional plot objects
//
// Revision 1.13  2000-08-18 13:35:13+01  ian_mayo
// tidy up action handlers, plus fire off PropertyChange event
//
// Revision 1.12  2000-05-23 13:37:30+01  ian_mayo
// provide drop-down lists of editable properties where possible
//
// Revision 1.11  2000-02-14 16:51:58+00  ian_mayo
// corrected code which creates "Edit" menu item for editable parents
//
// Revision 1.10  2000-02-04 16:08:28+00  ian_mayo
// tidying up
//
// Revision 1.9  2000-02-03 15:39:01+00  ian_mayo
// switch to using multiple lists of PlottableExtras,
// indexed by the PropertiesPanel used, instead of
// a single static list to support all AnalysisViews
//
// Revision 1.8  2000-02-02 14:25:56+00  ian_mayo
// check that PlottableExtras were available
//
// Revision 1.7  2000-01-20 10:14:29+00  ian_mayo
// tidier processing & new signature
//
// Revision 1.6  2000-01-18 15:07:42+00  ian_mayo
// created new, static CreateMenuFor method
//
// Revision 1.5  2000-01-14 11:58:57+00  ian_mayo
// Tidy up processing of methods and boolean parameters
//
// Revision 1.4  1999-12-03 14:34:33+00  ian_mayo
// when adding objects to menu, always add objects which don't return location
//
// Revision 1.3  1999-11-18 11:07:45+00  ian_mayo
// now handling SWING canvas
//
// Revision 1.2  1999-10-13 17:19:14+01  ian_mayo
// no need to create menu entries for 'additional info' elements which do not need  custom editor, since the PropertyPanel will display all of them
//
// Revision 1.1  1999-10-12 15:36:22+01  ian_mayo
// Initial revision
//
// Revision 1.4  1999-08-04 09:43:06+01  administrator
// make tools serializable
//
// Revision 1.3  1999-07-27 16:08:17+01  administrator
// show editors for screen decorations
//
// Revision 1.2  1999-07-27 12:08:21+01  administrator
// hit testing improved
//
// Revision 1.1  1999-07-27 10:59:46+01  administrator
// Initial revision
//
// Revision 1.3  1999-07-27 10:51:03+01  administrator
// improved hit-testing, & added projection-editor tool
//
// Revision 1.2  1999-07-27 09:27:02+01  administrator
// tidying up use of tools
//
// Revision 1.1  1999-07-16 10:01:55+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-12 08:09:28+01  administrator
// Initial revision
//

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExcludeFromRightClickEdit;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Action;
import MWC.GUI.Undo.UndoBuffer;

public class RightClickEdit implements PlainChart.ChartClickListener,
		Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////
	PropertiesPanel _thePanel;

	/**
	 * list of additional classes which we invite to extend our pop up menu
	 */
	Vector<MenuCreator> _theExtras;

	/**
	 * this static hashtable contains multiple lists of plottable extras, indexed
	 * by the properties panel they edit
	 * <p/>
	 * PlottableExtras are lists of additional classes which are able to extend
	 * the popup menu once a Plottable has been selected
	 */
	private final Hashtable<PropertiesPanel, Vector<PlottableMenuCreator>> _thePlottableExtras;

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	public RightClickEdit(final PropertiesPanel thePanel)
	{
		_thePanel = thePanel;
		_theExtras = new Vector<MenuCreator>(0, 1);
		_thePlottableExtras = new Hashtable<PropertiesPanel, Vector<PlottableMenuCreator>>();
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	/**
	 * get the list of extra editors which we know about
	 */
	public java.util.Vector<PlottableMenuCreator> getExtraPlottableEditors(
			final PropertiesPanel thePanel)
	{
		return (Vector<PlottableMenuCreator>) _thePlottableExtras.get(thePanel);
	}

	public void addMenuCreator(final MenuCreator mn)
	{
		_theExtras.addElement(mn);
	}

	public void removeMenuCreator(final MenuCreator mn)
	{
		_theExtras.removeElement(mn);
	}

	public void addPlottableMenuCreator(final PlottableMenuCreator mn,
			final PropertiesPanel thePanel)
	{
		// see if we have a list for this panel
		final Vector<PlottableMenuCreator> obj = _thePlottableExtras.get(thePanel);
		Vector<PlottableMenuCreator> vt = null;

		if (obj == null)
		{
			// we've got to make one
			vt = new Vector<PlottableMenuCreator>(0, 1);

			// and add it to our list
			_thePlottableExtras.put(thePanel, vt);
		}
		else
		{
			vt = obj;
		}

		// now put the menu creator into the vector
		vt.addElement(mn);
	}

	public void removePlottableMenuCreator(final PlottableMenuCreator mn,
			final PropertiesPanel thePanel)
	{
		// see if we have a list for this panel
		final Vector<PlottableMenuCreator> oj = _thePlottableExtras.get(thePanel);
		if (oj != null)
		{
			oj.removeElement(mn);
		}
	}

	public static class ObjectConstruct
	{
		public Plottable object = null;

		public double distance = -1;

		@SuppressWarnings("unused")
		private Layer parent = null;  // we don't currently use the parent, but let's keep it safe anyway

		public java.util.Vector<Plottable> rangeIndependent;

		public Layer topLayer;

		public void setData(final Plottable p, final double dist, final Layer l, final Layer top)
		{
			object = p;
			distance = dist;
			parent = l;
			topLayer = top;
		}

		public void addRangeIndependent(final Plottable p)
		{
			if (rangeIndependent == null)
				rangeIndependent = new Vector<Plottable>(1, 1);
			rangeIndependent.add(p);
		}
	}

	public static void findNearest(final Layer thisLayer,
			final MWC.GenericData.WorldLocation cursorPos,
			final ObjectConstruct currentNearest, final Layer topLayer)
	{
		// so, step through this layer
		if (thisLayer.getVisible())
		{
			// is it a 'special' layer that provides it's own range?
			if (thisLayer instanceof BaseLayer.ProvidesRange)
			{
				// how far away is it?
				final double rng = thisLayer.rangeFrom(cursorPos);

				// does it return a range?
				if (rng != -1.0)
				{
					// has our results object been initialised?
					if (currentNearest.object == null)
					{
						// no, just copy in the data
						currentNearest.setData(thisLayer, rng, thisLayer, topLayer);
					}
					else
					{
						// yes it has, copy the data items in
						if (rng < currentNearest.distance)
						{
							currentNearest.setData(thisLayer, rng, thisLayer, topLayer);
						}
					}
				}
			}

			else
			{

				// go through this layer
				final Enumeration<Editable> enumer = thisLayer.elements();

				// check something got returned
				if (enumer != null)
				{
					while (enumer.hasMoreElements())
					{
						final Object next = enumer.nextElement();

						// is this item a layer itself?
						if ((next instanceof Layer)
								&& (!(next instanceof Editable.DoNoInspectChildren)))
						{
							// cast to Layer
							final Layer l = (Layer) next;

							// find the nearest values
							findNearest(l, cursorPos, currentNearest, topLayer);
						}
						else
						{

							if (next instanceof Plottable)
							{
								final Plottable p = (Plottable) next;

								// is it visible even?
								if (p.getVisible())
								{

									// is there an editor for this item
									if (p.hasEditor())
									{
										// how far away is it?
										final double rng = p.rangeFrom(cursorPos);

										// does it return a range?
										if (rng != -1.0)
										{
											// has our results object been initialised?
											if (currentNearest.object == null)
											{
												// no, just copy in the data
												currentNearest.setData(p, rng, thisLayer, topLayer);
											}
											else
											{
												// yes it has, copy the data items in
												if (rng < currentNearest.distance)
												{
													currentNearest.setData(p, rng, thisLayer, topLayer);
												}
											}
										}
										else
										{
											// not range related, add to our list of non-location
											// related
											// entities (unless it's a type we specifically exclude
											// from
											// right-click editing, like Narrative Entries)
											if (p instanceof ExcludeFromRightClickEdit)
											{
												// just ignore it, we don't want to show it.
											}
											else
												currentNearest.addRangeIndependent(p);
										}
									}
									else
									{
										// no editor, so we can't create a menu item for it anyway
									}
								}
							} // else-part of is this a layer
						} // stepping through the enumereration
					} // whether anything was returned
				}
			} // whether its a layer that provides its own range

		}
	}

	public void CursorClicked(final java.awt.Point thePoint,
			final MWC.GenericData.WorldLocation thePos, final CanvasType theCanvas,
			final Layers theData)
	{

		//
		Plottable res = null;

		// the parent layer for this object
		Layer theParent = null;
		double dist = 0;

		// keep track of editable items which don't have a screen location
		Vector<Plottable> noPoints = null;

		// we also want to find which layer contained the nearest item - so we know
		// to update that layer
		double layerDist = -1;
		Layer topLayer = null;

		// find the nearest editable item
		final ObjectConstruct vals = new ObjectConstruct();
		final int num = theData.size();
		for (int i = 0; i < num; i++)
		{
			final Layer thisL = theData.elementAt(i);
			if (thisL.getVisible())
			{
				// find the nearest items, this method call will recursively pass down
				// through
				// the layers
				findNearest(thisL, thePos, vals, thisL);

				if ((layerDist == -1) || (vals.distance < layerDist))
				{
					layerDist = vals.distance;
					topLayer = thisL;
				}

			}
		}

		res = vals.object;
		theParent = vals.topLayer;
		dist = vals.distance;
		noPoints = vals.rangeIndependent;

		// see if this is in our dbl-click range
		if (HitTester.doesHit(thePoint, thePos, dist, theCanvas.getProjection()))
		{
			// do nothing, it's ok
		}
		else
		{
			res = null;
		}

		// so, we've spotted a valid plottable, see if there are any
		// other operations to apply to it.
		final JPopupMenu theMenu = createMenu((Plottable) res, thePoint, theParent,
				theCanvas, theData, topLayer);

		// separator, before we get the real tat
		theMenu.addSeparator();

		// and now extend this menu with the other new ones not related to this
		// plottable
		if (res == null)
		{
			final Enumeration<MenuCreator> creators = _theExtras.elements();
			while (creators.hasMoreElements())
			{
				final MenuCreator mc = (MenuCreator) creators.nextElement();
				mc.createMenu(theMenu, thePoint, theCanvas, _thePanel, theData);
			}
		}

		// are there any non-position related points?
		if ((noPoints != null) && (res == null))
		{

			final Enumeration<Plottable> pts = noPoints.elements();
			while (pts.hasMoreElements())
			{
				final Plottable p = (Plottable) pts.nextElement();

				// and add the items, starting with the plottable itself
				final Editable.EditorType et2 = p.getInfo();
				final JMenuItem m = new JMenuItem("Edit "
						+ et2.getBeanDescriptor().getDisplayName());
				m.addActionListener(new EditThisActionListener(_thePanel, et2,
						theParent));
				theMenu.add(m);

				// create temporary menu to hold any editors for this additional item
				final JMenu mn = new JMenu(et2.getBeanDescriptor().getDisplayName());

				// try for any boolean editors for this other object
				createBooleanEditors(mn, (Editable) et2.getData(), theCanvas,
						_thePanel, theData, theParent, topLayer);

				// try for any boolean editors for this other object
				createSelectionEditors(mn, (Editable) et2.getData(), theCanvas,
						_thePanel, theData, theParent, topLayer);

				// put in the separator if we need to
				if (mn.getItemCount() > 0)
				{
					mn.addSeparator();
				}

				// try for any bean methods for this
				createMethodInvokers(mn, (Editable) et2.getData(), theCanvas);

				// were any items added
				if (mn.getItemCount() > 0)
				{
					theMenu.add(mn);
				}

			}
		} // whether there were any position-independent points

		showMenu(theMenu, thePoint, theCanvas);

	}

	protected JPopupMenu createMenu(final Plottable data, final Point thePoint,
			final Layer theParent, final CanvasType theCanvas,
			final Layers theLayers, final Layer updateLayer)
	{
		Vector<PlottableMenuCreator> oj = null;

		if (_thePlottableExtras != null)
			oj = _thePlottableExtras.get(_thePanel);

		final JPopupMenu res = createMenuFor(data, thePoint, theCanvas, theParent,
				_thePanel, theLayers, oj, updateLayer);

		return res;

	}

	static public JPopupMenu createMenuFor(final Editable data,
			final Point thePoint, final CanvasType theCanvas, final Layer theParent,
			final PropertiesPanel thePanel, final Layers theLayers,
			final java.util.Vector<PlottableMenuCreator> extras,
			final Layer updateLayer)
	{

		// change the panel parameter to be a final
		final PropertiesPanel myPanel = thePanel;

		// create the popup menu
		final JPopupMenu pi = new JPopupMenu();

		// check we have info
		if (data != null)
		{

			// get the editable data for this item
			final Editable.EditorType bi = data.getInfo();

			if (bi != null)
			{
				// and add the items, starting with the plottable itself
				final Editable.EditorType et2 = bi;
				final JMenuItem m = new JMenuItem("Edit "
						+ et2.getBeanDescriptor().getDisplayName());
				m.addActionListener(new EditThisActionListener(myPanel, et2, theParent));
				pi.add(m);

				final JMenu mp = new JMenu(et2.getBeanDescriptor().getDisplayName());

				// also see if there are any boolean property editors for this ite,
				createBooleanEditors(mp, data, theCanvas, thePanel, theLayers,
						theParent, updateLayer);

				// are we showing combined lists?
				if (bi.combinePropertyLists())
					createAdditionalBooleanEditors(mp, data, theCanvas, thePanel,
							theLayers, theParent, updateLayer);

				// also see if there are any lists of properties which
				// we can create as a drop down list
				createSelectionEditors(mp, data, theCanvas, thePanel, theLayers,
						theParent, updateLayer);

				// are we showing combined lists?
				if (bi.combinePropertyLists())
					createAdditionalSelectionEditors(mp, data, theCanvas, thePanel,
							theLayers, theParent, updateLayer);

				// put in the separator if we need to
				if (mp.getItemCount() > 0)
				{
					mp.addSeparator();
				}

				// see if there are any methods we should call
				createMethodInvokers(mp, data, theCanvas);

				// are we showing combined lists?
				if (bi.combinePropertyLists())
					createAdditionalMethodInvokers(mp, data, theCanvas);

				// were any items added
				if (mp.getItemCount() > 0)
				{
					pi.add(mp);
				}

				if (!bi.combinePropertyLists())
				{
					// we've already added the additional items, we don't need to do it
					// here

					// add separator to indicate that this is a new item we're editing
					pi.addSeparator();

					// are there any additional edit types returned which have
					// a custom editor and therefore require their own entry?
					final BeanInfo[] bil = bi.getAdditionalBeanInfo();
					if (bil != null)
					{
						// so, we've got our beaninfo items, see it we can edit it
						final int num = bil.length;
						for (int j = 0; j < num; j++)
						{
							final BeanInfo bin = bil[j];
							if (bin instanceof MWC.GUI.Editable.EditorType)
							{
								final Editable.EditorType et = (Editable.EditorType) bin;
								final BeanDescriptor bd = et.getBeanDescriptor();

								// is there a bean descriptor?
								if (bd != null)
								{
									// produce the edit entry
									final JMenuItem mi = new JMenuItem("Edit "
											+ et.getBeanDescriptor().getDisplayName());

									// and add an action listener
									mi.addActionListener(new EditThisActionListener(myPanel, et,
											theParent));

									// add to the popup menu
									pi.add(mi);

								}

								// create temporary menu to hold any editors for this additional
								// item
								final JMenu mn = new JMenu(et.getBeanDescriptor()
										.getDisplayName());

								// try for any boolean editors for this other object
								createBooleanEditors(mn, (Editable) et.getData(), theCanvas,
										thePanel, theLayers, theParent, updateLayer);

								// try for any list editors for this other object
								createSelectionEditors(mn, (Editable) et.getData(), theCanvas,
										thePanel, theLayers, theParent, updateLayer);

								// put in the separator if we need to
								if (mn.getItemCount() > 0)
								{
									mn.addSeparator();
								}

								// try for any bean methods for this
								createMethodInvokers(mn, (Editable) et.getData(), theCanvas);

								// were any items added
								if (mn.getItemCount() > 0)
								{
									pi.add(mn);
								}

							} // end of if this beaninfo is editable

						} // looping through the additional bean items

					} // end of if there are any additional bean items at all

				} // end of whether any additional bean items were returned

			} // end of if any beaninfo was returned for this item

			// see if we have a list of extras for this panel
			if (extras != null)
			{

				pi.addSeparator();

				final Enumeration<PlottableMenuCreator> creators = extras.elements();
				while (creators.hasMoreElements())
				{
					final PlottableMenuCreator mc = (PlottableMenuCreator) creators
							.nextElement();
					mc.createMenu(pi, data, thePoint, theCanvas, thePanel, theParent,
							theLayers, updateLayer);
					// pi.addSeparator();
				}
			} // whether there were actually any plottable extras

		} // end of whether there we received any data.

		// separator, before we get the real tat
		// pi.addSeparator();

		return pi;

	}

	// CS-IGNORE:ON FINAL_PARAMETERS
	protected void showMenu(JPopupMenu menu, Point thePoint, CanvasType theCanvas)
	{
		if (theCanvas instanceof java.awt.Canvas)
		{
			MWC.Utilities.Errors.Trace
					.trace("POPUP MENUS NOT SUPPORTED UNDER AWT - IMPLEMENT MENU BUILDER HELPER CLASSES");
			// Canvas dest = (Canvas)theCanvas;
			// dest.add(theMenu);
			// theMenu.show(dest, thePoint.x, thePoint.y);
		}
		if (theCanvas instanceof javax.swing.JComponent)
		{
			final javax.swing.JComponent dest = (javax.swing.JComponent) theCanvas;
			dest.add(menu);
			menu.show(dest, thePoint.x, thePoint.y);
		}

		menu = null;
		thePoint = null;
		theCanvas = null;
	}

	// CS-IGNORE:OFF FINAL_PARAMETERS

	static protected void createAdditionalMethodInvokers(final JMenu theMenu,
			final Editable theItem, final CanvasType theCanvas)
	{

		final Editable.EditorType bi = theItem.getInfo();

		// are there any additional edit types returned which have
		// a custom editor and therefore require their own entry?
		final BeanInfo[] bil = bi.getAdditionalBeanInfo();
		if (bil != null)
		{
			// so, we've got our beaninfo items, see it we can edit it
			final int num = bil.length;
			for (int j = 0; j < num; j++)
			{
				final BeanInfo bin = bil[j];
				if (bin instanceof MWC.GUI.Editable.EditorType)
				{
					final Editable.EditorType et = (Editable.EditorType) bin;
					final BeanDescriptor bd = et.getBeanDescriptor();

					// is there a bean descriptor?
					if (bd != null)
					{
						// try for any boolean editors for this other object
						createMethodInvokers(theMenu, (Editable) et.getData(), theCanvas);
					}
				}
			}
		}
	}

	static public void createMethodInvokers(final JMenu theMenu,
			final Editable theItem, final CanvasType theCanvas)
	{

		// also check if our additional data has any methods
		// check we have methods
		final MethodDescriptor[] mds = theItem.getInfo().getMethodDescriptors();

		// now step through them
		if (mds != null)
		{
			final int cnt = mds.length;
			for (int i = 0; i < cnt; i++)
			{
				// get this method
				final MethodDescriptor md = mds[i];
				final Method me = md.getMethod();
				final JMenuItem m2 = new ourMenuItem(md.getDisplayName(), me, theItem,
						theCanvas, theItem);
				theMenu.add(m2);

			}
		}

	}

	static protected void createAdditionalBooleanEditors(final JMenu theMenu,
			final Editable theItem, final CanvasType theCanvas,
			final PropertiesPanel thePanel, final Layers theLayers,
			final Layer parentLayer, final Layer updateLayer)
	{

		final Editable.EditorType bi = theItem.getInfo();

		// are there any additional edit types returned which have
		// a custom editor and therefore require their own entry?
		final BeanInfo[] bil = bi.getAdditionalBeanInfo();
		if (bil != null)
		{
			// so, we've got our beaninfo items, see it we can edit it
			final int num = bil.length;
			for (int j = 0; j < num; j++)
			{
				final BeanInfo bin = bil[j];
				if (bin instanceof MWC.GUI.Editable.EditorType)
				{
					final Editable.EditorType et = (Editable.EditorType) bin;
					final BeanDescriptor bd = et.getBeanDescriptor();

					// is there a bean descriptor?
					if (bd != null)
					{
						// try for any boolean editors for this other object
						createBooleanEditors(theMenu, (Editable) et.getData(), theCanvas,
								thePanel, theLayers, parentLayer, updateLayer);
					}
				}
			}
		}
	}

	static protected void createBooleanEditors(final JMenu theMenu,
			final Editable theItem, final CanvasType theCanvas,
			final PropertiesPanel thePanel, final Layers theLayers,
			final Layer parentLayer, final Layer updateLayer)
	{

		final PropertiesPanel myPanel = thePanel;

		// step through the properties and see if any are boolean
		final PropertyDescriptor props[] = theItem.getInfo()
				.getPropertyDescriptors();
		if (props != null)
		{
			for (int i = 0; i < props.length; i++)
			{
				final PropertyDescriptor prop = props[i];
				final Class<?> thisType = prop.getPropertyType();
				final Class<?> boolClass = Boolean.class;
				if ((thisType == boolClass) || (thisType.equals(boolean.class)))
				{
					// hey we've found one
					final JCheckBoxMenuItem cm = new JCheckBoxMenuItem(
							prop.getDisplayName());

					try
					{
						// get the current value
						final Method getter = prop.getReadMethod();
						final Method setter = prop.getWriteMethod();
						final Object val = getter.invoke(theItem, (Object[]) null);
						final boolean current = ((Boolean) val).booleanValue();
						cm.setState(current);

						final BooleanOperationAction action = new BooleanOperationAction(
								setter, theItem, prop.getDisplayName(), theCanvas, !current,
								theItem.getInfo(), theLayers, parentLayer, updateLayer);

						cm.addItemListener(new DoThisListener(action, myPanel.getBuffer()));

						theMenu.add(cm);
					}
					catch (final Exception e)
					{
						MWC.Utilities.Errors.Trace.trace(e);
					}
				}

			}
		}
	}

	static protected void createAdditionalSelectionEditors(final JMenu theMenu,
			final Editable theItem, final CanvasType theCanvas,
			final PropertiesPanel thePanel, final Layers theLayers,
			final Layer parentLayer, final Layer updateLayer)
	{

		final Editable.EditorType bi = theItem.getInfo();

		// are there any additional edit types returned which have
		// a custom editor and therefore require their own entry?
		final BeanInfo[] bil = bi.getAdditionalBeanInfo();
		if (bil != null)
		{
			// so, we've got our beaninfo items, see it we can edit it
			final int num = bil.length;
			for (int j = 0; j < num; j++)
			{
				final BeanInfo bin = bil[j];
				if (bin instanceof MWC.GUI.Editable.EditorType)
				{
					final Editable.EditorType et = (Editable.EditorType) bin;
					final BeanDescriptor bd = et.getBeanDescriptor();

					// is there a bean descriptor?
					if (bd != null)
					{
						// try for any boolean editors for this other object
						createSelectionEditors(theMenu, (Editable) et.getData(), theCanvas,
								thePanel, theLayers, parentLayer, updateLayer);
					}
				}
			}
		}
	}

	static protected void createSelectionEditors(final JMenu theMenu,
			final Editable theItem, final CanvasType theCanvas,
			final PropertiesPanel thePanel, final Layers theLayers,
			final Layer theParent, final Layer updateLayer)
	{

		final PropertiesPanel myPanel = thePanel;

		// retrieve the list of properties
		final PropertyDescriptor props[] = theItem.getInfo()
				.getPropertyDescriptors();

		// are there any?
		if (props != null)
		{
			// step through the list
			for (int i = 0; i < props.length; i++)
			{
				// get this property
				final PropertyDescriptor prop = props[i];

				// the property editor we are going to use
				PropertyEditor pe = null;

				// find out the type of the editor
				final Method m = prop.getReadMethod();
				final Class<?> cl = m.getReturnType();

				// is there a custom editor for this type?
				final Class<?> c = prop.getPropertyEditorClass();

				// try to create an editor for this class
				try
				{
					if (c != null)
						pe = (PropertyEditor) c.newInstance();
				}
				catch (final Exception e)
				{
					MWC.Utilities.Errors.Trace.trace(e);
				}

				// did it work?
				if (pe == null)
				{
					// try to find an editor for this through our manager
					pe = PropertyEditorManager.findEditor(cl);
				}

				// have we managed to create an editor?
				if (pe != null)
				{
					// just check that we haven't already created a
					// boolean editor for this field already
					final Class<?> boolClass = Boolean.class;
					if ((cl != boolClass) && (!cl.equals(boolean.class)))
					{

						// retrieve the tags
						final String[] tags = pe.getTags();

						// are there any tags for this class?
						if (tags != null)
						{
							// create a drop-down list
							final JMenu thisMen = new JMenu(prop.getDisplayName());

							// sort out the setter details
							final Method getter = prop.getReadMethod();
							final Method setter = prop.getWriteMethod();

							// get the current value
							Object val = null;
							try
							{
								val = getter.invoke(theItem, (Object[]) null);
							}
							catch (final Exception e)
							{
								MWC.Utilities.Errors.Trace.trace(e);
							}
							pe.setValue(val);

							// convert the current value to text
							final String current = pe.getAsText();

							// and now a drop-down item for each options
							for (int j = 0; j < tags.length; j++)
							{
								final String thisTag = tags[j];

								// create the item
								final JCheckBoxMenuItem thisOption = new JCheckBoxMenuItem(
										thisTag, thisTag.equals(current));

								// add it to the menu
								thisMen.add(thisOption);

								// create the custom action
								final SelectionOperationAction action = new SelectionOperationAction(
										setter, theItem, prop.getDisplayName(), theCanvas, tags[j],
										current, pe, theItem.getInfo(), theLayers, theParent,
										updateLayer);

								// create the action listener

								thisOption.addItemListener(new DoThisListener(action, myPanel
										.getBuffer()));
							}

							// and add ourselves to the menu
							theMenu.add(thisMen);
						}
					}
				}
			}
		}
	}

	// //////////////////////////////////////////////////////////
	// embedded class containing information necessary to handle
	// menu action
	// //////////////////////////////////////////////////////////

	static public class EditThisActionListener implements
			java.awt.event.ActionListener
	{
		PropertiesPanel _myPanel;

		Editable.EditorType _myEditor;

		/**
		 * the layer this item belongs to (or null if not known)
		 */
		Layer _parentLayer;

		public EditThisActionListener(final PropertiesPanel thePanel,
				final Editable.EditorType editor, final Layer parentLayer)
		{
			_myPanel = thePanel;
			_myEditor = editor;
			_parentLayer = parentLayer;
		}

		public void actionPerformed(final java.awt.event.ActionEvent event)
		{
			_myPanel.addEditor(_myEditor, _parentLayer);
			_myPanel = null;
			_myEditor = null;
		}
	}

	static protected class DoThisListener implements java.awt.event.ItemListener,
			java.awt.event.ActionListener
	{
		private Action _myAction;

		private UndoBuffer _myBuffer;

		public DoThisListener(final Action action, final UndoBuffer buffer)
		{
			_myAction = action;
			_myBuffer = buffer;
		}

		public void itemStateChanged(final ItemEvent e)
		{
			doIt();
		}

		public void actionPerformed(final ActionEvent e)
		{
			doIt();
		}

		private void doIt()
		{
			// perform the operation
			_myAction.execute();
			// add ourselves to the buffer
			_myBuffer.add(_myAction);
			// clear local references
			_myAction = null;
			_myBuffer = null;
		}

	}

	// ///////////////////////////////////////////////////////////
	// embedded interface for classes which may wish to add menu
	// items
	// //////////////////////////////////////////////////////////
	static public interface PlottableMenuCreator
	{
		/**
		 * add extended functionality for the point found
		 * 
		 * @param menu
		 *          the Menu to add items to
		 * @param data
		 *          the Plottable point identified
		 * @param thePoint
		 *          the screen location of the mouse click
		 * @param theCanvas
		 *          the canvas to update following changes
		 * @param thePanel
		 *          the properties page current available
		 * @param theParent
		 *          the immediate parent for this object
		 * @param theLayers
		 *          the set of layers (plus data) currently in use
		 * @param updateLayer
		 *          the top level parent of this object (the one we have to refresh)
		 */
		public void createMenu(JPopupMenu menu, Editable data, Point thePoint,
				CanvasType theCanvas, PropertiesPanel thePanel, Layer theParent,
				Layers theLayers, Layer updateLayer);

	}

	// ///////////////////////////////////////////////////////////
	// embedded class which executes commands, but will also
	// allow them to be placed on the undo buffer
	// //////////////////////////////////////////////////////////

	static public class ourMenuItem extends JMenuItem implements ActionListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		transient protected Method _myMethod;

		protected Object _myData;

		protected CanvasType _theCanvas;

		protected Editable _theEditable;

		public ourMenuItem(final String name, final Method m, final Object data,
				final CanvasType theCanvas, final Editable theEditable)
		{
			super(name);
			_myData = data;
			_myMethod = m;
			_theCanvas = theCanvas;
			_theEditable = theEditable;

			addActionListener(this);

		}

		public void actionPerformed(final ActionEvent e)
		{
			doIt();
		}

		private void doIt()
		{
			try
			{
				_myMethod.invoke(_myData, (Object[]) null);
				_theCanvas.updateMe();
				// inform the object that we've updated it.
				_theEditable.getInfo().fireChanged(this, _myMethod.toString(), null,
						null);

			}
			catch (final Exception e)
			{
				MWC.Utilities.Errors.Trace.trace(e);
			}
		}

	}

	// /////////////////////////////////////////////////////////////////////
	//
	// ///////////////////////////////////////////////////////////////
	abstract static protected class PlainOperationAction implements Action
	{
		Object _theData;

		transient Editable.EditorType _theEditor;

		String _theDescriptor;

		CanvasType _theChart;

		protected PlainOperationAction(final Object theData,
				final Editable.EditorType theEditor, final String theDescriptor,
				final CanvasType theChart)
		{
			_theData = theData;
			_theEditor = theEditor;
			_theDescriptor = theDescriptor;
			_theChart = theChart;
		}

		protected void fireAction(final Object newVal, final Object oldVal)
		{
			_theEditor.fireChanged(this, _theDescriptor, oldVal, newVal);
		}

		public String toString()
		{
			return _theDescriptor;
		}

		protected void updateChart()
		{
			_theChart.updateMe();
		}
	}

	// //////////////////////////////////////////////////////////////////
	// store action information
	static protected class BooleanOperationAction extends PlainOperationAction
	{
		Method _theSetter;

		Object _theData1;

		boolean _newVal;

		Layers _theLayers;

		Layer _theParent;

		Layer _updateLayer;

		public BooleanOperationAction(final Method theSetter, final Object theData,
				final String descriptor, final CanvasType theChart,
				final boolean newVal, final Editable.EditorType theEditor,
				final Layers theLayers, final Layer theParent, final Layer updateLayer)
		{
			super(theData, theEditor, descriptor, theChart);
			_theSetter = theSetter;
			_theData1 = theData;
			_newVal = newVal;
			_theLayers = theLayers;
			_theParent = theParent;
			_updateLayer = updateLayer;
		}

		public boolean isRedoable()
		{
			return true;
		}

		public boolean isUndoable()
		{
			return true;
		}

		public void undo()
		{
			// hey, do the opposite!
			try
			{
				final Object args[] =
				{ new Boolean(!_newVal) };
				_theSetter.invoke(_theData1, args);

				// inform the editable that we've updated it
				fireAction(new Boolean(_newVal), new Boolean(!_newVal));

				// and trigger a redraw
				_theLayers.fireReformatted(_updateLayer);

			}
			catch (final Exception e)
			{
				MWC.Utilities.Errors.Trace.trace(e);
			}
		}

		public void execute()
		{
			// hey, do it!
			try
			{
				final Object args[] =
				{ new Boolean(_newVal) };
				_theSetter.invoke(_theData1, args);

				// inform the editable that we've updated it
				fireAction(new Boolean(!_newVal), new Boolean(_newVal));

				// and trigger a redraw
				_theLayers.fireReformatted(_updateLayer);
			}
			catch (final Exception e)
			{
				MWC.Utilities.Errors.Trace.trace(e);
			}
		}
	}

	// //////////////////////////////////////////////////////////////////
	// store action information
	static protected class SelectionOperationAction extends PlainOperationAction
	{
		Method _theSetter;

		String _newVal;

		String _oldVal;

		PropertyEditor _editor;

		Layers _theLayers;

		Layer _theParent;

		Layer _updateLayer;

		public SelectionOperationAction(final Method theSetter,
				final Object theData, final String descriptor,
				final CanvasType theChart, final String newVal, final String oldVal,
				final PropertyEditor editor, final Editable.EditorType theEditor,
				final Layers theLayers, final Layer theParent, final Layer updateLayer)
		{
			super(theData, theEditor, descriptor, theChart);
			_theSetter = theSetter;
			_newVal = newVal;
			_oldVal = oldVal;
			_editor = editor;
			_theLayers = theLayers;
			_theParent = theParent;
			_updateLayer = updateLayer;

			// over-ride the update layer if it's null, set it to the ParentLayer,
			// just in case that is a top level layer
			if (_updateLayer == null)
				_updateLayer = theParent;
		}

		public boolean isRedoable()
		{
			return true;
		}

		public boolean isUndoable()
		{
			return true;
		}

		public void undo()
		{
			// hey, do the opposite!
			try
			{
				_editor.setAsText(_oldVal);
				final Object val = _editor.getValue();

				final Object args[] =
				{ val };
				_theSetter.invoke(_theData, args);

				// inform the editable that we've updated it
				fireAction(null, null);

				// and trigger a redraw
				_theLayers.fireReformatted(_updateLayer);

			}
			catch (final Exception e)
			{
				MWC.Utilities.Errors.Trace.trace(e);
			}
		}

		public void execute()
		{
			// hey, do it!
			try
			{
				// convert the text to an integer (as received by the setter)
				_editor.setAsText(_newVal);
				final Object val = _editor.getValue();

				// prepare the storage data
				final Object args[] =
				{ val };
				_theSetter.invoke(_theData, args);

				// inform the editable that we've updated it
				fireAction(null, null);

				// and trigger a redraw
				_theLayers.fireReformatted(_updateLayer);
			}
			catch (final Exception e)
			{
				MWC.Utilities.Errors.Trace.trace(e);
			}
		}
	}

	// ///////////////////////////////////////////////////////////
	// embedded interface for classes which may wish to add menu
	// items
	// //////////////////////////////////////////////////////////
	static public interface MenuCreator
	{
		/**
		 * add extended functionality for the point found
		 * 
		 * @param menu
		 *          the Menu to add items to
		 * @param thePoint
		 *          the screen location of the mouse click
		 * @param theCanvas
		 *          the canvas to update following changes
		 * @param thePanel
		 *          the properties page current available
		 * @param theData
		 *          the set of layers (plus data) currently in use
		 */
		public void createMenu(JPopupMenu menu, java.awt.Point thePoint,
				MWC.GUI.CanvasType theCanvas,
				MWC.GUI.Properties.PropertiesPanel thePanel, MWC.GUI.Layers theData);

	}

	// ///////////////////////////////////////////////////
	// implementation of menuCreator class, which provides utilities
	// for creating boolean editors and method invokers
	// ///////////////////////////////////////////////////
	abstract static public class BaseMenuCreator implements MenuCreator
	{
		abstract public void createMenu(javax.swing.JPopupMenu menu,
				java.awt.Point thePoint, MWC.GUI.CanvasType theCanvas,
				MWC.GUI.Properties.PropertiesPanel thePanel, MWC.GUI.Layers theData);

		protected void createAdditionalItems(final javax.swing.JPopupMenu menu,
				final MWC.GUI.CanvasType theCanvas,
				final MWC.GUI.Properties.PropertiesPanel thePanel,
				final Editable theEditable, final MWC.GUI.Layers theData)
		{
			// create the editable properties and method invokers for this object
			final JMenu subMenu = new JMenu(theEditable.getInfo().getBeanDescriptor()
					.getDisplayName());
			createBooleanEditors(subMenu, theEditable, theCanvas, thePanel, theData,
					null, null);

			// try for any enumererated editors for this other object
			createSelectionEditors(subMenu, theEditable, theCanvas, thePanel,
					theData, null, null);

			// lastly create any applicable methods
			createMethodInvokers(subMenu, theEditable, theCanvas);

			// did we actually create any?
			if (subMenu.getItemCount() > 0)
				menu.add(subMenu);

		}

	}

}
