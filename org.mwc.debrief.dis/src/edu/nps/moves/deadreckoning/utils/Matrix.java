package edu.nps.moves.deadreckoning.utils;

/**
 * A class that performs some basic Matrix manipulations, stopping short of
 * Eigenvectors, Single Value Decomposition, LU, and other more advaced
 * manipulations.
 * <ol>
 * <li>Multiplication</li>
 * <li>Inverse</li>
 * <li>Add</li>
 * <li>Subtract</li>
 * <li>Transpose</li>
 * <li>Row swap</li>
 * <li>Sub determinate (2x2)</li>
 *
 * @author Sheldon L. Snyder
 */

public class Matrix {
	/**
	 * Adds a constant to each element of this matrix
	 *
	 * @param r1 - the matrix to receve the addition
	 * @param a  - the vlaue to ad to each cell of the matrix
	 */
	public static void add(final Matrix r1, final double a) throws Exception {
		for (int i = 0; i < r1.rows(); i++) {
			for (int j = 0; j < r1.cols(); j++)
				r1.setCell(i, j, r1.cell(i, j) + a);
		}
	}

	/***************************************************************************
	 * Static method to add any two matrices
	 *
	 * @param M1
	 * @param M2
	 * @return adds m1 and m2 only if of the same size
	 */
	public static Matrix add(final Matrix M1, final Matrix M2) throws MatrixException {
		if (M1.rows() != M2.rows() || M1.cols() != M2.cols())
			throw new MatrixException("not equal dim matrixes");

		return M1.add(M2);
	}// add(Matrix M1, Matrix M2) throws MatrixException-------------------------

	/**
	 * Solves the determinate of this 2x2 matrix
	 *
	 * @param d1 - a11
	 * @param d2 - a12
	 * @param d3 - a21
	 * @param d4 - a22
	 * @return the determinate of this 2x2
	 */
	public static double det(final double d1, final double d2, final double d3, final double d4) {
		return d1 * d4 - d3 * d2;
	}// det(double d1, double d2, double d3, double d4)--------------------------

	/**************************************************************************
	 * Given a 3 x 3 matrix and using Determinats to solve for inverse
	 *
	 * @param in - input 3x3 matrix
	 * @return - the inverse of this matrix
	 */
	public static Matrix inversMat3x3(final Matrix in) throws MatrixException {
		if (in.rows() != 3 || in.cols() != 3)
			throw new MatrixException("not a 3 x 3 matrix....Must be a 3x3");

		final double a = in.cell(0, 0);
		final double b = in.cell(0, 1);
		final double c = in.cell(0, 2);
		final double d = in.cell(1, 0);
		final double e = in.cell(1, 1);
		final double f = in.cell(1, 2);
		final double g = in.cell(2, 0);
		final double h = in.cell(2, 1);
		final double i = in.cell(2, 2);
		final double DETinv = 1 / ((a * det(e, f, h, i) - d * det(b, c, h, i)) + g * det(b, c, e, f));
		final Matrix inv = new Matrix(3);
		inv.setCell(0, 0, det(e, f, h, i) * DETinv);
		inv.setCell(0, 1, det(c, b, i, h) * DETinv);
		inv.setCell(0, 2, det(b, c, e, f) * DETinv);
		inv.setCell(1, 0, det(f, d, i, g) * DETinv);
		inv.setCell(1, 1, det(a, c, g, i) * DETinv);
		inv.setCell(1, 2, det(c, a, f, d) * DETinv);
		inv.setCell(2, 0, det(d, e, g, h) * DETinv);
		inv.setCell(2, 1, det(b, a, h, g) * DETinv);
		inv.setCell(2, 2, det(a, b, d, e) * DETinv);

		return inv;
	}// inversMat3x3(Matrix in) throws MatrixException---------------------------

	/***************************************************************************
	 * multiplies two matrixes together None Destructive
	 *
	 * @param M1 left hand side
	 * @param M2 right hand side
	 * @return a Matrix of M1 * M2
	 */
	public static Matrix mult(final Matrix M1, final Matrix M2) throws MatrixException {
		return M1.mult(M2);
	}// mult(Matrix M1, Matrix M2) throws MatrixException------------------------

	/**
	 * Performs Ax multiplication
	 *
	 * @param A
	 * @param x
	 * @return the resulting array
	 */
	public static double[] multVec(final Matrix A, final double[] x) throws MatrixException {
		if (A.cols() != x.length)
			throw new MatrixException("Matrix and Vector not compatable sizes in multVec()");

		final double[] rslt = new double[x.length];
		double buff = 0;

		// each row of the matrix
		for (int i = 0; i < A.rows(); i++) {
			buff = 0;
			for (int j = 0; j < A.cols(); j++) {
				buff += A.cell(i, j) * x[j];
			}

			rslt[i] = buff;
		}

		return rslt;
	}

