package org.mwc.debrief.timebar.views;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.timebar.controls.TimeBarControl;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;


public class TimeBarView extends ViewPart {
	
	TimeBarControl _control;
	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor;
	
	/**
	 * Debrief data
	 */
	private Layers _myLayers;

	private Layers.DataListener _myLayersListener;

	private ISelectionChangedListener _selectionChangeListener;
	
	private static Set<Layer> _pendingLayers = new TreeSet<Layer>();
	
	private static boolean _alreadyDeferring = false;
	
	
	@Override
	public void createPartControl(Composite parent) {
		_control = new TimeBarControl(parent, _myLayers);

		getSite().setSelectionProvider(_control);

		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());

		listenToMyParts();

		_selectionChangeListener = new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection sel = event.getSelection();
				if (!(sel instanceof IStructuredSelection))
					return;
				IStructuredSelection ss = (IStructuredSelection) sel;
				Object o = ss.getFirstElement();
				if (o instanceof EditableWrapper) {
					EditableWrapper pw = (EditableWrapper) o;
					editableSelected(sel, pw);
				}
			}
		};
		_control.addSelectionChangedListener(_selectionChangeListener);
	}	
	
	void processNewLayers(Object part)
	{
		// just check we're not already looking at it
		if (part.equals(_myLayers))
			return;
		
		_myLayers = (Layers) part;
		if (_myLayersListener == null)
		{
			_myLayersListener = new Layers.DataListener2()
			{

				public void dataModified(Layers theData, Layer changedLayer){}

				public void dataExtended(Layers theData)
				{
					dataExtended(theData, null, null);
				}

				public void dataReformatted(Layers theData, Layer changedLayer)
				{
					handleReformattedLayer(changedLayer);
				}

				public void dataExtended(Layers theData, Plottable newItem,
								Layer parentLayer)
				{
					processNewData(theData, newItem, parentLayer);
				}
			};
		}
		// right, listen for data being added
		_myLayers.addDataExtendedListener(_myLayersListener);

		// and listen for items being reformatted
		_myLayers.addDataReformattedListener(_myLayersListener);

		// do an initial population.
		processNewData(_myLayers, null, null);		
	}
	
	
	protected void handleReformattedLayer(Layer changedLayer)
	{
		// right - store this layer (if we have one)
		if (changedLayer != null)
			_pendingLayers.add(changedLayer);

		if (_alreadyDeferring)
		{
			// hey - already processing - add this layer to the pending ones
		}
		else
		{
			_alreadyDeferring = true;

			// right. we're not already doing some processing
			Display dis = Display.getDefault();
			dis.asyncExec(new Runnable()
			{
				public void run()
				{
					processReformattedLayers();
				}
			});
		}
	}
	
	protected void processReformattedLayers()
	{

		try
		{
			// right, we'll be building up a list of objects to refresh (all of the
			// objects in the indicated layer)
			Vector<Object> newList = new Vector<Object>(0, 1);
			Widget changed = null;

			if (_pendingLayers.size() > 0)
			{
				for (Iterator<Layer> iter = _pendingLayers.iterator(); iter.hasNext();)
				{
					Layer changedLayer = (Layer) iter.next();

					//TODO: implement changing the layer
					_control.drawDiagram(_myLayers);
				}
			}
			else
			{
				// hey, all of the layers need updating.
				// better get on with it.
				_control.drawDiagram(_myLayers);
			}
		}
		catch (Exception e)
		{

		}
		finally
		{
			_alreadyDeferring = false;
			_pendingLayers.clear();
		}
	}

	
	void processNewData(final Layers theData, final Editable newItem,
			final Layer parentLayer)
	{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					// ok, fire the change in the UI thread
					_control.drawDiagram(theData);
					// hmm, do we know about the new item? If so, better select it
					if (newItem != null)
					{
						// wrap the plottable
						EditableWrapper parentWrapper = new EditableWrapper(parentLayer,
								null, theData);
						EditableWrapper wrapped = new EditableWrapper(newItem,
								parentWrapper, theData);
						ISelection selected = new StructuredSelection(wrapped);

						// and select it
						editableSelected(selected, wrapped);
					}
				}
			});
	}	
	
	@Override
	public void setFocus() 
	{
		_control.setFocus();		
	}
	
	public void dispose()
	{
		super.dispose();

		// make sure we close the listeners
		clearLayerListener();
	}
	
	/**
	 * stop listening to the layer, if necessary
	 */
	void clearLayerListener()
	{
		if (_myLayers != null)
		{
			_myLayers.removeDataExtendedListener(_myLayersListener);
			_myLayersListener = null;
			_myLayers = null;
		}
	}
	
	private void listenToMyParts()
	{
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						processNewLayers(part);
					}
				});
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.OPENED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						processNewLayers(part);
					}
				});
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// is this our set of layers?
						if (part == _myLayers)
						{
							// stop listening to this layer
							clearLayerListener();
						}
					}

				});

		_myPartMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// aah, just check it's not is
						if (part != _control)
						{
							ISelectionProvider iS = (ISelectionProvider) part;
							iS.addSelectionChangedListener(_selectionChangeListener);
						}
					}
				});
		_myPartMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.DEACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// aah, just check it's not is
						if (part != _control)
						{
							ISelectionProvider iS = (ISelectionProvider) part;
							iS.removeSelectionChangedListener(_selectionChangeListener);
						}
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());
	}
	
	public void editableSelected(ISelection sel, EditableWrapper pw) 
	{

		// ahh, just check if this is a whole new layers object
		if (pw.getEditable() instanceof Layers) 
		{
			processNewLayers(pw.getEditable());
			return;
		}

		// just check that this is something we can work with
		if (sel instanceof StructuredSelection) 
		{
			StructuredSelection str = (StructuredSelection) sel;

			// hey, is there a payload?
			if (str.getFirstElement() != null) 
			{
				// sure is. we only support single selections, so get the first
				// element
				Object first = str.getFirstElement();
				if (first instanceof EditableWrapper) 
					_control.setSelectionToWidget((StructuredSelection) sel);				
			}
		}

	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter)
	{
		Object res = null;

		if (adapter == ISelectionProvider.class)
		{
			res = _control;
		}
		else
		{
			res = super.getAdapter(adapter);
		}

		return res;
	}

}
