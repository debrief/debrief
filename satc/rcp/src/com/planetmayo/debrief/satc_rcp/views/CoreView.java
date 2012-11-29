package com.planetmayo.debrief.satc_rcp.views;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.planetmayo.debrief.satc.model.generator.BoundsManager;
import com.planetmayo.debrief.satc_rcp.ui.PartMonitor;

public abstract class CoreView extends ViewPart
{

	private BoundsManager _generator;

	private PartMonitor _myPartMonitor;

	public CoreView()
	{
		super();
	}

	@Override
	public void dispose()
	{
		if (_generator != null)
			stopListeningTo(_generator);
		super.dispose();
	}

	protected BoundsManager getGenerator()
	{
		return _generator;
	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub

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
		_myPartMonitor.addPartListener(BoundsManager.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{

					@Override
					public void eventTriggered(String type, Object instance,
							IWorkbenchPart parentPart)
					{
						// get the generator
						BoundsManager newGenerator = (BoundsManager) instance;

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
		_myPartMonitor.addPartListener(BoundsManager.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{

					@Override
					public void eventTriggered(String type, Object instance,
							IWorkbenchPart parentPart)
					{
						// get the generator
						BoundsManager newGenerator = (BoundsManager) instance;

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

	protected abstract void startListeningTo(BoundsManager genny);

	protected abstract void stopListeningTo(BoundsManager genny);

}