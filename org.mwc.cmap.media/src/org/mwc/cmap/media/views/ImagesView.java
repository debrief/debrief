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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
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
import org.mwc.cmap.media.gallery.ImageGallery;
import org.mwc.cmap.media.gallery.ImageGalleryElementsBuilder;
import org.mwc.cmap.media.time.ITimeListener;
import org.mwc.cmap.media.views.images.ImageLoader;
import org.mwc.cmap.media.views.images.ImagePanel;
import org.mwc.cmap.media.views.images.ThumbnailPackage;

import MWC.GenericData.HiResDate;

public class ImagesView extends ViewPart
{
  static class ImageMetaData
  {
    private final String fileName;
    private final Date date;

    public ImageMetaData(final String fileName, final Date date)
    {
      this.fileName = fileName;
      this.date = date;
      if (date == null)
      {
        throw new IllegalArgumentException("date can't be null");
      }
    }

    public long distance(final ImageMetaData o)
    {
      final long time = date.getTime();
      final long anotherTime = o.date.getTime();
      return Math.abs(time - anotherTime);
    }

    @Override
    public boolean equals(final Object obj)
    {
      if (!(obj instanceof ImageMetaData))
      {
        return false;
      }
      final String anotherFileName = ((ImageMetaData) obj).getFileName();
      if (anotherFileName == fileName)
      {
        return true;
      }
      if (fileName == null)
      {
        return false;
      }
      return fileName.equals(anotherFileName);
    }

    public Date getDate()
    {
      return date;
    }

    public String getFileName()
    {
      return fileName;
    }

    @Override
    public int hashCode()
    {
      return fileName != null ? fileName.hashCode() : 0;
    }
  }

  static class ImageMetaDataComparator implements Comparator<ImageMetaData>
  {

    @Override
    public int compare(final ImageMetaData o1, final ImageMetaData o2)
    {
      final long time1 = o1.getDate().getTime();
      final long time2 = o2.getDate().getTime();
      if (time1 < time2)
      {
        return -1;
      }
      else if (time1 == time2)
      {
        return 0;
      }
      else
      {
        return 1;
      }
    }
  }

  private static final String STATE_STRETCH = "stretch";

  private static final String STATE_THUMBNAIL_WIDTH = "thumbnailWidth";

  private static final String STATE_THUMBNAIL_HEIGHT = "thumbnailHeight";

  public static final String ID = "org.mwc.cmap.media.views.ImagesView";
  private static final ImageMetaDataComparator IMAGES_COMPARATOR =
      new ImageMetaDataComparator();
  public static final int SMALL_ICON_WIDTH = 85;
  public static final int SMALL_ICON_HEIGHT = 45;
  public static final int MEDIUM_ICON_WIDTH = 135;
  public static final int MEDIUM_ICON_HEIGHT = 75;
  public static final int LARGE_ICON_WIDTH = 210;
  public static final int LARGE_ICON_HEIGHT = 140;
  private IMemento memento;
  private Composite main;

  private ImageGallery<ImageMetaData, ThumbnailPackage> gallery;

  private ImagePanel imagePanel;
  private String openedFolder;
  private Action open;
  private Action stretch;
  
  private Action smallIcons;
  private Action largeIcons;
  private Action mediumIcons;

  private List<ImageMetaData> images;

  private ITimeListener timeListener;

  private ControllableTime _controllableTime;

  private TimeProvider _timeProvider;
  private PartMonitor _myPartMonitor;

  private final PropertyChangeListener _propertyChangeListener =
      new PropertyChangeListener()
      {

        @Override
        public void propertyChange(final PropertyChangeEvent evt)
        {
          final Object newValue = evt.getNewValue();
          if (newValue instanceof HiResDate)
          {
            final HiResDate now = (HiResDate) newValue;
            if (now != null)
            {
              final long millis = now.getMicros() / 1000;
              selectImage(millis);
            }
          }
        }
      };

  private boolean _firingNewTime;

