package org.mwc.debrief.pepys.model.bean;

import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.SqliteDatabaseConnection;
import org.mwc.debrief.pepys.model.db.annotation.FieldName;
import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.ManyToOne;
import org.mwc.debrief.pepys.model.db.annotation.TableName;

import junit.framework.TestCase;

@TableName(name = "Sensors")
public class Sensor implements AbstractBean, Comparable<Sensor> {

	public static class SensorTest extends TestCase {
		public void testSensorQuery() {
			try {
				new SqliteDatabaseConnection().createInstance();
				final List<Sensor> list = DatabaseConnection.getInstance().listAll(Sensor.class, null);

				assertTrue("States - database entries", list.size() == 30);

				final Sensor plantFormSensor = DatabaseConnection.getInstance().listById(Sensor.class, 27);

				assertTrue("States - database entries", "PLATFORM-1".equals(plantFormSensor.getName()));
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | PropertyVetoException | SQLException
					| ClassNotFoundException e) {
				e.printStackTrace();
				fail("Couldn't connect to database or query error");
			}
		}
	}

	@Id
	private String sensor_id;
	private String name;

	@ManyToOne
	@FieldName(name = "sensor_type_id")
	private SensorType sensorType;

	@ManyToOne
	@FieldName(name = "host")
	private Platform platform;
	private Date created_date;

	public Sensor() {

	}

	@Override
	public int compareTo(final Sensor o) {
		return sensor_id.compareTo(o.sensor_id);
	}

	public Date getCreated_date() {
		return created_date;
	}

	public String getName() {
		return name;
	}

	public Platform getPlatform() {
		return platform;
	}

	public String getSensor_id() {
		return sensor_id;
	}

	public SensorType getSensorType() {
		return sensorType;
	}

	public void setCreated_date(final Date created_date) {
		this.created_date = created_date;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPlatform(final Platform platform) {
		this.platform = platform;
	}

	public void setSensor_id(final String sensor_id) {
		this.sensor_id = sensor_id;
	}

	public void setSensorType(final SensorType sensorType) {
		this.sensorType = sensorType;
	}

}
