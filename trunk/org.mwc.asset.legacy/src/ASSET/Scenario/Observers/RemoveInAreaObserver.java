/*
 * Desciption:
 * User: administrator
 * Date: Nov 11, 2001
 * Time: 12:29:16 PM
 */
package ASSET.Scenario.Observers;

import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Participants.Category;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.Scenario.Genetic.ScenarioRunner;
import ASSET.Scenario.Observers.Summary.BatchCollator;
import ASSET.Scenario.Observers.Summary.BatchCollatorHelper;
import Debrief.Wrappers.LabelWrapper;
import MWC.Algorithms.LiveData.DataDoublet;
import MWC.Algorithms.LiveData.IAttribute;
import MWC.GUI.CanvasType;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class RemoveInAreaObserver extends CoreObserver implements
		ScenarioObserver.ScenarioReferee, BatchCollator, IAttribute,
		ScenarioSteppedListener
{
	/***************************************************************
	 * member variables
	 ***************************************************************/

	protected int _numDitched = 0;
	private Vector<LabelWrapper> _myDeadParts;

	private boolean _plotTheDead = true;
	private WorldArea _myArea;
	private TargetType _myWatch;

	/**
	 * whether to override (cancel) writing per-scenario results to file
	 */
	private boolean _onlyBatch = true;

	private EditorType _myEditor1;

	/**
	 * our batch collator
	 */
	private BatchCollatorHelper _batcher = null;
	private int _myScore;
	private Vector<ParticipantType> _watchVessels;

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */
	public RemoveInAreaObserver(final TargetType watchVessel,
			final WorldArea targetArea, final String myName, final boolean isActive)
	{
		super(myName, isActive);
		_myArea = targetArea;
		_myWatch = watchVessel;
	}

	/***************************************************************
	 * member methods
	 ***************************************************************/

	/**
	 * add any applicable listeners
	 */
	protected void addListeners(final ScenarioType scenario)
	{
		// listen to the scenario stepping
		scenario.addScenarioSteppedListener(this);
	}

	// ////////////////////////////////////////////////
	// inter-scenario observer methods
	// ////////////////////////////////////////////////
	public void finish()
	{
		if (_batcher != null)
		{
			// ok, get the batch thingy to do it's stuff
			_batcher.writeOutput(getHeaderInfo());
		}
	}

	/**
	 * accessor to retrieve batch processing settings
	 */
	public BatchCollatorHelper getBatchHelper()
	{
		return _batcher;
	}

	/**
	 * whether to override (cancel) writing per-scenario results to file
	 * 
	 * @return whether to override batch processing
	 */
	public boolean getBatchOnly()
	{
		return _onlyBatch;
	}

	public DataDoublet getCurrent(Object index)
	{
		return getAttributeHelper().getCurrent(index);
	}

	public Vector<DataDoublet> getHistoricValues(Object index)
	{
		return getAttributeHelper().getValuesFor(index);
	}

	/**
	 * get the editor for this item
	 * 
	 * @return the BeanInfo data for this editable object
	 */
	public EditorType getInfo()
	{
		if (_myEditor1 == null)
			_myEditor1 = new RemoveInAreaObserverInfo(this);

		return _myEditor1;
	}

	/**
	 * define the filename for the batch output
	 * 
	 * @return
	 */
	private String getMySuffix()
	{
		return "csv";
	}

	/**
	 * return how well this scenario performed, according to this referee
	 */
	public ScenarioRunner.ScenarioOutcome getOutcome()
	{
		ScenarioRunner.ScenarioOutcome res = new ScenarioRunner.ScenarioOutcome();
		res.score = _myScore;
		res.summary = getSummary();
		return res;
	}

	/**
	 * get a text description of the outcome
	 */
	public String getSummary()
	{
		return "Number:" + _myScore;
	}

	/**
	 * get the types of vessel we are monitoring
	 */
	public TargetType getWatchType()
	{
		return _myWatch;
	}

	/**
	 * ok, we know the range from this target. handle it
	 * 
	 * @param rng
	 *          thje current range (in degrees)
	 * @param rng2
	 */
	protected void handleThisInstance(ScenarioType scenario, final long time,
			NetworkParticipant part)
	{
		_myScore++;
		// tell the attribute helper
		getAttributeHelper().newData(scenario, time, _myScore);

		// and remove him
		WorldLocation loc = part.getStatus().getLocation();
		Color hisColor = Category.getColorFor(part.getCategory());
		LabelWrapper lw = new LabelWrapper(part.getName(), loc, hisColor);
		lw.setSymbolType("Reference Position");

		if (_myDeadParts == null)
			_myDeadParts = new Vector<LabelWrapper>(0, 1);

		_myDeadParts.add(lw);

		scenario.removeParticipant(part.getId());

	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 * 
	 * @return yes/no
	 */
	public boolean hasEditor()
	{
		return true;
	}

	public void initialise(File outputDirectory)
	{
		// set the output directory for the batch collator
		if (_batcher != null)
			_batcher.setDirectory(outputDirectory);
	}

	public boolean isSignificant()
	{
		return true;
	}

	/**
	 * right, the scenario is about to close. We haven't removed the listeners or
	 * forgotten the scenario (yet).
	 * 
	 * @param scenario
	 *          the scenario we're closing from
	 */
	protected void performCloseProcessing(ScenarioType scenario)
	{
		// do we have a batcher?
		// are we recording to batch?
		if (_batcher != null)
		{
			_batcher.submitResult(_myScenario.getName(), _myScenario.getCaseId(),
					(int) MWC.Algorithms.Conversions.Degs2m(_myScore));
		}
		// clear out lists
		_watchVessels.removeAllElements();

		// reset the score
		_myScore = 0;
	}

	/**
	 * we're getting up and running. The observers have been created and we've
	 * remembered the scenario
	 * 
	 * @param scenario
	 *          the new scenario we're looking at
	 */
	protected void performSetupProcessing(ScenarioType scenario)
	{
		// find any vessels we're interested in which are already in the scenario
		final Integer[] lst = scenario.getListOfParticipants();
		for (int thisI = 0; thisI < lst.length; thisI++)
		{
			final Integer thisIndex = lst[thisI];
			if (thisIndex != null)
			{
				final ASSET.ParticipantType thisP = scenario
						.getThisParticipant(thisIndex.intValue());

				// is this of our watched category?
				if (_myWatch.matches(thisP.getCategory()))
				{
					_watchVessels.add(thisP);
				}
			}
		}
	}

	/**
	 * remove any listeners
	 */
	protected void removeListeners(ScenarioType scenario)
	{
		// stop listening to the scenario
		scenario.removeScenarioSteppedListener(this);
	}

	/**
	 * the scenario has restarted
	 */
	public void restart(ScenarioType scenario)
	{
		super.restart(scenario);

		_myScore = -1;
	}

	/**
	 * configure the batch processing
	 * 
	 * @param fileName
	 *          the filename to write to
	 * @param collationMethod
	 *          how to collate the data
	 * @param perCaseProcessing
	 *          whether to collate the stats on a per-case basis
	 * @param isActive
	 *          whether this collator is active
	 */
	public void setBatchCollationProcessing(String fileName,
			String collationMethod, boolean perCaseProcessing, boolean isActive)
	{
		_batcher = new BatchCollatorHelper(getName(), perCaseProcessing,
				collationMethod, isActive, "range (metres)");

		// do we have a filename?
		if (fileName == null)
			fileName = getName() + "." + getMySuffix();

		_batcher.setFileName(fileName);
	}

	/**
	 * whether to override (cancel) writing per-scenario results to file
	 * 
	 * @param override
	 */
	public void setBatchOnly(boolean override)
	{
		_onlyBatch = override;
	}

	@Override
	public void paint(CanvasType dest)
	{
		if (_plotTheDead)
		{
			if (_myDeadParts != null)
			{
				Object[] labels = _myDeadParts.toArray();
				for (int i = 0; i < labels.length; i++)
				{
					LabelWrapper labelWrapper = (LabelWrapper) labels[i];
					labelWrapper.paint(dest);
				}
			}
		}
	}

	/**
	 * set the types of vessel we are monitoring
	 */
	public void setWatchType(TargetType watchType)
	{
		this._myWatch = watchType;
	}

	/**
	 * the scenario has stepped forward
	 */
	public void step(ScenarioType scenario, long newTime)
	{

		// step through our watch vessels
		final Iterator<ParticipantType> thisV = _watchVessels.iterator();
		while (thisV.hasNext())
		{
			final NetworkParticipant thisWatch = (NetworkParticipant) thisV.next();

			if (_myArea.contains(thisWatch.getStatus().getLocation()))
			{
				handleThisInstance(scenario, newTime, thisWatch);
			}
		}
	}

	public String getUnits()
	{
		return "participants";
	}

	static public class RemoveInAreaObserverInfo extends EditorType
	{

		/**
		 * constructor for editable details
		 * 
		 * @param data
		 *          the object we're going to edit
		 */
		public RemoveInAreaObserverInfo(final RemoveInAreaObserver data)
		{
			super(data, data.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 * 
		 * @return property descriptions
		 */
		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{ prop("Name", "the name of this observer"),
						prop("WatchType", "the type of participant to monitor"), };
				return res;
			}
			catch (IntrospectionException e)
			{
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

}