  private void createActions()
  {
    open = new Action()
    {

      @Override
      public void run()
      {
        final DirectoryDialog dialog = new DirectoryDialog(getSite()
            .getShell());
        if (openedFolder != null)
        {
          dialog.setFilterPath(openedFolder);
        }
        dialog.setText("Open folder");
        dialog.setMessage("Select a folder");
        final String folder = dialog.open();
        if (folder != null)
        {
          openFolder(folder);
        }
      }
    };
    open.setImageDescriptor(PlanetmayoImages.OPEN.getImage());
    open.setText("Open");
    open.setEnabled(true);

    // refresh = new Action() {
    //
    // @Override
    // public void run() {
    // if (openedFolder != null) {
    // openFolder(openedFolder);
    // }
    // }
    // };
    // refresh.setImageDescriptor(PlanetmayoImages.REFRESH.getImage());
    // refresh.setText("Refresh");
    // refresh.setEnabled(false);

    smallIcons = new Action()
    {
      @Override
      public void run()
      {
        setSmallThumbnails();
      };
    };
    smallIcons.setEnabled(true);
    smallIcons.setText("SmallIcons");
    smallIcons.setImageDescriptor(PlanetmayoImages.VIEW_THUMBNAILS.getImage());

    mediumIcons = new Action()
    {
      @Override
      public void run()
      {
        setMediumThumbnails();
      };
    };
    mediumIcons.setEnabled(true);
    mediumIcons.setText("Medium Icons");
    mediumIcons.setImageDescriptor(PlanetmayoImages.VIEW_MEDIUM.getImage());

    largeIcons = new Action()
    {
      @Override
      public void run()
      {
        setLargeThumbnails();
      };
    };
    largeIcons.setEnabled(false);
    largeIcons.setText("Large Icons");
    largeIcons.setImageDescriptor(PlanetmayoImages.VIEW_FULLSIZE.getImage());

    stretch = new Action("Stretch", IAction.AS_CHECK_BOX)
    {

      @Override
      public void run()
      {
        final ScrolledComposite mainComposite = gallery.getMainComposite();
        Point size = mainComposite.getSize();
        mainComposite.redraw(0, 0, size.x, size.y, true);
        imagePanel.setStretchMode(isChecked());
        size = imagePanel.getSize();
        imagePanel.redraw(0, 0, size.x, size.y, true);
      }
    };
    stretch.setImageDescriptor(PlanetmayoImages.STRETCH.getImage());
  }

  private void createMainWindow(final Composite parent)
  {
    main = parent;
    final FillLayout mainLayout = new FillLayout();
    main.setLayout(mainLayout);
    final SashForm dividerPane = new SashForm(main, SWT.HORIZONTAL);
    gallery = new ImageGallery<ImageMetaData, ThumbnailPackage>(dividerPane);
    gallery.setThumbnailSize(LARGE_ICON_WIDTH, LARGE_ICON_HEIGHT);
    gallery.setDefaultImage(PlanetmayoImages.UNKNOWN.getImage().createImage(
        gallery.getMainComposite().getDisplay()));
    imagePanel = new ImagePanel(dividerPane);
    gallery.setLabelBuilder(
        new ImageGalleryElementsBuilder<ImageMetaData, ThumbnailPackage>()
        {

          @Override
          public Image buildImage(final ThumbnailPackage image)
          {
            return image.getScaled();
          }

          @Override
          public String buildLabel(final ImageMetaData imageName)
          {
            return new File(imageName.getFileName()).getName();
          }

          @Override
          public void disposeImage(final ThumbnailPackage i)
          {
            i.dispose();
          }

          @Override
          public void disposeMeta(final ImageMetaData t)
          {
            // ignore.
          }
        });
    gallery.addElementMouseListener(new MouseListener()
    {

      @Override
      public void mouseDoubleClick(final MouseEvent e)
      {
        // default method, nothing to do here
      }

      @Override
      public void mouseDown(final MouseEvent event)
      {
        // default method nothing to do
      }

      @Override
      @SuppressWarnings("unchecked")
      public void mouseUp(final MouseEvent event)
      {
        final ImageGallery<ImageMetaData, ThumbnailPackage>.ImageLabel label =
            (ImageGallery<ImageMetaData, ThumbnailPackage>.ImageLabel) event.data;
        final long timeToFire = label.getImageMeta().getDate().getTime();
        fireNewTime(new HiResDate(timeToFire));
        final int index = images.indexOf(label.getImageMeta());
        Activator.getDefault().getTimeProvider().fireNewTime(ImagesView.this,
            timeToFire);
        selectImage(index);
      }

    });
  }

