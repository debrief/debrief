package edu.nps.moves.deadreckoning.utils;

/**
 * The Exception class that is thrown by the Matrix.java class Very basic
 * Exception class, only passes a description of the error that will hopefully
 * lead to simple troubleshooting resolution...:)
 *
 * @author Sheldon L. Snyder
 */
public class MatrixException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public MatrixException(final String s) {
		super(s);
	}
}
