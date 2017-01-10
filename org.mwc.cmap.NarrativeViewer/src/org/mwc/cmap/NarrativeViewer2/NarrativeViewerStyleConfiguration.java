package org.mwc.cmap.NarrativeViewer2;

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

/**
 * Custom styling configuration for the narrative viewer.
 */
public class NarrativeViewerStyleConfiguration extends AbstractRegistryConfiguration {

	private RegexMarkupValue searchHighlighter = new RegexMarkupValue(
			"", 
			"<span style=\"background-color:rgb(255, 255, 0)\">",
			"</span>");
	private RegexMarkupValue selectionSearchHighlighter = new RegexMarkupValue(
			"", 
			"<span style=\"color:rgb(0, 0, 0)\"><span style=\"background-color:rgb(255, 255, 0)\">",
			"</span></span>");
	
	// default / body configuration
	public Color bgColor = GUIHelper.COLOR_WHITE;
	public Color fgColor = GUIHelper.COLOR_BLACK;
    public Color selectionBgColor = GUIHelper.COLOR_LIST_SELECTION;
    public Color selectionFgColor = GUIHelper.COLOR_WHITE;

    public Color selectionAnchorSelectionBgColor = GUIHelper.COLOR_LIST_SELECTION;
    public Color selectionAnchorSelectionFgColor = GUIHelper.COLOR_WHITE;

	public Font font = GUIHelper.DEFAULT_FONT;
	public HorizontalAlignmentEnum hAlign = HorizontalAlignmentEnum.LEFT;
	public VerticalAlignmentEnum vAlign = VerticalAlignmentEnum.MIDDLE;
	
	public ICellPainter cellPainter = 
			new BackgroundPainter(new PaddingDecorator(new RichTextCellPainter(true, true, false), 0, 5, 0, 5, false));

	// customized painter that also supports shrinking
	RichTextCellPainter wrappingAutomaticRowHeightPainter = new RichTextCellPainter(true, false, true) {
		protected boolean performRowResize(int contentHeight, Rectangle rectangle) {
			return ((contentHeight != rectangle.height) && this.calculateByTextHeight);
		};
	};
	RichTextCellPainter automaticRowHeightPainter = new RichTextCellPainter(false, false, true) {
		protected boolean performRowResize(int contentHeight, Rectangle rectangle) {
			return ((contentHeight != rectangle.height) && this.calculateByTextHeight);
		};
	};
	
	public ICellPainter wrappingEntryLogPainter = 
			new BackgroundPainter(new PaddingDecorator(wrappingAutomaticRowHeightPainter, 0, 5, 0, 5, false));
	public ICellPainter entryLogPainter = 
			new BackgroundPainter(new PaddingDecorator(automaticRowHeightPainter, 0, 5, 0, 5, false));

	// column header configuration
    public Color headerBgColor = GUIHelper.COLOR_WIDGET_BACKGROUND;
    public Color headerFgColor = GUIHelper.COLOR_WIDGET_FOREGROUND;
    public Color headerSelectionBgColor = GUIHelper.COLOR_GRAY;
    public Color headerSelectionFgColor = GUIHelper.COLOR_WHITE;

    public Font headerFont = GUIHelper.DEFAULT_FONT;
    public HorizontalAlignmentEnum headerHAlign = HorizontalAlignmentEnum.LEFT;
    public VerticalAlignmentEnum headerVAlign = VerticalAlignmentEnum.MIDDLE;

    public Boolean renderGridLines = Boolean.FALSE;

    public ICellPainter columnHeaderCellPainter = 
    		new BeveledBorderDecorator(
    				new PaddingDecorator(new AutomaticRowHeightTextPainter(), 0, 5, 2, 5, true));

	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {
		configureDefaultStyle(configRegistry);
		configureColumnHeaderStyle(configRegistry);
        configureSelectionStyle(configRegistry);
        configureMarkupDisplayConverter(configRegistry);
	}
	
