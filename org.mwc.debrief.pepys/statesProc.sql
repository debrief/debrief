select state_id, state_time as time, sensor_name, platform_name, platformtype_name as platform_type, nationality_name, state_location as location, elevation, heading, course, speed, reference from pepys.states_for (?, ?, ?, ?, ?, ?)
