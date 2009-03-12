package com.borlander.rac525791.dashboard.layout;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

public interface ControlUIModel {
	/**
	 * @return coordinates of the control's center in the parent coordinate
	 *         system. This method defines the "local" coordinate system used by
	 *         all other methods of this interface. In memory of Together
	 *         Control Center 4.0 -- the best Java IDE in early 2000-th :)
	 */
	public Point getControlCenter();

	/**
	 * @return the radius of the red sector.
	 */
	public int getRedSectorRadius();

	/**
	 * @return the positions of the "0" mark against the center of control. This
	 *         defines the minimum angle for AngleMapper.
	 * 
	 * NOTE: This method is not used for Directions controls, because they map
	 * the whole circle.
	 */
	public Dimension getZeroMark();

	/**
	 * @return the positions of the "10" mark against the center of control.
	 *         This defines the maximum angle for AngleMapper.
	 * 
	 * NOTE: This method is not used for Directions controls, because they map
	 * the whole circle.
	 */
	public Dimension getMaximumMark();

	/**
	 * @return <code>true</code> if there are no minimum/maximum marks for
	 *         angle mapper, and the full circle should be used instead. In this
	 *         case return value for <code>getZeroMark()</code> and
	 *         <code>getMaximumMark()</code> methods is useless and ignored
	 */
	public boolean isFullCircleMapped();

	/**
	 * 
	 * @return dimension of the rectangle of each <b>single</b> line where
	 *         actual units/multipliers is shown. That is, total height of the
	 *         units+multipliers block is twice more than returned value.
	 * 
	 * NOTE: For Depth control it does not include the place where "x" is shown.
	 */
	public Dimension getUnitsAndMultipliersSize();

	/**
	 * 
	 * @return the coordinates for the top-left corner of the rectangle where
	 *         actual units is shown (in control center's coordinate system).
	 *         Multipliers text is shown just below the units.
	 * 
	 * NOTE: For Depth control it does not include the place where "x" is shown.
	 */
	public Point getUnitsPosition();

	/**
	 * @return size of the rectangle that is used to display actual value.
	 */
	public Dimension getValueTextSize();

	/**
	 * @return coordinates of the top-left corner of "actual value" rectangle
	 *         (in control center's coordinate system).
	 */
	public Point getValueTextPosition();

}
