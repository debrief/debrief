package MWC.GUI.Shapes.Symbols.Vessels;

import java.util.Vector;

public class ScaledVesselSym extends ScaledSym
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Vector<double[][]> getCoords()
	{
		Vector<double[][]> hullLines = new Vector<double[][]>();

		double[][] hullForm;

		// ///////////////////
		hullForm = new double[][]
		{
		{ 0.00, 43.85 },
		{ 2.92, 35.08 },
		{ 5.85, 8.77 },
		{ 6.58, -14.62 },
		{ 6.58, -29.23 },
		{ 6.58, -43.85 },
		{ 5.85, -70.15 },
		{ 5.12, -84.77 },
		{ 3.65, -89.15 },
		{ 0.00, -89.15 } };
		hullLines.add(hullForm);
		

		hullForm = new double[][]
		{
		{ 0.00, 43.85 },
		{ -2.92, 35.08 },
		{ -5.85, 8.77 },
		{ -6.58, -14.62 },
		{ -6.58, -29.23 },
		{ -6.58, -43.85 },
		{ -5.85, -70.15 },
		{ -5.12, -84.77 },
		{ -3.65, -89.15 },
		{ 0.00, -89.15 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0.00, 35.08 },
		{ 2.92, 32.15 },
		{ 0.00, 43.85 },
		{ 0.00, 20.46 },
		{ 2.92, 20.46 },
		{ 2.92, 11.69 },
		{ 0.00, 11.69 },
		{ 0.00, 43.85 },
		{ 0.00, 7.31 },
		{ 2.92, 7.31 },
		{ 5.85, 5.85 },
		{ 5.85, 2.92 },
		{ 2.92, 2.92 },
		{ 2.92, -4.38 },
		{ 4.38, -5.85 },
		{ 4.38, -8.77 },
		{ 2.92, -10.23 },
		{ 2.92, -11.69 },
		{ 4.38, -13.15 },
		{ 4.38, -17.54 },
		{ 0.00, -17.54 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0.00, -21.92 },
		{ 4.38, -21.92 },
		{ 4.38, -36.54 },
		{ 2.92, -40.92 },
		{ 0.00, -40.92 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0.00, -43.85 },
		{ 4.38, -43.85 },
		{ 4.38, -55.54 },
		{ 2.92, -67.23 },
		{ 0.00, -67.23 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0.00, 35.08 },
		{ -2.92, 32.15 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0.00, 20.46 },
		{ -2.92, 20.46 },
		{ -2.92, 11.69 },
		{ 0.00, 11.69 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0.00, 7.31 },
		{ -2.92, 7.31 },
		{ -5.85, 5.85 },
		{ -5.85, 2.92 },
		{ -2.92, 2.92 },
		{ -2.92, -4.38 },
		{ -4.38, -5.85 },
		{ -4.38, -8.77 },
		{ -2.92, -10.23 },
		{ -2.92, -11.69 },
		{ -4.38, -13.15 },
		{ -4.38, -17.54 },
		{ 0.00, -17.54 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0.00, -21.92 },
		{ -4.38, -21.92 },
		{ -4.38, -36.54 },
		{ -2.92, -40.92 },
		{ 0.00, -40.92 },
		{ 0.00, 43.85 },
		{ 0.00, -43.85 },
		{ -4.38, -43.85 },
		{ -4.38, -55.54 },
		{ -2.92, -67.23 },
		{ 0.00, -67.23 } };
		hullLines.add(hullForm);
	
		return hullLines;

	}

	/**
	 * getType
	 * 
	 * @return the returned String
	 */
	public String getType()
	{
		return "ScaledVessel";
	}

}
