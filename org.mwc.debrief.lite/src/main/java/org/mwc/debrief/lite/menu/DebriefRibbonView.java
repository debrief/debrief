/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.lite.menu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

import org.geotools.swing.JMapPane;
import org.mwc.debrief.lite.custom.JRibbonLabel;
import org.mwc.debrief.lite.custom.JRibbonSlider;
import org.mwc.debrief.lite.custom.LabelComponentContentModel;
import org.mwc.debrief.lite.custom.RibbonLabelProjection;
import org.mwc.debrief.lite.custom.RibbonSliderProjection;
import org.mwc.debrief.lite.custom.SliderComponentContentModel;
import org.mwc.debrief.lite.gui.FitToWindow;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;
import org.mwc.debrief.lite.gui.LiteStepControl;
import org.mwc.debrief.lite.gui.ZoomOut;
import org.mwc.debrief.lite.gui.custom.SubstanceCommandToggleWithMenuButtonUI;
import org.mwc.debrief.lite.map.AdvancedZoomInAction;
import org.mwc.debrief.lite.map.DragElementAction;
import org.mwc.debrief.lite.map.DragElementTool;
import org.mwc.debrief.lite.map.DragWholeFeatureElementTool;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.mwc.debrief.lite.map.RangeBearingAction;
import org.mwc.debrief.lite.map.RangeBearingTool;
import org.mwc.debrief.lite.util.ResizableIconFactory;
import org.mwc.debrief.lite.view.actions.PanCommandAction;
import org.opengis.referencing.operation.MathTransform;
import org.pushingpixels.flamingo.api.common.AbstractCommandButton;
import org.pushingpixels.flamingo.api.common.CommandButtonPresentationState;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandMenuButton;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.JCommandToggleMenuButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.model.ActionButtonModel;
import org.pushingpixels.flamingo.api.common.model.ActionToggleButtonModel;
import org.pushingpixels.flamingo.api.common.model.Command;
import org.pushingpixels.flamingo.api.common.model.Command.Builder;
import org.pushingpixels.flamingo.api.common.model.CommandButtonPresentationModel;
import org.pushingpixels.flamingo.api.common.model.CommandMenuContentModel;
import org.pushingpixels.flamingo.api.common.model.CommandToggleGroupModel;
import org.pushingpixels.flamingo.api.common.popup.JCommandPopupMenu;
import org.pushingpixels.flamingo.api.common.popup.model.AbstractPopupMenuPresentationModel;
import org.pushingpixels.flamingo.api.common.popup.model.CommandPopupMenuPresentationModel;
import org.pushingpixels.flamingo.api.common.projection.CommandButtonProjection;
import org.pushingpixels.flamingo.api.common.projection.CommandPopupMenuProjection;
import org.pushingpixels.flamingo.api.common.projection.Projection;
import org.pushingpixels.flamingo.api.common.projection.Projection.ComponentSupplier;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand.PresentationPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.projection.RibbonApplicationMenuCommandButtonProjection;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentPresentationModel;
import org.pushingpixels.flamingo.api.ribbon.synapse.projection.ComponentProjection;
import org.pushingpixels.flamingo.internal.substance.common.ui.SubstanceCommandToggleButtonUI;
import org.pushingpixels.flamingo.internal.ui.common.CommandButtonUI;
import org.pushingpixels.flamingo.internal.ui.ribbon.appmenu.RibbonApplicationMenuPanelProjection;

import MWC.GUI.Layers;
import MWC.GenericData.WorldDistance;

