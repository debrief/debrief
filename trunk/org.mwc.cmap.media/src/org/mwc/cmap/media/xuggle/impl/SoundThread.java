package org.mwc.cmap.media.xuggle.impl;

import java.util.LinkedList;
import java.util.Queue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IStreamCoder;

public class SoundThread extends Thread {
	private final AudioFormat audioFormat;
	private final SourceDataLine soundLine;
	private final boolean blocked;	
	private final Queue<byte[]> buffers = new LinkedList<byte[]>();
	
	private volatile boolean reset; 
	
	public SoundThread(IStreamCoder aAudioCoder, boolean blocked) throws LineUnavailableException {
		audioFormat = new AudioFormat(aAudioCoder.getSampleRate(),
    			(int)IAudioSamples.findSampleBitDepth(aAudioCoder.getSampleFormat()),
    			aAudioCoder.getChannels(),
    			true,
    			false);
    	DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
    	soundLine = (SourceDataLine) AudioSystem.getLine(info);
    	openSoundLine();
    	this.blocked = blocked;
    	
    	setDaemon(true);
	}	
	
	@Override
	public void interrupt() {
		if (isAlive()) {
			super.interrupt();
		} else {
			soundLine.close();
		}
	}

	public void play(IAudioSamples samples, boolean notify) {
		synchronized(buffers) {
			if (reset) {
				return;
			}
			buffers.add(samples.getData().getByteArray(0, samples.getSize()));
			if (notify) {
				buffers.notify();
			}
		}
	}
	
	public void reset() {
		synchronized(buffers) {
			reset = true;
			buffers.add(new byte[0]);
			buffers.notify();
		}
	}
	
	private void closeSoundLine() {
		soundLine.flush();
		soundLine.drain();
		soundLine.close();
	}
	
	private void openSoundLine() throws LineUnavailableException {
		soundLine.open(audioFormat);
		soundLine.start();
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				byte[] buffer;
				if (isInterrupted()) {
					break;
				}				
				try {
					synchronized (buffers) {
						if (buffers.isEmpty()) {
							buffers.wait();
						}
						buffer = buffers.poll();
						if (reset) {
							closeSoundLine();
							try {
								openSoundLine();
							} catch (LineUnavailableException ex) {
								ex.printStackTrace();
								break;
							}
							buffers.clear();
							reset = false;
							continue;
						}
						if (blocked) {
							soundLine.write(buffer, 0, buffer.length);
						}
					}
				} catch (InterruptedException ex) {
					break;
				}
				if (! blocked) {
					soundLine.write(buffer, 0, buffer.length);
				}
			}
		} finally {
			closeSoundLine();
		}
	}
}