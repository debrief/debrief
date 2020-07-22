package Debrief.Wrappers.Track;

import java.util.Enumeration;
import java.util.HashSet;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Plottables;
import MWC.GUI.Plottables.PlotMeFirst;
import MWC.GenericData.HiResDate;

public class FixWrapperCollisionCheck {

	/**
	 * We are creating this method because the following ticket
	 * https://github.com/debrief/debrief/issues/4894
	 * 
	 * FixWrapper were initially adding with a +1 time delay to avoid collisions.
	 * 
	 * Collision check modifies the time in the fixWrapper given to adapt it to 
	 * avoid collision.
	 * 
	 * @param _fixWrapper FixWrapper to modify 
	 * @param _currentPlotables List of all the Editables
	 */
	public static void correctTimeCollision(final FixWrapper _fixWrapper, final Plottables _currentPlotables) {
		final Enumeration<Editable> enumer = _currentPlotables.elements();
		final HashSet<Long> usedTime = new HashSet<Long>();
		while (enumer.hasMoreElements()) {
			final Editable currentEditable = enumer.nextElement();
			if (currentEditable instanceof FixWrapper) {
				final FixWrapper currentFixWrapper = (FixWrapper)currentEditable;
				if (currentFixWrapper.getTime() != null) { 
					usedTime.add(currentFixWrapper.getTime().getMicros());
				}
			}
		}
		
		// Ok, at this point we have in the set `usedTime` all the used times.
		// Let's adapt the FixWrapper
		if (_fixWrapper.getTime() != null) {
			long timeToAdd = _fixWrapper.getTime().getMicros();
			boolean modified = false;
			while (usedTime.contains(timeToAdd)) {
				timeToAdd += 1000; // add 1 second
				modified = true;
			}
			if (modified) {
				_fixWrapper.getFix().setTime(new HiResDate(timeToAdd / 1000L));
			}
		}
	}
}