	/**
	 * Configure the customized default style.
	 * 
	 * @param configRegistry
	 */
	private void configureDefaultStyle(IConfigRegistry configRegistry) {
		configRegistry.registerConfigAttribute(
				CellConfigAttributes.CELL_PAINTER, 
				this.cellPainter);

		configRegistry.registerConfigAttribute(
				CellConfigAttributes.CELL_PAINTER, 
				this.wrappingEntryLogPainter,
				DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 4);

		Style cellStyle = new Style();
		cellStyle.setAttributeValue(
				CellStyleAttributes.BACKGROUND_COLOR, 
				this.bgColor);
		cellStyle.setAttributeValue(
				CellStyleAttributes.FOREGROUND_COLOR, 
				this.fgColor);
		cellStyle.setAttributeValue(
				CellStyleAttributes.FONT, 
				this.font);
		cellStyle.setAttributeValue(
				CellStyleAttributes.HORIZONTAL_ALIGNMENT, 
				this.hAlign);
		cellStyle.setAttributeValue(
				CellStyleAttributes.VERTICAL_ALIGNMENT, 
				this.vAlign);

		configRegistry.registerConfigAttribute(
				CellConfigAttributes.CELL_STYLE, 
				cellStyle);

		configRegistry.registerConfigAttribute(
				CellConfigAttributes.DISPLAY_CONVERTER,
				new DefaultDisplayConverter());
	}
	
	/**
	 * Configure the customized column header style.
	 * 
	 * @param configRegistry
	 */
	private void configureColumnHeaderStyle(IConfigRegistry configRegistry) {
		// special style configuration for the column header
        Style columnHeaderCellStyle = new Style();
        columnHeaderCellStyle.setAttributeValue(
        		CellStyleAttributes.BACKGROUND_COLOR,
                this.headerBgColor);
        columnHeaderCellStyle.setAttributeValue(
        		CellStyleAttributes.FOREGROUND_COLOR,
                this.headerFgColor);
        columnHeaderCellStyle.setAttributeValue(
        		CellStyleAttributes.HORIZONTAL_ALIGNMENT,
                this.headerHAlign);
        columnHeaderCellStyle.setAttributeValue(
        		CellStyleAttributes.VERTICAL_ALIGNMENT,
                this.headerVAlign);
        columnHeaderCellStyle.setAttributeValue(
        		CellStyleAttributes.FONT, 
        		this.headerFont);

        configRegistry.registerConfigAttribute(
        		CellConfigAttributes.CELL_STYLE,
                columnHeaderCellStyle, 
                DisplayMode.NORMAL, 
                GridRegion.COLUMN_HEADER);

        configRegistry.registerConfigAttribute(
                CellConfigAttributes.CELL_PAINTER, 
                this.columnHeaderCellPainter,
                DisplayMode.NORMAL, 
                GridRegion.COLUMN_HEADER);

        configRegistry.registerConfigAttribute(
                CellConfigAttributes.CELL_PAINTER, 
                this.columnHeaderCellPainter,
                DisplayMode.SELECT, 
                GridRegion.COLUMN_HEADER);

        // configure whether to render grid lines or not
        // e.g. for the BeveledBorderDecorator the rendering of the grid lines
        // should be disabled
        configRegistry.registerConfigAttribute(
                CellConfigAttributes.RENDER_GRID_LINES, 
                this.renderGridLines,
                DisplayMode.NORMAL, 
                GridRegion.COLUMN_HEADER);
	}
	
