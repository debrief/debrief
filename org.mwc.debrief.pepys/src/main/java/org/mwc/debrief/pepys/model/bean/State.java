package org.mwc.debrief.pepys.model.bean;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.mwc.debrief.pepys.model.PepsysException;
import org.mwc.debrief.pepys.model.db.Condition;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.SqliteDatabaseConnection;
import org.mwc.debrief.pepys.model.db.annotation.FieldName;
import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.Location;
import org.mwc.debrief.pepys.model.db.annotation.ManyToOne;
import org.mwc.debrief.pepys.model.db.annotation.TableName;
import org.mwc.debrief.pepys.model.db.annotation.Time;
import org.mwc.debrief.pepys.model.db.config.ConfigurationReader;
import org.mwc.debrief.pepys.model.db.config.DatabaseConfiguration;
import org.mwc.debrief.pepys.model.tree.TreeStructurable;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import junit.framework.TestCase;

@TableName(name = "States")
public class State implements AbstractBean, TreeStructurable {

	public static class StatesTest extends TestCase {

		public void testStatesQuery() {
			try {
				final DatabaseConfiguration _config = new DatabaseConfiguration();
				ConfigurationReader.loadDatabaseConfiguration(_config,
						DatabaseConnection.DEFAULT_SQLITE_TEST_DATABASE_FILE,
						DatabaseConnection.DEFAULT_SQLITE_TEST_DATABASE_FILE);
				final SqliteDatabaseConnection sqlite = new SqliteDatabaseConnection();
				sqlite.initializeInstance(_config);
				final List<State> list = sqlite.listAll(State.class, null);

				assertTrue("States - database entries", list.size() == 12209);

				final List<State> list2 = sqlite.listAll(State.class,
						Arrays.asList(new Condition[] { new Condition("source_id = 18") }));

				assertTrue("States - database entries", list2.size() == 402);
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | PropertyVetoException | SQLException
					| ClassNotFoundException | IOException | PepsysException e) {
				e.printStackTrace();
				fail("Couldn't connect to database or query error");
			}

		}
	}

	@Id
	private String state_id;

	@Time
	private Timestamp time;

	@ManyToOne
	@FieldName(name = "sensor_id")
	private Sensor sensor;
	private double heading;
	private double course;

	private double speed;
	@ManyToOne
	@FieldName(name = "source_id")
	private Datafile datafile;
	private String privacy_id;
	private Timestamp created_date;

	@Location
	private WorldLocation location;

	public State() {

	}

	@Override
	public void doImport(final Layers _layers) {
		// see if the track is in already
		final String trackName = getPlatform().getName();
		final Layer target = _layers.findLayer(trackName, true);
		final TrackWrapper track;
		if (target != null && target instanceof TrackWrapper) {
			// ok, use it
			track = (TrackWrapper) target;
		} else {
			// create a new track
			track = new TrackWrapper();
			track.setName(trackName);
			// and store it
			_layers.addThisLayer(track);
		}

		// create the wrapper for this annotation
		final FixWrapper fixWrapper = new FixWrapper(new Fix(new HiResDate(time.getTime()), location, course, speed));
		fixWrapper.setName(time.toString());
		track.add(fixWrapper);
	}

	public double getCourse() {
		return course;
	}

	public Timestamp getCreated_date() {
		return created_date;
	}

	@Override
	public Datafile getDatafile() {
		return datafile;
	}

	public double getHeading() {
		return heading;
	}

	public WorldLocation getLocation() {
		return location;
	}

	@Override
	public Platform getPlatform() {
		if (sensor != null) {
			return sensor.getPlatform();
		}
		return null;
	}

	public String getPrivacy_id() {
		return privacy_id;
	}

	@Override
	public SensorType getSensorType() {
		if (sensor != null) {
			return sensor.getSensorType();
		}
		return null;
	}

	public double getSpeed() {
		return speed;
	}

	public String getState_id() {
		return state_id;
	}

	@Override
	public Date getTime() {
		return time;
	}

	public void setCourse(final double course) {
		this.course = course;
	}

	public void setCreated_date(final Timestamp created_date) {
		this.created_date = created_date;
	}

	public void setDatafile(final Datafile datafile) {
		this.datafile = datafile;
	}

	public void setHeading(final double heading) {
		this.heading = heading;
	}

	public void setLocation(final WorldLocation location) {
		this.location = location;
	}

	public void setPrivacy_id(final String privacy_id) {
		this.privacy_id = privacy_id;
	}

	public void setSensor(final Sensor sensor) {
		this.sensor = sensor;
	}

	public void setSpeed(final double speed) {
		this.speed = speed;
	}

	public void setState_id(final String state_id) {
		this.state_id = state_id;
	}

	public void setTime(final Timestamp time) {
		this.time = time;
	}
}
