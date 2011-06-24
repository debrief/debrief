package MWC.GenericData;


/** a mobile vehicle that we may choose to watch
 * 
 * @author ianmayo
 *
 */
public interface Watchable
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

  /** find out the colour of this watchable
   */
  public java.awt.Color getColor();

}
