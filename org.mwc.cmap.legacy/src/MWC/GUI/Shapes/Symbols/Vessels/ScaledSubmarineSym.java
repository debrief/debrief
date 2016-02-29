/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GUI.Shapes.Symbols.Vessels;

import java.util.Vector;

import MWC.GenericData.WorldDistance;

public class ScaledSubmarineSym extends WorldScaledSym
{

	public ScaledSubmarineSym()
	{
		super(new WorldDistance(100, WorldDistance.METRES),
				new WorldDistance(20, WorldDistance.METRES));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Vector<double[][]> getCoords()
	{
		final Vector<double[][]> hullLines = new Vector<double[][]>();

		double[][] hullForm;

		hullForm = new double[][]{
		{	0.00	,	41.80	},
		{	2.39	,	40.61	},
		{	4.78	,	35.83	},
		{	5.98	,	28.65	},
		{	6.58	,	23.87	},
		{	6.58	,	-35.91	},
		{	5.98	,	-40.70	},
		{	4.78	,	-50.26	},
		{	3.59	,	-57.04	},
		{	1.89	,	-61.02	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	6.58	,	23.87	},
		{	9.57	,	23.87	},
		{	10.16	,	22.67	},
		{	9.57	,	20.88	},
		{	6.58	,	20.88	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	3.59	,	-57.04	},
		{	7.17	,	-58.63	},
		{	7.17	,	-61.62	},
		{	2.39	,	-61.62	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	0.00	,	4.14	},
		{	1.79	,	2.35	},
		{	2.39	,	-0.04	},
		{	2.99	,	-2.43	},
		{	2.39	,	-7.22	},
		{	1.79	,	-12.00	},
		{	1.20	,	-14.39	},
		{	0.00	,	-20.37	}};
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	0.00	,	-54.45	},
		{	1.20	,	-55.04	},
		{	1.79	,	-68.63	},
		{	1.79	,	-60.02	},
		{	1.20	,	-63.41	},
		{	0.00	,	-67.00	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	3.59	,	28.65	},
		{	5.38	,	28.65	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	2.39	,	28.65	},
		{	2.39	,	4.74	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	0.60	,	34.63	},
		{	0.60	,	32.24	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	0.00	,	1.15	},
		{	1.20	,	1.15	},
		{	1.79	,	-1.24	},
		{	0.00	,	-1.24	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	2.39	,	-14.39	},
		{	2.39	,	-40.70	},
		{	1.79	,	-50.26	},
		{	1.20	,	-53.85	}};
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	4.78	,	20.28	},
		{	4.78	,	17.89	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	4.78	,	8.33	},
		{	4.78	,	5.93	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	4.78	,	-27.54	},
		{	4.78	,	-29.93	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	1.20	,	10.72	},
		{	1.20	,	8.33	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	3.59	,	-23.96	},
		{	5.38	,	-23.96	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	0.00	,	43.00	},
		{	0.00	,	41.80	},
		{	-2.39	,	40.61	},
		{	-4.78	,	35.83	},
		{	-5.98	,	28.65	},
		{	-6.58	,	23.87	},
		{	-6.58	,	-35.91	},
		{	-5.98	,	-40.70	},
		{	-4.78	,	-50.26	},
		{	-3.59	,	-55.04	},
		{	-1.89	,	-61.02	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	-6.58	,	23.87	},
		{	-9.57	,	23.87	},
		{	-10.16	,	22.67	},
		{	-9.57	,	20.88	},
		{	-6.58	,	20.88	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	-3.59	,	-55.04	},
		{	-7.17	,	-58.63	},
		{	-7.17	,	-61.62	},
		{	-2.39	,	-61.62	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	0.00	,	4.14	},
		{	-1.79	,	2.35	},
		{	-2.39	,	-0.04	},
		{	-2.99	,	-2.43	},
		{	-2.39	,	-7.22	},
		{	-1.79	,	-12.00	},
		{	-1.20	,	-14.39	},
		{	0.00	,	-20.37	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	0.00	,	-54.45	},
		{	-1.20	,	-55.04	},
		{	-1.79	,	-58.63	},
		{	-1.79	,	-61.02	},
		{	-1.20	,	-63.41	},
		{	0.00	,	-67.00	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	-3.59	,	28.65	},
		{	-5.38	,	28.65	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	-2.39	,	28.65	},
		{	-2.39	,	4.74	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	-0.60	,	34.63	},
		{	-0.60	,	32.24	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	0.00	,	1.15	},
		{	-1.20	,	1.15	},
		{	-1.79	,	-1.24	},
		{	0.00	,	-1.24	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	-2.39	,	-14.39	},
		{	-2.39	,	-40.70	},
		{	-1.79	,	-50.26	},
		{	-1.20	,	-53.85	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	-4.78	,	20.28	},
		{	-4.78	,	17.89	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	-4.78	,	8.33	},
		{	-4.78	,	5.93	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	-4.78	,	-27.54	},
		{	-4.78	,	-29.93	} };
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	-1.20	,	10.72	} ,
		{	-1.20	,	8.33	}};
		hullLines.add(hullForm);
		hullForm = new double[][]{
		{	-3.59	,	-23.96	},
		{	-5.38	,	-23.96	} };
		hullLines.add(hullForm);
		
		////////////////////
		return hullLines;

	}

	/**
	 * getType
	 * 
	 * @return the returned String
	 */
	public String getType()
	{
		return "ScaledSubmarine";
	}

	@Override
	protected double getWidthNormalFactor()
	{
		return 20;
	}

	@Override
	protected double getLengthNormalFactor()
	{
		return 110;
	}

}
