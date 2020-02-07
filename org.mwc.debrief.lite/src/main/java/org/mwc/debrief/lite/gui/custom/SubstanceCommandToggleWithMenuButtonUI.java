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
package org.mwc.debrief.lite.gui.custom;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import org.mwc.debrief.lite.menu.DebriefRibbonView.JCommandToggleWithMenuButton;
import org.pushingpixels.flamingo.api.common.AbstractCommandButton;
import org.pushingpixels.flamingo.api.common.CommandButtonLayoutManager;
import org.pushingpixels.flamingo.api.common.CommandButtonPresentationState;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.JCommandToggleMenuButton;
import org.pushingpixels.flamingo.api.ribbon.AbstractRibbonBand;
import org.pushingpixels.flamingo.internal.substance.common.GlowingResizableIcon;
import org.pushingpixels.flamingo.internal.substance.common.ui.ActionPopupTransitionAwareUI;
import org.pushingpixels.flamingo.internal.substance.utils.CommandButtonBackgroundDelegate;
import org.pushingpixels.flamingo.internal.substance.utils.CommandButtonVisualStateTracker;
import org.pushingpixels.flamingo.internal.substance.utils.SubstanceDisabledResizableIcon;
import org.pushingpixels.flamingo.internal.ui.common.BasicCommandToggleButtonUI;
import org.pushingpixels.neon.icon.ResizableIconUIResource;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceCortex;
import org.pushingpixels.substance.api.SubstanceSlices.AnimationFacet;
import org.pushingpixels.substance.api.SubstanceSlices.ComponentStateFacet;
import org.pushingpixels.substance.api.painter.border.SubstanceBorderPainter;
import org.pushingpixels.substance.api.painter.fill.SubstanceFillPainter;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;
import org.pushingpixels.substance.api.shaper.SubstanceButtonShaper;
import org.pushingpixels.substance.internal.animation.StateTransitionTracker;
import org.pushingpixels.substance.internal.animation.StateTransitionTracker.ModelStateInfo;
import org.pushingpixels.substance.internal.utils.ButtonBackgroundDelegate;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceSizeUtils;
import org.pushingpixels.substance.internal.utils.SubstanceTextUtilities;
import org.pushingpixels.substance.internal.utils.WidgetUtilities;
import org.pushingpixels.substance.internal.utils.icon.TransitionAware;
import org.pushingpixels.substance.internal.widget.animation.effects.GhostPaintingUtils;
import org.pushingpixels.substance.internal.widget.animation.effects.GhostingListener;

/**
 * UI for command buttons {@link JCommandToggleButton} in <b>Substance </b> look
 * and feel.
 *
 * @author Kirill Grouchnikov
 */
