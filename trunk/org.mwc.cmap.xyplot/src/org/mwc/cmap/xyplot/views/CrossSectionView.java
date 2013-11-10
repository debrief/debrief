package org.mwc.cmap.xyplot.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.xyplot.views.providers.CrossSectionDatasetProvider;
import org.mwc.cmap.xyplot.views.providers.ICrossSectionDatasetProvider;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.HiResDate;
// This import leads to cycle in plugin dependencies
// TODO: import org.mwc.cmap.TimeController.views.TimeController;
import MWC.GenericData.WorldDistance;

public class CrossSectionView extends ViewPart
{
	
	CrossSectionViewer _viewer;

	/**
	 * listen to line annotations being selected
	 */
	private ISelectionChangedListener _selectionChangeListener;
	
	/**
	 * helper application to help track activation/closing of new plots
	 */
	private PartMonitor _partMonitor;
	
	/**
	 * listen out for new times
	 */
	private PropertyChangeListener _temporalListener = new NewTimeListener();
	
	TimeProvider _timeProvider;
	
	/**
	 * somebody to listen to the time changes
	 */
	//TODO: is this needed?
	private PropertyChangeListener _timeListener;
	

	private PropertyChangeListener _lineListener = new LineChangeListener();
	
	/**
	 *  current line annotation being selected 
	 */
	private LineShape _line = null;
	
	/**
	 * Debrief data
	 */
	private Layers _myLayers;
	
	private Layers.DataListener _myLayersListener;
	
	private ICrossSectionDatasetProvider _datasetProvider = 
			new CrossSectionDatasetProvider(WorldDistance.KM);	
	//TODO: declare actions

	@Override
	public void createPartControl(final Composite parent) 
	{
		_viewer = new CrossSectionViewer(parent);	

		_partMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());
		
		 
		listenToMyParts();
		//TODO: makeActions();
		//TODO: contributeToActionBars();
		
		_selectionChangeListener = new ISelectionChangedListener() 
		{
			@Override
			public void selectionChanged(final SelectionChangedEvent event) 
			{
				final ISelection sel = event.getSelection();
				if (!(sel instanceof IStructuredSelection))
					return;
				final IStructuredSelection ss = (IStructuredSelection) sel;
				final Object o = ss.getFirstElement();
				if (o instanceof EditableWrapper) 
				{
					// check if Line annotation is selected
					final Editable eb = ((EditableWrapper) o).getEditable();
					if (eb instanceof ShapeWrapper)
					{
						final PlainShape shape = ((ShapeWrapper) eb).getShape();
						if (shape instanceof LineShape && !shape.equals(_line))
						{
							_line = (LineShape) shape;
							_viewer.fillPlot(_myLayers, _line, _datasetProvider);
						}
					}		
				}
			}
		};
		_viewer.addSelectionChangedListener(_selectionChangeListener);
		_viewer.addPropertyChangedListener(_lineListener);
	}
	
	private void listenToMyParts()
	{
		// Listen to Layers
		_partMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						_line = null;
						processNewLayers(part);
					}
				});
		_partMonitor.addPartListener(Layers.class, PartMonitor.OPENED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						_line = null;
						processNewLayers(part);
					}
				});
		_partMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// is this our set of layers?
						if (part == _myLayers)
						{
							_line = null;
							// stop listening to this layer
							clearLayerListener();
						}					
					}

				});
		
		// Listen to time provider
