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

package com.planetmayo.debrief.satc.model.generator.jobs;

import org.eclipse.core.runtime.IProgressMonitor;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.log.LogService;
import com.planetmayo.debrief.satc.model.generator.IJobsManager;

public class SingleThreadJobsManager implements IJobsManager {
	public static class EmptyMonitor implements IProgressMonitor {
		private boolean canceled;

		@Override
		public void beginTask(final String name, final int totalWork) {
		}

		@Override
		public void done() {
		}

		@Override
		public void internalWorked(final double arg0) {
		}

		@Override
		public boolean isCanceled() {
			return canceled;
		}

		@Override
		public void setCanceled(final boolean value) {
			canceled = value;
		}

		@Override
		public void setTaskName(final String name) {
		}

		@Override
		public void subTask(final String name) {
		}

		@Override
		public void worked(final int work) {
		}
	}

	private IProgressMonitor currentMonitor;

	private Job<?, ?> currentJob;

	@Override
	public <T, P> void cancel(final Job<T, P> job) {
		// because this jobs manager has only one thread, method may be called only when
		// the job is already finished or from the job itself
		if (currentJob == job && !job.isComplete()) {
			currentMonitor.setCanceled(true);
		}
	}

	@Override
	public void cancelGroup(final String group) {
		if (group == null || currentJob == null) {
			return;
		}
		if (group.equals(currentJob.getGroup())) {
			currentMonitor.setCanceled(true);
		}
	}

	@Override
	public <T, P> Job<T, P> schedule(final Job<T, P> job) {
		return scheduleAfter(job, null);
	}

	@Override
	public <T, P, E> Job<T, P> scheduleAfter(final Job<T, P> job, final Job<P, E> previous) {
		final LogService log = LogFactory.getLog();
		if (previous != null && !previous.isComplete()) {
			throw new IllegalArgumentException("The job " + previous.getName() + " wasn't scheduled");
		}
		try {
			currentJob = job;
			currentMonitor = new EmptyMonitor();
			job.startJob(currentMonitor, previous);
		} catch (final InterruptedException ex) {
			log.warn("Job: " + job.getName() + " is canceled");
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			currentJob = null;
			currentMonitor = null;
		}
		return job;
	}

	@Override
	public <T, P> void waitFor(final Job<T, P> job) throws InterruptedException {
		// because this jobs manager has only one thread, method may be called only when
		// the job is already finished - do nothing
	}
}