	/**
	 * Configure the customized selection style.
	 * 
	 * @param configRegistry
	 */
	private void configureSelectionStyle(IConfigRegistry configRegistry) {
        // default selection style
		Style selectionCellStyle = new Style();
        selectionCellStyle.setAttributeValue(
                CellStyleAttributes.FONT, 
                this.font);
        selectionCellStyle.setAttributeValue(
                CellStyleAttributes.BACKGROUND_COLOR,
                this.selectionBgColor);
        selectionCellStyle.setAttributeValue(
                CellStyleAttributes.FOREGROUND_COLOR,
                this.selectionFgColor);

        configRegistry.registerConfigAttribute(
                CellConfigAttributes.CELL_STYLE,
                selectionCellStyle, 
                DisplayMode.SELECT);
        
        // selection anchor
        IStyle selectionAnchorStyle = new Style();
        selectionAnchorStyle.setAttributeValue(
                CellStyleAttributes.BACKGROUND_COLOR,
                this.selectionAnchorSelectionBgColor);
        selectionAnchorStyle.setAttributeValue(
                CellStyleAttributes.FOREGROUND_COLOR,
                this.selectionAnchorSelectionFgColor);
        configRegistry.registerConfigAttribute(
                CellConfigAttributes.CELL_STYLE, 
                selectionAnchorStyle,
                DisplayMode.SELECT,
                SelectionStyleLabels.SELECTION_ANCHOR_STYLE);

		// column header selection style
		Style columnHeaderSelectionCellStyle = new Style();
		columnHeaderSelectionCellStyle.setAttributeValue(
				CellStyleAttributes.FONT, 
				this.headerFont);
		columnHeaderSelectionCellStyle.setAttributeValue(
				CellStyleAttributes.BACKGROUND_COLOR,
				this.headerSelectionBgColor);
		columnHeaderSelectionCellStyle.setAttributeValue(
				CellStyleAttributes.FOREGROUND_COLOR,
				this.headerSelectionFgColor);
		
		configRegistry.registerConfigAttribute(
				CellConfigAttributes.CELL_STYLE,
				columnHeaderSelectionCellStyle, 
				DisplayMode.SELECT, 
				GridRegion.COLUMN_HEADER);
	}
	
	/**
	 * Configure the {@link MarkupDisplayConverter} for highlighting special words.
	 * 
	 * @param configRegistry
	 */
	private void configureMarkupDisplayConverter(IConfigRegistry configRegistry) {
		String highlightString = "(IMPORTANT|URGENT)";
		
		// markup for highlighting
		MarkupDisplayConverter markupConverter = new MarkupDisplayConverter();
		markupConverter.registerRegexMarkup(highlightString,
				"<span style=\"background-color:rgb(204, 204, 204)\">",
				"</span>");
		markupConverter.registerMarkup("search", searchHighlighter);
		// if every word should be highlighted with a different markup the following markups need to be registered
//		markupConverter.registerMarkup("IMPORTANT",
//				"<span style=\"background-color:rgb(204, 204, 204)\">",
//				"</span>");
//		markupConverter.registerMarkup("URGENT",
//				"<span style=\"background-color:rgb(204, 204, 204)\">",
//				"</span>");
		
		// register markup display converter for normal displaymode
		configRegistry.registerConfigAttribute(
				CellConfigAttributes.DISPLAY_CONVERTER,
				markupConverter,
				DisplayMode.NORMAL,
				GridRegion.BODY);

		// markup for highlighting in selection mode
		MarkupDisplayConverter selectionMarkupConverter = new MarkupDisplayConverter();
		selectionMarkupConverter.registerRegexMarkup(highlightString,
				"<span style=\"color:rgb(0, 0, 0)\"><span style=\"background-color:rgb(204, 204, 204)\">",
				"</span></span>");
		selectionMarkupConverter.registerMarkup("search", selectionSearchHighlighter);
		
		// register markup display converter for selection displaymode
		configRegistry.registerConfigAttribute(
				CellConfigAttributes.DISPLAY_CONVERTER,
				selectionMarkupConverter,
				DisplayMode.SELECT,
				GridRegion.BODY);
	}
	
	
	public void updateSearchHighlight(String searchValue) {
		this.searchHighlighter.setRegexValue(searchValue);
		this.selectionSearchHighlighter.setRegexValue(searchValue);
	}
}