package Debrief.ReaderWriter.ais;

import java.sql.Timestamp;

public interface IPositionMessage
{

	public abstract int getMmsi();

	public abstract int getTrueHeading();

	public abstract double getSog();

	public abstract Timestamp getMsgTimestamp();

	public abstract double getLongitude();

	public abstract double getLatitude();

	public abstract int getMsgId();

	public abstract double getCog();

}
