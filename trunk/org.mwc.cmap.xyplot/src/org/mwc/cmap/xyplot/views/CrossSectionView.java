package org.mwc.cmap.xyplot.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

// This import leads to cycle in plugin dependencies
// TODO: import org.mwc.cmap.TimeController.views.TimeController;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.ui_support.PartMonitor;

import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;

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
	
	//TODO: declare actions

	@Override
	public void createPartControl(Composite parent) 
	{
		_viewer = new CrossSectionViewer(parent);
		getSite().setSelectionProvider(_viewer);		

		_partMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());
		
		 
		listenToMyParts();
		//TODO: makeActions();
		//TODO: contributeToActionBars();
		
		_selectionChangeListener = new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				// TODO Auto-generated method stub
			}
		};
		_viewer.addSelectionChangedListener(_selectionChangeListener);
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
						// TODO Auto-generated method stub
					}
				});
		_partMonitor.addPartListener(Layers.class, PartMonitor.OPENED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// TODO Auto-generated method stub
					}
				});
		_partMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// TODO Auto-generated method stub		
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
		_viewer.setFocus();		
	}
	
	public void dispose()
	{
		super.dispose();

		// make sure we close the listeners
		//TODO: clearLayerListener();
		if (_timeProvider != null)
		{
			_timeProvider.removeListener(_temporalListener, 
					TimeProvider.TIME_CHANGED_PROPERTY_NAME);
			_temporalListener = null;
			_timeProvider = null;
			
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