//		_partMonitor.addPartListener(TimeController.class, PartMonitor.ACTIVATED,
//				new PartMonitor.ICallback()
//				{
//					public void eventTriggered(final String type, final Object part,
//							final IWorkbenchPart parentPart)
//					{
//						final TimeProvider provider = ((TimeController) part).getTimeProvider();						
//						if (provider != null && provider.equals(_timeProvider))
//							return;						
//						_timeProvider = provider;
//						_timeProvider.addListener(_temporalListener, 
//								TimeProvider.TIME_CHANGED_PROPERTY_NAME);
//					}
//			});
//		_partMonitor.addPartListener(TimeController.class, PartMonitor.OPENED,
//				new PartMonitor.ICallback()
//				{
//					public void eventTriggered(final String type, final Object part,
//							final IWorkbenchPart parentPart)
//					{
//						final TimeProvider provider = ((TimeController) part).getTimeProvider();						
//						if (provider != null && provider.equals(_timeProvider))
//							return;						
//						_timeProvider = provider;
//						_timeProvider.addListener(_temporalListener, 
//								TimeProvider.TIME_CHANGED_PROPERTY_NAME);
//					}
//			});
//		
//		_partMonitor.addPartListener(TimeController.class, PartMonitor.CLOSED,
//				new PartMonitor.ICallback()
//				{
//					public void eventTriggered(final String type, final Object part,
//							final IWorkbenchPart parentPart)
//					{
//						final TimeProvider provider = ((TimeController) part).getTimeProvider();						
//						if (provider != null && provider.equals(_timeProvider))
//						{
//							_timeProvider.removeListener(_temporalListener, 
//									TimeProvider.TIME_CHANGED_PROPERTY_NAME);
//						}
//					}
//
//				});
		
		
		
		
		_partMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// aah, just check it's not is
						if (part != _viewer)
						{
							final ISelectionProvider iS = (ISelectionProvider) part;
							iS.addSelectionChangedListener(_selectionChangeListener);
						}
					}
				});
		_partMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.DEACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// aah, just check it's not is
						if (part != _viewer)
						{
							final ISelectionProvider iS = (ISelectionProvider) part;
							iS.removeSelectionChangedListener(_selectionChangeListener);
						}
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_partMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());
	}


	@Override
	public void setFocus() 
	{
		//TODO:
	}	
	
	void processNewLayers(final Object part)
	{
		// just check we're not already looking at it
		if (part.equals(_myLayers))
			return;
		
		_myLayers = (Layers) part;
		if (_myLayersListener == null)
		{
			_myLayersListener = new Layers.DataListener2()
			{

				public void dataModified(final Layers theData, final Layer changedLayer)
				{						
				}

				public void dataExtended(final Layers theData)
				{
					dataExtended(theData, null, null);
				}

				public void dataReformatted(final Layers theData, final Layer changedLayer)
				{
					processReformattedLayer(theData, changedLayer);
				}

				public void dataExtended(final Layers theData, final Plottable newItem,
								final Layer parentLayer)
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
	
	void processReformattedLayer(final Layers theData, final Layer changedLayer)
	{
		//TODO: _viewer.drawDiagram(theData);
	}
	
	void processNewData(final Layers theData, final Editable newItem,
			final Layer parentLayer)
	{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					// ok, fire the change in the UI thread
					_viewer.fillPlot(theData, _line, _datasetProvider);
				}
			});
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
	
	public void dispose()
	{
		super.dispose();
		// make sure we close the listeners
		clearLayerListener();
		_line = null;
		if (_timeProvider != null)
		{
			_timeProvider.removeListener(_temporalListener, 
					TimeProvider.TIME_CHANGED_PROPERTY_NAME);
			_temporalListener = null;
			_timeProvider = null;			
		}
	}
	
	protected final class LineChangeListener implements PropertyChangeListener
	{
		public void propertyChange(final PropertyChangeEvent event)
		{
			// see if it's the time or the period which
			// has changed
			if (event.getPropertyName().equals(
					PlainWrapper.LOCATION_CHANGED))
			{
				final Runnable nextEvent = new Runnable()
				{
					public void run()
					{
						if (event.getSource().equals(_line))
						{
							_viewer.fillPlot(_myLayers, _line, _datasetProvider);
						}
					}
				};
				Display.getDefault().syncExec(nextEvent);				
			}
		}
	}
	
	protected final class NewTimeListener implements PropertyChangeListener
	{
		public void propertyChange(final PropertyChangeEvent event)
		{
			// see if it's the time or the period which
			// has changed
			if (event.getPropertyName().equals(
					TimeProvider.TIME_CHANGED_PROPERTY_NAME))
			{
				// ok, use the new time
				final HiResDate newDTG = (HiResDate) event.getNewValue();
				final HiResDate oldDTG = (HiResDate) event.getOldValue();
				final Runnable nextEvent = new Runnable()
				{
					public void run()
					{
						// TODO: implement
					}
				};
				Display.getDefault().syncExec(nextEvent);				
			}
		}
	}

}
