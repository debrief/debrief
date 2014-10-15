/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GenericData;


/** a mobile vehicle that we may choose to watch
 * 
 * @author ianmayo
 *
 */
public interface Watchable extends ColoredWatchable
{
  /** get the current location of the watchable
   * @return the location
   */
  public WorldLocation getLocation();
  /** get the current course of the watchable (rads)
   * @return course in radians
   */
  public double getCourse();
  /** get the current speed of the watchable (kts)
   * @return speed in knots
   */
  public double getSpeed();

  /** get the current depth of the watchable (m)
   * @return depth in metres
   */
  public double getDepth();

  /** get the bounds of the object (used when we are painting it)
   */
  public WorldArea getBounds();

	/** specify if this Watchable is visible or not
	 * @param val whether it's visible
	 */
	public void setVisible(boolean val);

	/** determine if this Watchable is visible or not
	 * @return boolean whether it's visible
	 */
	public boolean getVisible();

  /** find out the time of this watchable
   */
  public HiResDate getTime();

  /** find out the name of this watchable
   */
  public String getName();

}
