package edu.nps.moves.deadreckoning;

import edu.nps.moves.deadreckoning.utils.Matrix;

/**
 *
 * (SECONDARY Methods Group) Fixed, rate of position, body coordinates || Linear
 * motion without rotation
 * <p>
 * it is coded up, but the linear motion does not seem to work....
 * <p>
 * The alogrithm is coded IAW IEEE 1278.1-1995 so perhaps it is a coordinate
 * change of basis issue and since I am not working in both world and body
 * coordinates, it fails or limits to 0
 *
 * @author Sheldon L. Snyder
 */
public class DIS_DR_FPB_06 extends DIS_DeadReckoning {
	Matrix initInv;

	double[] velVec = { entityLinearVelocity_X, entityLinearVelocity_Y, entityLinearVelocity_Z };
	double[] updated;

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

	/**
	 * The driver for a DIS_DR_FPB_06 DR algorithm from the Runnable interface
	 * <p>
	 * linear motion only without rotation
	 */
	@Override
	public void run() {
		try {
			initInv = Matrix.transpose(initOrien);
			// initInv = Matrix.inversMat3x3(initOrien);

			while (true) {
				deltaCt++;
				Thread.sleep(stall);

				// solve for the new position
				updated = Matrix.multVec(initInv, makeR1());

				// set the new position...
				entityLocation_X += updated[0];
				entityLocation_Y += updated[1];
				entityLocation_Z += updated[2];

			} // while(true)
		} // try
		catch (final Exception e) {
			System.out.println(e);
		}
	}// run()--------------------------------------------------------------------

}