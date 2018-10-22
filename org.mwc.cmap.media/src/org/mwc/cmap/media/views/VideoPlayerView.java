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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.media.Activator;
import org.mwc.cmap.media.PlanetmayoFormats;
import org.mwc.cmap.media.PlanetmayoImages;
import org.mwc.cmap.media.dialog.VideoPlayerStartTimeDialog;
import org.mwc.cmap.media.utility.DateUtils;
import org.mwc.cmap.media.xuggle.PlayerAdapter;
import org.mwc.cmap.media.xuggle.PlayerListener;
import org.mwc.cmap.media.xuggle.XugglePlayer;

import MWC.GenericData.HiResDate;

public class VideoPlayerView extends ViewPart
{
  private static final String STATE_VIDEO_FILE = "filename";
  private static final String STATE_POSITION = "position";
  private static final String STATE_START_TIME = "startTime";
  private static final String STATE_STRETCH = "stretch";

  public static final String LAST_VIDEO_START_TIME = "lastVideoStartTime";

  public static final String ID = "org.mwc.cmap.media.views.VideoPlayerView";

  private IMemento memento;
  private XugglePlayer player;
  private Scale scale;
  private Date startTime;
  private Label playTime;
  private Label playTimeLeft;

  private Action open;
  private Action editStartTime;
  private Button play;
  private Button stop;
  private Action stretch;

  private boolean fireNewTime;

  private SimpleDateFormat timeFormat;

  private ControllableTime _controllableTime;

  private TimeProvider _timeProvider;

  private boolean _firingNewTime;

  private PartMonitor _myPartMonitor;

  private String _currentFilename;

  private PropertyChangeListener _propertyChangeListener =
      new PropertyChangeListener()
      {

        @Override
        public void propertyChange(PropertyChangeEvent evt)
        {
          if (!player.isOpened() || _firingNewTime || _timeProvider == null
              || _timeProvider.getPeriod() == null || _timeProvider
                  .getTime() == null || !player.isOpened())
          {
            return;
          }
          Object newValue = evt.getNewValue();
          if (newValue instanceof HiResDate)
          {
            HiResDate now = (HiResDate) newValue;
            if (now != null)
            {
              Date startDate = new Date(startTime.getTime());
              Date date = now.getDate();
              DateUtils.removeMilliSeconds(date);
              long millis = date.getTime() - startDate.getTime();
              if (millis < 0)
              {
                millis = 0;
              }
              if (millis > player.getDuration())
              {
                millis = player.getDuration();
              }
              if (player.isPlaying())
              {
                player.pause();
              }
              
              // tell the player listener not to fire the next update
              _modifiedAdapter.setIgnoreNext();
              
              player.seek(millis);
            }
          }
        }
      };
      private final PlayerListener playerScaleListener = new PlayerAdapter()
      {

        @Override
        public void onPlaying(XugglePlayer player, long milli)
        {
          scale.setSelection((int) Math.round(milli / 1000.0));
        }
      };
  private ModifiedAdapter _modifiedAdapter;

  public void setVideoStartTime(Date date)
  {
    startTime = date;
    PlatformUI.getPreferenceStore().setValue(_currentFilename, date
        .getTime());
    fireNewTime(new HiResDate(date));
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

  @Override
  public void init(final IViewSite site, IMemento memento)
      throws PartInitException
  {
    super.init(site, memento);
    this.memento = memento;
    timeFormat = PlanetmayoFormats.getInstance().getTimeFormat();
  }

  
  @SuppressWarnings("deprecation")
  @Override
  public void dispose()
  {
    super.dispose();
    _myPartMonitor.dispose(getSite().getWorkbenchWindow().getPartService());
  }

  @Override
  public void saveState(IMemento memento)
  {
    super.saveState(memento);
    if (player.isOpened())
    {
      memento.putString(STATE_VIDEO_FILE, player.getFileName());
      memento.putString(STATE_START_TIME, Long.toString(startTime.getTime()));
      memento.putString(STATE_POSITION, Long.toString(player
          .getCurrentPosition()));
      memento.putBoolean(STATE_STRETCH, player.isStretchMode());
    }
  }

  public void createPartControl(Composite parent)
  {
    initDrop(parent);
    Composite composite = createMainWindow(parent);
    createVideoScaleActions(composite);
    createActions();
    fillToolbarManager();
    restoreSavedState();
    // and start listing for any part action
    setupListeners();

    // ok we're all ready now. just try and see if the current part is valid
    _myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
        .getActivePage());
  }
  
  private void createVideoScaleActions(Composite composite) {
    Composite parent = new Composite(composite,SWT.NULL);
    parent.setLayout(new GridLayout(5,false));
    parent.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
    
    play = createPlayBtn(composite, parent);
    stop = createStopBtn(composite, parent);
    
    playTime = new Label(parent, SWT.LEFT);
    playTime.setText("00:00:00");
    
    scale = createScaleControl(parent);
    
    playTimeLeft = new Label(parent, SWT.LEFT);
    playTimeLeft.setText("00:00:00.000");
  }

