package org.mwc.cmap.grideditor.data;

import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.DebriefProperty;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.EventStack;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptorExtension;
import org.mwc.cmap.gridharness.data.GriddableSeries;
import org.mwc.cmap.gridharness.views.WorldLocationHelper;

import MWC.GUI.Editable;
import MWC.GUI.Griddable;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.Editable.EditorType;
import MWC.GenericData.WorldLocation;

/**
 * class that makes one of our tactical wrapper objects look like a griddable
 * series We aren't aaplying this functionality directly to the wrapper data
 * objects, since it introduces Eclipse-related objects
 * 
 * @author Ian Mayo
 * 
 */
public class GriddableWrapper implements GriddableSeries
{
	/**
	 * the thing we're wrapping
	 * 
	 */
	final EditableWrapper _item;

	private boolean _onlyVisItems = false;

	private static EventStack myStack = new EventStack(50);

	/**
	 * a cached set of attributes - we have to get them quite frequently
	 * 
	 */
	private GriddableItemDescriptor[] _myAttributes;

	public boolean isOnlyShowVisibleItems()
	{
		return _onlyVisItems;
	}

	public void setOnlyShowVisibleItems(boolean onlyVisItems)
	{
		_onlyVisItems = onlyVisItems;
	}

	public GriddableWrapper(EditableWrapper item)
	{
		_item = item;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		if (_item.getEditable() instanceof SupportsPropertyListeners)
		{
			SupportsPropertyListeners pw = (SupportsPropertyListeners) _item
					.getEditable();
			pw.addPropertyChangeListener(listener);
		}
	}

	public void deleteItem(TimeStampedDataItem subject)
	{
		GriddableSeriesMarker gs = (GriddableSeriesMarker) _item.getEditable();
		gs.removeElement((Editable) subject);

		// tell everybody something's changed
		fireExtended(PROPERTY_DELETED, subject);
	}

	/**
	 * broadcast the fact that something in this layer has changed
	 * 
	 * @param propertyName
	 * @param subject
	 * 
	 */
	public void fireExtended(String propertyName, TimeStampedDataItem subject)
	{
		// start off with a plain modified event (to inform ourselves)
		SupportsPropertyListeners pw = (SupportsPropertyListeners) _item
				.getEditable();
		// ok, inform any listeners
		pw.firePropertyChange(propertyName, null, subject);

		// now tell the world.
		_item.getLayers().fireExtended(null, _item.getTopLevelLayer());
	}

	public void fireModified(TimeStampedDataItem subject)
	{
		if (_item.getEditable() instanceof SupportsPropertyListeners)
		{
			SupportsPropertyListeners pw = (SupportsPropertyListeners) _item
					.getEditable();
			// ok, inform any listeners
			pw.firePropertyChange(GriddableSeries.PROPERTY_CHANGED, null, subject);
		}
		else
			CorePlugin.logError(IStatus.ERROR,
					"Item in grid doesn't let us watch it's properties", null);

		// also tell the layers object that we've changed
		cachedFireModified(_item.getLayers(), _item.getTopLevelLayer());
		// _item.getLayers().fireModified(_item.getTopLevelLayer());
	}

	private void cachedFireModified(final Layers layers, final Layer topLayer)
	{
		// create the runnable
		Runnable runme = new Runnable()
		{
			public void run()
			{
				layers.fireModified(topLayer);
			}
		};

		// add it to the cache
		myStack.addEvent(runme);

	}

	public GriddableItemDescriptor[] getAttributes()
	{
		if (_myAttributes == null)
		{
			Vector<GriddableItemDescriptor> items = new Vector<GriddableItemDescriptor>();
			GriddableSeriesMarker series = (GriddableSeriesMarker) _item
					.getEditable();

			Editable sampleItem = series.getSampleGriddable();
			// just check we've got some sample data
			if(sampleItem == null)
				return _myAttributes;
			
			
			EditorType info = sampleItem.getInfo();
			if (info instanceof Griddable)
			{
				IPropertyDescriptor[] props = _item.getGriddablePropertyDescriptors();

				// wrap them
				for (int i = 0; i < props.length; i++)
				{
					DebriefProperty desc = (DebriefProperty) props[i];

					Object dataObject = desc.getRawValue();
					Class<?> dataClass = dataObject.getClass();
					GriddableItemDescriptor gd;

					// aah, is this a 'special' class?
					if (dataClass == WorldLocation.class)
					{
						WorldLocationHelper worldLocationHelper = new WorldLocationHelper();
						WorldLocation sample = new WorldLocation(1, 1, 1);
						String sampleLocationText = worldLocationHelper.getLabelFor(
								sample).getText(sample);

						gd = new GriddableItemDescriptorExtension("Location", "Location",
								WorldLocation.class, new WorldLocationHelper(),
								sampleLocationText);
					}
					else
					{
						gd = new GriddableItemDescriptor(desc.getDisplayName(), desc
								.getDisplayName(), dataClass, desc.getHelper());
					}

					items.add(gd);
				}
			}

			if (items.size() > 0)
			{
				_myAttributes = items.toArray(new GriddableItemDescriptor[]
				{ null });
			}
		}
		return _myAttributes;
	}

	public List<TimeStampedDataItem> getItems()
	{
		List<TimeStampedDataItem> list = null;
		Editable obj = _item.getEditable();
		if (obj instanceof Layer)
		{
			list = new Vector<TimeStampedDataItem>();
			Layer layer = (Layer) obj;
			Enumeration<Editable> enumer = layer.elements();
			while (enumer.hasMoreElements())
			{
				Editable ed = enumer.nextElement();

				if (_onlyVisItems)
				{
					// right, this should be a plottable - just check
					if (ed instanceof Plottable)
					{
						Plottable pl = (Plottable) ed;
						if (pl.getVisible())
						{
							list.add(0, (TimeStampedDataItem) ed);
						}
					}
				}
				else
				{
					// just show all of them
					list.add(0, (TimeStampedDataItem) ed);
				}
			}
		}
		return list;
	}

	public String getName()
	{
		return _item.getEditable().getName();
	}

	/**
	 * return the thing being edited
	 * 
	 * @return
	 */
	public EditableWrapper getWrapper()
	{
		return _item;
	}

	public void insertItem(TimeStampedDataItem subject)
	{
		GriddableSeriesMarker gs = (GriddableSeriesMarker) _item.getEditable();
		gs.add((Editable) subject);

		// tell everybody something's changed
		fireExtended(GriddableSeries.PROPERTY_ADDED, subject);
	}

	public void insertItemAt(TimeStampedDataItem subject, int index)
	{
		// we don't need to worry about the order of the item,
		// since they're chronologically ordered anyway
		insertItem(subject);
	}

	public TimeStampedDataItem makeCopy(TimeStampedDataItem item)
	{
		GriddableSeriesMarker gs = (GriddableSeriesMarker) _item.getEditable();
		return gs.makeCopy(item);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		if (_item.getEditable() instanceof SupportsPropertyListeners)
		{
			SupportsPropertyListeners pw = (SupportsPropertyListeners) _item
					.getEditable();
			pw.removePropertyChangeListener(listener);
		}
	}

}