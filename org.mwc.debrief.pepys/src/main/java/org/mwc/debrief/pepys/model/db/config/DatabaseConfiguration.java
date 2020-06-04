package org.mwc.debrief.pepys.model.db.config;

import java.util.HashMap;

import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.PostgresDatabaseConnection;
import org.mwc.debrief.pepys.model.db.SqliteDatabaseConnection;
import org.mwc.debrief.pepys.model.db.config.LoaderOption.LoaderType;

public class DatabaseConfiguration {

	public static class DatabaseConfigurationFactory {
		public static DatabaseConfiguration createSqliteConfiguration(final String path) {
			final DatabaseConfiguration _config = new DatabaseConfiguration();
			final HashMap<String, String> databaseTag = new HashMap<String, String>();
			databaseTag.put(SqliteDatabaseConnection.CONFIGURATION_DB_NAME, path);
			databaseTag.put(DatabaseConnection.CONFIGURATION_DATABASE_TYPE, DatabaseConnection.SQLITE);
			_config.categories.put(DatabaseConnection.CONFIGURATION_TAG, databaseTag);
			_config.setLoaderOption(new LoaderOption(LoaderType.DRAG_AND_DROP_SQLITE, path));
			return _config;
		}
	}

	private final HashMap<String, HashMap<String, String>> categories = new HashMap<>();

	/**
	 * Field to specify where we are reading the database from and what type of
	 * configuration it is.
	 */
	private LoaderOption _loaderOption;

	public DatabaseConfiguration() {

	}

	public void clear() {
		categories.clear();
	}

	public HashMap<String, String> getCategory(final String str) {
		if (str != null) {
			if (!categories.containsKey(str)) {
				categories.put(str, new HashMap<String, String>());
			}
			return categories.get(str);
		}
		return null;
	}

	public LoaderOption getLoaderOption() {
		return _loaderOption;
	}

	public void setLoaderOption(LoaderOption _loaderOption) {
		this._loaderOption = _loaderOption;
	}

	public static boolean isValid(final DatabaseConfiguration _config) {
		if (!_config.categories.containsKey(DatabaseConnection.CONFIGURATION_TAG)) {
			return false;
		}
		final HashMap<String, String> category = _config.categories.get(DatabaseConnection.CONFIGURATION_TAG);
		if (!category.containsKey(DatabaseConnection.CONFIGURATION_DATABASE_TYPE)) {
			return false;
		}
		
		final String databaseType = category.get(DatabaseConnection.CONFIGURATION_DATABASE_TYPE);
		if (databaseType != null) {
			switch (databaseType) {
			case DatabaseConnection.POSTGRES:
				return PostgresDatabaseConnection.validateDatabaseConfiguration(_config);
			case DatabaseConnection.SQLITE:
				return SqliteDatabaseConnection.validateDatabaseConfiguration(_config);
			default:
				return false;
			}
		}
		return false;
	}

}
