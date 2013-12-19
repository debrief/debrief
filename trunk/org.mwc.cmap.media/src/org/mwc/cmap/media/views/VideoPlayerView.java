package org.mwc.cmap.media.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Scale;
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
import org.mwc.cmap.media.time.ITimeListener;
import org.mwc.cmap.media.utility.DateUtils;
import org.mwc.cmap.media.xuggle.PlayerAdapter;
import org.mwc.cmap.media.xuggle.PlayerListener;
import org.mwc.cmap.media.xuggle.XugglePlayer;

import MWC.GenericData.HiResDate;

public class VideoPlayerView extends ViewPart {
	private static final String STATE_VIDEO_FILE = "filename";
	private static final String STATE_POSITION = "position";
	private static final String STATE_START_TIME = "startTime";
	private static final String STATE_STRETCH = "stretch";

	public static final String ID = "org.mwc.cmap.media.views.VideoPlayerView";
	
	private IMemento memento;
	private ITimeListener timeListener;
	
	private XugglePlayer player;
	private Scale scale;
	private FormattedText startTime;
	private Label playTime;
	
	private Action open;
	private Action play;
	private Action stop;
	private Action stretch;
	
	private boolean fireNewTime;
	
	private SimpleDateFormat timeFormat;
	
	private ControllableTime _controllableTime;
	
	private TimeProvider _timeProvider;
	
	private boolean _firingNewTime;
	
	private PartMonitor _myPartMonitor;
	
	private String _selected;
	
