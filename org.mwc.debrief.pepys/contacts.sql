with
	ui_filter_input as
	(select
			? start_time, --Input should be same as for Phase 1
			? end_time,  --Input should be same as for Phase 1
			? "location", --Input should be same as for Phase 1
			?::text[] sensor_id,  --Input from Phase 2 of import, can be set as null: null as sensor_id
			?::text[] source_id,  --Input from Phase 2 of import, can be set as null: null as source_id
			?::text[] platform_id,  --Input from Phase 2 of import, can be set as null: null as platform_id
			--null as platform_id,  --Example on how to provide null
			1::integer page_no, --Pagination input. Page No For ex. if there are 1000 records paginated into pages of 100 records each, 1 here will return the first page or first 100 records
			1000000::integer page_size --Pagination input - No. of records per page
	),
	processed_ui_filter_values as
	(select
			case when (trim(ui_input.start_time)='' OR ui_input.start_time is null) then '1000-01-01 00:00:00.000000'::timestamp else to_timestamp(ui_input.start_time, 'YYYY-MM-DD HH24:MI:SS.US') end as start_time,
			case when (trim(ui_input.end_time)='' OR ui_input.end_time is null) then '9999-12-12 23:59:59.000000'::timestamp else to_timestamp(ui_input.end_time, 'YYYY-MM-DD HH24:MI:SS.US') end as end_time,
			case when (trim(ui_input.location)='' OR ui_input.location is null) then null else ST_GeomFromText(ui_input.location) end as location,
			case when (coalesce(array_length(ui_input.sensor_id,1),0)::int = 0) then null else ui_input.sensor_id end as sensor_id,
			case when (coalesce(array_length(ui_input.source_id,1),0)::int = 0) then null else ui_input.source_id end as source_id,
			case when (coalesce(array_length(ui_input.platform_id,1),0)::int = 0) then null else ui_input.platform_id end as platform_id,
			case when (ui_input.page_no is null OR ui_input.page_no <=0) then 1 else ui_input.page_no end as page_no,
			case when (ui_input.page_size is null OR ui_input.page_size <=0) then 100 else ui_input.page_size end as page_size
		from
				ui_filter_input as ui_input
	),
	selected_sensors as
	(select
		sensor_id,
		name,
		host
	from
		pepys."Sensors" sen
	where
		--Platform criteria from the UI
		((select platform_id from processed_ui_filter_values) is null OR sen.host in (select unnest(platform_id::uuid[]) from processed_ui_filter_values)) AND
		--Sensor criteria from the UI
		((select sensor_id from processed_ui_filter_values) is null OR sen.sensor_id in (select unnest(sensor_id::uuid[]) from processed_ui_filter_values))
	),
	filtered_sensors as
	(select
		sen.sensor_id,
		sen.name sensor_name,
		plat.name platform_name,
		platty.name platformtype_name,
		nat.name nationality_name
	from
		selected_sensors as sen inner join
		pepys."Platforms" as plat on sen.host=plat.platform_id inner join
		pepys."PlatformTypes" as platty on plat.platform_type_id = platty.platform_type_id inner join
		pepys."Nationalities" as nat on plat.nationality_id = nat.nationality_id
	),
	filtered_datafiles as
	(select
		datafile_id,
		reference
	from
		pepys."Datafiles" dat
	where
		--Source criteria from the UI
		((select source_id from processed_ui_filter_values) is null OR dat.datafile_id in (select unnest(source_id::uuid[]) from processed_ui_filter_values))
	),
	filtered_contacts as
	(
		select
			con.contact_id,
			con.time,
			con.bearing,
			con.range,
			con.location,
			con.source_id,
			con.sensor_id
		from
			pepys."Contacts" con
		where
			--Start and End Time criteria from the UI
			tsrange((select start_time::timestamp from processed_ui_filter_values), (select end_time::timestamp from processed_ui_filter_values), '[]') @> con.time AND
			--Spatial criteria from the UI
			((select location from processed_ui_filter_values) is null OR ST_Contains((select location from processed_ui_filter_values),con.location)) AND
			--Sensor criteria from the UI
			((select sensor_id from processed_ui_filter_values) is null OR con.sensor_id in (select unnest(sensor_id::uuid[]) from processed_ui_filter_values)) AND
			--Source criteria from the UI
			((select source_id from processed_ui_filter_values) is null OR con.source_id in (select unnest(source_id::uuid[]) from processed_ui_filter_values))
	),
	filtered_limits as
	(select
		case when (ui_input.page_no = -1 OR ui_input.page_size = -1) then 1 else ui_input.page_no end as page_no,
		case when (ui_input.page_no = -1 OR ui_input.page_size = -1) then (select count(1) from filtered_contacts) else ui_input.page_size end as page_size
	from
		ui_filter_input as ui_input
	)
select 
	filtered_contacts.contact_id, 
	filtered_contacts.time, 
	filtered_sensors.sensor_name, 
	filtered_sensors.platform_name,
	filtered_sensors.platformtype_name, 
	filtered_sensors.nationality_name,
	filtered_contacts.bearing, 
	filtered_contacts.range, 
	filtered_contacts.location,
	filtered_datafiles.reference
from
	filtered_contacts inner join
	filtered_datafiles on filtered_contacts.source_id=filtered_datafiles.datafile_id inner join
	filtered_sensors on filtered_contacts.sensor_id = filtered_sensors.sensor_id
--Sort clause for pagination
order by
	filtered_contacts.contact_id asc
	limit (select page_size from filtered_limits)
	offset (select page_size*(page_no -1) from filtered_limits);