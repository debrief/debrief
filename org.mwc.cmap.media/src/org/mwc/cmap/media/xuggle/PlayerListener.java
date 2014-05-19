package org.mwc.cmap.media.xuggle;

public interface PlayerListener {
	
	void onPlaying(XugglePlayer player, long milli);
	
	void onPlay(XugglePlayer player);
	
	void onStop(XugglePlayer player);
	
	void onPause(XugglePlayer player);
	
	void onSeek(XugglePlayer player, long milli);
	
	void onVideoOpened(XugglePlayer player, String fileName);
}
