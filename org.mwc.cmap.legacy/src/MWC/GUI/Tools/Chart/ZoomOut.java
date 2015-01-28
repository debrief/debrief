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
package MWC.GUI.Tools.Chart;


import java.awt.Component;
import java.awt.Dimension;

import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GUI.Canvas.MockCanvasType;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;


public class ZoomOut extends PlainTool
{
  
  /** my copy of the chart, so that I can tell it to update
   */
  protected PlainChart _theChart;
  
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  public ZoomOut(final ToolParent theParent, 
                 final PlainChart theChart){      
    super(theParent, "Zoom Out", "images/zoomout.gif");
    // remember the chart we are acting upon
    _theChart = theChart;
  }

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  public Action getData()
  {
    // get the current data area
    final WorldArea oldArea = _theChart.getCanvas().getProjection().getDataArea();
    return new ZoomOutAction(_theChart, oldArea, 2.0);
  }


  ///////////////////////////////////////////////////////
  // store action information
  ///////////////////////////////////////////////////////
  public static class ZoomOutAction implements Action{
    private final PlainChart _theChart;  
    private final WorldArea _oldArea;
    private final double _zoomFactor;

    public ZoomOutAction(final PlainChart theChart,
                          final WorldArea oldArea,
                         final double zoomFactor){
      _theChart = theChart;
      _oldArea = oldArea;
      _zoomFactor = zoomFactor;
    }

    public boolean isRedoable(){
      return true;
    }
    
    
    public boolean isUndoable(){
      return true;
    }
                 
    public String toString(){
      return "Zoom out";
    }                                        
    
    public void undo()      
    {
      // set the data area for the chart to the old area
      _theChart.getCanvas().getProjection().setDataArea(_oldArea);
      
      // get the projection to refit-itself
  //    _theChart.getCanvas().getProjection().zoom(0.0);
      
      // and redraw the whole plot
      _theChart.update();
    }
    
    public void execute(){
      // set the new zoom factor (which will trigger a resize operation
      _theChart.getCanvas().getProjection().zoom(_zoomFactor);
      
      // and trigger a redraw
      // no, don't bother - the chart catches the data-area change
   //   _theChart.update();
    }
  }
  
	public static class ZoomOutAreaAction implements Action {

		private PlainChart _theChart;
		private WorldArea _oldArea;
		private WorldArea _selectedArea;
		private double _zoomFactor;

		public ZoomOutAreaAction(final PlainChart theChart,
				final WorldArea oldArea, final WorldArea selectedArea,
				final double zoomFactor) {
			_theChart = theChart;
			_oldArea = oldArea;
			_selectedArea = selectedArea;
			_zoomFactor = zoomFactor;
		}

		@Override
		public boolean isUndoable() {
			return true;
		}

		@Override
		public boolean isRedoable() {
			return true;
		}

		@Override
		public void undo() {
			// set the data area for the chart to the old area
		     _theChart.getCanvas().getProjection().setDataArea(_oldArea);

		     // get the projection to refit-itself
		 //    _theChart.getCanvas().getProjection().zoom(0.0);
		}

		@Override
		public void execute() {
			 _theChart.getCanvas().getProjection().zoom(_zoomFactor, _selectedArea);

		    // get the projection to refit-itself
		 //   _theChart.getCanvas().getProjection().zoom(0.0);
		}

		public String toString() {
			return "Zoom out area operation";
		}


		static public class zoomOutAreaActionTest extends junit.framework.TestCase
		{
			PlainChart _chart;

			public void setUp()
			{
				_chart = new MockChart(null);
			}

			public void testUndoRestoresArea()
			{
				final PlainProjection proj = _chart.getCanvas().getProjection();

				final WorldLocation topLeft = new WorldLocation(0, 0, 0);
				final WorldLocation bottomRight = new WorldLocation(10, 10, 0);
				final WorldArea oldArea = new WorldArea(topLeft, bottomRight);

				final WorldLocation selTopLeft = new WorldLocation(1, 2, 0);
				final WorldLocation selBottomRight = new WorldLocation(5, 5, 0);
				final WorldArea selectedArea = new WorldArea(selTopLeft,
						selBottomRight);

				proj.setDataArea(oldArea);
				proj.setScreenArea(new Dimension(100, 100));
				final ZoomOutAreaAction action = new ZoomOutAreaAction(_chart,
						oldArea, selectedArea, 2);

				assertEquals(oldArea, proj.getDataArea());
				action.execute();
				assertNotSame(oldArea, proj.getDataArea());
				action.undo();
				assertEquals(oldArea, proj.getDataArea());
			}

			class MockCanvas extends MockCanvasType
			{
				PlainProjection _theProjection;

				public MockCanvas()
				{
					_theProjection = new FlatProjection();
				}

				@Override
				public PlainProjection getProjection()
				{
					return _theProjection;
				}
			}

			class MockChart extends PlainChart
			{
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				MockCanvasType _theCanvas;

				public MockChart(Layers theLayers)
				{
					super(theLayers);
					_theCanvas = new MockCanvas();
				}

				@Override
				public void rescale() {}

				@Override
				public void update() {}

				@Override
				public void update(Layer changedLayer) {}

				@Override
				public void repaint() {}

				@Override
				public void repaintNow(java.awt.Rectangle rect) {}

				@Override
				public Dimension getScreenSize()
				{
					return null;
				}

				@Override
				public CanvasType getCanvas()
				{
					return _theCanvas;
				}

				@Override
				public Component getPanel() {
					return null;
				}

			}

		}


	}
  
}
