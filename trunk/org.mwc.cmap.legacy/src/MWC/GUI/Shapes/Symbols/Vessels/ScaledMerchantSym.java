package MWC.GUI.Shapes.Symbols.Vessels;

import java.util.Vector;

import MWC.GenericData.WorldDistance;

public class ScaledMerchantSym extends WorldScaledSym
{

	public ScaledMerchantSym()
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
		{ 0, -10 },
		{ 2.5, -10 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -7 },
		{ 2, -7 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -3 },
		{ 2, -3 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 1 },
		{ 2, 1 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 5 },
		{ 2, 5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 9 },
		{ 2, 9 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 13 },
		{ 2, 13 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 17 },
		{ 2, 17 } };
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
		{ 0, -7 },
		{ -2, -7 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -3 },
		{ -2, -3 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 1 },
		{ -2, 1 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 5 },
		{ -2, 5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 9 },
		{ -2, 9 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 13 },
		{ -2, 13 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 17 },
		{ -2, 17 } };
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
