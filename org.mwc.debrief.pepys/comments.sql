with
ui_filter_input as
	(select
			? start_time, --Input should be same as for Phase 1
			? end_time,  --Input should be same as for Phase 1
			? comment_search_string, --Input should be same as for Phase 1
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
			case when (trim(ui_input.comment_search_string)='' OR ui_input.comment_search_string is null) then null::varchar else '%'||upper(ui_input.comment_search_string)||'%' end as comment_search_string,
			case when (coalesce(array_length(ui_input.source_id,1),0)::int = 0) then null else ui_input.source_id end as source_id,
			case when (coalesce(array_length(ui_input.platform_id,1),0)::int = 0) then null else ui_input.platform_id end as platform_id
		from
				ui_filter_input as ui_input
	),
	filtered_comments as
	(
		select
			com.comment_id,
			com.time,
			com.content,
			com.source_id,
			com.platform_id,
			com.comment_type_id
		from
			pepys."Comments" com
		where
			--Start and End Time criteria from the UI
			tsrange((select start_time::timestamp from processed_ui_filter_values), (select end_time::timestamp from processed_ui_filter_values), '[]') @> com.time AND
			--Comment search criteria from the UI
			((select comment_search_string from processed_ui_filter_values) is null OR upper(com.content) like (select comment_search_string from processed_ui_filter_values)) AND
			--Source criteria from the UI
			((select source_id from processed_ui_filter_values) is null OR com.source_id in (select unnest(source_id::uuid[]) from processed_ui_filter_values)) AND
			--Platform criteria from the UI
			((select platform_id from processed_ui_filter_values) is null OR com.platform_id in (select unnest(platform_id::uuid[]) from processed_ui_filter_values))
	),
	filtered_limits as
	(select
		case when (ui_input.page_no = -1 OR ui_input.page_size = -1) then 1 else ui_input.page_no end as page_no,
		case when (ui_input.page_no = -1 OR ui_input.page_size = -1) then (select count(1) from filtered_comments) else ui_input.page_size end as page_size
	from
		ui_filter_input as ui_input
	)
select 
	filtered_comments.comment_id,
	filtered_comments.time, 
	Platforms.name as platform_name,
	PlatformTypes.name as platform_type_name, 
	Nationalities.name as nationalities_name,
	filtered_comments.content, 
	CommentTypes.name as comment_type_name,
	Datafiles.reference 
from
	filtered_comments inner join
	pepys."Datafiles" as Datafiles on Datafiles.datafile_id=filtered_comments.source_id inner join
	pepys."Platforms" as Platforms on filtered_comments.platform_id=Platforms.platform_id inner join
	pepys."PlatformTypes" as PlatformTypes on Platforms.platform_type_id = PlatformTypes.platform_type_id inner join
	pepys."Nationalities" as Nationalities on Platforms.nationality_id = Nationalities.nationality_id inner join
	pepys."CommentTypes" as CommentTypes on filtered_comments.comment_type_id = CommentTypes.comment_type_id
--Sort clause for pagination
order by
	filtered_comments.comment_id asc
	limit (select page_size from filtered_limits)
	offset (select page_size*(page_no -1) from filtered_limits);