	/***************************************************************************
	 * static Subtraces M2 from M1 5 = 8 - 3
	 *
	 * @param M1 - subtraced from matrix (the 8 in the above)
	 * @param M2 - what is subtracte (the 3 in the above)
	 * @return - the result of subrtaction (the 5 in the above)
	 */
	public static Matrix subtract(final Matrix M1, final Matrix M2) throws MatrixException {
		if (M1.rows() != M2.rows() || M1.cols() != M2.cols())
			throw new MatrixException("not equal dim matrixes");
		return M1.subtract(M2);
	}// subtract(Matrix M1, Matrix M2) throws MatrixException--------------------

	/**************************************************************************
	 * Makes a transpose of the input matrix
	 * <p>
	 * rows become columns<br>
	 * Row 1 is now column 1<br>
	 * Row 2 is now column 2<br>
	 * Row n is now column n<br>
	 *
	 * @param in - input matrix
	 * @return - the transpose of the input
	 */
	public static Matrix transpose(final Matrix in) throws MatrixException {
		if (in.rows() < 1 || in.cols() < 1)
			throw new MatrixException("not a valid sized matrix one of the dim < 1");

		final int r = in.rows();
		final int c = in.cols();

		final Matrix trans = new Matrix(c, r);

		for (int i = 0; i < r; i++)
			for (int j = 0; j < c; j++)
				trans.setCell(j, i, in.cell(i, j));

		return trans;
	}// transpose(Matrix in) throws MatrixException-----------------------------

	/** Data of this Matrix */
	private final double[][] data;

	/** Degree of accuracy limit...not used yet */
	private final double epsilon = 0.000000001;

	/***************************************************************************
	 * create a square matrix initialized to the identity
	 *
	 * @param dimension - size to make a square matrix
	 */
	public Matrix(final int dimension) {
		data = new double[dimension][dimension];
		for (int i = 0; i < dimension; i++)
			for (int j = 0; j < dimension; j++)
				data[i][j] = ((i == j) ? 1 : 0);
	}// Matrix(int dimension)----------------------------------------------------

