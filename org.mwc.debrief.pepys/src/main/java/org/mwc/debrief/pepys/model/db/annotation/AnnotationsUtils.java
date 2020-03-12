package org.mwc.debrief.pepys.model.db.annotation;

import java.lang.reflect.Field;

public class AnnotationsUtils {
	
	public static Field getIdField(final Class<?> type) {
		final Field[] fields = type.getDeclaredFields();
		for (final Field field : fields) {
			if ( field.isAnnotationPresent(Id.class) ) {
				return field;
			}
		}
		return null;
	}
	
	public static String getColumnName(final Field field) {
		if (field.isAnnotationPresent(FieldName.class)) {
			final FieldName fieldName = field.getAnnotation(FieldName.class);
			return fieldName.name();
		}else {
			return field.getName();
		}
	}
	
	public static String getTableName(final Class<?> type) {
		if (!type.isAnnotationPresent(TableName.class)) {
			return type.getSimpleName();
		}else {
			final TableName tableName = (TableName) type.getAnnotation(TableName.class);
			return tableName.name();
		}
	}
}
