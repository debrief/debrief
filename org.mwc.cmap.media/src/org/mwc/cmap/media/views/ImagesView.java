package org.mwc.cmap.media.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.media.Activator;
import org.mwc.cmap.media.PlanetmayoFormats;
import org.mwc.cmap.media.PlanetmayoImages;
import org.mwc.cmap.media.dialog.ShowImageDialog;
import org.mwc.cmap.media.gallery.ImageGallery;
import org.mwc.cmap.media.gallery.ImageGalleryElementsBuilder;
import org.mwc.cmap.media.time.ITimeListener;
import org.mwc.cmap.media.views.images.ImageLoader;
import org.mwc.cmap.media.views.images.ImagePanel;
import org.mwc.cmap.media.views.images.ThumbnailPackage;

import MWC.GenericData.HiResDate;

public class ImagesView extends ViewPart {
	private static final String STATE_FULLSIZE = "fullSize";
	private static final String STATE_FOLDER = "folder";
	private static final String STATE_SELECTED_IMAGE = "selectedImage";
	private static final String STATE_STRETCH = "stretch";	
	
	public static final String ID = "org.mwc.cmap.media.views.ImagesView";
	
	private static final ImageMetaDataComparator IMAGES_COMPARATOR = new ImageMetaDataComparator();
	
	private IMemento memento;
	
	private StackLayout modeLayout;
	private Composite main;
	private ImageGallery<ImageMetaData, ThumbnailPackage> gallery;
	private ImagePanel imagePanel;
	
	private String openedFolder;
	private boolean loadedGallery;
	
	private Action open;
	private Action refresh;
	private Action stretch;
	private Action viewFullsize;
	private Action viewThumbnails;
	
	private List<ImageMetaData> images;
	
	private ITimeListener timeListener;
	
	private ControllableTime _controllableTime;
	
	private TimeProvider _timeProvider;
	
	private PartMonitor _myPartMonitor;
	
