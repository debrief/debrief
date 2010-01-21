/*
 * Desciption:
 * User: administrator
 * Date: Nov 11, 2001
 * Time: 12:29:16 PM
 */
package ASSET.GUI.SuperSearch.Observers;

import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.HashSet;

public class ProportionDetectedObserver extends
		ASSET.Scenario.Observers.DetectionObserver
{
	/***************************************************************
	 * member variables
	 ***************************************************************/

	/**
	 * a running count of how many targets are in the scenario
	 */
	private int _numTargets = 0;
	private HashSet<EntryHolder> _entryHolder;

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */
	public ProportionDetectedObserver(final TargetType watchVessel,
			final TargetType targetVessel, final String myName,
			final Integer detectionLevel, final boolean isActive)
	{
		super(watchVessel, targetVessel, myName, detectionLevel, isActive);

		_entryHolder = new HashSet<EntryHolder>();

	}

	// ////////////////////////////////////////////////
	// batch processing results
	// ////////////////////////////////////////////////
	/**
	 * return the calculated result for the batch processing
	 * 
	 * @return string to be used in results collation
	 */
	protected Number getBatchResult()
	{
		return new Double(getProportionDetected());
	}

	/***************************************************************
	 * member methods
	 ***************************************************************/

	/**
	 * valid detection happened, process it
	 */
	protected void validDetection(final DetectionEvent detection)
	{
		// remove this target
		final int tgt = detection.getTarget();

		// get sensor which has made the detection
		final int host = detection.getHost();

		final EntryHolder eh = new EntryHolder(tgt, host);

		if ((_myDetections == null) || (_myDetections.contains(eh)))
		{

		}
		else
		{
			// create combined entry
			_entryHolder.add(eh);
		}

	}

	/**
	 * return the proportion of targets which have been detected
	 * 
	 * @return 0..1 representing how many detected
	 */
	public double getProportionDetected()
	{
		double res = 0;
		if(_myDetections != null)
		{
			res = _myDetections.size() / _numTargets;
		}
		return res;
	}

	/**
	 * get the total number of targets in the scenario
	 */
	public int getNumTargets()
	{
		return _numTargets;
	}

	/**
	 * set the total number of targets in the scenario
	 */
	public void setNumTargets(int numTargets)
	{
		_numTargets = numTargets;
	}

	/**
	 * how many targets have we deleted?
	 */
	public int getNumDetected()
	{
		int res = 0;
		if (_myDetections != null)
			res = _myDetections.size();

		return res;
	}

	/**
	 * the indicated participant has been added to the scenario
	 */
	public void newParticipant(int index)
	{
		super.newParticipant(index);

		// is this of our target type?
		if (super.getTargetType().matches(
				_myScenario.getThisParticipant(index).getCategory()))
		{
			_numTargets++;
		}
	}

	/**
	 * ************************************************************ embedded class
	 * which lets us keep track of initial detections
	 * *************************************************************
	 */
	private class EntryHolder
	{
		private int _target;
		private int _host;

		public EntryHolder(final int tgt, final int host)
		{
			_target = tgt;
			_host = host;
		}

		public boolean equals(final Object obj)
		{
			boolean res = false;
			final EntryHolder eh = (EntryHolder) obj;
			res = ((_target == eh._target) && (_host == eh._host));
			return res;
		}

		public int hashCode()
		{
			return _target * 1000 + _host;
		}

	}

	/***************************************************************
	 * plottable properties
	 ***************************************************************/
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

	/**
	 * get the editor for this item
	 * 
	 * @return the BeanInfo data for this editable object
	 */
	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new ProportionInfo(this, getName());

		return _myEditor;
	}

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public class ProportionInfo extends Editable.EditorType
	{

		public ProportionInfo(final ProportionDetectedObserver data,
				final String name)
		{
			super(data, name, "");
		}

		public String getName()
		{
			return ProportionDetectedObserver.this.getName();
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{ prop("Name", "the name of this observer"),
						prop("NumDetected", "the number of targets detected"),
						prop("NumTargets", "the number of targets in the scenario"),
						prop("Active", "whether this listener is active"), };
				return res;
			}
			catch (IntrospectionException e)
			{
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 * ************************************************************ a gui class to
	 * show progress of this monitor
	 * *************************************************************
	 */

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	public static class PropDetectedTest extends SupportTesting.EditableTesting
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public PropDetectedTest(final String val)
		{
			super(val);
		}

		/**
		 * get an object which we can test
		 * 
		 * @return Editable object which we can check the properties for
		 */
		public Editable getEditable()
		{
			MWC.GUI.Editable ed = new ProportionDetectedObserver(null, null,
					"how many", new Integer(2), true);
			return ed;
		}
	}

}
