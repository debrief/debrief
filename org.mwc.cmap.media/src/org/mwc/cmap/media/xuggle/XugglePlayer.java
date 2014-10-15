/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.media.xuggle;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewPart;
import org.mwc.cmap.media.utility.ImageUtils;
import org.mwc.cmap.media.views.VideoPlayerView;
import org.mwc.cmap.media.xuggle.impl.PlayingThread;
import org.mwc.cmap.media.xuggle.impl.ThreadUINotifier;

public class XugglePlayer extends Composite {

	private final Set<PlayerListener> playerListeners = new HashSet<PlayerListener>();
    private final Canvas videoBuffer;
    private final FrameUpdater frameUpdater = new FrameUpdater();    
    private BufferedImage currentFrame;

    private boolean stretchMode = true; 
    private String fileName;

    private PlayingThread thread;
		private IViewPart view;
		private Label label;
		private String message;
		
		private ControlContribution statusContribution = new ControlContribution(VideoPlayerView.ID)
		{

			@Override
			protected Control createControl(Composite parent)
			{
				if (label != null && label.isDisposed())
				{

					label.dispose();
					label = null;
				}

				label = new Label(parent, SWT.RIGHT | SWT.BORDER);
				label.setText(message);
				return label;
			}

			@Override
			public void dispose()
			{
				if (label != null && !label.isDisposed()) {
					label.dispose();
				}
				label = null;
				super.dispose();
			}

		};