  private Scale createScaleControl(Composite parent)
  {
    final Scale scaleCtrl = new Scale(parent, SWT.HORIZONTAL|SWT.NULL);
    GridData data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 100;
    data.horizontalAlignment = SWT.FILL;
    scaleCtrl.setMinimum(0);
    scaleCtrl.setMaximum(3600 * 5);
    scaleCtrl.setIncrement(1);
    scaleCtrl.setPageIncrement(3600 * 5);
    scaleCtrl.addKeyListener(new KeyListener()
    {

      @Override
      public void keyReleased(KeyEvent event)
      {
        // ignore
      }

      @Override
      public void keyPressed(KeyEvent event)
      {
        if (event.keyCode == SWT.PAGE_DOWN || event.keyCode == SWT.PAGE_UP)
        {
          event.doit = false;
        }
      }
    });
    scaleCtrl.setCapture(true);
    scaleCtrl.setLayoutData(data);
    scaleCtrl.addSelectionListener(new SelectionListener()
    {

      @Override
      public void widgetSelected(SelectionEvent event)
      {
        fireNewTime = true;
        if (player.hasVideo())
        {
          player.seek(scaleCtrl.getSelection() * 1000);
        }
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent event)
      {
        // ignore event
      }
    });
    scaleCtrl.addMouseListener(new MouseListener()
    {

      @Override
      public void mouseUp(MouseEvent event)
      {
        if (!player.hasVideo())
        {
          boolean wasPlaying = player.isPlaying();
          player.pause();
          player.seek(scaleCtrl.getSelection() * 1000);
          if (wasPlaying)
          {
            player.play();
          }
        }
        player.addPlayerListener(playerScaleListener);
      }

      @Override
      public void mouseDown(MouseEvent event)
      {
        player.removePlayerListener(playerScaleListener);
      }

      @Override
      public void mouseDoubleClick(MouseEvent event)
      {
        // ignore behaviour
      }
    });
    scaleCtrl.setEnabled(false);
    return scaleCtrl;
  }

