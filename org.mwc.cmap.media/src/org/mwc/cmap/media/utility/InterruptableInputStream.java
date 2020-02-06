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

package org.mwc.cmap.media.utility;

import java.io.IOException;
import java.io.InputStream;

public abstract class InterruptableInputStream extends InputStream {

	private boolean interrupted;
	private final InputStream in;

	public InterruptableInputStream(final InputStream in) {
		this.in = in;
	}

	@Override
	public int available() throws IOException {
		doCheck();
		return in.available();
	}

	protected abstract void checkInterrupted() throws IOException;

	@Override
	public void close() throws IOException {
		doCheck();
		in.close();
	}

	private void doCheck() throws IOException {
		try {
			checkInterrupted();
		} catch (final IOException ex) {
			interrupted = true;
			throw ex;
		}
	}

	@Override
	public synchronized void mark(final int readlimit) {
		in.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	@Override
	public int read() throws IOException {
		doCheck();
		return in.read();
	}

	@Override
	public int read(final byte[] b) throws IOException {
		doCheck();
		return in.read(b);
	}

	@Override
	public int read(final byte[] b, final int off, final int len) throws IOException {
		doCheck();
		return in.read(b, off, len);
	}

	@Override
	public synchronized void reset() throws IOException {
		doCheck();
		in.reset();
	}

	@Override
	public long skip(final long n) throws IOException {
		doCheck();
		return in.skip(n);
	}

	public boolean wasInterrupted() {
		return interrupted;
	}
}
