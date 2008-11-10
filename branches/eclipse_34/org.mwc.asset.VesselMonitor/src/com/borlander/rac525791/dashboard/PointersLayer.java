package com.borlander.rac525791.dashboard;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import com.borlander.rac525791.dashboard.layout.BaseDashboardLayout;
import com.borlander.rac525791.dashboard.layout.ControlUISuite;
import com.borlander.rac525791.dashboard.layout.DashboardImages;
import com.borlander.rac525791.dashboard.layout.DashboardUIModel;
import com.borlander.rac525791.dashboard.layout.SelectableImageFigure;
import com.borlander.rac525791.dashboard.rotatable.AngleMapper;
import com.borlander.rac525791.dashboard.rotatable.DirectionArrow;
import com.borlander.rac525791.dashboard.rotatable.DirectionDemandedArrow;
import com.borlander.rac525791.dashboard.rotatable.FullCircleAngleMapper;
import com.borlander.rac525791.dashboard.rotatable.RedSector;
import com.borlander.rac525791.dashboard.rotatable.SpeedDepthArrow;
import com.borlander.rac525791.dashboard.rotatable.SpeedDepthDemandedValueArrow;
import com.borlander.rac525791.draw2d.ext.InvisibleRectangle;
import com.borlander.rac525791.draw2d.ext.RotatableDecorationExt;

/**
 * Intended to correct z-order of controls components. We need to draw them in
 * following order:
 * 
 * <code>
 * 1. Control background
 * 2. Red Sector (if any)
 * 3. White numbers 
 * 4. Demanded decoration
 * 5. Actual arrows
 * 6. Lids
 * </code>
 */
public class PointersLayer extends InvisibleRectangle {
	private ScaledControlPointersLayer mySpeedArrows;

	private ScaledControlPointersLayer myDepthArrows;

	private ControlPointersLayer myDirectionArrows;

	private ImageFigure myCircles;

	private ImageFigure myNumbers;

	public PointersLayer(DashboardUIModel uiModel) {
		mySpeedArrows = new ScaledControlPointersLayer(SPEED_DEPTH_FACTORY, uiModel, ControlUISuite.SPEED);
		myDepthArrows = new ScaledControlPointersLayer(SPEED_DEPTH_FACTORY, uiModel, ControlUISuite.DEPTH);
		myDirectionArrows = new ControlPointersLayer(DIRECTION_FACTORY, uiModel, ControlUISuite.DIRECTION);

		myNumbers = new SelectableImageFigure(uiModel){
			@Override
			protected Image selectImage(DashboardImages images) {
				return images.getNumbers();
			}
		};
		myCircles = new SelectableImageFigure(uiModel){
			@Override
			protected Image selectImage(DashboardImages images) {
				return images.getCircleLids();
			}
		};

		// implicit z-order is here:
		// 1. (is managed by DashboardFigure)
		// 2. red sectors
		this.add(mySpeedArrows.getRedSector());
		this.add(myDepthArrows.getRedSector());
		this.add(myDirectionArrows.getRedSector());
		// 3. White numbers
		this.add(myNumbers);
		// 4. Demanded decorations
		this.add(mySpeedArrows.getDemandedPointer());
		this.add(myDepthArrows.getDemandedPointer());
		this.add(myDirectionArrows.getDemandedPointer());
		// 5. Actual arrows
		this.add(mySpeedArrows.getActualArrow());
		this.add(myDepthArrows.getActualArrow());
		this.add(myDirectionArrows.getActualArrow());
		// 6. Lids
		this.add(myCircles);

		setLayoutManager(new Layout(uiModel));
	}

	public ScaledControlPointersLayer getSpeedArrows() {
		return mySpeedArrows;
	}

	public ScaledControlPointersLayer getDepthArrows() {
		return myDepthArrows;
	}

	public ControlPointersLayer getDirectionArrows() {
		return myDirectionArrows;
	}

	private class Layout extends BaseDashboardLayout {
		private final Rectangle RECT = new Rectangle();

		public Layout(DashboardUIModel uiModel) {
			super(uiModel);
		}

		@Override
		public void layout(IFigure container) {
			// each control pointers group can layout itself
			mySpeedArrows.layoutGroup(container);
			myDepthArrows.layoutGroup(container);
			myDirectionArrows.layoutGroup(container);

			RECT.setSize(getSuite(container).getPreferredSizeRO());
			placeAtTopLeft(container, RECT);

			myCircles.setBounds(RECT);
			myNumbers.setBounds(RECT);
		}
	}

	private static final ControlPointersLayer.Factory SPEED_DEPTH_FACTORY = new ControlPointersLayer.Factory() {
		public RedSector createRedSector(AngleMapper mapper) {
			return new RedSector(mapper, false);
		}

		public RotatableDecorationExt createDemandedArrow() {
			return new SpeedDepthDemandedValueArrow();
		}

		public RotatableDecorationExt createActualArrow() {
			return new SpeedDepthArrow();
		}

		public AngleMapper createAngleMapper() {
			return new ArcAngleMapper(0, 1000, true);
		}
	};

	private static final ControlPointersLayer.Factory DIRECTION_FACTORY = new ControlPointersLayer.Factory() {
		public RedSector createRedSector(AngleMapper mapper) {
			return new RedSector(mapper, true);
		}

		public RotatableDecorationExt createDemandedArrow() {
			return new DirectionDemandedArrow();
		}

		public RotatableDecorationExt createActualArrow() {
			return new DirectionArrow();
		}

		public AngleMapper createAngleMapper() {
			return new FullCircleAngleMapper(-Math.PI / 2, 360);
		}
	};

}
