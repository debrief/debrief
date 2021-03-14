with
ui_filter_input as
	(select
			? as start_time, --Input should be same as for Phase 1
			? as end_time,  --Input should be same as for Phase 1
			? as location, --Input should be same as for Phase 1
			? as sensor_id,  --Input from Phase 2 of import, can be set as null: null as sensor_id
			? as source_id,  --Input from Phase 2 of import, can be set as null: null as source_id
			? as platform_id,  --Input from Phase 2 of import, can be set as null: null as platform_id
			--null as platform_id,  --Example on how to provide null
			1 as page_no, --Pagination input. Page No For ex. if there are 1000 records paginated into pages of 100 records each, 1 here will return the first page or first 100 records
			1000000 as page_size --Pagination input - No. of records per page
	),
	processed_ui_filter_values as
	(select
			case when (trim(ui_input.start_time)='' OR ui_input.start_time is null) then '1000-01-01 00:00:00.000000'::timestamp else to_timestamp(ui_input.start_time, 'YYYY-MM-DD HH24:MI:SS.US') end as start_time,
			case when (trim(ui_input.end_time)='' OR ui_input.end_time is null) then '9999-12-12 23:59:59.000000'::timestamp else to_timestamp(ui_input.end_time, 'YYYY-MM-DD HH24:MI:SS.US') end as end_time,
			case when (trim(ui_input.location)='' OR ui_input.location is null) then null else ST_GeomFromText(ui_input.location) end as location,
			case when (trim(ui_input.sensor_id)='' OR ui_input.sensor_id is null) then null else string_to_array(ui_input.sensor_id,',') end as sensor_id,
			case when (trim(ui_input.source_id)='' OR ui_input.source_id is null) then null else string_to_array(ui_input.source_id,',') end as source_id,
			case when (trim(ui_input.platform_id)='' OR ui_input.platform_id is null) then null else string_to_array(ui_input.platform_id,',') end as platform_id,
			case when (ui_input.page_no is null OR ui_input.page_no <=0) then 1 else ui_input.page_no end as page_no,
			case when (ui_input.page_size is null OR ui_input.page_size <=0) then 100 else ui_input.page_size end as page_size
		from
				ui_filter_input as ui_input
		)
select filtered_states.state_id, filtered_states.time, Sensors.name as sensor_name, Platforms.name as platform_name, filtered_states.source_id as sourceid,
	PlatformTypes.name as platform_type, Nationalities.name as nationality_name,
	filtered_states.location, filtered_states.elevation, filtered_states.heading, filtered_states.course, filtered_states.speed from
	pepys."States" as filtered_states inner join
	pepys."Sensors" as Sensors on filtered_states.sensor_id = Sensors.sensor_id inner join
	pepys."Platforms" as Platforms on Sensors.host=Platforms.platform_id inner join
	pepys."PlatformTypes" as PlatformTypes on Platforms.platform_type_id = PlatformTypes.platform_type_id inner join
	pepys."Nationalities" as Nationalities on Platforms.nationality_id = Nationalities.nationality_id
		WHERE
			--Start and End Time criteria from the UI
			tsrange((select start_time::timestamp from processed_ui_filter_values), (select end_time::timestamp from processed_ui_filter_values), '[]') @> filtered_states.time AND
			--Spatial criteria from the UI
			((select location from processed_ui_filter_values) is null OR ST_Contains((select location from processed_ui_filter_values),filtered_states.location)) AND
			--Sensor criteria from the UI
			((select sensor_id from processed_ui_filter_values) is null OR filtered_states.sensor_id in (select unnest(sensor_id::uuid[]) from processed_ui_filter_values)) AND
			--Source criteria from the UI
			((select source_id from processed_ui_filter_values) is null OR filtered_states.source_id in (select unnest(source_id::uuid[]) from processed_ui_filter_values)) AND
			--Platform criteria from the UI
			((select platform_id from processed_ui_filter_values) is null OR Sensors.host in (select unnest(platform_id::uuid[]) from processed_ui_filter_values))
			--Sort clause for pagination
			order by filtered_states.state_id asc limit (select page_size from processed_ui_filter_values) offset (select page_size*(page_no -1) from processed_ui_filter_values);