public class DebriefRibbonView
{
  private static ComponentProjection<JRibbonSlider, SliderComponentContentModel>
      addAlphaSlider(final ChangeListener alphaListener, final float alpha)
  {

    final SliderComponentContentModel sliderModel = SliderComponentContentModel
        .builder().setEnabled(true).setMinimum(0).setMaximum(100)
        .setMajorTickSpacing(20).setPaintTickSpacing(true).setPaintLabels(false)
        .setChangeListener(alphaListener).build();
    // set the values for the slider here.
    final ComponentSupplier<JRibbonSlider, SliderComponentContentModel, ComponentPresentationModel> jribbonSlider =
        (Projection<JRibbonSlider, SliderComponentContentModel, ComponentPresentationModel> projection) -> JRibbonSlider::new;
    final ComponentProjection<JRibbonSlider, SliderComponentContentModel> projection =
        new RibbonSliderProjection(sliderModel, ComponentPresentationModel
            .withDefaults(), jribbonSlider);
    JSlider slider = projection.buildComponent();
    slider.setToolTipText("Modify transparency");
    slider.setBackground(Color.DARK_GRAY);
    slider.setName("transparencyslider");
    slider.setValue((int) (alpha * 100f));
    return projection;
  }

  protected static void addViewTab(final JRibbon ribbon,
      final GeoToolMapRenderer geoMapRenderer, final Layers layers,
      final JLabel statusBar, final GeoToolMapProjection projection,
      final MathTransform transform, final ChangeListener alphaListener,
      final float alpha)
  {
    final JRibbonBand mouseMode = createMouseModes(geoMapRenderer, statusBar,
        layers, projection, transform);
    final JRibbonBand mapCommands = createMapCommands(geoMapRenderer, layers);

    // and the slider
    final JRibbonBand layersMenu = new JRibbonBand("Background", null);
    final ComponentProjection<JRibbonSlider, SliderComponentContentModel> slider =
        addAlphaSlider(alphaListener, alpha);
    layersMenu.addRibbonComponent(slider);

    LabelComponentContentModel timeLabelModel = LabelComponentContentModel
        .builder().setText("Transparency").build();
    final ComponentSupplier<JRibbonLabel, LabelComponentContentModel, ComponentPresentationModel> jTimeLabel =
        (Projection<JRibbonLabel, LabelComponentContentModel, ComponentPresentationModel> projection2) -> JRibbonLabel::new;
    RibbonLabelProjection timeLabelProjection = new RibbonLabelProjection(
        timeLabelModel, ComponentPresentationModel.withDefaults(), jTimeLabel);
    // final JLabel timeLabel = timeLabelProjection.buildComponent();
    // timeLabel.setPreferredSize(new Dimension(40,18));
    layersMenu.addRibbonComponent(timeLabelProjection);
    final RibbonTask viewTask = new RibbonTask("View", mouseMode, mapCommands,
        layersMenu);
    ribbon.addTask(viewTask);
  }

