package org.mwc.debrief.pepys.model.db.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class AnnotationsUtils {

	public static String getColumnName(final Field field) {
		if (field.isAnnotationPresent(FieldName.class)) {
			final FieldName fieldName = field.getAnnotation(FieldName.class);
			return fieldName.name();
		} else {
			return field.getName();
		}
	}

	public static Field getField(final Class<?> type, final Class<? extends Annotation> annotation) {
		final Field[] fields = type.getDeclaredFields();
		for (final Field field : fields) {
			if (field.isAnnotationPresent(annotation)) {
				return field;
			}
		}
		return null;
	}

	public static String getTableName(final Class<?> type) {
		if (!type.isAnnotationPresent(TableName.class)) {
			return type.getSimpleName();
		} else {
			final TableName tableName = type.getAnnotation(TableName.class);
			return tableName.name();
		}
	}
}
