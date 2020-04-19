package org.mwc.debrief.pepys.model.db.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AnnotationsUtils {

	public static class FieldsTable {
		private final String tableName;
		private final String FieldName;

		public FieldsTable(final String tableName, final String fieldName) {
			super();
			this.tableName = tableName;
			FieldName = fieldName;
		}

		public String getFieldName() {
			return FieldName;
		}

		public String getTableName() {
			return tableName;
		}
	}

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

	public static Field[] getFields(final Class<?> type, final Class<? extends Annotation> annotation) {
		final List<Field> fields = new ArrayList<>();
		for (final Field field : type.getDeclaredFields()) {
			if (field.isAnnotationPresent(annotation)) {
				fields.add(field);
			}
		}
		return fields.toArray(new Field[] {});
	}

	public static Collection<FieldsTable> getRecursiveFields(final Class<?> type,
			final Class<? extends Annotation> annotation, final String prefix) {
		final String baseTableName = AnnotationsUtils.getTableName(type);
		final List<FieldsTable> answer = new ArrayList<>();
		final Field[] fields = type.getDeclaredFields();
		for (final Field field : fields) {
			if (field.isAnnotationPresent(annotation)) {
				answer.add(new FieldsTable(prefix + baseTableName, AnnotationsUtils.getColumnName(field)));
			} else if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
				answer.addAll(getRecursiveFields(field.getType(), annotation,
						prefix + baseTableName + AnnotationsUtils.getColumnName(field)));
			}
		}
		return answer;
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
