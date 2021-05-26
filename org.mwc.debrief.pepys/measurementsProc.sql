select platform_name, platform_id, datatype, sensor_name, sensor_id, reference, datafile_id, agg_count as state_agg_count from pepys.datafiles_for (?, ?, ?, ?, ?)
