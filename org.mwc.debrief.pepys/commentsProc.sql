with parameters as
  (select ? as start_time,
          ? as end_time,
          ? as comment_search_string,
          ? as sensor_id,
          ? as source_id,
          ? as platform_id)

select comment_id, comment_time as time, platform_name, platformtype_name as platform_type_name, nationality_name as nationalities_name, content, comment_type_name, reference from pepys.comments_for (
	(select start_time from parameters),
	(select end_time from parameters),
	(select comment_search_string from parameters),
	(select source_id from parameters),
	(select platform_id from parameters))