public class SubstanceCommandToggleWithMenuButtonUI extends BasicCommandToggleButtonUI
		implements ActionPopupTransitionAwareUI {

	public final static Polygon MENU_INDICATOR_POLYGON = new Polygon(new int[] { 38, 51, 51 }, new int[] { 35, 20, 35 },
			3);

	public static ComponentUI createUI(final JComponent c) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(c);
		return new SubstanceCommandToggleWithMenuButtonUI();
	}

	/**
	 * Property change listener.
	 */
	private PropertyChangeListener substancePropertyListener;

	/**
	 * Model change listener for ghost image effects.
	 */
	private GhostingListener substanceModelChangeListener;

	/**
	 * Tracker for visual state transitions.
	 */
	private CommandButtonVisualStateTracker substanceVisualStateTracker;

	/**
	 * The matching glowing icon. Is used only when
	 * {@link SubstanceCortex.ComponentScope#isAnimationAllowed(Component, AnimationFacet)}
	 * returns true on {@link AnimationFacet#ICON_GLOW}.
	 */
	private GlowingResizableIcon glowingIcon;

	/**
	 * Creates a new UI delegate for ribbon button.
	 */
	SubstanceCommandToggleWithMenuButtonUI() {
		super();
		/**
		 * Delegate for painting the background.
		 */
		final ButtonBackgroundDelegate backgroundDelegate = new ButtonBackgroundDelegate();

		this.substanceVisualStateTracker = new CommandButtonVisualStateTracker();
	}

	protected void drawMenuIndicator(final Graphics2D graphic) {
		if (this.commandButton instanceof JCommandToggleWithMenuButton) {
			final JCommandToggleWithMenuButton commandWithMenu = (JCommandToggleWithMenuButton) this.commandButton;
			if (commandWithMenu.getPopupMenu() != null) {
				graphic.fillPolygon(MENU_INDICATOR_POLYGON);
			}
		}
	}

	@Override
	public StateTransitionTracker getActionTransitionTracker() {
		return this.substanceVisualStateTracker.getActionStateTransitionTracker();
	}

	protected Color getForegroundColor(final ModelStateInfo modelStateInfo) {
		Color fgColor = this.commandButton.getForeground();
		if (fgColor instanceof UIResource) {
			final float buttonAlpha = SubstanceColorSchemeUtilities.getAlpha(this.commandButton,
					modelStateInfo.getCurrModelState());
			fgColor = SubstanceTextUtilities.getForegroundColor(this.commandButton, this.commandButton.getText(),
					modelStateInfo, buttonAlpha);
		}
		return fgColor;
	}

	@Override
	public StateTransitionTracker getPopupTransitionTracker() {
		return this.substanceVisualStateTracker.getPopupStateTransitionTracker();
	}

	@Override
	public Dimension getPreferredSize(final JComponent c) {
		final AbstractCommandButton button = (AbstractCommandButton) c;
		final SubstanceButtonShaper shaper = SubstanceCoreUtilities.getButtonShaper(button);

		final Dimension superPref = super.getPreferredSize(button);
		if (superPref == null)
			return null;

		if (shaper == null)
			return superPref;

		// fix for issue 35 on Flamingo - do not enforce
		// min size on buttons in the ribbon
		if ((button.getPresentationState() == CommandButtonPresentationState.MEDIUM)
				&& (SwingUtilities.getAncestorOfClass(AbstractRibbonBand.class, button) == null)) {
			final JButton dummy = new JButton(button.getText(), button.getIcon());
			return shaper.getPreferredSize(dummy, superPref);
		}
		return superPref;
	}

	@Override
	public StateTransitionTracker getTransitionTracker() {
		return this.substanceVisualStateTracker.getActionStateTransitionTracker();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		SubstanceCortex.ComponentScope.setButtonShaper(this.commandButton, ClassicButtonShaper.INSTANCE);

		this.commandButton.setOpaque(false);
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		this.substanceVisualStateTracker.installListeners(this.commandButton);

		this.substancePropertyListener = (final PropertyChangeEvent evt) -> {
			if (AbstractButton.MODEL_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
				if (substanceModelChangeListener != null)
					substanceModelChangeListener.unregisterListeners();
				substanceModelChangeListener = new GhostingListener(commandButton, commandButton.getActionModel());
				substanceModelChangeListener.registerListeners();
			}
			if ("icon".equals(evt.getPropertyName())) {
				trackGlowingIcon();
			}
		};
		this.commandButton.addPropertyChangeListener(this.substancePropertyListener);

		this.substanceModelChangeListener = new GhostingListener(this.commandButton,
				this.commandButton.getActionModel());
		this.substanceModelChangeListener.registerListeners();

		this.trackGlowingIcon();
	}

	@Override
	public boolean isInside(final MouseEvent me) {
		return this.getLayoutInfo().actionClickArea.contains(me.getPoint());
	}

	@Override
	protected boolean isPaintingBackground() {
		if (super.isPaintingBackground()) {
			return true;
		}
		return this.commandButton.hasFocus()
				|| (this.getActionTransitionTracker().getFacetStrength(ComponentStateFacet.ROLLOVER) > 0.0f);
	}

	@Override
	protected boolean isPaintingSeparators() {
		return false;
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		final Graphics2D g2d = (Graphics2D) g.create();
		g2d.setFont(this.commandButton.getFont());

		this.layoutInfo = this.layoutManager.getLayoutInfo(this.commandButton);
		commandButton.putClientProperty("icon.bounds", layoutInfo.iconRect);

		if (this.isPaintingBackground()) {
			this.paintButtonBackground(g2d, new Rectangle(0, 0, c.getWidth(), c.getHeight()));
		}

		// action model should be used to
		// compute the foreground color of the command button's text
		final ModelStateInfo modelStateInfo = this.substanceVisualStateTracker.getActionStateTransitionTracker()
				.getModelStateInfo();
		final Color fgColor = this.getForegroundColor(modelStateInfo);

		if (layoutInfo.textLayoutInfoList != null) {
			for (final CommandButtonLayoutManager.TextLayoutInfo mainTextLayoutInfo : layoutInfo.textLayoutInfoList) {
				if (mainTextLayoutInfo.text != null) {
					SubstanceTextUtilities.paintText(g2d, c, mainTextLayoutInfo.textRect, mainTextLayoutInfo.text, -1,
							g2d.getFont(), fgColor, g2d.getClipBounds());
				}
			}
		}

		if (layoutInfo.extraTextLayoutInfoList != null) {
			Color disabledFgColor = SubstanceColorSchemeUtilities
					.getColorScheme(this.commandButton, ComponentState.DISABLED_UNSELECTED).getForegroundColor();
			final float buttonAlpha = SubstanceColorSchemeUtilities.getAlpha(this.commandButton,
					ComponentState.DISABLED_UNSELECTED);
			if (buttonAlpha < 1.0f) {
				final Color bgFillColor = SubstanceColorUtilities.getBackgroundFillColor(this.commandButton);
				disabledFgColor = SubstanceColorUtilities.getInterpolatedColor(disabledFgColor, bgFillColor,
						buttonAlpha);
			}
			if (modelStateInfo.getCurrModelState().isDisabled()) {
				disabledFgColor = SubstanceColorUtilities.getInterpolatedColor(disabledFgColor,
						SubstanceColorUtilities.getBackgroundFillColor(c), 0.5);
			}
			for (final CommandButtonLayoutManager.TextLayoutInfo extraTextLayoutInfo : layoutInfo.extraTextLayoutInfoList) {
				if (extraTextLayoutInfo.text != null) {
					SubstanceTextUtilities.paintText(g2d, c, extraTextLayoutInfo.textRect, extraTextLayoutInfo.text, -1,
							g2d.getFont(), disabledFgColor, g2d.getClipBounds());
				}
			}
		}

		if (layoutInfo.iconRect != null) {
			this.paintButtonIcon(g2d, layoutInfo.iconRect);
		}

		final float focusRingPadding = SubstanceSizeUtils
				.getFocusRingPadding(SubstanceSizeUtils.getComponentFontSize(this.commandButton));
		final Rectangle actionClickArea = layoutInfo.actionClickArea;
		final Shape focusArea = new Rectangle2D.Float(actionClickArea.x + focusRingPadding,
				actionClickArea.y + focusRingPadding, actionClickArea.width - 2 * focusRingPadding,
				actionClickArea.height - 2 * focusRingPadding);
		SubstanceCoreUtilities.paintFocus(g2d, this.commandButton, this.commandButton, this, focusArea,
				layoutInfo.actionClickArea, 1.0f, 0);

		drawMenuIndicator(g2d);

		g2d.dispose();
	}

	private void paintButtonBackground(final Graphics graphics, final Rectangle toFill) {
		if (SubstanceCoreUtilities.isButtonNeverPainted(this.commandButton))
			return;

		final ButtonModel actionModel = this.commandButton.getActionModel();

		final SubstanceFillPainter fillPainter = SubstanceCoreUtilities.getFillPainter(this.commandButton);
		final SubstanceBorderPainter borderPainter = SubstanceCoreUtilities.getBorderPainter(this.commandButton);

		final boolean ignoreSelections = this.commandButton instanceof JCommandToggleMenuButton;
		final BufferedImage fullAlphaBackground = CommandButtonBackgroundDelegate.getFullAlphaBackground(
				this.commandButton, actionModel, fillPainter, borderPainter, this.commandButton.getWidth(),
				this.commandButton.getHeight(), this.getActionTransitionTracker(), ignoreSelections);

		final StateTransitionTracker.ModelStateInfo modelStateInfo = getActionTransitionTracker().getModelStateInfo();
		final Map<ComponentState, StateTransitionTracker.StateContributionInfo> activeStates = ignoreSelections
				? modelStateInfo.getStateNoSelectionContributionMap()
				: modelStateInfo.getStateContributionMap();

		// Two special cases here:
		// 1. Button has flat appearance and does not have focus
		// 2. Button is disabled.
		// For both cases, we need to set custom translucency.
		final boolean isFlat = this.commandButton.isFlat() && !this.commandButton.hasFocus();
		final boolean isSpecial = isFlat || !this.commandButton.isEnabled();
		float extraAlpha = 1.0f;
		if (isSpecial) {
			if (isFlat) {
				extraAlpha = 0.0f;
				for (final Map.Entry<ComponentState, StateTransitionTracker.StateContributionInfo> activeEntry : activeStates
						.entrySet()) {
					final ComponentState activeState = activeEntry.getKey();
					if (activeState.isDisabled())
						continue;
					if (activeState == ComponentState.ENABLED)
						continue;
					extraAlpha += activeEntry.getValue().getContribution();
				}
			} else {
				final ComponentState actionAreaState = ComponentState.getState(actionModel, this.commandButton);
				if (actionAreaState.isDisabled()) {
					extraAlpha = SubstanceColorSchemeUtilities.getAlpha(this.commandButton, actionAreaState);
				}
			}
		}
		// System.out.println(extraAlpha);
		extraAlpha = Math.min(1.0f, extraAlpha);
		if (extraAlpha > 0.0f) {
			final Graphics2D g2d = (Graphics2D) graphics.create();
			g2d.setComposite(WidgetUtilities.getAlphaComposite(this.commandButton, extraAlpha, graphics));
			org.pushingpixels.neon.NeonCortex.drawImage(g2d, fullAlphaBackground, 0, 0);
			g2d.dispose();
		}
	}

	protected void paintButtonIcon(final Graphics g, final Rectangle iconRect) {
		final JCommandToggleButton jctb = (JCommandToggleButton) this.commandButton;
		Icon regular = jctb.getIcon();
		if (toUseDisabledIcon() && (jctb.getDisabledIcon() != null)
				&& ((regular != null) && !regular.getClass().isAnnotationPresent(TransitionAware.class))) {
			regular = jctb.getDisabledIcon();
		}

		if ((iconRect == null) || (regular == null) || (iconRect.width == 0) || (iconRect.height == 0)) {
			return;
		}

		if (regular != null) {
			final Graphics2D g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

			GhostPaintingUtils.paintGhostIcon(g2d, jctb, regular, iconRect);
			g2d.setComposite(WidgetUtilities.getAlphaComposite(jctb, g));

			CommandButtonBackgroundDelegate.paintCommandButtonIcon(g2d, iconRect, jctb, regular, glowingIcon,
					jctb.getActionModel(), this.substanceVisualStateTracker.getActionStateTransitionTracker());
			g2d.dispose();
		}
	}

	@Override
	protected void syncDisabledIcon() {
		final org.pushingpixels.neon.icon.ResizableIcon currDisabledIcon = this.commandButton.getDisabledIcon();
		final org.pushingpixels.neon.icon.ResizableIcon icon = this.commandButton.getIcon();
		if ((currDisabledIcon == null) || ((currDisabledIcon instanceof UIResource)
				&& !currDisabledIcon.getClass().isAnnotationPresent(TransitionAware.class))) {
			if (icon != null) {
				this.commandButton
						.setDisabledIcon(new ResizableIconUIResource(new SubstanceDisabledResizableIcon(icon)));
			} else {
				this.commandButton.setDisabledIcon(null);
			}
		} else {
			// disabled icon coming from app code
			if (icon != null) {
				this.commandButton.getDisabledIcon()
						.setDimension(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
			}
		}
	}

	/**
	 * Tracks possible usage of glowing icon.
	 */
	protected void trackGlowingIcon() {
		final org.pushingpixels.neon.icon.ResizableIcon currIcon = this.commandButton.getIcon();
		if (currIcon instanceof GlowingResizableIcon)
			return;
		if (currIcon == null)
			return;
		this.glowingIcon = new GlowingResizableIcon(currIcon,
				this.substanceVisualStateTracker.getActionStateTransitionTracker().getIconGlowTracker());
	}

	@Override
	protected void uninstallListeners() {
		this.substanceVisualStateTracker.uninstallListeners(this.commandButton);
		this.substanceVisualStateTracker = null;

		this.commandButton.removePropertyChangeListener(this.substancePropertyListener);
		this.substancePropertyListener = null;

		this.substanceModelChangeListener.unregisterListeners();
		this.substanceModelChangeListener = null;

		super.uninstallListeners();
	}

	@Override
	protected void updateBorder() {
		final Border currBorder = this.commandButton.getBorder();
		if ((currBorder == null) || (currBorder instanceof UIResource)) {
			final Insets extra = SubstanceSizeUtils
					.getDefaultBorderInsets(SubstanceSizeUtils.getComponentFontSize(this.commandButton));
			final double hgapScaleFactor = this.commandButton.getHGapScaleFactor();
			final double vgapScaleFactor = this.commandButton.getVGapScaleFactor();

			final int top = 1 + (int) (vgapScaleFactor * extra.top);
			final int left = 2 + (int) (hgapScaleFactor * (1 + extra.left));
			final int bottom = 0 + (int) (vgapScaleFactor * extra.bottom);
			final int right = 2 + (int) (hgapScaleFactor * (1 + extra.right));
			this.commandButton.setBorder(new BorderUIResource.EmptyBorderUIResource(top, left, bottom, right));
		}
	}
}