  @Override
  public void createPartControl(final Composite parent)
  {
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

  @SuppressWarnings("deprecation")
  @Override
  public void dispose()
  {
    Activator.getDefault().getTimeProvider().removeListener(timeListener);
    _myPartMonitor.dispose(getSite().getWorkbenchWindow().getPartService());
    if (_timeProvider != null)
    {
      _timeProvider.removeListener(_propertyChangeListener,
          TimeProvider.TIME_CHANGED_PROPERTY_NAME);
    }
    gallery.dispose();
    super.dispose();
  }

  private void fillMenu()
  {
    final IMenuManager menu = getViewSite().getActionBars().getMenuManager();
    menu.add(stretch);
    menu.add(new Separator());
    menu.add(smallIcons);
    menu.add(mediumIcons);
    menu.add(largeIcons);
    menu.add(new Separator());
    menu.add(open);
    // menu.add(refresh);
  }

  private void fillToolbarManager()
  {
    final IToolBarManager toolbar = getViewSite().getActionBars()
        .getToolBarManager();
    toolbar.add(stretch);
    toolbar.add(new Separator());
    toolbar.add(smallIcons);
    toolbar.add(mediumIcons);
    toolbar.add(largeIcons);
    toolbar.add(new Separator());
    toolbar.add(open);
  }

  public void fireNewTime(final HiResDate dtg)
  {
    if (_controllableTime == null)
    {
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

  public String getOpenedFolder()
  {
    return this.openedFolder;
  }

  @Override
  public void init(final IViewSite site, final IMemento memento)
      throws PartInitException
  {
    super.init(site, memento);
    this.memento = memento;
    timeListener = new ITimeListener()
    {

      @Override
      public void newTime(final Object src, final long millis)
      {
        if (src == ImagesView.this || images == null || images.isEmpty())
        {
          return;
        }
        selectImage(millis);
      }
    };
  }

  private void initDrop(final Control control)
  {
    final DropTarget target = new DropTarget(control, DND.DROP_MOVE
        | DND.DROP_COPY | DND.DROP_LINK);
    final Transfer[] transfers = new Transfer[]
    {FileTransfer.getInstance()};
    target.setTransfer(transfers);
    target.addDropListener(new DropTargetAdapter()
    {
      @Override
      public void dragEnter(final DropTargetEvent e)
      {
        if (e.detail == DND.DROP_NONE)
        {
          e.detail = DND.DROP_LINK;
        }
      }

      @Override
      public void dragOperationChanged(final DropTargetEvent e)
      {
        if (e.detail == DND.DROP_NONE)
        {
          e.detail = DND.DROP_LINK;
        }
      }

      @Override
      public void drop(final DropTargetEvent e)
      {
        if (e.data == null)
        {
          e.detail = DND.DROP_NONE;
          return;
        }
        if (e.data instanceof String[])
        {
          final String[] fileNames = (String[]) e.data;
          if (fileNames.length > 0 && fileNames[0] != null && !fileNames[0]
              .isEmpty())
          {
            final String fileName = fileNames[0];
            if (!fileName.equals(openedFolder) && new File(fileName)
                .isDirectory())
            {
              openFolder(fileName);
            }

          }
        }
      }
    });
  }

  public boolean openFolder(final String folderName)
  {
    this.openedFolder = folderName;
    gallery.removeAll();
    imagePanel.setCurrentImage(null, null, true);
    imagePanel.setNextImage(null, null);
    final File folder = new File(openedFolder);
    if (!folder.exists() || !folder.isDirectory())
    {
      return false;
    }
    setPartName("Images: " + folder.getName());
    final File[] childs = folder.listFiles();
    images = new ArrayList<ImagesView.ImageMetaData>();
    for (final File child : childs)
    {
      if (!child.isFile())
      {
        continue;
      }
      String childName = child.getName();
      if (!PlanetmayoFormats.getInstance().isSupportedImage(childName))
      {
        continue;
      }
      final Date date = PlanetmayoFormats.getInstance().parseDateFromFileName(
          childName);
      if (date != null)
      {
        images.add(new ImageMetaData(child.getAbsolutePath(), date));
      }
      childName = childName.substring(0, childName.lastIndexOf('.'));
    }

    Collections.sort(images, IMAGES_COMPARATOR);
    for (final ImageMetaData image : images)
    {
      gallery.addImage(image, null);
      ImageLoader.getInstance().load(image.getFileName(), image, gallery);
    }
    if (!images.isEmpty())
    {
      imagePanel.setCurrentImage(images.get(0).getFileName(), null, false);
      ImageLoader.getInstance().load(imagePanel);
    }
    return true;
  }

  private void reloadImages()
  {
    if (images != null && !images.isEmpty())
    {
      for (final ImageMetaData image : images)
      {
        gallery.addImage(image, null);
        ImageLoader.getInstance().load(image.getFileName(), image, gallery);
      }
      if (!images.isEmpty())
      {
        imagePanel.setCurrentImage(images.get(0).getFileName(), null, false);
        ImageLoader.getInstance().load(imagePanel);
      }
      gallery.redrawGallery();
      main.layout();
    }
  }

  public void restoreSavedState()
  {
    if (memento == null)
    {
      return;
    }
    if (memento.getBoolean(STATE_STRETCH) != null)
    {
      imagePanel.setStretchMode(memento.getBoolean(STATE_STRETCH));
      stretch.setChecked(memento.getBoolean(STATE_STRETCH));
    }
    if (memento.getInteger(STATE_THUMBNAIL_WIDTH) != null)
    {
      if (memento.getInteger(STATE_THUMBNAIL_WIDTH) == LARGE_ICON_WIDTH)
      {
        setLargeThumbnails();
      }
      else if (memento.getInteger(STATE_THUMBNAIL_WIDTH) == MEDIUM_ICON_WIDTH)
      {
        setMediumThumbnails();
      }
      else
      {
        setSmallThumbnails();
      }
    }
  }

  @Override
  public void saveState(final IMemento memento)
  {
    super.saveState(memento);
    memento.putBoolean(STATE_STRETCH, stretch.isChecked());
    if (!largeIcons.isEnabled())
    {
      memento.putInteger(STATE_THUMBNAIL_WIDTH, LARGE_ICON_WIDTH);
      memento.putInteger(STATE_THUMBNAIL_HEIGHT, LARGE_ICON_HEIGHT);
    }
    if (!smallIcons.isEnabled())
    {
      memento.putInteger(STATE_THUMBNAIL_WIDTH, SMALL_ICON_WIDTH);
      memento.putInteger(STATE_THUMBNAIL_HEIGHT, SMALL_ICON_HEIGHT);
    }
    if (!mediumIcons.isEnabled())
    {
      memento.putInteger(STATE_THUMBNAIL_WIDTH, MEDIUM_ICON_WIDTH);
      memento.putInteger(STATE_THUMBNAIL_HEIGHT, MEDIUM_ICON_HEIGHT);
    }

  }

  private void selectImage(final int index)
  {
    // run this in a ui thread.
    Display.getDefault().syncExec(new Runnable()
    {

      @Override
      public void run()
      {
        final ImageMetaData current = images.get(index);
        gallery.selectImage(current, true);
        imagePanel.setCurrentImage(current.getFileName(), null, false);
        if (index != images.size() - 1)
        {
          imagePanel.setNextImage(images.get(index + 1).getFileName(), null);
        }
        ImageLoader.getInstance().load(imagePanel);
      }
    });

  }

  /**
   * Select nearest image from the images list based on date in image name
   *
   * @param millis
   */
  private void selectImage(final long millis)
  {
    if (images != null)
    {
      final ImageMetaData toSelect = new ImageMetaData(null, new Date(millis));
      int result = Collections.binarySearch(images, toSelect,
          IMAGES_COMPARATOR);
      if (result >= 0)
      {
        selectImage(result);
        return;
      }
      result = (-(result + 1)) + 1;
      long nearest = Long.MAX_VALUE;
      int nearestImage = -1;
      for (int i = result - 2; i <= result; i++)
      {
        if (i >= 0 && i < images.size())
        {
          final long distance = toSelect.distance(images.get(i));
          if (distance < nearest)
          {
            nearest = distance;
            nearestImage = i;
          }
        }
      }
      if (nearestImage >= 0)
      {
        selectImage(nearestImage);
      }
    }
  }

  @Override
  public void setFocus()
  {

  }

  private void setLargeThumbnails()
  {
    smallIcons.setEnabled(true);
    largeIcons.setEnabled(false);
    mediumIcons.setEnabled(true);
    gallery.setThumbnailSize(LARGE_ICON_WIDTH, LARGE_ICON_HEIGHT);
    reloadImages();
  }

  private void setMediumThumbnails()
  {
    smallIcons.setEnabled(true);
    largeIcons.setEnabled(true);
    mediumIcons.setEnabled(false);
    gallery.setThumbnailSize(MEDIUM_ICON_WIDTH, MEDIUM_ICON_HEIGHT);
    reloadImages();
  }

  private void setSmallThumbnails()
  {
    smallIcons.setEnabled(false);
    largeIcons.setEnabled(true);
    mediumIcons.setEnabled(true);
    gallery.setThumbnailSize(SMALL_ICON_WIDTH, SMALL_ICON_HEIGHT);
    reloadImages();
  }

  private void setupListeners()
  {
    _myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
        .getPartService());

    _myPartMonitor.addPartListener(ControllableTime.class,
        PartMonitor.ACTIVATED, new PartMonitor.ICallback()
        {
          @Override
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
          @Override
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
          @Override
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
          @Override
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