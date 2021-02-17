with
ui_filter_input as
	(select
			? as start_time, --Input should be same as for Phase 1
			? as end_time,  --Input should be same as for Phase 1
			? as comment_search_string, --Input should be same as for Phase 1
			? as sensor_id,  --Input from Phase 2 of import, can be set as null: null as sensor_id
			? as source_id,  --Input from Phase 2 of import, can be set as null: null as source_id
			? as platform_id,  --Input from Phase 2 of import, can be set as null: null as platform_id
			--null as platform_id,  --Example on how to provide null
			1 as page_no, --Pagination input. Page No For ex. if there are 1000 records paginated into pages of 100 records each, 1 here will return the first page or first 100 records
			? as page_size --Pagination input - No. of records per page
	),
	processed_ui_filter_values as
	(select
			case when (trim(ui_input.start_time)='' OR ui_input.start_time is null) then '1000-01-01 00:00:00.000000'::timestamp else to_timestamp(ui_input.start_time, 'YYYY-MM-DD HH24:MI:SS.US') end as start_time,
			case when (trim(ui_input.end_time)='' OR ui_input.end_time is null) then '9999-12-12 23:59:59.000000'::timestamp else to_timestamp(ui_input.end_time, 'YYYY-MM-DD HH24:MI:SS.US') end as end_time,
			case when (trim(ui_input.comment_search_string)='' OR ui_input.comment_search_string is null) then null::varchar else '%'||upper(ui_input.comment_search_string)||'%' end as comment_search_string,
			case when (trim(ui_input.sensor_id)='' OR ui_input.sensor_id is null) then null else string_to_array(ui_input.sensor_id,',') end as sensor_id,
			case when (trim(ui_input.source_id)='' OR ui_input.source_id is null) then null else string_to_array(ui_input.source_id,',') end as source_id,
			case when (trim(ui_input.platform_id)='' OR ui_input.platform_id is null) then null else string_to_array(ui_input.platform_id,',') end as platform_id,
			case when (ui_input.page_no is null OR ui_input.page_no <=0) then 1 else ui_input.page_no end as page_no,
			case when (ui_input.page_size is null OR ui_input.page_size <=0) then 100 else ui_input.page_size end as page_size
		from
				ui_filter_input as ui_input
		)
select filtered_comments.comment_id, filtered_comments.time, Platforms.name,
		PlatformTypes.name, Nationalities.name,
		filtered_comments.content, CommentTypes.name from
		pepys."Comments" as filtered_comments inner join
		pepys."Platforms" as Platforms on filtered_comments.platform_id=Platforms.platform_id inner join
		pepys."PlatformTypes" as PlatformTypes on Platforms.platform_type_id = PlatformTypes.platform_type_id inner join
		pepys."Nationalities" as Nationalities on Platforms.nationality_id = Nationalities.nationality_id inner join
		pepys."CommentTypes" as CommentTypes on filtered_comments.comment_type_id = CommentTypes.comment_type_id
		WHERE
			--Start and End Time criteria from the UI
			tsrange((select start_time::timestamp from processed_ui_filter_values), (select end_time::timestamp from processed_ui_filter_values), '[]') @> filtered_comments.time AND
			--Comment search criteria from the UI
			((select comment_search_string from processed_ui_filter_values) is null OR upper(filtered_comments.content) like (select comment_search_string from processed_ui_filter_values)) AND
			--Source criteria from the UI
			((select source_id from processed_ui_filter_values) is null OR filtered_comments.source_id in (select unnest(source_id::uuid[]) from processed_ui_filter_values)) AND
			--Platform criteria from the UI
			((select platform_id from processed_ui_filter_values) is null OR filtered_comments.platform_id in (select unnest(platform_id::uuid[]) from processed_ui_filter_values))
			--Sort clause for pagination
			order by filtered_comments.comment_id asc limit (select page_size from processed_ui_filter_values) offset (select page_size*(page_no -1) from processed_ui_filter_values);
