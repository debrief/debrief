package org.bitbucket.es4gwt.shared.spec;

/**
 * @author Mikael Couzic
 */
public enum FilterMode {

	ANY_OF,
	ALL_OF;

	@Override
	public String toString() {
		switch (this) {
		case ANY_OF:
			return "At least one of... ";
		case ALL_OF:
			return "All of... ";
		default:
			throw new UnsupportedOperationException("toString() method is not supported for FilterMode." + name());
		}
	};

}
