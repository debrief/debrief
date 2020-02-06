package edu.nps.moves.deadreckoning;

import edu.nps.moves.deadreckoning.utils.Matrix;

/**
 *
 * (SECONDARY Methods Group) Rotating, rate of position, body coordinates ||
 * Linear motion with Rotation
 * <p>
 * it is coded up, but the linear motion does not seem to work....rotation works
 * but linear motion fails...not sure why they are calculating the linear motion
 * the way they are...
 * <p>
 * The alogrithm is coded IAW IEEE 1278.1-1995 so perhaps it is a coordinate
 * change of basis issue and since I am not working in both world and body
 * coordinates, it fails or limits to 0
 *
 * @author Sheldon L. Snyder
 */
public class DIS_DR_RPB_07 extends DIS_DeadReckoning {
	Matrix ident = new Matrix(3);
	Matrix DR = new Matrix(3);
	Matrix DRR = new Matrix(3);
	Matrix initInv;
	double[] velVec = { entityLinearVelocity_X, entityLinearVelocity_Y, entityLinearVelocity_Z };
	double[] updated = new double[3];

	/***************************************************************************
	 * Makes the R1 matrix
	 *
	 * @return - the vector R1
	 * @throws java.lang.Exception
	 */
	private double[] makeR1() throws Exception {
		Matrix R1 = new Matrix(3);
		final Matrix ident = new Matrix(3);

		// common factors
		final double wDelta = wMag * changeDelta * deltaCt;

		// matrix scalars
		final double wwScale = (wDelta - Math.sin(wDelta)) / (wSq * wMag);
		final double identScalar = Math.sin(wDelta) / wMag;
		final double skewScale = 1 - (Math.cos(wDelta) / wSq);

		// scaled matrixes
		final Matrix wwTmp = ww.mult(wwScale);
		final Matrix identTmp = ident.mult(identScalar);
		final Matrix skwTmp = skewOmega.mult(skewScale);

		R1 = Matrix.add(wwTmp, identTmp);
		R1 = Matrix.subtract(R1, skwTmp);

		return Matrix.multVec(R1, velVec);
	}// makeR1() throws Exception------------------------------------------------

	/***************************************************************************
	 * Makes this iterations DR matrix
	 *
	 * @throws java.lang.Exception
	 */
	private void makeThisDR() throws Exception {
		final double wDelta = wMag * changeDelta * deltaCt;
		final double cosWdelta = Math.cos(wDelta);

		final double wwScale = (1 - cosWdelta) / wSq;
		final double identScalar = cosWdelta;
		final double skewScale = Math.sin(wDelta) / wMag;

		final Matrix wwTmp = ww.mult(wwScale);
		final Matrix identTmp = ident.mult(identScalar);
		final Matrix skwTmp = skewOmega.mult(skewScale);

		DR = Matrix.add(wwTmp, identTmp);
		DR = Matrix.subtract(DR, skwTmp);
	}// makeThisDR() throws Exception--------------------------------------------

	/**
	 * The driver for a DIS_DR_RPB_07 DR algorithm from the Runnable interface
	 * <p>
	 * linear motion and rotation
	 */
	@Override
	public void run() {
		try {
			initInv = Matrix.transpose(initOrien);

			while (true) {
				deltaCt++;
				Thread.sleep(stall);

				// solve for the new position
				updated = Matrix.multVec(initInv, makeR1());
				// set new positons
				entityLocation_X += updated[0];
				entityLocation_Y += updated[1];
				entityLocation_Z += updated[2];

				// make the rotation same as 1-4 rotations
				makeThisDR();
				DRR = Matrix.mult(DR, initOrien);

				entityOrientation_theta = (float) Math.asin(-DRR.cell(0, 2));
				entityOrientation_psi = (float) (Math.acos(DRR.cell(0, 0) / Math.cos(entityOrientation_theta))
						* Math.signum(DRR.cell(0, 1)));
				entityOrientation_phi = (float) (Math.acos(DRR.cell(2, 2) / Math.cos(entityOrientation_theta))
						* Math.signum(DRR.cell(1, 2)));

				if (Double.isNaN(entityOrientation_psi))
					entityOrientation_psi = 0;
				if (Double.isNaN(entityOrientation_theta))
					entityOrientation_theta = 0;
				if (Double.isNaN(entityOrientation_phi))
					entityOrientation_phi = 0;
			} // while(true)
		} // try
		catch (final Exception e) {
			System.out.println(e);
		}
	}// run()--------------------------------------------------------------------

}