    public XugglePlayer(Composite composite, IViewPart viewPart) {
        super(composite, SWT.EMBEDDED);
        this.view = viewPart;
        setLayout(new FillLayout());        
        Frame frame = SWT_AWT.new_Frame(this);
        frame.setBackground(new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue()));
        videoBuffer = new Canvas() {
        	private Point scaledSize = new Point(0, 0);
			private static final long serialVersionUID = 1L;

			@Override
            public void paint(Graphics g) {
                if (currentFrame != null) {
                	g.setColor(Color.BLACK);
                	if (stretchMode) {
                		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                		g.drawImage(currentFrame, 1, 1, getWidth() - 2, getHeight() - 2, null);
                	} else {
                		ImageUtils.getScaledSize(currentFrame.getWidth(), currentFrame.getHeight(), getWidth() - 2, getHeight() - 2, scaledSize);
                		int startX =  (getWidth() - 2 - scaledSize.x) / 2;
                		int startY =  (getHeight() - 2 - scaledSize.y) / 2;
                		g.drawRect(startX, startY, scaledSize.x + 1, scaledSize.y + 1);
                		g.drawImage(currentFrame, startX + 1,  startY + 1, scaledSize.x, scaledSize.y, null);
                	}
                }
            }
        };
        frame.add(videoBuffer);
        videoBuffer.addMouseListener(new MouseAdapter()
				{

					@Override
					public void mouseExited(MouseEvent e)
					{
						Display.getDefault().asyncExec(new Runnable()
						{
							
							@Override
							public void run()
							{
								if (XugglePlayer.this.isDisposed()) {
									return;
								}
								view.getViewSite().getActionBars().getStatusLineManager().remove(statusContribution);
								view.getViewSite().getActionBars().updateActionBars();
							}
						});
					}
        	
				});
        videoBuffer.addMouseMotionListener(new MouseMotionAdapter()
				{
					
					@Override
					public void mouseMoved(MouseEvent e)
					{
						java.awt.Point point = e.getPoint();
						message = "(" + point.x + "," + (videoBuffer.getHeight() - point.y) + ")";
						Display.getDefault().syncExec(new Runnable()
						{
							
							@Override
							public void run()
							{
								if (XugglePlayer.this.isDisposed()) {
									return;
								}
								view.getViewSite().getActionBars().getStatusLineManager().markDirty();
								view.getViewSite().getActionBars().getStatusLineManager().remove(statusContribution);
								view.getViewSite().getActionBars().getStatusLineManager().add(statusContribution);
								view.getViewSite().getActionBars().updateActionBars();
							}
						});
					}
					
				});
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent disposeEvent) {
                removeDisposeListener(this);
                stop();
                view.getViewSite().getActionBars().getStatusLineManager().remove(statusContribution);
                statusContribution.dispose();
            }
        });
    }
    
    public void addPlayerListener(PlayerListener listener) {
    	playerListeners.add(listener);
    }
    
    public void removePlayerListener(PlayerListener listener) {
    	playerListeners.remove(listener);
    }

    public String getFileName() {
        return fileName;
    }

    public void stop() {
        if (isDisposed()) {
            throw new SWTException("Widget is disposed");
        }
        if (thread != null) {
            thread.stopPlaying();
            this.thread = null;
            fireStop();
        }
    }

    public boolean open(String fileName) {
        return open(fileName, true);
    }

    public boolean open(String fileName, boolean loadFirstFrame) {
        if (isDisposed()) {
            throw new SWTException("Widget is disposed");
        }
        stop();
        this.fileName = fileName;
        if (! new File(fileName).exists()) {
        	return false;
        }
        currentFrame = null;
        videoBuffer.repaint();
        thread = new PlayingThread(fileName, frameUpdater);
        if (! thread.startVideoThread()){
        	return false;
        }
        if (loadFirstFrame) {
        	thread.seek(0);
        }        
        fireVideoOpened();
        return true;
    }

    public boolean reopen() {
    	return open(fileName);
    }

    public void play() {
        if (isDisposed()) {
            throw new SWTException("Widget is disposed");
        }
        if (thread != null) {
        	thread.resumePlaying();
        	firePlay();
        }
    }

    public void pause() {
        if (isDisposed()) {
            throw new SWTException("Widget is disposed");
        }
        if (thread != null) {
            thread.pause();
            firePause();
        }
    }

    public void seek(long millisecond) {
        if (isDisposed()) {
            throw new SWTException("Widget is disposed");
        }
        if (thread != null) {
            thread.seek(millisecond);
        }
    }
    
    public boolean isPaused() {
    	return thread.isPaused();
    }
    
    public boolean isPlaying() {
    	return isOpened() && !isPaused();
    }
    
    public boolean hasAudio() {
    	return thread.hasAudio();
    }
    
    public boolean hasVideo() {
    	return thread.hasVideo();
    }    
    
    public boolean isOpened() {
    	return thread != null;
    }    
    
    public long getDuration() {
    	return thread == null ? 0 : thread.getDuration();
    }
    
    public long getCurrentPosition() {
    	return thread == null ? 0 : thread.getCurrentPosition();
    }
    
    public boolean isStretchMode() {
		return stretchMode;
	}

	public void setStretchMode(boolean stretchMode) {
		this.stretchMode = stretchMode;
	}

	protected void firePlaying(long milli) {
		for (PlayerListener listener : playerListeners) {
			listener.onPlaying(XugglePlayer.this, milli);
		}
    }
    
    protected void firePlay() {
		for (PlayerListener listener : playerListeners) {
			listener.onPlay(XugglePlayer.this);
		}
    }
    
    protected void fireStop() {
		for (PlayerListener listener : playerListeners) {
			listener.onStop(XugglePlayer.this);
		}
    }
    
    protected void firePause() {
		for (PlayerListener listener : playerListeners) {
			listener.onPause(XugglePlayer.this);
		}
    }
    
    protected void fireSeek(final long milli) {
		for (PlayerListener listener : playerListeners) {
			listener.onSeek(XugglePlayer.this, milli);
		}
    }
    
    protected void fireVideoOpened() {
		for (PlayerListener listener : playerListeners) {
			listener.onVideoOpened(XugglePlayer.this, getFileName());
		}
    }    

    private class FrameUpdater implements Runnable, ThreadUINotifier {
    	
    	private static final int SEEK_EVENTS_PORTION = 100;

    	private volatile long[] seekEvents = new long[SEEK_EVENTS_PORTION];
    	private volatile boolean done = true;
    	private volatile BufferedImage nextFrame;
    	private volatile long nextPosition;
    	private volatile int seekIndex;
    	
		private void addSeekEvent(long seek) {
			if (seek == -1) {
				return;
			}
			if (seekIndex >= seekEvents.length) {
				long[] oldEvents = seekEvents;
				seekEvents = new long[oldEvents.length + SEEK_EVENTS_PORTION];
				System.arraycopy(oldEvents, 0, seekEvents, 0, oldEvents.length);
			}
			seekEvents[seekIndex] = seek;
			seekIndex++;			
    	}
		
    	@Override
		public void applyPause() {
   			getDisplay().asyncExec(new Runnable() {
					
				@Override
				public void run() {
					if (!isDisposed()) {
						pause();
					}
				}
			});
		}

		@Override
		public void applyStop() {
   			getDisplay().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					if (!isDisposed()) {
						stop();
					}					
				}
			});
		}

		public synchronized void updateFrame(BufferedImage nextFrame, long nextPosition, long seek) {
    		this.nextPosition = nextPosition;
    		addSeekEvent(seek);
    		this.nextFrame = nextFrame;
    		if (done) {
    			done = false;
    			getDisplay().asyncExec(this);
    		}
    	}
    	
		@Override
		public synchronized void run() {
        	if (!isDisposed()) {
        		currentFrame = nextFrame;
        		if (isVisible() && videoBuffer != null && videoBuffer.getGraphics() != null) {
        			Graphics graphics = videoBuffer.getGraphics();
        			videoBuffer.paint(graphics);
        			graphics.dispose();
        		}
        		firePlaying(nextPosition);
        		for (int i = 0; i < seekIndex; i++) {
        			fireSeek(seekEvents[i]);
        		}        		
        	}
    		seekIndex = 0;
    		if (seekEvents.length > SEEK_EVENTS_PORTION) {
    			seekEvents = new long[SEEK_EVENTS_PORTION];
    		}
    		done = true;
		}    	
    }    
}