	private PropertyChangeListener _propertyChangeListener = new PropertyChangeListener()
	{
		
		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (!player.isOpened() || _firingNewTime || _timeProvider == null
					|| _timeProvider.getPeriod() == null
					|| _timeProvider.getTime() == null
					|| !player.isOpened())
			{
				return;
			}
			Object newValue = evt.getNewValue();
			if (newValue instanceof HiResDate)
			{
				HiResDate now = (HiResDate) newValue;
				if (now != null)
				{
					Date startDate = (Date) startTime.getValue();
					Date date = now.getDate();
					DateUtils.removeMilliSeconds(date);
					long millis = date.getTime() - startDate.getTime();
					if (millis < 0) {
						millis = 0;
					}
					if (millis > player.getDuration()) {
						millis = player.getDuration();
					}
					if (player.isPlaying()) {
						player.pause();
					}
					player.seek(millis);
				}
			}
		}
	};

	public VideoPlayerView() {
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
	public void init(final IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.memento = memento;
		timeFormat = PlanetmayoFormats.getInstance().getTimeFormat();
		timeListener = new ITimeListener() {
			
			@Override
			public void newTime(Object src, long millis) {
				millis -= ((Date) startTime.getValue()).getTime();
				if (millis < 0) {
					millis = 0;
				}
				if (millis > player.getDuration()) {
					millis = player.getDuration();
				}
				if (src != VideoPlayerView.this && player.isOpened()) {
					if (player.isPlaying()) {
						player.pause();						
					}	
					player.seek(millis);
				}
			}
		};		
	}
	
	@Override
	public void dispose() {
		super.dispose();
		_myPartMonitor.dispose(getSite().getWorkbenchWindow().getPartService());
		Activator.getDefault().getTimeProvider().removeListener(timeListener);		
	}	

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		if (player.isOpened()) {
			Date start = (Date) startTime.getValue();
			memento.putString(STATE_VIDEO_FILE, player.getFileName());
			memento.putString(STATE_START_TIME, Long.toString(start.getTime()));
			memento.putString(STATE_POSITION, Long.toString(player.getCurrentPosition()));
			memento.putBoolean(STATE_STRETCH, player.isStretchMode());
		} 
	}

	public void createPartControl(Composite parent) {
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

	private void updatePlayTime(long milli) {
		playTime.setText(timeFormat.format(new Date(milli - TimeZone.getDefault().getOffset(0))));
	}
	
	private void createMainWindow(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		
		Composite control = new Composite(composite, SWT.NONE);
		layout = new GridLayout(4, false);
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
		
		new Label(control, SWT.LEFT).setText("Start time: ");
		startTime = new FormattedText(control);
		startTime.setFormatter(PlanetmayoFormats.getInstance().getDateTimeFormatter());
		startTime.setValue(new Date());
		startTime.getControl().setEnabled(false);
		startTime.getControl().addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				updatePlayTime(player.getCurrentPosition());
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});
		
		scale = new Scale(control, SWT.HORIZONTAL);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.minimumWidth = 100;
		data.horizontalAlignment = SWT.FILL;

		final PlayerListener playerScaleListener = new PlayerAdapter() {
			
			@Override
			public void onPlaying(XugglePlayer player, long milli) {
				scale.setSelection((int) Math.round(milli / 1000.0));
			}
		};			
		scale.setMinimum(0);
		scale.setMaximum(3600 * 5);
		scale.setIncrement(1);
		scale.setPageIncrement(3600 * 5);		
		scale.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent event) {
				
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.keyCode == SWT.PAGE_DOWN || event.keyCode == SWT.PAGE_UP) {
					event.doit = false;
				}
			}
		});
		scale.setCapture(true);
		scale.setLayoutData(data);
		scale.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				fireNewTime = true;
				if (player.hasVideo()) {
					player.seek(scale.getSelection() * 1000);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				
			}
		});
		scale.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent event) {
				if (! player.hasVideo()) {
					boolean wasPlaying = player.isPlaying();
					player.pause();
					player.seek(scale.getSelection() * 1000);
					if (wasPlaying) {
						player.play();
					}
				}
				player.addPlayerListener(playerScaleListener);				
			}
			
			@Override
			public void mouseDown(MouseEvent event) {
				player.removePlayerListener(playerScaleListener);
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent event) {
			}
		});
		scale.setEnabled(false);
		
		playTime = new Label(control, SWT.LEFT);
		playTime.setText("00:00:00.000");
		
		player = new XugglePlayer(composite);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = SWT.FILL;
		data.horizontalAlignment = SWT.FILL;
		player.setLayoutData(data);		
		player.addPlayerListener(playerScaleListener);
		player.addPlayerListener(new PlayerAdapter() {

			@Override
			public void onPlaying(XugglePlayer player, long milli) {
				updatePlayTime(milli);
				if ((getViewSite().getPage().getActivePart() == VideoPlayerView.this && fireNewTime) || player.isPlaying()) {
					Date start = (Date) startTime.getValue();
					DateUtils.removeMilliSeconds(start);
					long time = milli + start.getTime();
					Activator.getDefault().getTimeProvider().fireNewTime(VideoPlayerView.this, time);
				}
				if (player.isPlaying()) {
					update(player, milli);
				}
			}
			
			@Override
			public void onStop(XugglePlayer player)
			{
				super.onStop(player);
				if (startTime != null && startTime.getControl() != null
						&& !startTime.getControl().isDisposed())
				{
					Date startDate = (Date) startTime.getValue();
					fireNewTime(new HiResDate(startDate));
				}
			}
			
			@Override
			public void onVideoOpened(XugglePlayer player, String fileName)
			{
				super.onVideoOpened(player, fileName);
			}

			@Override
			public void onSeek(XugglePlayer player, long milli)
			{
				updatePlayTime(milli);
				update(player, milli);
			}

			private void update(XugglePlayer player, long milli)
			{
				scale.setSelection((int) (milli/1000));
				if ( _timeProvider != null 
						&& _timeProvider.getPeriod() != null && player.isOpened()) {
					Date start = (Date) startTime.getValue();
					long step = milli + start.getTime();
					HiResDate newDTG = new HiResDate(step);
					if (_timeProvider.getPeriod().contains(newDTG)) {
						fireNewTime(newDTG);
					}
				}
			}
		});		
	}

	public void setFocus() {
		player.setFocus();
		this.fireNewTime = false;
	}

	private void createActions() {
		open = new Action() {

			@Override
			public void run() {
				fireNewTime = true;
		        FileDialog fd = new FileDialog(getViewSite().getShell(), SWT.OPEN);
		        fd.setText("Open Movie");
		        String[] filterExt = { "*.avi", "*.vob", "*.mp4", "*.mov", "*.mpeg", "*.flv", "*.mp3", "*.wma", "*.*" };
		        fd.setFilterExtensions(filterExt);
		        String selected = fd.open();
		        VideoPlayerView.this.open(selected);
			}
		};
		open.setText("Open");
		open.setImageDescriptor(PlanetmayoImages.OPEN.getImage());
		
		play = new Action() {
			
			@Override
			public void run() {
				fireNewTime = true;
				if (! player.isPlaying()) {
					player.play();
				} else {
					player.pause();
				}
			}
		};
		play.setImageDescriptor(PlanetmayoImages.PLAY.getImage());
		play.setText("Play");
		play.setEnabled(false);
		
		stop = new Action() {

			@Override
			public void run() {
				fireNewTime = true;
				player.reopen();
			}
		};
		stop.setImageDescriptor(PlanetmayoImages.STOP.getImage());
		stop.setText("Stop");
		stop.setEnabled(false);
		
		stretch = new Action("Stretch", Action.AS_CHECK_BOX) {

			@Override
			public void run() {
				player.setStretchMode(isChecked());
				Point size = player.getSize();
				player.redraw(0, 0, size.x, size.y, true);
			}
		};
		stretch.setChecked(true);
		
		player.addPlayerListener(new PlayerAdapter() {

			@Override
			public void onPlay(XugglePlayer player) {
				play.setImageDescriptor(PlanetmayoImages.PAUSE.getImage());
				play.setText("Pause");
			}

			@Override
			public void onStop(XugglePlayer player) {
				play.setImageDescriptor(PlanetmayoImages.PLAY.getImage());
				play.setText("Play");
			}

			@Override
			public void onPause(XugglePlayer player) {
				onStop(player);
			}

			@Override
			public void onVideoOpened(XugglePlayer player, String fileName) {
				movieOpened(new File(fileName).getName(), player);
			}			
			
		});
	}
	
	private void movieOpened(String videoName, XugglePlayer player) {
		boolean enabled = videoName != null;
		play.setEnabled(enabled);		
		stop.setEnabled(enabled);
		scale.setEnabled(enabled);
    	startTime.getControl().setEnabled(enabled);
    	
    	if (videoName != null) {
    		VideoPlayerView.this.setPartName("Video player: " + videoName);
    	} else {
    		VideoPlayerView.this.setPartName("Video player");
    	}    	
		play.setImageDescriptor(PlanetmayoImages.PLAY.getImage());
		play.setText("Play");
		if (videoName != null) {
			scale.setMaximum((int) Math.floor(player.getDuration() / 1000.0));
			scale.setPageIncrement(scale.getMaximum());
			Date start = PlanetmayoFormats.getInstance().parseDateFromFileName(videoName);
			if (start == null) {
				start = new Date();
			}
			startTime.setValue(start);
		}
	}
	
	private void fillToolbarManager() {
		IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
		toolbar.add(stretch);
		toolbar.add(new Separator());		
		toolbar.add(open);
		toolbar.add(new Separator());
		toolbar.add(play);
		toolbar.add(stop);
	}
	
	private void fillMenu() {
		IMenuManager menu = getViewSite().getActionBars().getMenuManager();
		menu.add(stretch);
		menu.add(new Separator());		
		menu.add(open);
		menu.add(new Separator());
		menu.add(play);
		menu.add(stop);
	}
	
	private void restoreSavedState() {
		if (memento == null) {
			return;
		}
		if (memento.getString(STATE_VIDEO_FILE) != null) {
			Boolean stretchBool = memento.getBoolean(STATE_STRETCH);
			if (stretchBool != null) {
				player.setStretchMode(stretchBool);
				stretch.setChecked(stretchBool);
			}
			if (player.open(memento.getString(STATE_VIDEO_FILE))) {
				_selected = memento.getString(STATE_VIDEO_FILE);
				try {
					// Issue #545 - We don't need set position
					// player.seek(Long.parseLong(memento.getString(STATE_POSITION)));
					startTime.setValue(new Date(Long.parseLong(memento.getString(STATE_START_TIME))));
				} catch (NumberFormatException ex) {
					// can't restore state
					Activator.log(ex);					
				}
			}
		}		
	}

	public String getSelected()
	{
		return _selected;
	}

	public void open(String selected)
	{
		if (selected != null) {
			if (! player.open(selected)) {
				movieOpened(null, null);
				MessageBox message = new MessageBox(getSite().getShell(), SWT.ICON_WARNING | SWT.OK);
				message.setText("Video player: " + new File(selected).getName());
				message.setMessage("This file format isn't supported.");
				message.open();
			} else {
				_selected = selected;
			}
		}
	}
}