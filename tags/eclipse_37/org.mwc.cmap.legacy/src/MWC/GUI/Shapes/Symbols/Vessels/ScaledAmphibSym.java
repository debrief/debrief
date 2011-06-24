package MWC.GUI.Shapes.Symbols.Vessels;

import java.util.Vector;

import MWC.GenericData.WorldDistance;

public class ScaledAmphibSym extends WorldScaledSym
{

	public ScaledAmphibSym()
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

		hullForm = new double[][]
		{
		{ 0, -11.5 },
		{ 1, -11.5 },
		{ 1, -9.5 },
		{ 0, -9.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -8.5 },
		{ 2, -8.5 },
		{ 2.5, -8 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -5 },
		{ 2.75, -5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 2, -5 },
		{ 2, 13 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 13 },
		{ 2.5, 13 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -4 },
		{ 1, -4 },
		{ 1, -1 },
		{ 0, -1 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -3.5 },
		{ 0.75, -3.5 },
		{ 0.75, -1.5 },
		{ 0, -1.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 13 },
		{ 3, 13 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 13.5 },
		{ 3, 13.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 2.5, 2 },
		{ 3.5, 2 },
		{ 3.5, 5 },
		{ 2.5, 5 },
		{ 2.5, 2 },
		{ 2.5, 5 },
		{ 2.5, 6 },
		{ 3.5, 6 },
		{ 3.5, 5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 2.5, 7 },
		{ 3.5, 7 },
		{ 3.5, 10 },
		{ 2.5, 10 },
		{ 2.5, 7 },
		{ 2.5, 7 },
		{ 2.5, 11 },
		{ 3.5, 11 },
		{ 3.5, 10 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0.75, 10 },
		{ 1.5, 10 },
		{ 1.5, 12 },
		{ 0.75, 12 },
		{ 0.75, 10 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 2.5, 27 },
		{ 0, 27 },
		{ 0, 29 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -11.5 },
		{ -1, -11.5 },
		{ -1, -9.5 },
		{ 0, -9.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -8.5 },
		{ -2, -8.5 },
		{ -2.5, -8 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -5 },
		{ -2.75, -5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ -2, -5 },
		{ -2, 13 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 13 },
		{ -2.5, 13 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -4 },
		{ -1, -4 },
		{ -1, -1 },
		{ 0, -1 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -3.5 },
		{ -0.75, -3.5 },
		{ -0.75, -1.5 },
		{ 0, -1.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 13 },
		{ -3, 13 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 13.5 },
		{ -3, 13.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ -2.5, 2 },
		{ -3.5, 2 },
		{ -3.5, 5 },
		{ -2.5, 5 },
		{ -2.5, 2 },
		{ -2.5, 5 },
		{ -2.5, 6 },
		{ -3.5, 6 },
		{ -3.5, 5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ -2.5, 7 },
		{ -3.5, 7 },
		{ -3.5, 10 },
		{ -2.5, 10 },
		{ -2.5, 7 },
		{ -2.5, 7 },
		{ -2.5, 11 },
		{ -3.5, 11 },
		{ -3.5, 10 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ -0.75, 10 },
		{ -1.5, 10 },
		{ -1.5, 12 },
		{ -0.75, 12 },
		{ -0.75, 10 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ -2.5, 27 },
		{ 0, 27 },
		{ 0, 29 } };
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
