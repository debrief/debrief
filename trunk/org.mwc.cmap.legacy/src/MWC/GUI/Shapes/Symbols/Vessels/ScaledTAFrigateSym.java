package MWC.GUI.Shapes.Symbols.Vessels;

import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GenericData.WorldLocation;

public class ScaledTAFrigateSym extends ScaledSym
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	
	@Override
	public void paint(CanvasType dest, WorldLocation theLocation, double direction)
	{
		// sort out the symbol scale - so our data is presented in screen units
		
		
		this.setScaleVal(100);
		
		
		super.paint(dest, theLocation, direction);
	}


	public ScaledTAFrigateSym()
	{
		super();
	}


	protected Vector<double[][]> getCoords()
	{
		Vector<double[][]> hullLines = new Vector<double[][]>();

		// start with the cross-over bits
		hullLines.add(new double[][]
		{
		{ -4, -2 }, { 4, 2 } });

		hullLines.add(new double[][]
		                       		{
		                       		{ -4, 2 }, { 4, -2 } });
		hullLines.add(new double[][]
			                       		{
			                       		{ -4, 2 }, { -4, -2 } });
		hullLines.add(new double[][]
			                       		{
			                       		{ 4, 2 }, { 4, -2 } });

		hullLines.add(new double[][]
			                       		{
			                       		{ -4, 2 }, { 4, -2 } });

		// now the dangly bits
		hullLines.add(new double[][]
			                       		{
			                       		{ -4, 2 }, { 0, 5 } });
		hullLines.add(new double[][]
			                       		{
			                       		{ 4, 2 }, { 0, 5 } });

		// and the tail
		hullLines.add(new double[][]
			                       		{
		{ 0,	5},
		{ -0.4,	5.3},
		{ -0.5,	6},
		{ -0.4,	6.7},
		{ 0,	7},
		{ 0.4,	7.3},
		{ 0.5,	8},
		{ 0.4,	8.7},
		{ 0,	9 } });
	
		return hullLines;

	}

	/**
	 * getType
	 * 
	 * @return the returned String
	 */
	public String getType()
	{
		return "TA Frigate";
	}

}
