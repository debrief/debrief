package org.mwc.debrief.pepys.model.bean;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
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
import org.mwc.debrief.pepys.model.db.annotation.Transient;
import org.mwc.debrief.pepys.model.db.config.ConfigurationReader;
import org.mwc.debrief.pepys.model.db.config.DatabaseConfiguration;
import org.mwc.debrief.pepys.model.db.config.LoaderOption;
import org.mwc.debrief.pepys.model.db.config.LoaderOption.LoaderType;
import org.mwc.debrief.pepys.model.tree.TreeStructurable;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
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
			if (System.getProperty("os.name").toLowerCase().indexOf("mac") == -1) {
				try {
					final DatabaseConfiguration _config = new DatabaseConfiguration();
					ConfigurationReader.loadDatabaseConfiguration(_config,
							new LoaderOption[] { new LoaderOption(LoaderType.DEFAULT_FILE,
									DatabaseConnection.DEFAULT_SQLITE_TEST_DATABASE_FILE) });
					final SqliteDatabaseConnection sqlite = new SqliteDatabaseConnection();
					sqlite.initializeInstance(_config);
					final List<State> list = sqlite.listAll(State.class, (Collection<Condition>) null);

					assertTrue("States - database entries", list.size() == 12239);

					final List<State> list2 = sqlite.listAll(State.class, Arrays.asList(
							new Condition[] { new Condition("source_id = \"638471a99e264761830b3f6575816e67\"") }));

					assertTrue("States - database entries", list2.size() == 5);

					final List<State> list3 = sqlite.listAll(State.class, Arrays.asList(
							new Condition[] { new Condition("source_id = \"db8692a392924d27bfacdbddc4eb9a29\"") }));

					assertTrue("States - database entries", list3.size() == 11400);
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException | PropertyVetoException | SQLException
						| ClassNotFoundException | IOException | PepsysException e) {
					e.printStackTrace();
					fail("Couldn't connect to database or query error");
				}
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

	@Transient
	private int count;

	public State() {

	}

	@Override
	public void doImport(final Layers _layers, final boolean splitByDatafile) {
		final LightweightTrackWrapper track = getParent(_layers, getDatafile().getReference(),
				getPlatform().getTrackName(), splitByDatafile);

		// create the wrapper for this annotation
		final FixWrapper fixWrapper = new FixWrapper(new Fix(new HiResDate(time.getTime()), location, course, speed));
		fixWrapper.setName(time.toString());
		track.add(fixWrapper);
	}

	@Override
	public int getCount() {
		return count;
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

	private LightweightTrackWrapper getParent(final Layers layers, final String datafile, final String trackName,
			final boolean splitByDatafile) {
		// first the parent folder
		if (splitByDatafile) {
			Layer parent = layers.findLayer(datafile, false);
			if (parent == null) {
				parent = new BaseLayer();
				parent.setName(datafile);
				layers.addThisLayer(parent);
			}

			// now the track
			LightweightTrackWrapper track = null;
			final Enumeration<Editable> iter = parent.elements();
			while (iter.hasMoreElements() && track == null) {
				final Editable item = iter.nextElement();
				if (item instanceof LightweightTrackWrapper && item.getName().equals(trackName)) {
					track = (LightweightTrackWrapper) item;
				}
			}

			// did we find it?
			if (track == null) {
				// create a new track. Since we're inside a parent folder,
				// just use lightweight track
				track = new LightweightTrackWrapper();
				track.setName(trackName);
				// and store it
				parent.add(track);
			}
			return track;
		} else {
			// If we don't want to split by datafile, then we will add the track directly.
			// Let's find it then
			TrackWrapper track = null;

			final Enumeration<Editable> iter = layers.elements();
			while (iter.hasMoreElements() && track == null) {
				final Editable item = iter.nextElement();
				if (item instanceof TrackWrapper && item.getName().equals(trackName)) {
					track = (TrackWrapper) item;
				}
			}

			// did we find it?
			if (track == null) {
				// create a new track
				track = new TrackWrapper();
				track.setName(trackName);
				// and store it
				layers.addThisLayer(track);
			}

			return track;
		}
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

	public Sensor getSensor() {
		return sensor;
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

	public void setCount(final int count) {
		this.count = count;
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
