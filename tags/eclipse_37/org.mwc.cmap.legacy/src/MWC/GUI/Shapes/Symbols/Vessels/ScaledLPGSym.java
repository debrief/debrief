package MWC.GUI.Shapes.Symbols.Vessels;

import java.util.Vector;

import MWC.GenericData.WorldDistance;

public class ScaledLPGSym extends WorldScaledSym
{

	public ScaledLPGSym()
	{
		super(new WorldDistance(100, WorldDistance.METRES), new WorldDistance(13,
				WorldDistance.METRES));
	}

	protected double getWidthNormalFactor()
	{
		return 4.5;
	}

	protected double getLengthNormalFactor()
	{
		return -45;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Vector<double[][]> getCoords()
	{
		Vector<double[][]> hullLines = new Vector<double[][]>();

		double[][] hullForm;

		hullForm = new double[][]
		{
		{ 0, -15 },
		{ 1, -14.5 },
		{ 2, -13 },
		{ 2.5, -11 },
		{ 3, -9 },
		{ 3, 0 },
		{ 3, 22 },
		{ 2.5, 28 },
		{ 2.25, 30 },
		{ 0, 30 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -15 },
		{ -1, -14.5 },
		{ -2, -13 },
		{ -2.5, -11 },
		{ -3, -9 },
		{ -3, 0 },
		{ -3, 22 },
		{ -2.5, 28 },
		{ -2.25, 30 },
		{ 0, 30 } };
		hullLines.add(hullForm);

		// fwd line
		hullForm = new double[][]
		{
		{ 0, -10 },
		{ 2.5, -10 } };
		hullLines.add(hullForm);

		// cargo
		hullForm = new double[][]
		{
		{ 0, -8.5 },
		{ 1.25, -8.17 },
		{ 2.17, -7.25 },
		{ 2.5, -6 },
		{ 2.17, -4.75 },
		{ 1.25, -3.83 },
		{ 0, -3.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -3.5 },
		{ 1.25, -3.17 },
		{ 2.17, -2.25 },
		{ 2.5, -1 },
		{ 2.17, 0.25 },
		{ 1.25, 1.17 },
		{ 0, 1.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 1.5 },
		{ 1.25, 1.83 },
		{ 2.17, 2.75 },
		{ 2.5, 4 },
		{ 2.17, 5.25 },
		{ 1.25, 6.17 },
		{ 0, 6.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 6.5 },
		{ 1.25, 6.83 },
		{ 2.17, 7.75 },
		{ 2.5, 9 },
		{ 2.17, 10.25 },
		{ 1.25, 11.17 },
		{ 0, 11.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 11.5 },
		{ 1.25, 11.83 },
		{ 2.17, 12.75 },
		{ 2.5, 14 },
		{ 2.17, 15.25 },
		{ 1.25, 16.17 },
		{ 0, 16.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -9 },
		{ 0.5, -9 },
		{ 0.5, 17 },
		{ 0, 17 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 20 },
		{ 2.75, 20 },
		{ 3, 22 },
		{ 0, 22 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 2, 22 },
		{ 1.75, 29 },
		{ 0, 29 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 24 },
		{ 1.5, 24 },
		{ 1.25, 27 },
		{ 0, 27 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -10 },
		{ -2.5, -10 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -8.5 },
		{ -1.25, -8.17 },
		{ -2.17, -7.25 },
		{ -2.5, -6 },
		{ -2.17, -4.75 },
		{ -1.25, -3.83 },
		{ 0, -3.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -3.5 },
		{ -1.25, -3.17 },
		{ -2.17, -2.25 },
		{ -2.5, -1 },
		{ -2.17, 0.25 },
		{ -1.25, 1.17 },
		{ 0, 1.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 1.5 },
		{ -1.25, 1.83 },
		{ -2.17, 2.75 },
		{ -2.5, 4 },
		{ -2.17, 5.25 },
		{ -1.25, 6.17 },
		{ 0, 6.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 6.5 },
		{ -1.25, 6.83 },
		{ -2.17, 7.75 },
		{ -2.5, 9 },
		{ -2.17, 10.25 },
		{ -1.25, 11.17 },
		{ 0, 11.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 11.5 },
		{ -1.25, 11.83 },
		{ -2.17, 12.75 },
		{ -2.5, 14 },
		{ -2.17, 15.25 },
		{ -1.25, 16.17 },
		{ 0, 16.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -9 },
		{ -0.5, -9 },
		{ -0.5, 17 },
		{ 0, 17 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 20 },
		{ -2.75, 20 },
		{ -3, 22 },
		{ 0, 22 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ -2, 22 },
		{ -1.75, 29 },
		{ 0, 29 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 24 },
		{ -1.5, 24 },
		{ -1.25, 27 },
		{ 0, 27 } };
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
		return "ScaledLPG";
	}

}
