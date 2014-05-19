package com.borlander.rac525791.dashboard.layout.data;

import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.mwc.asset.vesselmonitor.Activator;

import com.borlander.rac525791.dashboard.layout.DashboardImages;

/**
 * The very simple implementation similar to
 * org.eclipse.jface.resource.ImageRegistry class.
 * 
 * (Written to avoid dependency on jface.resource)
 */
class DashboardImagesImpl implements DashboardImages {
	private static final String CONTROLS = "controls.png";

	private static final String CIRCLES = "circles.png";

	private static final String NUMBERS = "text.png";

	private final String myControlsPath;

	private final String myCirclesPath;

	private final String myNumbersPath;

	private final Display myDisplay;

	private final HashMap<String, Image> myImages = new HashMap<String, Image>();

	public DashboardImagesImpl(Display display, String folder, boolean preload) {
		myControlsPath = folder + "/" + CONTROLS;
		myCirclesPath = folder + "/" + CIRCLES;
		myNumbersPath = folder + "/" + NUMBERS;

		myDisplay = display;
		myDisplay.disposeExec(new Runnable() {
			public void run() {
				DashboardImagesImpl.this.dispose();
			}
		});

		if (preload) {
			getNumbers();
			getCircleLids();
			getControls();
		}
	}

	public void dispose() {
		for (Image next : myImages.values()) {
			if (next != null && !next.isDisposed()) {
				next.dispose();
			}
		}
		myImages.clear();
	}

	public Image getNumbers() {
		return getImage(myNumbersPath);
	}

	public Image getCircleLids() {
		return getImage(myCirclesPath);
	}

	public Image getControls() {
		return getImage(myControlsPath);
	}

	private Image getImage(String filePath) {
		Image result = myImages.get(filePath);
		if (result == null || result.isDisposed()) {
			// XXX: Strange, but it does not always work for png images
			// Issues with alpha channel were found
			// result = new Image(myDisplay, filePath);

			ImageDescriptor desc = Activator.getImageDescriptor(filePath);
//			ImageLoader loader = new ImageLoader();
//			loader.load(filePath);
//			result = new Image(myDisplay, loader.data[0]);
			result = desc.createImage();
			myImages.put(filePath, result);
		}
		return result;
	}
}
