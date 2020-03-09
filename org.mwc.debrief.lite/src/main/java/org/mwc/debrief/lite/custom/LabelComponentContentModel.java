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
import java.awt.Font;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.border.Border;

import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentContentModel;
import org.pushingpixels.neon.icon.ResizableIcon;
import org.pushingpixels.neon.icon.ResizableIcon.Factory;

/**
 * @author Ayesha
 *
 */
public class LabelComponentContentModel implements ComponentContentModel {

	public static class Builder {
		private final boolean isEnabled = true;
		private ResizableIcon.Factory iconFactory;
		private String caption;
		private RichTooltip richTooltip;
		private String text;
		private ActionListener actionListener;

		private Border border;
		private Color background;
		private Color foreground;
		private String name;
		private Font font;

		public LabelComponentContentModel build() {
			final LabelComponentContentModel model = new LabelComponentContentModel();
			model.setText(this.text);
			model.actionListener = this.actionListener;
			model.isEnabled = this.isEnabled;
			model.iconFactory = this.iconFactory;
			model.caption = this.caption;
			model.richTooltip = this.richTooltip;
			model.setBackground(this.background);
			model.setForeground(this.foreground);
			model.setName(this.name);
			model.setBorder(this.border);
			model.setFont(this.font);
			return model;
		}

		public Builder setBackground(final Color background) {
			this.background = background;
			return this;
		}

		public Builder setBorder(final Border border) {
			this.border = border;
			return this;
		}

		public Builder setCaption(final String caption) {
			this.caption = caption;
			return this;
		}

		public Builder setFont(final Font font) {
			this.font = font;
			return this;
		}

		public Builder setForeground(final Color foreground) {
			this.foreground = foreground;
			return this;
		}

		public Builder setName(final String name) {
			this.name = name;
			return this;
		}

		public Builder setRichTooltip(final RichTooltip richtooltip) {
			this.richTooltip = richtooltip;
			return this;
		}

		public Builder setText(final String text) {
			this.text = text;
			return this;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	private boolean isEnabled;
	private ResizableIcon.Factory iconFactory;
	private String caption;
	private String text;

	private RichTooltip richTooltip;
	private ActionListener actionListener;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private Border border;

	private Color background;

	private Color foreground;
	private String name;
	private Font font;

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);

	}

	public Color getBackground() {
		return background;
	}

	public Border getBorder() {
		return border;
	}

	@Override
	public String getCaption() {
		return caption;
	}

	public Font getFont() {
		return font;
	}

	public Color getForeground() {
		return foreground;
	}

	@Override
	public Factory getIconFactory() {
		return iconFactory;
	}

	public String getName() {
		return name;
	}

	@Override
	public RichTooltip getRichTooltip() {
		return richTooltip;
	}

	public String getText() {
		return text;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void removePropertyChangeListener(final PropertyChangeListener pcl) {
		pcs.removePropertyChangeListener(pcl);

	}

	public void setBackground(final Color background) {
		this.background = background;
	}

	public void setBorder(final Border border) {
		this.border = border;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (this.isEnabled != enabled) {
			this.isEnabled = enabled;
			this.pcs.firePropertyChange("enabled", !this.isEnabled, this.isEnabled);
		}

	}

	public void setFont(final Font font) {
		this.font = font;

	}

	public void setForeground(final Color foreground) {
		this.foreground = foreground;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setText(final String text) {
		final String oldText = this.text;
		this.text = text;
		this.pcs.firePropertyChange("text", oldText, this.text);
	}

}
