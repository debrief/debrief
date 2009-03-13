package com.borlander.rac525791.dashboard;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.geometry.Point;

import com.borlander.rac525791.dashboard.layout.BaseDashboardLayout;
import com.borlander.rac525791.dashboard.layout.ControlUIModel;
import com.borlander.rac525791.dashboard.layout.ControlUISuite;
import com.borlander.rac525791.dashboard.layout.DashboardUIModel;
import com.borlander.rac525791.dashboard.rotatable.AngleMapper;
import com.borlander.rac525791.dashboard.rotatable.RedSector;
import com.borlander.rac525791.draw2d.ext.RotatableDecorationExt;

/**
 * It is not a layer because it just creates and manages figures that are
 * children of another layer.
 * 
 * Semantic grouping (red-sector, demanded arrow, actual arrow) differs from
 * figure-containment grouping because we have 3 separate groups for each of the
 * controls but only single numbers layer and we need to paint numbers above all
 * of 3 red sectors but below all arrows.
 */
public class ControlPointersLayer {
	private final LayoutManager myLayouter;

	final RotatableDecorationExt myArrow;

	final RotatableDecorationExt myDemandedPointer;

	final AngleMapper myMapper;

	final RedSector myRedSector;

	int myActualValue;

	int myDemandedValue;

	public static interface Factory {
		public RotatableDecorationExt createActualArrow();

		public RotatableDecorationExt createDemandedArrow();

		public AngleMapper createAngleMapper();

		public RedSector createRedSector(AngleMapper mapper);
	}

	public ControlPointersLayer(Factory factory, DashboardUIModel uiModel, ControlUISuite.ControlAccess control) {
		myLayouter = new Layout(uiModel, control);
		myMapper = factory.createAngleMapper();
		myArrow = factory.createActualArrow();
		myRedSector = factory.createRedSector(myMapper);
		myDemandedPointer = factory.createDemandedArrow();
	}

	/**
	 * Intentionally returned as IFigure -- all that caller can do with it is to
	 * add this figure to some container.
	 */
	public IFigure getRedSector() {
		return myRedSector;
	}

	/**
	 * Intentionally just IFigure.
	 */
	public IFigure getActualArrow() {
		return myArrow;
	}

	/**
	 * Intentionally just IFigure.
	 */
	public IFigure getDemandedPointer() {
		return myDemandedPointer;
	}
	
	public void layoutGroup(IFigure container){
		myLayouter.layout(container);
	}

	public void updateShowRedSector(boolean isOnThreshold) {
		myRedSector.setVisible(!isOnThreshold);
	}

	public void setActualValue(int value) {
		if (myActualValue != value) {
			myActualValue = value;
			myArrow.setRotation(computeAngle(myActualValue));
			myRedSector.setActualValue(myActualValue);
		}
	}

	public void setDemandedValue(int value) {
		if (myDemandedValue != value) {
			myDemandedValue = value;
			myDemandedPointer.setRotation(computeAngle(myDemandedValue));
			myRedSector.setDemandedValue(myDemandedValue);
		}
	}
	
	public void setIgnoreDemandedValue(boolean ignore){
		myDemandedPointer.setVisible(!ignore);
	}

	private double computeAngle(int value) {
		return myMapper.computeAngle(value);
	}

	private class Layout extends BaseDashboardLayout {
		private final ControlUISuite.ControlAccess myControl;
		private final Point TEMP = new Point();

		public Layout(DashboardUIModel uiModel, ControlUISuite.ControlAccess control) {
			super(uiModel);
			myControl = control;
		}
		
		public void layout(IFigure container) {
			assert container == ControlPointersLayer.this;
			//System.out.println("ControlPointersLayer.Layout.layout()");
			
			ControlUISuite suite = getSuite(container);
			ControlUIModel positions = myControl.selectControl(suite);
			double templatesScale = suite.getTemplatesScale();

			placeAtTopLeft(container, TEMP);
			TEMP.translate(positions.getControlCenter());
			myArrow.setLocation(TEMP);
			myArrow.setScale(templatesScale, templatesScale);
			
			myDemandedPointer.setLocation(TEMP);
			myDemandedPointer.setScale(templatesScale, templatesScale);
			
			myRedSector.setCenterLocation(TEMP);
			myRedSector.setRadius(positions.getRedSectorRadius());

			if (!positions.isFullCircleMapped()) {
				myMapper.setAnglesRange(positions.getZeroMark(), positions
						.getMaximumMark());
				// angles may change - reset pointers rotation
				myArrow.setRotation(myMapper.computeAngle(myActualValue));
				myDemandedPointer.setRotation(myMapper
						.computeAngle(myDemandedValue));
			}
		}
		
	}

}
