package org.mwc.debrief.timebar.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.timebar.controls.TimeBarControl;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;

// TODO: get rid of copy&paste from LayerManagerVieww - create base View class for the both views
public class TimeBarView extends ViewPart {
	
	TimeBarControl _control;
	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor;
	
	Layers _myLayers;

	private Layers.DataListener _myLayersListener;

	ISelectionChangedListener _selectionChangeListener;
	
	protected TrackManager _theTrackDataListener;
	
	@Override
	public void createPartControl(Composite parent) 
	{
		_control = new TimeBarControl(parent);
		
		getSite().setSelectionProvider(_control);
		
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());
		
		listenToMyParts();
		

		_selectionChangeListener = new ISelectionChangedListener() {
			
			@Override			
			public void selectionChanged(SelectionChangedEvent event)
			{
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
		if (part != _myLayers)
		{
			_myLayers = (Layers) part;
				if (_myLayersListener == null)
				{
					_myLayersListener = new Layers.DataListener2()
					{

						public void dataModified(Layers theData, Layer changedLayer)
						{
						}

						public void dataExtended(Layers theData)
						{
							dataExtended(theData, null, null);
						}

						public void dataReformatted(Layers theData, Layer changedLayer)
						{
							//TODO: handleReformattedLayer(changedLayer);
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
	public void setFocus() {
		// TODO Auto-generated method stub
		
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

		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// cool, remember about it.
						_theTrackDataListener = (TrackManager) part;
					}
				});
		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// ok, ditch it.
						_theTrackDataListener = null;
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
						{
							_control.setSelectionToWidget((StructuredSelection) sel);
						}
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
