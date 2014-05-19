package com.borlander.rac525791.dashboard;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.borlander.rac525791.dashboard.layout.BaseDashboardLayout;
import com.borlander.rac525791.dashboard.layout.DashboardImages;
import com.borlander.rac525791.dashboard.layout.DashboardUIModel;
import com.borlander.rac525791.dashboard.layout.SelectableImageFigure;
import com.borlander.rac525791.draw2d.ext.InvisibleRectangle;

public class BackgroundLayer extends InvisibleRectangle {
	IFigure myBack;
	ImageFigure myControls;
	ImageFigure myTopText;
	
	public BackgroundLayer(DashboardUIModel uiModel){
		setLayoutManager(new Layout(uiModel));
		
		myBack = new DoubleGradientRectangle();
		myControls = new SelectableImageFigure(uiModel){
			
			protected Image selectImage(DashboardImages images) {
				return images.getControls();
			}
		};
		//XXX
		myTopText = new SelectableImageFigure(uiModel){
			protected Image selectImage(DashboardImages images) {
				return images.getControls();
			}
		};

		this.add(myBack);
		this.add(myControls);
//		this.add(myTopText);
	}
	
	protected static class DoubleGradientRectangle extends InvisibleRectangle {
		private static final Color TOP = new Color(null, 129, 129, 129);
		private static final Color BOTTOM = new Color(null, 88, 88, 88);
		private static final Color MIDDLE = new Color(null, 200, 200, 200);
		
		@Override
		protected void fillShape(Graphics g) {
			Rectangle b = getBounds();
			int smallHeight = b.height / 15;
			
			g.pushState();
			g.setForegroundColor(TOP);
			g.setBackgroundColor(MIDDLE);
			g.fillGradient(b.x, b.y, b.width, smallHeight, true);
			
			g.setForegroundColor(MIDDLE);
			g.setBackgroundColor(BOTTOM);
			
			g.fillGradient(b.x, b.y + smallHeight, b.width, b.height - smallHeight, true);
			
			g.restoreState();
		}
	}
	
	private class Layout extends BaseDashboardLayout {
		private final Rectangle RECT = new Rectangle();
		
		public Layout(DashboardUIModel uiModel){
			super(uiModel);
		}
		
		public void layout(IFigure container) {
			placeAtTopLeft(container, RECT);
			RECT.setSize(getSuite(container).getPreferredSize());
			
			myControls.setBounds(RECT);
			myTopText.setBounds(RECT);

			myBack.setBounds(container.getBounds());
		}
	}
}
