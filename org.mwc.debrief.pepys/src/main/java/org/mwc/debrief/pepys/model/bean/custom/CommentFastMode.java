package org.mwc.debrief.pepys.model.bean.custom;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.mwc.debrief.pepys.model.bean.AbstractBean;
import org.mwc.debrief.pepys.model.bean.PlainBean;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;

public class CommentFastMode implements AbstractBean, PlainBean {

	private String comment_id;

	private Timestamp time;

	private String platform_name;

	private String platform_type_name;

	private String nationalities_name;

	private String content;

	private String comment_type_name;

	public CommentFastMode() {

	}

	public String getComment_id() {
		return comment_id;
	}

	public String getComment_type_name() {
		return comment_type_name;
	}

	public String getContent() {
		return content;
	}

	public String getNationalities_name() {
		return nationalities_name;
	}

	public String getPlatform_name() {
		return platform_name;
	}

	public String getPlatform_type_name() {
		return platform_type_name;
	}

	public Timestamp getTime() {
		return time;
	}

	@Override
	public void retrieveObject(final ResultSet resultSet, final DatabaseConnection connection) throws SQLException {
		setComment_id(resultSet.getString("comment_id"));
		setTime(resultSet.getTimestamp("time"));
		setPlatform_name(resultSet.getString("platform_name"));
		setPlatform_type_name(resultSet.getString("platform_type_name"));
		setNationalities_name(resultSet.getString("nationalities_name"));
		setContent(resultSet.getString("content"));
		setComment_type_name(resultSet.getString("comment_type_name"));
	}

	public void setComment_id(final String comment_id) {
		this.comment_id = comment_id;
	}

	public void setComment_type_name(final String comment_type_name) {
		this.comment_type_name = comment_type_name;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public void setNationalities_name(final String nationalities_name) {
		this.nationalities_name = nationalities_name;
	}

	public void setPlatform_name(final String platform_name) {
		this.platform_name = platform_name;
	}

	public void setPlatform_type_name(final String platform_type_name) {
		this.platform_type_name = platform_type_name;
	}

	public void setTime(final Timestamp time) {
		this.time = time;
	}
}
