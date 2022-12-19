/*
 * @(#)JDK10AudioClip.java  1.1  April 23, 2003
 *
 * Copyright (c) 2003 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.eightsvx;

//import sun.audio.*;

/**
 * A JDK 1.0 compatible audio clip for signed linear 8 encoded audio PCM samples.
 *
 * @author  Werner Randelshofer, Hausmatt 10, CH-6405 Goldau, Switzerland
 * @version 1.1 2003-04-23 Use com.sun.media.sound.JavaSoundAudioClip instead of
 * sun.applet.AppletAudioClip because applets may not access classes in the
 * 'sun.applet' package.
 */
/*public class JDK10AudioClip implements LoopableAudioClip {
    private int sampleRate;
    private byte[] samples;
    private AudioDataStream audioStream;*/
    
    /**
     * For constructing directly from Jar entries, or any other
     * raw Audio data. Note that the data provided must include the format
     * header.
     */
    /*public JDK10AudioClip(byte [] samples, int sampleRate) {
        this.samples = samples;
        this.sampleRate = sampleRate;
    }
    
    
    public synchronized void play() {
        stop();
        byte[] data = new byte[samples.length + 24];
        writeSunAudioHeader(data, sampleRate, samples.length);
        System.arraycopy(samples, 0, data, 24, samples.length);
        AudioData audioData = new AudioData(data);
        audioStream = new AudioDataStream(audioData);
        AudioPlayer.player.start(audioStream);
    }
    
    
    public synchronized void loop() {
        stop();
        byte[] data = new byte[samples.length + 24];
        writeSunAudioHeader(data, sampleRate, samples.length);
        System.arraycopy(samples, 0, data, 24, samples.length);
        AudioData audioData = new AudioData(data);
        AudioDataStream audioStream = new ContinuousAudioDataStream(audioData);
        AudioPlayer.player.start(audioStream);
    }*/
    
    
    /** Starts looping playback from the current position.   Playback will
     * continue to the loop's end point, then loop back to the loop start point
     * <code>count</code> times, and finally continue playback to the end of
     * the clip.
     * <p>
     * If the current position when this method is invoked is greater than the
     * loop end point, playback simply continues to the
     * end of the clip without looping.
     * <p>
     * A <code>count</code> value of 0 indicates that any current looping should
     * cease and playback should continue to the end of the clip.  The behavior
     * is undefined when this method is invoked with any other value during a
     * loop operation.
     * <p>
     * If playback is stopped during looping, the current loop status is
     * cleared; the behavior of subsequent loop and start requests is not
     * affected by an interrupted loop operation.
     *
     * @param count the number of times playback should loop back from the
     * loop's end position to the loop's  start position, or
     * <code>{@link #LOOP_CONTINUOUSLY}</code> to indicate that looping should
     * continue until interrupted
     *
     */
    /*public void loop(int count) {
        if (count == 1 || count == 0) play();
        else if (count == LOOP_CONTINUOUSLY) loop();
        else {
            // FIXME
            // We stop playback of this sound clip here. This does not conform
            // to the API specification of this method.
            stop();
            
            byte[] data = new byte[samples.length * count + 24];
            writeSunAudioHeader(data, sampleRate, samples.length * count);
            for (int i=0; i < count; i++) {
                System.arraycopy(samples, 0, data, 24 + i * samples.length, samples.length);
            }
            AudioData audioData = new AudioData(data);
            AudioDataStream audioStream = new ContinuousAudioDataStream(audioData);
            AudioPlayer.player.start(audioStream);
        }
        
    }
    
    public synchronized void stop() {
        if (audioStream != null) {
            AudioPlayer.player.stop(audioStream);
            audioStream = null;
        }
    }
    
    /**
     * Overwrites the 24 first bites of the provided data array with
     * a sun audio header.
     */
    /*public static void writeSunAudioHeader(byte[] data, int sampleRate, int datasize) {
        int headersize = 24;
        
        // create the header
        byte[] header = {
            // Sun magic = ".snd"
            (byte) 0x2e, (byte) 0x73, (byte) 0x6e, (byte) 0x64,
            // header size in bytes
            (byte) (headersize >>> 24 & 0xff), (byte) (headersize >>> 16 & 0xff),
            (byte) (headersize >>> 8 & 0xff), (byte) (headersize & 0xff),
            // data size in bytes
            (byte) (datasize >>> 24 & 0xff), (byte) (datasize >>> 16 & 0xff),
            (byte) (datasize >>> 8 & 0xff), (byte) (datasize & 0xff),
            // Sun uLaw format
            (byte) 0, (byte) 0, (byte) 0, (byte) 1,
            // sample rate (only 8000 is supported by Java 1.1)
            (byte) (sampleRate >>> 24 & 0xff), (byte) (sampleRate >>> 16 & 0xff),
            (byte) (sampleRate >>> 8 & 0xff), (byte) (sampleRate & 0xff),
            // one channel for mono (don't care for left or right speakers).
            (byte) 0, (byte) 0, (byte) 0, (byte) 1
        };
        
        // Create the output array
        System.arraycopy(header, 0, data, 0, headersize);
    }*/
//}
