package Debrief.ReaderWriter.ais;

import java.sql.Timestamp;

public interface IPositionMessage {

	public abstract double getCog();

	public abstract double getLatitude();

	public abstract double getLongitude();

	public abstract int getMmsi();

	public abstract int getMsgId();

	public abstract Timestamp getMsgTimestamp();

	public abstract double getSog();

	public abstract int getTrueHeading();

}
