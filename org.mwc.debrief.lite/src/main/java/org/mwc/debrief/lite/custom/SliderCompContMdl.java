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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;

import javax.swing.event.ChangeListener;

import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentContentModel;
import org.pushingpixels.neon.api.icon.ResizableIcon;

/**
 * @author Ayesha
 *
 */
public class SliderCompContMdl implements ComponentContentModel {
	public static class Builder {
		private boolean isEnabled = true;
		private ResizableIcon.Factory iconFactory;
		private String caption;
		private RichTooltip richTooltip;
		private String text;
		private ChangeListener changeListener;

		private int minimum;
		private int maximum;

		private int value;

		private int majorTickSpacing;
		private int minorTickSpacing;
		private boolean paintTickSpacing;
		private boolean paintLabels;

		public SliderCompContMdl build() {
			final SliderCompContMdl model = new SliderCompContMdl();
			model.setText(this.text);
			model.changeListener = this.changeListener;
			model.isEnabled = this.isEnabled;
			model.iconFactory = this.iconFactory;
			model.caption = this.caption;
			model.richTooltip = this.richTooltip;
			model.majorTickSpacing = this.majorTickSpacing;
			model.minorTickSpacing = this.minorTickSpacing;
			model.setValue(this.value);
			model.setMinimum(this.minimum);
			model.setMaximum(this.maximum);
			model.paintLabels = this.paintLabels;
			model.paintTickSpacing = this.paintTickSpacing;
			return model;
		}

		public int getMajorTickSpacing() {
			return majorTickSpacing;
		}

		public int getMaximum() {
			return maximum;
		}

		public int getMinimum() {
			return minimum;
		}

		public int getMinorTickSpacing() {
			return minorTickSpacing;
		}

		public int getValue() {
			return value;
		}

		public boolean isPaintLabels() {
			return paintLabels;
		}

		public boolean isPaintTickSpacing() {
			return paintTickSpacing;
		}

		public Builder setCaption(final String caption) {
			this.caption = caption;
			return this;
		}

		public Builder setChangeListener(final ChangeListener changeListener) {
			this.changeListener = changeListener;
			return this;
		}

		public Builder setEnabled(final boolean enabled) {
			this.isEnabled = enabled;
			return this;
		}

		public Builder setIconFactory(final ResizableIcon.Factory iconFactory) {
			this.iconFactory = iconFactory;
			return this;
		}

		public Builder setMajorTickSpacing(final int majorTickSpacing) {
			this.majorTickSpacing = majorTickSpacing;
			return this;
		}

		public Builder setMaximum(final Calendar date) {
			return setMaximum(toInt(date));
		}

		public Builder setMaximum(final int maximum) {
			this.maximum = maximum;
			return this;
		}

		public Builder setMinimum(final Calendar date) {
			return setMinimum(toInt(date));
		}

		public Builder setMinimum(final int minimum) {
			this.minimum = minimum;
			return this;
		}

		public Builder setMinorTickSpacing(final int minorTickSpacing) {
			this.minorTickSpacing = minorTickSpacing;
			return this;
		}

		public Builder setPaintLabels(final boolean paintLabels) {
			this.paintLabels = paintLabels;
			return this;
		}

		public Builder setPaintTickSpacing(final boolean paintTickSpacing) {
			this.paintTickSpacing = paintTickSpacing;
			return this;
		}

		public Builder setRichTooltip(final RichTooltip richTooltip) {
			this.richTooltip = richTooltip;
			return this;
		}

		public Builder setText(final String text) {
			this.text = text;
			return this;
		}

		public Builder setValue(final int value) {
			this.value = value;
			return this;
		}

		private int toInt(final Calendar date) {
			return (int) (date.getTimeInMillis() / 1000L);
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
	private ChangeListener changeListener;
	private int majorTickSpacing;
	private int minorTickSpacing;
	private boolean paintTickSpacing;
	private boolean paintLabels;

	private int minimum;

	private int maximum;

	private int value;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener pcl) {
		this.pcs.addPropertyChangeListener(pcl);

	}

