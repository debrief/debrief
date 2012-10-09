package com.planetmayo.debrief.satc.views;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.planetmayo.debrief.satc.model.generator.TrackGenerator;
import com.planetmayo.debrief.satc.ui.PartMonitor;

public abstract class CoreView extends ViewPart
{

	private TrackGenerator _generator;

	protected abstract void stopListeningTo(TrackGenerator genny);

	protected abstract void startListeningTo(TrackGenerator genny);

	private PartMonitor _myPartMonitor;

	public CoreView()
	{
		super();
	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub
		
	}

	protected TrackGenerator getGenerator()
	{
		return _generator;
	}


	protected void setupMonitor()
	{
		// start listening to data
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());

		/**
		 * listen out for a new generator
		 * 
		 */
		_myPartMonitor.addPartListener(TrackGenerator.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{

					@Override
					public void eventTriggered(String type, Object instance,
							IWorkbenchPart parentPart)
					{
						// get the generator
						TrackGenerator newGenerator = (TrackGenerator) instance;

						// is this new?
						if (newGenerator != _generator)
						{
							// yes
							
							// do we already have a generator?
							if (_generator != null)
								stopListeningTo(_generator);

							// ok, store the new one
							_generator = newGenerator;

							// and start listening to it
							startListeningTo(_generator);
						}
					}
				});

		/**
		 * listen out for a our geneator closing
		 * 
		 */
		_myPartMonitor.addPartListener(TrackGenerator.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{

					@Override
					public void eventTriggered(String type, Object instance,
							IWorkbenchPart parentPart)
					{
						// get the generator
						TrackGenerator newGenerator = (TrackGenerator) instance;

						// is this new?
						if (newGenerator == _generator)
						{
							stopListeningTo(_generator);
							_generator = null;
						}
					}
				});
		
		// just see if there's anything of interest
	}

	@Override
	public void dispose()
	{
		if (_generator != null)
			stopListeningTo(_generator);
		super.dispose();
	}

}