/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

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

	private class FrameUpdater implements Runnable, ThreadUINotifier {

		private static final int SEEK_EVENTS_PORTION = 100;

		private volatile long[] seekEvents = new long[SEEK_EVENTS_PORTION];
		private volatile boolean done = true;
		private volatile BufferedImage nextFrame;
		private volatile long nextPosition;
		private volatile int seekIndex;

		private void addSeekEvent(final long seek) {
			if (seek == -1) {
				return;
			}
			if (seekIndex >= seekEvents.length) {
				final long[] oldEvents = seekEvents;
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

		@Override
		public synchronized void run() {
			if (!isDisposed()) {
				currentFrame = nextFrame;
				if (isVisible() && videoBuffer != null && videoBuffer.getGraphics() != null) {
					final Graphics graphics = videoBuffer.getGraphics();
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

		@Override
		public synchronized void updateFrame(final BufferedImage nextFrame, final long nextPosition, final long seek) {
			this.nextPosition = nextPosition;
			addSeekEvent(seek);
			this.nextFrame = nextFrame;
			if (done) {
				done = false;
				getDisplay().asyncExec(this);
			}
		}
	}

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

	private final ControlContribution statusContribution = new ControlContribution(VideoPlayerView.ID) {

		@Override
		protected Control createControl(final Composite parent) {
			if (label != null && label.isDisposed()) {

				label.dispose();
				label = null;
			}

			label = new Label(parent, SWT.RIGHT | SWT.BORDER);
			label.setText(message);
			return label;
		}

		@Override
		public void dispose() {
			if (label != null && !label.isDisposed()) {
				label.dispose();
			}
			label = null;
			super.dispose();
		}

	};

	public XugglePlayer(final Composite composite, final IViewPart viewPart) {
		super(composite, SWT.EMBEDDED);
		this.view = viewPart;
		setLayout(new FillLayout());
		final Frame frame = SWT_AWT.new_Frame(this);
		frame.setBackground(new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue()));
		videoBuffer = new Canvas() {
			private static final long serialVersionUID = 1L;
			private final Point scaledSize = new Point(0, 0);

			@Override
			public void paint(final Graphics g) {
				if (currentFrame != null) {
					g.setColor(Color.BLACK);
					if (stretchMode) {
						g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
						g.drawImage(currentFrame, 1, 1, getWidth() - 2, getHeight() - 2, null);
					} else {
						ImageUtils.getScaledSize(currentFrame.getWidth(), currentFrame.getHeight(), getWidth() - 2,
								getHeight() - 2, scaledSize);
						final int startX = (getWidth() - 2 - scaledSize.x) / 2;
						final int startY = (getHeight() - 2 - scaledSize.y) / 2;
						g.drawRect(startX, startY, scaledSize.x + 1, scaledSize.y + 1);
						g.drawImage(currentFrame, startX + 1, startY + 1, scaledSize.x, scaledSize.y, null);
					}
				}
			}
		};
		frame.add(videoBuffer);
		videoBuffer.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited(final MouseEvent e) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						if (XugglePlayer.this.isDisposed()) {
							return;
						}
						view.getViewSite().getActionBars().getStatusLineManager().remove(statusContribution);
						view.getViewSite().getActionBars().updateActionBars();
					}
				});
			}

		});
		videoBuffer.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(final MouseEvent e) {
				final java.awt.Point point = e.getPoint();
				message = "(" + point.x + "," + (videoBuffer.getHeight() - point.y) + ")";
				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {
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
			@Override
			public void widgetDisposed(final DisposeEvent disposeEvent) {
				removeDisposeListener(this);
				stop();
				view.getViewSite().getActionBars().getStatusLineManager().remove(statusContribution);
				statusContribution.dispose();
			}
		});
	}

	public void addPlayerListener(final PlayerListener listener) {
		playerListeners.add(listener);
	}

	protected void firePause() {
		for (final PlayerListener listener : playerListeners) {
			listener.onPause(XugglePlayer.this);
		}
	}

	protected void firePlay() {
		for (final PlayerListener listener : playerListeners) {
			listener.onPlay(XugglePlayer.this);
		}
	}

	protected void firePlaying(final long milli) {
		for (final PlayerListener listener : playerListeners) {
			listener.onPlaying(XugglePlayer.this, milli);
		}
	}

	protected void fireSeek(final long milli) {
		for (final PlayerListener listener : playerListeners) {
			listener.onSeek(XugglePlayer.this, milli);
		}
	}

	protected void fireStop() {
		for (final PlayerListener listener : playerListeners) {
			listener.onStop(XugglePlayer.this);
		}
	}

	protected void fireVideoOpened() {
		for (final PlayerListener listener : playerListeners) {
			listener.onVideoOpened(XugglePlayer.this, getFileName());
		}
	}

	public long getCurrentPosition() {
		return thread == null ? 0 : thread.getCurrentPosition();
	}

	public long getDuration() {
		return thread == null ? 0 : thread.getDuration();
	}

	public String getFileName() {
		return fileName;
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

	public boolean isPaused() {
		return thread.isPaused();
	}

	public boolean isPlaying() {
		return isOpened() && !isPaused();
	}

	public boolean isStretchMode() {
		return stretchMode;
	}

	public boolean open(final String fileName) {
		return open(fileName, true);
	}

	public boolean open(final String fileName, final boolean loadFirstFrame) {
		if (isDisposed()) {
			throw new SWTException("Widget is disposed");
		}
		stop();
		this.fileName = fileName;
		if (!new File(fileName).exists()) {
			return false;
		}
		currentFrame = null;
		videoBuffer.repaint();
		thread = new PlayingThread(fileName, frameUpdater);
		if (!thread.startVideoThread()) {
			return false;
		}
		if (loadFirstFrame) {
			thread.seek(0);
		}
		fireVideoOpened();
		return true;
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

	public void play() {
		if (isDisposed()) {
			throw new SWTException("Widget is disposed");
		}
		if (thread != null) {
			thread.resumePlaying();
			firePlay();
		}
	}

	public void removePlayerListener(final PlayerListener listener) {
		playerListeners.remove(listener);
	}

	public boolean reopen() {
		return open(fileName);
	}

	public void seek(final long millisecond) {
		if (isDisposed()) {
			throw new SWTException("Widget is disposed");
		}
		if (thread != null) {
			thread.seek(millisecond);
		}
	}

	public void setStretchMode(final boolean stretchMode) {
		this.stretchMode = stretchMode;
		videoBuffer.repaint();
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
}
