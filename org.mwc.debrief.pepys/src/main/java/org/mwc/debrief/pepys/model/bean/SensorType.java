package org.mwc.debrief.pepys.model.bean;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.SqliteDatabaseConnection;
import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.TableName;
import org.mwc.debrief.pepys.model.db.config.DatabaseConfiguration;

import junit.framework.TestCase;

@TableName(name = "SensorTypes")
public class SensorType implements AbstractBean {

	public static class SensorTypeTest extends TestCase {

		public void testSensorTypeQuery() {
			try {
				final DatabaseConfiguration _config = new DatabaseConfiguration();
				final String configurationFilename;
				final String path = DatabaseConnection.class.getProtectionDomain().getCodeSource().getLocation()
						.getPath();
				if (path.endsWith("jar")) {
					// We are not running an unit test or we are running from a .jar, so we load it
					// from the root folder
					configurationFilename = Paths.get(DatabaseConnection.DEFAULT_SQLITE_TEST_DATABASE_FILE)
							.getFileName().toString();

				} else {
					configurationFilename = DatabaseConnection.DEFAULT_SQLITE_TEST_DATABASE_FILE;
				}

				DatabaseConnection.loadDatabaseConfiguration(_config, configurationFilename);
				new SqliteDatabaseConnection().createInstance(_config);
				final List<SensorType> list = DatabaseConnection.getInstance().listAll(SensorType.class, null);

				assertTrue("States - database entries", list.size() == 4);

				final SensorType gpsSensor = DatabaseConnection.getInstance().listById(SensorType.class, 4);

				assertTrue("States - database entries", "GPS".equals(gpsSensor.getName()));
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | PropertyVetoException | SQLException
					| ClassNotFoundException | FileNotFoundException e) {
				e.printStackTrace();
				fail("Couldn't connect to database or query error");
			}
		}
	}

	@Id
	private String sensor_type_id;
	private String name;

	public SensorType() {

	}

	public String getName() {
		return name;
	}

	public String getSensor_type_id() {
		return sensor_type_id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setSensor_type_id(final String sensor_type_id) {
		this.sensor_type_id = sensor_type_id;
	}

}
