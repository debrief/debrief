package org.mwc.cmap.NarrativeViewer2;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;
import org.eclipse.nebula.widgets.nattable.extension.nebula.richtext.MarkupDisplayConverter;
import org.eclipse.nebula.widgets.nattable.extension.nebula.richtext.RegexMarkupValue;
import org.eclipse.nebula.widgets.nattable.extension.nebula.richtext.RichTextCellPainter;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.AutomaticRowHeightTextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.BackgroundPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.BeveledBorderDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.PaddingDecorator;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.SelectionStyleLabels;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.mwc.cmap.NarrativeViewer.preferences.NarrativeViewerPrefsPage;

/**
 * Custom styling configuration for the narrative viewer.
 */
public class NarrativeViewerStyleConfiguration extends
    AbstractRegistryConfiguration
{
  final private IPreferenceStore store;

  private static final String[] PHRASES_COLORS = new String[]
  {" 178, 180, 255", "147, 232, 207", "232, 205, 167", " 255, 192, 215",};

  private final RegexMarkupValue searchHighlighter = new RegexMarkupValue("",
      "<span style=\"background-color:rgb(255, 251, 204)\">", "</span>");

  private final RegexMarkupValue selectionSearchHighlighter =
      new RegexMarkupValue(
          "",
          "<span style=\"color:rgb(0, 0, 0)\"><span style=\"background-color:rgb(255, 251, 204)\">",
          "</span></span>");

  // default / body configuration
  private final Color bgColor = GUIHelper.COLOR_WHITE;
  private final Color fgColor = GUIHelper.COLOR_BLACK;
  private final Color selectionBgColor = GUIHelper.COLOR_LIST_SELECTION;
  private final Color selectionFgColor = GUIHelper.COLOR_WHITE;
  private final Color selectionAnchorSelectionBgColor =
      GUIHelper.COLOR_LIST_SELECTION;

  private final Color selectionAnchorSelectionFgColor = GUIHelper.COLOR_WHITE;
  private final Font font = GUIHelper.DEFAULT_FONT;

  private final HorizontalAlignmentEnum hAlign = HorizontalAlignmentEnum.LEFT;
  private final VerticalAlignmentEnum vAlign = VerticalAlignmentEnum.MIDDLE;
  private final ICellPainter cellPainter = new BackgroundPainter(
      new PaddingDecorator(new RichTextCellPainter(true, true, false), 0, 5, 0,
          5, false));

  // customized painter that also supports shrinking
  private final RichTextCellPainter wrappingAutomaticRowHeightPainter =
      new RichTextCellPainter(true, false, true)
      {
        @Override
        protected boolean performRowResize(final int contentHeight,
            final Rectangle rectangle)
        {
          return ((contentHeight != rectangle.height) && this.calculateByTextHeight);
        };
      };

  private final RichTextCellPainter automaticRowHeightPainter =
      new RichTextCellPainter(false, false, true)
      {
        @Override
        protected boolean performRowResize(final int contentHeight,
            final Rectangle rectangle)
        {
          return ((contentHeight != rectangle.height) && this.calculateByTextHeight);
        };
      };
  public final ICellPainter wrappingEntryLogPainter =
      new BackgroundPainter(new PaddingDecorator(
          wrappingAutomaticRowHeightPainter, 0, 5, 0, 5, false));
  public final ICellPainter automaticRowHeightLogPainter =
      new BackgroundPainter(new PaddingDecorator(automaticRowHeightPainter, 0,
          5, 0, 5, false));

  // column header configuration
  private final Color headerBgColor = GUIHelper.COLOR_WIDGET_BACKGROUND;

  private final Color headerFgColor = GUIHelper.COLOR_WIDGET_FOREGROUND;
  private final Color headerSelectionBgColor = GUIHelper.COLOR_GRAY;
  private final Color headerSelectionFgColor = GUIHelper.COLOR_WHITE;
  private final Font headerFont = GUIHelper.DEFAULT_FONT;

  private final HorizontalAlignmentEnum headerHAlign =
      HorizontalAlignmentEnum.LEFT;
  private final VerticalAlignmentEnum headerVAlign =
      VerticalAlignmentEnum.MIDDLE;
  private final Boolean renderGridLines = Boolean.FALSE;

  private final ICellPainter columnHeaderCellPainter =
      new BeveledBorderDecorator(new PaddingDecorator(
          new AutomaticRowHeightTextPainter(), 0, 5, 2, 5, true));

  private MarkupDisplayConverter selectionMarkupConverter;

  private MarkupDisplayConverter markupConverter;

  private final List<String> phrasesMarkups = new ArrayList<String>();

  public NarrativeViewerStyleConfiguration(final IPreferenceStore store)
  {
    this.store = store;
  }

  /**
   * Configure the customized column header style.
   * 
   * @param configRegistry
   */
  private void configureColumnHeaderStyle(final IConfigRegistry configRegistry)
  {
    // special style configuration for the column header
    final Style columnHeaderCellStyle = new Style();
    columnHeaderCellStyle.setAttributeValue(
        CellStyleAttributes.BACKGROUND_COLOR, this.headerBgColor);
    columnHeaderCellStyle.setAttributeValue(
        CellStyleAttributes.FOREGROUND_COLOR, this.headerFgColor);
    columnHeaderCellStyle.setAttributeValue(
        CellStyleAttributes.HORIZONTAL_ALIGNMENT, this.headerHAlign);
    columnHeaderCellStyle.setAttributeValue(
        CellStyleAttributes.VERTICAL_ALIGNMENT, this.headerVAlign);
    columnHeaderCellStyle.setAttributeValue(CellStyleAttributes.FONT,
        this.headerFont);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE,
        columnHeaderCellStyle, DisplayMode.NORMAL, GridRegion.COLUMN_HEADER);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
        this.columnHeaderCellPainter, DisplayMode.NORMAL,
        GridRegion.COLUMN_HEADER);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
        this.columnHeaderCellPainter, DisplayMode.SELECT,
        GridRegion.COLUMN_HEADER);

    // configure whether to render grid lines or not
    // e.g. for the BeveledBorderDecorator the rendering of the grid lines
    // should be disabled
    configRegistry.registerConfigAttribute(
        CellConfigAttributes.RENDER_GRID_LINES, this.renderGridLines,
        DisplayMode.NORMAL, GridRegion.COLUMN_HEADER);
  }

  /**
   * Configure the customized default style.
   * 
   * @param configRegistry
   */
  private void configureDefaultStyle(final IConfigRegistry configRegistry)
  {
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
        this.cellPainter);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
        this.wrappingEntryLogPainter, DisplayMode.NORMAL,
        ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 3);

    final Style cellStyle = new Style();
    cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
        this.bgColor);
    cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR,
        this.fgColor);
    cellStyle.setAttributeValue(CellStyleAttributes.FONT, this.font);
    cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT,
        this.hAlign);
    cellStyle.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT,
        this.vAlign);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE,
        cellStyle);

    configRegistry.registerConfigAttribute(
        CellConfigAttributes.DISPLAY_CONVERTER, new DefaultDisplayConverter());
  }

  /**
   * Configure the {@link MarkupDisplayConverter} for highlighting special words.
   * 
   * @param configRegistry
   */
  private void configureMarkupDisplayConverter(
      final IConfigRegistry configRegistry)
  {

    // markup for highlighting
    markupConverter = new MarkupDisplayConverter();
    // markup for highlighting in selection mode
    selectionMarkupConverter = new MarkupDisplayConverter();

    markupConverter.registerMarkup("search", searchHighlighter);

    updatePhrasesStyle();
    // register markup display converter for normal displaymode
    configRegistry.registerConfigAttribute(
        CellConfigAttributes.DISPLAY_CONVERTER, markupConverter,
        DisplayMode.NORMAL, GridRegion.BODY);

    selectionMarkupConverter.registerMarkup("search",
        selectionSearchHighlighter);

    // register markup display converter for selection displaymode
    configRegistry.registerConfigAttribute(
        CellConfigAttributes.DISPLAY_CONVERTER, selectionMarkupConverter,
        DisplayMode.SELECT, GridRegion.BODY);
  }

  @Override
  public void configureRegistry(final IConfigRegistry configRegistry)
  {
    configureDefaultStyle(configRegistry);
    configureColumnHeaderStyle(configRegistry);
    configureSelectionStyle(configRegistry);
    configureMarkupDisplayConverter(configRegistry);
  }

  /**
   * Configure the customized selection style.
   * 
   * @param configRegistry
   */
  private void configureSelectionStyle(final IConfigRegistry configRegistry)
  {
    // default selection style
    final Style selectionCellStyle = new Style();
    selectionCellStyle.setAttributeValue(CellStyleAttributes.FONT, this.font);
    selectionCellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
        this.selectionBgColor);
    selectionCellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR,
        this.selectionFgColor);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE,
        selectionCellStyle, DisplayMode.SELECT);

    // selection anchor
    final IStyle selectionAnchorStyle = new Style();
    selectionAnchorStyle.setAttributeValue(
        CellStyleAttributes.BACKGROUND_COLOR,
        this.selectionAnchorSelectionBgColor);
    selectionAnchorStyle.setAttributeValue(
        CellStyleAttributes.FOREGROUND_COLOR,
        this.selectionAnchorSelectionFgColor);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE,
        selectionAnchorStyle, DisplayMode.SELECT,
        SelectionStyleLabels.SELECTION_ANCHOR_STYLE);

    // column header selection style
    final Style columnHeaderSelectionCellStyle = new Style();
    columnHeaderSelectionCellStyle.setAttributeValue(CellStyleAttributes.FONT,
        this.headerFont);
    columnHeaderSelectionCellStyle.setAttributeValue(
        CellStyleAttributes.BACKGROUND_COLOR, this.headerSelectionBgColor);
    columnHeaderSelectionCellStyle.setAttributeValue(
        CellStyleAttributes.FOREGROUND_COLOR, this.headerSelectionFgColor);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE,
        columnHeaderSelectionCellStyle, DisplayMode.SELECT,
        GridRegion.COLUMN_HEADER);
  }

  protected String[] getPhrases()
  {
    final String phrasesText =
        store
            .getString(NarrativeViewerPrefsPage.PreferenceConstants.HIGHLIGHT_PHRASES);

    if (phrasesText != null && !phrasesText.trim().isEmpty())
    {
      final String[] split = phrasesText.split(",");
      final String[] phrases = new String[split.length];
      for (int i = 0; i < phrases.length; i++)
      {
        phrases[i] = split[i].trim().toLowerCase();
      }
      return phrases;
    }

    return new String[]
    {};
  }

  public void updatePhrasesStyle()
  {
    for (final String key : phrasesMarkups)
    {
      markupConverter.unregisterMarkup(key);
      selectionMarkupConverter.unregisterMarkup(key);
    }

    final String[] phrases = getPhrases();
    for (int i = 0; i < phrases.length; i++)
    {
      final String bg = PHRASES_COLORS[i % PHRASES_COLORS.length];
      final String phrase = phrases[i];
      final RegexMarkupValue phraseHighlighter =
          new RegexMarkupValue("", "<span style=\"background-color:rgb(" + bg
              + ")\">", "</span>");

      final RegexMarkupValue phraseSearchHighlighter =
          new RegexMarkupValue("",
              "<span style=\"color:rgb(0, 0, 0)\"><span style=\"background-color:rgb("
                  + bg + ")\">", "</span></span>");

      markupConverter.registerMarkup(phrase, phraseHighlighter);
      selectionMarkupConverter.registerMarkup(phrases[i],
          phraseSearchHighlighter);

      phrasesMarkups.add(phrase);
      phraseHighlighter.setRegexValue("(" + phrase + ")");
      phraseSearchHighlighter.setRegexValue("(" + phrase + ")");

    }

  }

  public void updateSearchHighlight(final String searchValue)
  {
    this.searchHighlighter.setRegexValue(searchValue);
    this.selectionSearchHighlighter.setRegexValue(searchValue);
  }

}