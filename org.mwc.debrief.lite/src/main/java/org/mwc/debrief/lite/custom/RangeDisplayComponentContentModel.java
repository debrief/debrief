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

package org.mwc.debrief.lite.custom;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentContentModel;
import org.pushingpixels.neon.api.icon.ResizableIcon;
import org.pushingpixels.neon.api.icon.ResizableIcon.Factory;

/**
 * @author Ayesha
 *
 */
public class RangeDisplayComponentContentModel implements ComponentContentModel {

	public static class Builder {
		private final boolean isEnabled = true;
		private ResizableIcon.Factory iconFactory;
		private String caption;
		private RichTooltip richTooltip;
		private String minValueText;
		private String maxValueText;
		private Color background;
		private Color foreground;
		private ActionListener actionListener;

		public RangeDisplayComponentContentModel build() {
			final RangeDisplayComponentContentModel model = new RangeDisplayComponentContentModel();
			model.setMinValueText(this.minValueText);
			model.setMaxValueText(this.maxValueText);
			model.actionListener = this.actionListener;
			model.isEnabled = this.isEnabled;
			model.iconFactory = this.iconFactory;
			model.caption = this.caption;
			model.richTooltip = this.richTooltip;
			model.setBackgroundColor(this.background);
			model.setForegroundColor(this.foreground);
			return model;
		}

		public Builder setBackground(final Color background) {
			this.background = background;
			return this;
		}

		public Builder setCaption(final String caption) {
			this.caption = caption;
			return this;
		}

		public Builder setForeground(final Color foreground) {
			this.foreground = foreground;
			return this;
		}

		public Builder setMaxValueText(final String text) {
			this.maxValueText = text;
			return this;
		}

		public Builder setMinValueText(final String text) {
			this.minValueText = text;
			return this;
		}

		public Builder setRichTooltip(final RichTooltip richtooltip) {
			this.richTooltip = richtooltip;
			return this;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	private boolean isEnabled;
	private ResizableIcon.Factory iconFactory;
	private String caption;
	private String minValueText;
	private String maxValueText;
	private Color backgroundColor;
	private Color foregroundColor;

	private RichTooltip richTooltip;

	private ActionListener actionListener;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);

	}

	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	@Override
	public String getCaption() {
		return caption;
	}

	/**
	 * @return the foregroundColor
	 */
	public Color getForegroundColor() {
		return foregroundColor;
	}

	@Override
	public Factory getIconFactory() {
		return iconFactory;
	}

	public String getMaxValueText() {
		return maxValueText;
	}

	public String getMinValueText() {
		return minValueText;
	}

	@Override
	public RichTooltip getRichTooltip() {
		return richTooltip;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void removePropertyChangeListener(final PropertyChangeListener pcl) {
		pcs.removePropertyChangeListener(pcl);

	}

	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(final Color backgroundColor) {
		final Color oldValue = this.backgroundColor;
		if (backgroundColor != null && !backgroundColor.equals(oldValue)) {
			this.backgroundColor = backgroundColor;
			this.pcs.firePropertyChange("background", oldValue, backgroundColor);
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (this.isEnabled != enabled) {
			this.isEnabled = enabled;
			this.pcs.firePropertyChange("enabled", !this.isEnabled, this.isEnabled);
		}
	}

	/**
	 * @param foregroundColor the foregroundColor to set
	 */
	public void setForegroundColor(final Color foregroundColor) {
		final Color oldValue = this.foregroundColor;
		if (foregroundColor != null && !foregroundColor.equals(oldValue)) {
			this.foregroundColor = foregroundColor;
			this.pcs.firePropertyChange("foreground", oldValue, foregroundColor);
		}
	}

	public void setMaxValueText(final String maxValueText) {
		final String oldValue = this.maxValueText;
		if (maxValueText != null && !maxValueText.equals(oldValue)) {
			this.maxValueText = maxValueText;
			this.pcs.firePropertyChange("maxValueText", oldValue, maxValueText);
		}
	}

	public void setMinValueText(final String minValueText) {
		final String oldValue = this.minValueText;
		if (minValueText != null && !minValueText.equals(oldValue)) {
			this.minValueText = minValueText;
			this.pcs.firePropertyChange("minValueText", oldValue, minValueText);
		}
	}

}
