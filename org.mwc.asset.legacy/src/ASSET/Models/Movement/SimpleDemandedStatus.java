package ASSET.Models.Movement;

import java.io.Serializable;

import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldDistance;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 19-Aug-2003 Time: 14:51:58 To
 * change this template use Options | File Templates.
 */
public class SimpleDemandedStatus extends DemandedStatus implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**********************************************************************
	 * member variables
	 *********************************************************************/

	/**
	 * demanded course (degs)
	 * 
	 */
	private double _course;

	/**
	 * demanded height (to use if we don't have a location value)
	 * 
	 */
	private double _height;

	/**
	 * demanded speed (m/sec)
	 * 
	 */
	private WorldSpeed _speed;

	/**********************************************************************
	 * constructor
	 *********************************************************************/
	public SimpleDemandedStatus(final long id, final long time)
	{
		super(id, time);
	}

	public String toString()
	{
		String res = "";

		res += "[t:" + super.getTime() + " crse:" + (int) this.getCourse()
				+ " spd:" + (int) this.getSpeed() + " ht:" + (int) this.getHeight()
				+ "]";
		return res;
	}

	/**
	 * make demanded status follow indicated status (so we are in a steady state)
	 * 
	 * @param time
	 *          the current time
	 * @param status
	 *          the participant status to preserve
	 */
	public SimpleDemandedStatus(final long time, final Status status)
	{
		this(status.getId(), time);
		setCourse(status.getCourse());
		if (status.getSpeed() != null)
			setSpeed(status.getSpeed().getValueIn(WorldSpeed.M_sec));
		if (status.getLocation() != null)
			setHeight(-status.getLocation().getDepth());
	}

	/**
	 * copy constructor - to take a copy of a demanded status
	 * 
	 */
	public SimpleDemandedStatus(long time, SimpleDemandedStatus original)
	{
		this(original.getId(), time);
		// now the others
		this._course = original._course;
		this._height = original._height;
		setSpeed(original.getSpeed());
	}

	/**
	 * set the course (degs)
	 * 
	 */
	public void setCourse(double degs)
	{
		while (degs < 0)
			degs += 360;

		while (degs >= 360)
			degs -= 360;

		_course = degs;
	}

	/**
	 * get the course (degs)
	 * 
	 */
	public double getCourse()
	{
		return _course;
	}

	/**
	 * get the height (m)
	 * 
	 */
	public double getHeight()
	{
		return _height;
	}

	/**
	 * set the height
	 * 
	 */
	public void setHeight(final double val)
	{
		_height = val;
	}

	/**
	 * set the height using object
	 * 
	 */
	public void setHeight(WorldDistance height)
	{
		_height = height.getValueIn(WorldDistance.METRES);
	}

	/**
	 * set the speed (m_sec)
	 * 
	 */
	public void setSpeed(double m_sec)
	{
		_speed = new WorldSpeed( m_sec, WorldSpeed.M_sec);
	}

	/**
	 * setter which takes world speed param
	 * 
	 */
	public void setSpeed(WorldSpeed spd)
	{
		_speed = spd;
	}

	/** the speed (with units)
	 * 
	 * @return
	 */
	public WorldSpeed getSpeedVal()
	{
		return _speed;
	}
	
	/**
	 * get the speed (m_sec)
	 * 
	 */
	public double getSpeed()
	{
		return _speed.getValueIn(WorldSpeed.M_sec);
	}
}
