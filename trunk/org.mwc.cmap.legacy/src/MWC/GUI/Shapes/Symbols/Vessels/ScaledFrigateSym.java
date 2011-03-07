package MWC.GUI.Shapes.Symbols.Vessels;

import java.util.Vector;

import MWC.GenericData.WorldDistance;

public class ScaledFrigateSym extends WorldScaledSym
{

	public ScaledFrigateSym()
	{
		super(new WorldDistance(100, WorldDistance.METRES), new WorldDistance(13,
				WorldDistance.METRES));
	}

	protected double getWidthNormalFactor()
	{
		return 13.4;
	}

	protected double getLengthNormalFactor()
	{
		return -133;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Vector<double[][]> getCoords()
	{
		Vector<double[][]> hullLines = new Vector<double[][]>();

		double[][] hullForm;

		// ///////////////////////
		hullForm = new double[][]
		{
		{ 0, -15 },
		{ 1, -12 },
		{ 2, -3 },
		{ 2.25, 5 },
		{ 2.25, 10 },
		{ 2.25, 15 },
		{ 2, 24 },
		{ 1.75, 29 },
		{ 1.25, 30.5 },
		{ 0, 30.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -15 },
		{ -1, -12 },
		{ -2, -3 },
		{ -2.25, 5 },
		{ -2.25, 10 },
		{ -2.25, 15 },
		{ -2, 24 },
		{ -1.75, 29 },
		{ -1.25, 30.5 },
		{ 0, 30.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -12 },
		{ 1, -11 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -6.75 },
		{ 0.75, -6.75 },
		{ 1, -4.25 },
		{ 0, -4.25 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -7 },
		{ 1, -7 },
		{ 1.25, -4 },
		{ 0, -4 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -2.5 },
		{ 1, -2.5 },
		{ 2, -2 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 2, -1 },
		{ 1.5, -1 },
		{ 1.5, 6 },
		{ 0, 6 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -1 },
		{ 1, -1 },
		{ 1, 3 },
		{ 0.75, 3.5 },
		{ 0, 3.75 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 1.25, 3.5 },
		{ 0, 5 },
		{ 0, 5.25 },
		{ 1.25, 3.75 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 1.25, 6.5 },
		{ 0, 5 },
		{ 0, 5.25 },
		{ 1.25, 6.75 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 7.5 },
		{ 1.5, 7.5 },
		{ 1.5, 12.5 },
		{ 1, 14 },
		{ 0, 14 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 9 },
		{ 1, 9 },
		{ 1, 11 },
		{ 0, 11 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0.5, 11.5 },
		{ 1, 11.5 },
		{ 1, 12.5 },
		{ 0.5, 13 },
		{ 0.5, 11.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 15 },
		{ 1.5, 15 },
		{ 1.5, 19 },
		{ 1, 23 },
		{ 0, 23 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 16 },
		{ 1.5, 16 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 28 },
		{ 1.75, 28 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 28 },
		{ 0, 30 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -12 },
		{ -1, -11 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -6.75 },
		{ -0.75, -6.75 },
		{ -1, -4.25 },
		{ 0, -4.25 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -7 },
		{ -1, -7 },
		{ -1.25, -4 },
		{ 0, -4 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -2.5 },
		{ -1, -2.5 },
		{ -2, -2 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ -2, -1 },
		{ -1.5, -1 },
		{ -1.5, 6 },
		{ 0, 6 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -1 },
		{ -1, -1 },
		{ -1, 3 },
		{ -0.75, 3.5 },
		{ 0, 3.75 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ -1.25, 3.5 },
		{ 0, 5 },
		{ 0, 5.25 },
		{ -1.25, 3.75 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ -1.25, 6.5 },
		{ 0, 5 },
		{ 0, 5.25 },
		{ -1.25, 6.75 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 7.5 },
		{ -1.5, 7.5 },
		{ -1.5, 12.5 },
		{ -1, 14 },
		{ 0, 14 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 9 },
		{ -1, 9 },
		{ -1, 11 },
		{ 0, 11 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ -0.5, 11.5 },
		{ -1, 11.5 },
		{ -1, 12.5 },
		{ -0.5, 13 },
		{ -0.5, 11.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 15 },
		{ -1.5, 15 },
		{ -1.5, 19 },
		{ -1, 23 },
		{ 0, 23 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 16 },
		{ -1.5, 16 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 28 },
		{ -1.75, 28 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 28 },
		{ 0, 30 } };
		hullLines.add(hullForm);

		// done
		return hullLines;

	}

	/**
	 * getType
	 * 
	 * @return the returned String
	 */
	public String getType()
	{
		return "ScaledFrigate";
	}

}