	@Override
	public String getCaption() {
		return caption;
	}

	/**
	 * @return the changeListener
	 */
	public ChangeListener getChangeListener() {
		return changeListener;
	}

	@Override
	public ResizableIcon.Factory getIconFactory() {
		return iconFactory;
	}

	/**
	 * @return the majorTickSpacing
	 */
	public int getMajorTickSpacing() {
		return majorTickSpacing;
	}

	public int getMaximum() {
		return maximum;
	}

	public int getMinimum() {
		return minimum;
	}

	/**
	 * @return the minorTickSpacing
	 */
	public int getMinorTickSpacing() {
		return minorTickSpacing;
	}

	@Override
	public RichTooltip getRichTooltip() {
		return richTooltip;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	public int getValue() {
		return value;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * @return the paintLabels
	 */
	public boolean isPaintLabels() {
		return paintLabels;
	}

	/**
	 * @return the paintTickSpacing
	 */
	public boolean isPaintTickSpacing() {
		return paintTickSpacing;
	}

	@Override
	public void removePropertyChangeListener(final PropertyChangeListener pcl) {
		this.pcs.removePropertyChangeListener(pcl);

	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (this.isEnabled != enabled) {
			this.isEnabled = enabled;
			this.pcs.firePropertyChange("enabled", !this.isEnabled, this.isEnabled);
		}

	}

	public void setIconFactory(final ResizableIcon.Factory iconFactory) {
		this.iconFactory = iconFactory;
	}

	/**
	 * @param majorTickSpacing the majorTickSpacing to set
	 */
	public void setMajorTickSpacing(final int majorTickSpacing) {
		if (this.majorTickSpacing != majorTickSpacing) {
			final int oldValue = this.majorTickSpacing;
			this.majorTickSpacing = majorTickSpacing;
			this.pcs.firePropertyChange("majorTickSpacing", oldValue, majorTickSpacing);
		}
	}

	public void setMaximum(final int maximum) {
		if (this.maximum != maximum) {
			final int oldValue = this.maximum;
			this.maximum = maximum;
			this.pcs.firePropertyChange("maximum", oldValue, maximum);
		}
	}

	public void setMinimum(final int minimum) {
		if (this.minimum != minimum) {
			final int oldValue = this.minimum;
			this.minimum = minimum;
			this.pcs.firePropertyChange("minimum", oldValue, minimum);
		}
	}

	/**
	 * @param minorTickSpacing the minorTickSpacing to set
	 */
	public void setMinorTickSpacing(final int minorTickSpacing) {
		if (this.minorTickSpacing != minorTickSpacing) {
			final int oldValue = this.minorTickSpacing;
			this.minorTickSpacing = minorTickSpacing;
			this.pcs.firePropertyChange("minorTickSpacing", oldValue, minorTickSpacing);
		}
	}

	/**
	 * @param paintLabels the paintLabels to set
	 */
	public void setPaintLabels(final boolean paintLabels) {
		if (this.paintLabels != paintLabels) {
			this.paintLabels = paintLabels;
			this.pcs.firePropertyChange("paintTickSpacing", !paintLabels, paintLabels);
		}
	}

	/**
	 * @param paintTickSpacing the paintTickSpacing to set
	 */
	public void setPaintTickSpacing(final boolean paintTickSpacing) {
		if (this.paintTickSpacing != paintTickSpacing) {
			this.paintTickSpacing = paintTickSpacing;
			this.pcs.firePropertyChange("paintTickSpacing", !paintTickSpacing, paintTickSpacing);
		}
	}

	public void setRichTooltip(final RichTooltip richTooltip) {
		this.richTooltip = richTooltip;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(final String text) {
		this.text = text;
	}

	public void setValue(final int value) {
		if (this.value != value) {
			final int oldValue = this.value;
			this.value = value;
			this.pcs.firePropertyChange("value", oldValue, value);
		}
	}

}