	/***************************************************************************
	 * create a matrix of any dimensions initialized to all zeroes.
	 *
	 * @param rows
	 * @param cols
	 */
	public Matrix(final int rows, final int cols) {
		data = new double[rows][cols];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++)
				data[i][j] = 0;
		}
	}// Matrix(int rows, int cols)-----------------------------------------------

	/***************************************************************************
	 * Creates a matrix of a matrix...a copy
	 *
	 * @param M - matrix to copy
	 */
	public Matrix(final Matrix M) throws MatrixException {
		data = new double[M.rows()][M.cols()];
		replace(M);
	}// Matrix(Matrix M) throws MatrixException----------------------------------

	/***************************************************************************
	 * Adds two matrices together
	 * <p>
	 * non-destructive
	 *
	 * @param M2 - what to add to this
	 * @return this matrix with M2 added to itthis + M2 of same only if both are
	 *         same dim
	 */
	public Matrix add(final Matrix M2) throws MatrixException {
		if (rows() != M2.rows() || cols() != M2.cols())
			throw new MatrixException("ERROR NOT SAME SIZE MATRIXES!!!!");
		final Matrix result = new Matrix(rows(), cols());
		for (int i = 0; i < rows(); i++) {
			for (int j = 0; j < cols(); j++)
				result.setCell(i, j, cell(i, j) + M2.cell(i, j));
		}
		return result;
	}// add(Matrix M2) throws MatrixException------------------------------------

	/***************************************************************************
	 * return the value in this matrix located at the ith row and jth column
	 * <p>
	 * Should have made this getCell, but I got too far along to make all the
	 * changes....
	 *
	 * @param i - row
	 * @param j - column
	 * @return - the value at row i and column j
	 */
	public double cell(final int i, final int j) throws MatrixException {
		if (rows() <= i || rows() < 0)
			throw new MatrixException("Bad Row index");
		if (cols() <= j || cols() < 0)
			throw new MatrixException("Bad COL index");
		return data[i][j];
	}// cell(int i, int j) throws MatrixException--------------------------------

	/***************************************************************************
	 * Get the number of columns in this matrix
	 *
	 * @return the number of columns in this matrix
	 */
	public int cols() {
		return (data.length > 0) ? data[0].length : 0;
	}// cols()-------------------------------------------------------------------

	/***************************************************************************
	 * scales a matrix, but does not destroy the content of the original
	 * <p>
	 * Non-destructive multiply
	 *
	 * @param a - the scalar
	 * @return this multipled by a
	 */
	public Matrix mult(final double a) throws MatrixException {
		final Matrix result = new Matrix(rows(), cols());
		result.replace(this);
		result.multSelf(a);
		return result;
	}// mult(double a)----------------------------------------------------------

	/***************************************************************************
	 * Multiplies 2 matrixes together
	 * <p>
	 * 3x1 * 1x3 = 3x3
	 * <p>
	 * None Destructive
	 *
	 * @param M2
	 * @return a Matrix of this * M2
	 */
	public Matrix mult(final Matrix M2) throws MatrixException {
		if (cols() != M2.rows())
			throw new MatrixException("Wrong size matrix to multiply to this\n" + "this.cols must equal M2.rows");

		final Matrix result = new Matrix(rows(), M2.cols());
		double tmp = 0;
		final double tmp2 = 0, sum = 0;

		for (int i = 0; i < result.rows(); i++) {
			for (int j = 0; j < result.cols(); j++) {
				for (int k = 0; k < result.cols(); k++) {
					tmp += cell(i, k) * M2.cell(k, j);
				}
				result.setCell(i, j, tmp);
				tmp = 0;
			}
		}
		return result;
	}// mult(Matrix M2) throws MatrixException-----------------------------------

	/**
	 * Scalar multiply in place
	 * <p>
	 * Destructive multiply
	 *
	 * @param a
	 */
	public void multSelf(final double a) {
		for (int i = 0; i < rows(); i++)
			for (int j = 0; j < cols(); j++)
				data[i][j] *= a;
	}// multSelf(double a)-------------------------------------------------------a

	/***************************************************************************
	 * Prints the content of a matrix to standard out
	 */
	public void print() throws MatrixException {
		String ln;
		for (int j = 0; j < rows(); j++) {
			ln = "";
			for (int i = 0; i < cols(); i++)
				ln += cell(j, i) + "\t";
			System.out.println(ln);
		}
	}// print()------------------------------------------------------------------

	/***************************************************************************
	 * copy each cell from M to this.data
	 * <p>
	 * Replaces the value of this...destructive copy
	 * <P>
	 * to just get a copy of a matrix<br>
	 * Matrix mm33 is a matrix initialized to some value<br>
	 * Matrix inv = Matrix.inverseMxM(mm33);
	 * <p>
	 * This makes a copy of mm33 into inv. mm33 is not altered in this process.
	 *
	 * @param M - matrix to copy
	 */
	public void replace(final Matrix M) throws MatrixException {
		if (M.rows() != rows() || M.cols() != cols())
			throw new MatrixException("M dim not = this dim");
		for (int i = 0; i < M.rows(); i++)
			for (int j = 0; j < M.cols(); j++)
				setCell(i, j, M.cell(i, j));
	}// replace(Matrix M) throws MatrixException---------------------------------

	/***************************************************************************
	 * Gets the number of rows in this matrix
	 *
	 * @return the number rows in this matrix
	 */
	public int rows() {
		return data.length;
	}// rows()-------------------------------------------------------------------

	/***************************************************************************
	 * set the value of the cell at the ith row and jth column to value
	 *
	 * @param i     - row
	 * @param j     - column
	 * @param value - the double to put in the cell (i,j)
	 */
	public void setCell(final int i, final int j, final double value) throws MatrixException {
		if (rows() <= i || rows() < 0)
			throw new MatrixException("Bad Row index");
		if (cols() <= j || cols() < 0)
			throw new MatrixException("Bad COL index");

		data[i][j] = value;
	}// setCell(int i, int j, double value) throws MatrixException---------------

	/***************************************************************************
	 * Subtracts a matrix from this
	 * <p>
	 * non-destructive
	 *
	 * @param M2
	 * @return this - M2
	 */
	public Matrix subtract(final Matrix M2) throws MatrixException {
		if (rows() != M2.rows() || cols() != M2.cols())
			throw new MatrixException("ERROR NOT SAME SIZE MATRIXES!!!!");
		final Matrix result = new Matrix(rows(), cols());
		for (int i = 0; i < rows(); i++)
			for (int j = 0; j < cols(); j++)
				result.setCell(i, j, cell(i, j) - M2.cell(i, j));
		return result;
	}// subtract(Matrix M2) throws MatrixException-------------------------------

}// MATRIX class-----------------------------------------------------------------