  private Button createStopBtn(Composite composite, Composite parent)
  {
    Button stopBtn = new Button(parent,SWT.TRANSPARENT);
    stopBtn.addSelectionListener(new SelectionAdapter()
    {
      
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        fireNewTime = true;
        player.reopen();
      }
    });
    stopBtn.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    stopBtn.setImage(PlanetmayoImages.STOP.getImage().createImage());
    stopBtn.setToolTipText("Stop");
    stopBtn.setEnabled(false);
    return stopBtn;
  }

  private Button createPlayBtn(Composite composite, Composite parent)
  {
    Button playBtn = new Button(parent,SWT.NONE);
    playBtn.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    playBtn.addSelectionListener(new SelectionAdapter()
    {
      
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        fireNewTime = true;
        if (!player.isPlaying())
        {
          player.play();
        }
        else
        {
          player.pause();
        }
      }
    });
    playBtn.setImage(PlanetmayoImages.PLAY.getImage().createImage());
    playBtn.setToolTipText("Play");
    playBtn.setEnabled(false);
    return playBtn;
  }

  private void initDrop(Control control)
  {
    final DropTarget target = new DropTarget(control, DND.DROP_MOVE
        | DND.DROP_COPY | DND.DROP_LINK);
    Transfer[] transfers = new Transfer[]
    {FileTransfer.getInstance()};
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
          if (fileNames.length > 0 && fileNames[0] != null && !fileNames[0]
              .isEmpty())
          {
            String fileName = fileNames[0];

            int index = fileName.lastIndexOf(".");
            if (index >= 0 && new File(fileName).isFile())
            {
              String extension = fileName.substring(index + 1);
              String[] supportedExtensions =
              {"avi", "vob", "mp4", "mov", "mpeg", "flv", "mp3", "wma", "wav"};
              boolean supported = false;
              for (String ext : supportedExtensions)
              {
                if (ext.equalsIgnoreCase(extension))
                {
                  supported = true;
                  break;
                }
              }
              if (supported && !fileName.equals(_currentFilename))
              {
                initializePlayer(fileName);
              }
            }

          }
        }
      }
    });
  }

  private void initializePlayer(final String fileName)
  {
    // #2940 #6
    // if we cannot get the start time from filename open the dialog
    Date start = PlanetmayoFormats.getInstance().parseDateFromFileName(new File(
        fileName).getName());
    if (start == null)
    {
      // try to get the start time from last video start time.
      long lastStartTime = PlatformUI.getPreferenceStore().getLong(
          _currentFilename);
      VideoPlayerStartTimeDialog dialog = new VideoPlayerStartTimeDialog();
      dialog.setStartTime(lastStartTime > 0 ? new Date(lastStartTime) : null);
      dialog.setBlockOnOpen(true);
      if (dialog.open() == Window.OK)
      {
        open(fileName, dialog.getStartTime());
      }
    }
    else
    {
      open(fileName, start);
    }
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

  private void updatePlayTime(long milli)
  {
    long curPlayTime = milli-TimeZone.getDefault().getOffset(0);
    playTime.setText(timeFormat.format(new Date(startTime.getTime()+curPlayTime)));
    playTimeLeft.setText(timeFormat.format(new Date(player.getDuration()-curPlayTime)));
  }

  private Composite createMainWindow(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout());

    Composite control = new Composite(composite, SWT.RIGHT);
    GridLayout layout = new GridLayout(2, false);
    layout.marginBottom = 0;
    layout.marginLeft = 0;
    layout.marginRight = 0;
    layout.marginTop = 0;
    GridData data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.verticalAlignment = SWT.TOP;
    data.horizontalAlignment = SWT.FILL;
    control.setLayout(layout);
    control.setLayoutData(data);

    player = new XugglePlayer(composite, this);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.grabExcessVerticalSpace = true;
    data.verticalAlignment = SWT.FILL;
    data.horizontalAlignment = SWT.FILL;
    data.horizontalSpan=2;
    player.setLayoutData(data);
    player.addPlayerListener(playerScaleListener);
    _modifiedAdapter = new ModifiedAdapter();
    player.addPlayerListener(_modifiedAdapter);
    return composite;
  }

  private class ModifiedAdapter extends PlayerAdapter
  {
    private long ignoreUntil = 0;

    public void setIgnoreNext()
    {
      ignoreUntil = System.currentTimeMillis() + 100;
    }
    
    @Override
    public void onSeek(XugglePlayer player, long milli)
    {
      updatePlayTime(milli);
      long dNow = System.currentTimeMillis();

      // check we're not trying to ignore some mistaken event firings
      if (dNow >= ignoreUntil)
      {
        update(player, milli);
      }
    }

    @Override
    public void onPlaying(XugglePlayer player, long milli)
    {
      updatePlayTime(milli);
      //if playing from timecontroller
      long curPlayTime = milli-TimeZone.getDefault().getOffset(0);
      long currTime = startTime.getTime()+curPlayTime;
      long timeLeft = player.getDuration()-curPlayTime;
      //System.out.println("current Time:"+timeFormat.format(new Date(currTime))+",endTime:"+timeFormat.format(new Date(endTime)));
      if(currTime>startTime.getTime() && timeLeft>0) 
      {
        play.setImage(PlanetmayoImages.PAUSE.getImage().createImage());
        play.setToolTipText("Pause");
      }
      else {
        play.setImage(PlanetmayoImages.PLAY.getImage().createImage());
        play.setToolTipText("Play");
      }

      if ((getViewSite().getPage().getActivePart() == VideoPlayerView.this
          && fireNewTime) || player.isPlaying())
      {
        Date start = new Date(startTime.getTime());
        DateUtils.removeMilliSeconds(start);
        long time = milli + start.getTime();
        Activator.getDefault().getTimeProvider().fireNewTime(
            VideoPlayerView.this, time);
      }
      if (player.isPlaying())
      {
        update(player, milli);
      }
    }

    @Override
    public void onStop(XugglePlayer player)
    {
      super.onStop(player);
      fireNewTime(new HiResDate(startTime));
    }

    @Override
    public void onVideoOpened(XugglePlayer player, String fileName)
    {
      super.onVideoOpened(player, fileName);
    }


    private void update(XugglePlayer player, long milli)
    {
      scale.setSelection((int) (milli / 1000));
      if (_timeProvider != null && _timeProvider.getPeriod() != null && player
          .isOpened())
      {
        Date start = new Date(startTime.getTime());
        long step = milli + start.getTime();
        HiResDate newDTG = new HiResDate(step);
        if (_timeProvider.getPeriod().contains(newDTG))
        {
          fireNewTime(newDTG);
        }
      }
    }
  }

  public void setFocus()
  {
    player.setFocus();
    this.fireNewTime = false;
  }
  
  private void openEditStartTimeDialog() {
    VideoPlayerStartTimeDialog startTimeDialog = new VideoPlayerStartTimeDialog(getViewSite().getShell());
    startTimeDialog.setStartTime(new Date(startTime.getTime()));
    startTimeDialog.setBlockOnOpen(true);
    if(startTimeDialog.open()==Window.OK) {
      setVideoStartTime(startTimeDialog.getStartTime());
      
      // ok, and update the play time
      updatePlayTime(0);
    }
  }

  private void createActions()
  {
    open = new Action()
    {

      @Override
      public void run()
      {
        fireNewTime = true;
        FileDialog fd = new FileDialog(getViewSite().getShell(), SWT.OPEN);
        fd.setText("Open Movie");

        fd.setFilterExtensions(CorePlugin.SUPPORTED_MEDIA_FORMATS);
        String selected = fd.open();
        VideoPlayerView.this.open(selected, new Date());
      }
    };
    open.setText("Open");
    open.setImageDescriptor(PlanetmayoImages.OPEN.getImage());
    
    editStartTime = new Action() {
      @Override
      public void run()
      {
        openEditStartTimeDialog();
      }
    };
    editStartTime.setText("Edit Start time");
    editStartTime.setImageDescriptor(PlanetmayoImages.CONTROL_TIME.getImage());
    stretch = new Action("Stretch", Action.AS_CHECK_BOX)
    {

      @Override
      public void run()
      {
        player.setStretchMode(isChecked());
        Point size = player.getSize();
        player.redraw(0, 0, size.x, size.y, true);
      }
    };
    stretch.setImageDescriptor(PlanetmayoImages.STRETCH.getImage());
    stretch.setChecked(true);

    player.addPlayerListener(new PlayerAdapter()
    {

      @Override
      public void onPlay(XugglePlayer player)
      {
        Display.getDefault().syncExec(new Runnable()
        {
          
          @Override
          public void run()
          {
            play.setImage(PlanetmayoImages.PAUSE.getImage().createImage());
            play.setToolTipText("Pause");    
          }
        });
        
      }

      @Override
      public void onStop(XugglePlayer player)
      {
        Display.getDefault().syncExec(new Runnable()
        {
          
          @Override
          public void run()
          {
            play.setImage(PlanetmayoImages.PLAY.getImage().createImage());
            play.setToolTipText("Play");    
          }
        });
        
      }

      @Override
      public void onPause(XugglePlayer player)
      {
        onStop(player);
      }

      @Override
      public void onVideoOpened(XugglePlayer player, String fileName)
      {
        movieOpened(new File(fileName).getName(), player);
      }

    });
  }

  private void movieOpened(String videoName, XugglePlayer player)
  {
    boolean enabled = videoName != null;
    play.setEnabled(enabled);
    stop.setEnabled(enabled);
    scale.setEnabled(enabled);

    if (videoName != null)
    {
      VideoPlayerView.this.setPartName("Media player: " + videoName);
    }
    else
    {
      VideoPlayerView.this.setPartName("Media player");
    }
    play.setImage(PlanetmayoImages.PLAY.getImage().createImage());
    play.setToolTipText("Play");
    if (videoName != null)
    {
      scale.setMaximum((int) Math.floor(player.getDuration() / 1000.0));
      scale.setPageIncrement(scale.getMaximum());
      Date start = PlanetmayoFormats.getInstance().parseDateFromFileName(
          videoName);
      if (start == null)
      {
        if(startTime != null)
        {
          start = startTime;
        }
        else
        {
          start = new Date();
        }
      }
      startTime = start;
    }
  }

  private void fillToolbarManager()
  {
    IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
    toolbar.add(stretch);
    toolbar.add(new Separator());
    toolbar.add(open);
    toolbar.add(new Separator());
    toolbar.add(editStartTime);
  }

  private void restoreSavedState()
  {
    if (memento == null)
    {
      return;
    }
    if (memento.getString(STATE_VIDEO_FILE) != null)
    {
      Boolean stretchBool = memento.getBoolean(STATE_STRETCH);
      if (stretchBool != null)
      {
        player.setStretchMode(stretchBool);
        stretch.setChecked(stretchBool);
      }
      if (player.open(memento.getString(STATE_VIDEO_FILE)))
      {
        _currentFilename = memento.getString(STATE_VIDEO_FILE);
        try
        {
          // Issue #545 - We don't need set position
          // player.seek(Long.parseLong(memento.getString(STATE_POSITION)));
          startTime = (new Date(Long.parseLong(memento.getString(
              STATE_START_TIME))));
        }
        catch (NumberFormatException ex)
        {
          // can't restore state
          Activator.log(ex);
        }
      }
    }
  }

  public String getSelected()
  {
    return _currentFilename;
  }

  public void open(final String currentFilename, final Date videoStartTime)
  {
    if (currentFilename != null)
    {
      Display.getCurrent().asyncExec(new Runnable() {

        @Override
        public void run()
        {
          if (!player.open(currentFilename))
          {
            movieOpened(null, null);
            MessageBox message = new MessageBox(getSite().getShell(),
                SWT.ICON_WARNING | SWT.OK);
            message.setText("Media player: " + new File(currentFilename).getName());
            message.setMessage("This file format isn't supported.");
            message.open();
          }
          else
          {
            _currentFilename = currentFilename;
            setVideoStartTime(videoStartTime);
          }
        }
      });
    }
  }
}