  private static JRibbonBand createMapCommands(
      final GeoToolMapRenderer geoMapRenderer, final Layers layers)
  {
    final JMapPane mapPane = (JMapPane) geoMapRenderer.getMap();
    final JRibbonBand commandBand = new JRibbonBand("Map commands", null);
    commandBand.startGroup();
    MenuUtils.addCommand("Zoom Out", "icons/24/zoomout.png", new ZoomOut(
        mapPane), commandBand, PresentationPriority.TOP);
    final FitToWindow doFit = new FitToWindow(layers, mapPane);
    MenuUtils.addCommand("Fit to Window", "icons/24/fit_to_win.png", doFit,
        commandBand, null);
    commandBand.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        commandBand));
    return commandBand;
  }

  public static class CustomCommand extends Command
  {
    private JPopupMenu popMenu;

    public CustomCommand()
    {

    }

    @Override
    public CommandButtonProjection<Command> project(
        CommandButtonPresentationModel commandPresentation)
    {
      return new CustomCommandButtonProjection(this, commandPresentation);
    }

    public void setPopMenu(JPopupMenu popMenu)
    {
      this.popMenu = popMenu;
    }

    public JPopupMenu getPopMenu()
    {
      return popMenu;
    }
  }

  public static class CustomBuilder extends Builder
  {
    private JPopupMenu popMenu;

    public CustomBuilder()
    {

    }

    @Override
    public Command build()
    {
      Command command = new CustomCommand();
      configureBaseCommand(command);

      return command;
    }

    public Builder setPopMenu(JPopupMenu popMenu)
    {
      this.popMenu = popMenu;
      return this;
    }

    @Override
    protected void configureBaseCommand(Command command)
    {
      super.configureBaseCommand(command);
      if (command instanceof CustomCommand)
      {
        ((CustomCommand) command).setPopMenu(this.popMenu);
      }
    }

  }

  public static class JCommandToggleWithMenuButton extends JCommandToggleButton
  {

    public JCommandToggleWithMenuButton(
        Projection<AbstractCommandButton, ? extends Command, CommandButtonPresentationModel> projection)
    {
      super(projection);

      addMouseListener(new MouseAdapter()
      {

        @Override
        public void mouseClicked(MouseEvent e)
        {
          if (!SubstanceCommandToggleWithMenuButtonUI.MENU_INDICATOR_POLYGON.contains(e.getPoint()))
          {
            super.mouseClicked(e);
          }
          else
          {
            final JPopupMenu popMenu = getPopupMenu();

            if (popMenu != null)
            {
              final Component component = (Component) e.getSource();
              popMenu.show(component, 0, 0);

              final Point p = component.getLocationOnScreen();
              popMenu.setLocation(p.x, p.y + component.getHeight());
            }
          }
        }

      });
    }

    /**
     * 
     */
    private static final long serialVersionUID = -4029977054020995335L;

    @Override
    public void updateUI()
    {
      this.setUI(SubstanceCommandToggleWithMenuButtonUI.createUI(this));
    }

    public JPopupMenu getPopupMenu()
    {
      if (command instanceof CustomCommand)
      {
        return ((CustomCommand) command).getPopMenu();
      }
      return null;
    }

  }

  public static class CustomCommandButtonProjection extends
      CommandButtonProjection
  {

    public CustomCommandButtonProjection(Command command,
        CommandButtonPresentationModel commandPresentation)
    {
      super(command, commandPresentation, getDefaultSupplier());
    }

    private static <M extends Command>
        ComponentSupplier<AbstractCommandButton, M, CommandButtonPresentationModel>
        getDefaultSupplier()
    {
      return (
          Projection<AbstractCommandButton, M, CommandButtonPresentationModel> projection) -> {
        if (projection.getPresentationModel().isMenu())
        {
          return projection.getContentModel().isToggle()
              ? JCommandToggleMenuButton::new : JCommandMenuButton::new;
        }
        else
        {
          return projection.getContentModel().isToggle()
              ? JCommandToggleWithMenuButton::new : JCommandButton::new;
        }
      };
    }
  }

  private static JRibbonBand createMouseModes(
      final GeoToolMapRenderer geoMapRenderer, final JLabel statusBar,
      final Layers layers, final GeoToolMapProjection projection,
      final MathTransform transform)
  {
    final JRibbonBand viewBand = new JRibbonBand("Mouse mode", null);
    final JMapPane mapPane = (JMapPane) geoMapRenderer.getMap();

    // group for the mosue mode radio buttons
    final CommandToggleGroupModel mouseModeGroup =
        new CommandToggleGroupModel();

    viewBand.startGroup();
    MenuUtils.addCommandToggleButton("Pan", "icons/24/hand.png",
        new PanCommandAction(mapPane), viewBand, PresentationPriority.TOP, true,
        mouseModeGroup, false);
    final AdvancedZoomInAction zoomInAction = new AdvancedZoomInAction(mapPane);
    // MenuUtils.addCommandToggleButton("Pan", "icons/24/hand.png", new PanAction(
    // mapPane)
    // {
    //
    // /**
    // *
    // */
    // private static final long serialVersionUID = 1072919666918011233L;
    //
    // @Override
    // public void actionPerformed(final ActionEvent ev)
    // {
    // getMapPane().setCursorTool(new PanTool()
    // {
    //
    // @Override
    // public void onMouseDragged(final MapMouseEvent ev)
    // {
    // if (ev.getButton() != MouseEvent.BUTTON3)
    // {
    // super.onMouseDragged(ev);
    // }
    // }
    //
    // @Override
    // public void onMousePressed(final MapMouseEvent ev)
    // {
    //
    // if (ev.getButton() != MouseEvent.BUTTON3)
    // {
    // super.onMousePressed(ev);
    // }
    // }
    // });
    // }
    //
    // }, viewBand, RibbonElementPriority.TOP, true, mouseModeGroup, false);
    // final ZoomInAction zoomInAction = new AdvancedZoomInAction(mapPane);
    MenuUtils.addCommandToggleButton("Zoom In", "icons/24/zoomin.png",
        zoomInAction, viewBand, PresentationPriority.TOP, true, mouseModeGroup,
        true);
    final RangeBearingAction rangeAction = new RangeBearingAction(mapPane,
        false, statusBar, transform);
    MenuUtils.addCommandToggleButton("Rng/Brg", "icons/24/rng_brg.png",
        rangeAction, viewBand, PresentationPriority.TOP, true, mouseModeGroup,
        false);

    final JPopupMenu menu = new JPopupMenu();
    // ButtonGroup for radio buttons
    final ButtonGroup unitsGroup = new ButtonGroup();

    final ActionListener changeUnits = new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        final String unit = e.getActionCommand();
        RangeBearingTool.setBearingUnit(WorldDistance.getUnitIndexFor(unit));
        geoMapRenderer.getMap().repaint();
      }
    };

    for (int i = 0; i < WorldDistance.UnitLabels.length; i++)
    {
      final JRadioButtonMenuItem unitRadioButton = new JRadioButtonMenuItem(
          WorldDistance.UnitLabels[i]);
      unitRadioButton.setSelected(RangeBearingTool.getBearingUnit() == i);
      unitRadioButton.addActionListener(changeUnits);
      menu.add(unitRadioButton);
      unitsGroup.add(unitRadioButton);
    }

    ImageWrapperResizableIcon imageIcon = null;
    final String imagePath = "icons/24/rng_brg.png";
    if (imagePath != null)
    {
      final Image zoominImage = MenuUtils.createImage(imagePath);
      imageIcon = ImageWrapperResizableIcon.getIcon(zoominImage,
          MenuUtils.ICON_SIZE_16);
    }
    // final Command.Builder builder = Command.builder()
    final CustomBuilder builder = new CustomBuilder();
    builder.setPopMenu(menu).setText("Rng").setIconFactory(ResizableIconFactory
        .factory(imageIcon)).setAction(rangeAction);
    // .setTitleClickAction();

    builder.setToggle();
    builder.setToggleSelected(false);
    builder.setSecondaryContentModel(DebriefRibbonFile.getSavePopupContentModel(
        null, null));
    builder.inToggleGroup(mouseModeGroup);
    final Command command = builder.build();
    CommandButtonProjection<Command> projectionModel = command.project(
        CommandButtonPresentationModel.builder().setActionKeyTip("NA")
            // .setPopupCallback(popupCallback)
            .setPopupMenuPresentationModel(CommandPopupMenuPresentationModel
                .builder().setMaxVisibleMenuCommands(4).build()).build());
    viewBand.addRibbonCommand(projectionModel, PresentationPriority.TOP);

    final DragElementAction dragWholeFeatureInAction = new DragElementAction(
        mapPane, new DragWholeFeatureElementTool(layers, projection, mapPane));
    MenuUtils.addCommandToggleButton("Drag Whole Feature",
        "icons/24/select_feature.png", dragWholeFeatureInAction, viewBand,
        PresentationPriority.TOP, true, mouseModeGroup, false);
    final DragElementAction dragElementInAction = new DragElementAction(mapPane,
        new DragElementTool(layers, projection, mapPane));
    MenuUtils.addCommandToggleButton("Drag Element",
        "icons/24/select_component.png", dragElementInAction, viewBand,
        PresentationPriority.TOP, true, mouseModeGroup, false);

    // tell the zoom in action that it's live
    zoomInAction.actionPerformed(null);

    viewBand.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        viewBand));
    return viewBand;
  }
}