	private PropertyChangeListener _propertyChangeListener = new PropertyChangeListener()
	{
		
		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			Object newValue = evt.getNewValue();
			if (newValue instanceof HiResDate)
			{
				HiResDate now = (HiResDate) newValue;
				if (now != null)
				{
					long millis = now.getMicros()/1000;
					selectImage(millis);
				}
			}
		}
	};
	private boolean _firingNewTime;
	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.memento = memento;
		timeListener = new ITimeListener() {
			
			@Override
			public void newTime(Object src, long millis) {
				if (src == ImagesView.this || images == null || images.isEmpty()) {
					return;
				}
				selectImage(millis);				
			}
		};		
	}
	
	@Override
	public void dispose() {
		Activator.getDefault().getTimeProvider().removeListener(timeListener);
		_myPartMonitor.dispose(getSite().getWorkbenchWindow().getPartService());
		gallery.dispose();
		super.dispose();
	}
	
	public void fireNewTime(final HiResDate dtg)
	{
		if (_controllableTime == null) {
			return;
		}
		if (!_firingNewTime)
		{
			_firingNewTime = true;
			try
			{
				_controllableTime.setTime(this, dtg, true);
			}
			finally
			{
				_firingNewTime = false;
			}
		}
	}
	
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putBoolean(STATE_FULLSIZE, isFullSize());
		memento.putBoolean(STATE_STRETCH, stretch.isChecked());
		if (openedFolder != null) {
			memento.putString(STATE_FOLDER, openedFolder);
			ImageMetaData meta = gallery.getSelectedImage();
			if (meta != null) {
				memento.putString(STATE_SELECTED_IMAGE, meta.getFileName());
			}
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		initDrop(parent);
		createMainWindow(parent);
		createActions();
		fillToolbarManager();
		fillMenu();
		restoreSavedState();
		Activator.getDefault().getTimeProvider().addListener(timeListener);
		// and start listing for any part action
		setupListeners();

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());
	}
	
	private void initDrop(Control control)
	{
		final DropTarget target = new DropTarget(control, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
		Transfer[] transfers = new Transfer[] {FileTransfer.getInstance()};
		target.setTransfer(transfers);
		target.addDropListener(new DropTargetAdapter()
		{
			@Override
			public void dragEnter(DropTargetEvent e)
			{
				if (e.detail == DND.DROP_NONE)
				{
					e.detail = DND.DROP_LINK;
				}
			}

			@Override
			public void dragOperationChanged(DropTargetEvent e)
			{
				if (e.detail == DND.DROP_NONE)
				{
					e.detail = DND.DROP_LINK;
				}
			}

			@Override
			public void drop(DropTargetEvent e)
			{
				if (e.data == null)
				{
					e.detail = DND.DROP_NONE;
					return;
				}
				if (e.data instanceof String[])
				{
					String[] fileNames = (String[]) e.data;
					if (fileNames.length > 0 && fileNames[0] != null
							&& !fileNames[0].isEmpty())
					{
						String fileName = fileNames[0];
						if (!fileName.equals(openedFolder)
								&& new File(fileName).isDirectory())
						{
							openFolder(fileName);
						}

					}
				}
			}
		});
	}

	private void createMainWindow(Composite parent) {
		main = parent;
		modeLayout = new StackLayout();
		main.setLayout(modeLayout);
		
		imagePanel = new ImagePanel(parent);
		
		gallery = new ImageGallery<ImageMetaData, ThumbnailPackage>(parent);
		gallery.setThumbnailSize(110, 110);
		gallery.setDefaultImage(PlanetmayoImages.UNKNOWN.getImage().createImage(gallery.getMainComposite().getDisplay()));
		gallery.setLabelBuilder(new ImageGalleryElementsBuilder<ImageMetaData, ThumbnailPackage>() {
			
			@Override
			public String buildLabel(ImageMetaData imageName) {				
				return new File(imageName.getFileName()).getName();
			}

			@Override
			public Image buildImage(ThumbnailPackage image) {
				if (stretch.isChecked()) {
					return image.getStretched();
				}
				return image.getScaled();
			}

			@Override
			public void disposeImage(ThumbnailPackage i) {
				i.dispose();				
			}

			@Override
			public void disposeMeta(ImageMetaData t) {
				
			}
		});
		gallery.addElementMouseListener(new MouseListener() {
			
			@Override
			@SuppressWarnings("unchecked")			
			public void mouseUp(MouseEvent event) {
				ImageGallery<ImageMetaData, ThumbnailPackage>.ImageLabel label = (ImageGallery<ImageMetaData, ThumbnailPackage>.ImageLabel) event.data;
				long timeToFire = label.getImageMeta().getDate().getTime();
				fireNewTime(new HiResDate(timeToFire));
				int index = images.indexOf(label.getImageMeta());
				Activator.getDefault().getTimeProvider().fireNewTime(ImagesView.this, timeToFire);
				selectImage(index);				
			}
			
			@Override
			public void mouseDown(MouseEvent event) {
				
			}
			
			@Override
			@SuppressWarnings("unchecked")
			public void mouseDoubleClick(MouseEvent event) {
				ImageGallery<ImageMetaData, ThumbnailPackage>.ImageLabel label = (ImageGallery<ImageMetaData, ThumbnailPackage>.ImageLabel) event.data;
				ShowImageDialog dialog = new ShowImageDialog(getSite().getShell(), label.getImageMeta().getFileName(), stretch.isChecked());
				dialog.show();
			}
		});
		modeLayout.topControl = gallery.getMainComposite();
	}
	
	private void createActions() {
		open = new Action() {

			@Override
			public void run() {
				DirectoryDialog dialog = new DirectoryDialog(getSite().getShell());
				if (openedFolder != null) {
					dialog.setFilterPath(openedFolder);
				}
		        dialog.setText("Open folder");
		        dialog.setMessage("Select a folder");
		        String folder = dialog.open();
		        if (folder != null) {
		        	openFolder(folder);		        	
		        }				
			}
		};
		open.setImageDescriptor(PlanetmayoImages.OPEN.getImage());
		open.setText("Open");
		open.setEnabled(true);	
		
		refresh = new Action() {

			@Override
			public void run() {
				if (openedFolder != null) {
					openFolder(openedFolder);
				}
			}
		};
		refresh.setImageDescriptor(PlanetmayoImages.REFRESH.getImage());
		refresh.setText("Refresh");
		refresh.setEnabled(false);
		
		viewFullsize = new Action() {

			@Override
			public void run() {
				setFullSize(true);
			}
		};
		viewFullsize.setEnabled(true);
		viewFullsize.setImageDescriptor(PlanetmayoImages.VIEW_FULLSIZE.getImage());
		viewFullsize.setText("Full size");
		
		viewThumbnails = new Action() {

			@Override
			public void run() {
				setFullSize(false);
			}
		};
		viewThumbnails.setEnabled(false);
		viewThumbnails.setImageDescriptor(PlanetmayoImages.VIEW_THUMBNAILS.getImage());
		viewThumbnails.setText("Thumbnails");		
		
		stretch = new Action("Stretch", Action.AS_CHECK_BOX) {

			@Override
			public void run() {
				ScrolledComposite mainComposite = gallery.getMainComposite();
				Point size = mainComposite.getSize();
				mainComposite.redraw(0, 0, size.x, size.y, true);
				imagePanel.setStretchMode(isChecked());
				size = imagePanel.getSize();
				imagePanel.redraw(0, 0, size.x, size.y, true);				
			}
		};
	}
	
	private void fillToolbarManager() {
		IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
		toolbar.add(stretch);
		toolbar.add(new Separator());
		toolbar.add(viewFullsize);
		toolbar.add(viewThumbnails);
		toolbar.add(new Separator());		
		toolbar.add(refresh);		
		toolbar.add(open);
	}
	
	private void fillMenu() {
		IMenuManager menu = getViewSite().getActionBars().getMenuManager();
		menu.add(stretch);
		menu.add(new Separator());
		menu.add(viewFullsize);
		menu.add(viewThumbnails);
		menu.add(new Separator());			
		menu.add(open);
		menu.add(refresh);
	}	
	
	public boolean openFolder(String folderName) {
		this.openedFolder = folderName;
		loadedGallery = false;
		gallery.removeAll();
		imagePanel.setCurrentImage(null, null, true);
		imagePanel.setNextImage(null, null);
		File folder = new File(openedFolder);
		if (! folder.exists() || ! folder.isDirectory()) {
			return false;
		}
		setPartName("Images: " + folder.getName());
		File[] childs = folder.listFiles();
		images = new ArrayList<ImagesView.ImageMetaData>();
		for (File child : childs) {
			if (! child.isFile()) {
				continue;
			}
			String childName = child.getName();
			if (! PlanetmayoFormats.getInstance().isSupportedImage(childName)) {
				continue;
			}
			Date date = PlanetmayoFormats.getInstance().parseDateFromFileName(childName);
			if (date != null) {
				images.add(new ImageMetaData(child.getAbsolutePath(), date));
			}
			childName = childName.substring(0, childName.lastIndexOf('.'));
		}
		
		Collections.sort(images, IMAGES_COMPARATOR);
		loadedGallery = !isFullSize();
		for (ImageMetaData image : images) {
			gallery.addImage(image, null);
			if (loadedGallery) {
				ImageLoader.getInstance().load(image.getFileName(), image, gallery);
			}
		}
		if (! images.isEmpty()) {
			imagePanel.setCurrentImage(images.get(0).getFileName(), null, false);
			ImageLoader.getInstance().load(imagePanel);
		}		
		refresh.setEnabled(true);
		return true;
	}
	
	private void selectImage(int index) {
		ImageMetaData current = images.get(index);		
		gallery.selectImage(current, true);
		imagePanel.setCurrentImage(current.getFileName(), null, false);
		if (index != images.size() - 1) {
			imagePanel.setNextImage(images.get(index + 1).getFileName(), null);
		}
		ImageLoader.getInstance().load(imagePanel);
	}
	
	private void setFullSize(boolean fullSize) {
		viewFullsize.setEnabled(! fullSize);
		viewThumbnails.setEnabled(fullSize);
		modeLayout.topControl = fullSize ? imagePanel : gallery.getMainComposite();
		if (! fullSize && ! loadedGallery && images != null) {
			loadedGallery = true;
			for (ImageMetaData image : images) {
				ImageLoader.getInstance().load(image.getFileName(), image, gallery);
			}
		}
		main.layout();
	}
	
	private boolean isFullSize() {
		return modeLayout.topControl == imagePanel; 
	}
	
	@Override
	public void setFocus() {
		
	}
	
	public void restoreSavedState() {
		if (memento == null) {
			return;
		}
		if (memento.getBoolean(STATE_FULLSIZE) != null) {
			setFullSize(memento.getBoolean(STATE_FULLSIZE));
		}
		if (memento.getBoolean(STATE_STRETCH) != null) {
			imagePanel.setStretchMode(memento.getBoolean(STATE_STRETCH));
			stretch.setChecked(memento.getBoolean(STATE_STRETCH));
		}
		if (memento.getString(STATE_FOLDER) != null) {
			openFolder(memento.getString(STATE_FOLDER));
			// Issue #545 - we don't need select a image
//			if (memento.getString(STATE_SELECTED_IMAGE) != null) {
//				String selectedImage = memento.getString(STATE_SELECTED_IMAGE);
//				for (int i = 0; i < images.size(); i++) {
//					if (images.get(i).getFileName().equals(selectedImage)) {
//						selectImage(i);
//						break;
//					}
//				}
//			}
		}
	}
	
	static class ImageMetaData {
		
		private final String fileName;
		private final Date date;
		
		public ImageMetaData(String fileName, Date date) {			
			this.fileName = fileName;
			this.date = date;
			if (date == null) {
				throw new IllegalArgumentException("date can't be null");
			}
		}

		@Override
		public int hashCode() {
			return fileName != null ? fileName.hashCode() : 0;
		}

		@Override
		public boolean equals(Object obj) {
			if (! (obj instanceof ImageMetaData)) {
				return false;
			}
			String anotherFileName = ((ImageMetaData) obj).getFileName();
			if (anotherFileName == fileName) {
				return true;
			}
			if (fileName == null) {
				return false;
			}
			return fileName.equals(anotherFileName);
		}

		public long distance(ImageMetaData o) {
			long time = date.getTime();
			long anotherTime = o.date.getTime();
			return Math.abs(time - anotherTime);
		}		

		public String getFileName() {
			return fileName;
		}

		public Date getDate() {
			return date;
		}
	}
	
	static class ImageMetaDataComparator implements Comparator<ImageMetaData> {

		@Override
		public int compare(ImageMetaData o1, ImageMetaData o2) {
			long time1 = o1.getDate().getTime();
			long time2 = o2.getDate().getTime();
			if (time1 < time2) {
				return -1;
			} else if (time1 == time2) {
				return 0;				
			} else {
				return 1;
			}
		}
	}

	public String getOpenedFolder()
	{
		return this.openedFolder;
	}

	/**
	 * Select nearest image from the images list based on date in image name
	 * 
	 * @param millis
	 */
	private void selectImage(long millis)
	{
		ImageMetaData toSelect = new ImageMetaData(null, new Date(millis));
		int result = Collections.binarySearch(images, toSelect, IMAGES_COMPARATOR);
		if (result >= 0) {
			selectImage(result);
			return;
		}
		result = (- (result + 1)) + 1;
		long nearest = Long.MAX_VALUE;
		int nearestImage = -1;
		for (int i = result - 2; i <= result; i++) {
			if (i >= 0 && i < images.size()) {
				long distance = toSelect.distance(images.get(i));
				if (distance < nearest) {
					nearest = distance;
					nearestImage = i;
				}
			}					
		}
		selectImage(nearestImage);
	}
	
	private void setupListeners()
	{
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());

		_myPartMonitor.addPartListener(ControllableTime.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{

						if (_controllableTime != part)
						{
							// implementation here.
							final ControllableTime ct = (ControllableTime) part;
							_controllableTime = ct;
						}
					}

				});
		_myPartMonitor.addPartListener(ControllableTime.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						if (part == _controllableTime)
						{
							_controllableTime = null;
						}
					}
				});

		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{

						if (_timeProvider != part)
						{
							// implementation here.
							final TimeProvider tp = (TimeProvider) part;
							_timeProvider = tp;
							_timeProvider.addListener(_propertyChangeListener,
									TimeProvider.TIME_CHANGED_PROPERTY_NAME);
						}
					}

				});
		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						if (part == _timeProvider)
						{
							_timeProvider.removeListener(_propertyChangeListener,
									TimeProvider.TIME_CHANGED_PROPERTY_NAME);
							_timeProvider = null;
						}
					}
				});
	}

}