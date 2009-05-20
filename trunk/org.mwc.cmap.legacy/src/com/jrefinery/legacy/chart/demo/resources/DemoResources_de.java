/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.object-refinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * ---------------------
 * DemoResources_de.java
 * ---------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Thomas Meier;
 *
 * $Id: DemoResources_de.java,v 1.1.1.1 2003/07/17 10:06:39 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 15-Mar-2002 : Version 1, translation by Thomas Meier (DG);
 *
 */
package com.jrefinery.legacy.chart.demo.resources;

import java.util.ListResourceBundle;

/**
 * Localised resources for Germany.
 */
public class DemoResources_de extends ListResourceBundle {

    /**
     * Returns the array of strings in the resource bundle.
     */
    public Object[][] getContents() {
        return contents;
    }

    /** The resources to be localised. */
    static final Object[][] contents = {

        // about frame...
        {"about.title", "Info..."},
        {"about.version.label", "Version"},

        // menu labels...
        {"menu.file", "Datei"},
        {"menu.file.mnemonic", new Character('D')},

        {"menu.file.exit", "Beenden"},
        {"menu.file.exit.mnemonic", new Character('B')},

        {"menu.help", "Hilfe"},
        {"menu.help.mnemonic", new Character('H')},

        {"menu.help.about", "Info..."},
        {"menu.help.about.mnemonic", new Character('I')},

        // dialog messages...
        {"dialog.exit.title", "Programm beenden..."},
        {"dialog.exit.message", "Soll das Programm beendet werden?"},

        // labels for the tabs in the main window...
        {"tab.bar",      "Balkendiagramme"},
        {"tab.pie",      "Kreisdiagramme"},
        {"tab.xy",       "XY-Diagramme"},
        {"tab.time",     "Zeitreihen-Diagramme"},
        {"tab.other",    "Andere Diagramme"},
        {"tab.test",     "Testdiagramme"},
        {"tab.combined", "Kombinierte Diagramme"},

        // sample chart descriptions...
        {"chart1.title",       "Horizontales Balkendiagrammet: "},
        {"chart1.description", "Anzeige eines horizontalen Balkendiagramms mit den Daten eines "
                              +"CategoryDataset. Die numerische Achse ist invertiert."},

        {"chart2.title",       "Horizontales Stacked Balkendiagramm: "},
        {"chart2.description", "Anzeige eines horizontalen stacked Balkediagramms mit den Daten "
                              +"eines CategoryDataset."},

        {"chart3.title",       "Vertikales Balkendiagrammt: "},
        {"chart3.description", "Anzeige eines vertikalen Balkendiagramms mit den Daten eines "
                              +"CategoryDataset."},

        {"chart4.title",       "Vertikales 3D Balkendiagramm: "},
        {"chart4.description", "Anzeige eines vertikalen 3D Balkendiagramms mit den Daten eines "
                              +"CategoryDataset."},

        {"chart5.title",       "Vertikales Stacked Balkendiagramm: "},
        {"chart5.description", "Displays vertical stacked bars, representing data from a "
                              +"CategoryDataset."},

        {"chart6.title",       "Vertikales Stacked 3D Balkendiagramm: "},
        {"chart6.description", "Displays vertical stacked bars with a 3D effect, representing "
                              +"data from a CategoryDataset."},

        {"chart7.title",       "Kreisdiagramm 1: "},
        {"chart7.description", "A pie chart showing one section exploded."},

        {"chart8.title",       "Kreisdiagramm 2: "},
        {"chart8.description", "A pie chart showing percentages on the category labels.  Also, "
                              +"this plot has a background image."},

        {"chart9.title",       "XY Plot: "},
        {"chart9.description", "A line chart using data from an XYDataset.  Both axes are "
                              +"numerical."},

        {"chart10.title",       "Zeitreihe 1: "},
        {"chart10.description", "A time series chart, representing data from an XYDataset.  This "
                               +"chart also demonstrates the use of multiple chart titles."},

        {"chart11.title",       "Zeitreihe 2: "},
        {"chart11.description", "A time series chart, representing data from an XYDataset.  The "
                               +"vertical axis has a logarithmic scale."},

        {"chart12.title",       "Zeitreihe 3: "},
        {"chart12.description", "A time series chart with a moving average."},

        {"chart13.title",       "High/Low/Open/Close Diagramm: "},
        {"chart13.description", "A high/low/open/close chart based on data in a HighLowDataset."},

        {"chart14.title",       "Candlestick Diagramm: "},
        {"chart14.description", "A candlestick chart based on data in a HighLowDataset."},

        {"chart15.title",       "Signal Diagramm: "},
        {"chart15.description", "A signal chart based on data in a SignalDataset."},

        {"chart16.title",       "Wind Plot: "},
        {"chart16.description", "A wind plot, represents wind direction and intensity (supplied "
                               +"via a WindDataset)."},

        {"chart17.title",       "Scatter Plot: "},
        {"chart17.description", "A scatter plot, representing data in an XYDataset."},

        {"chart18.title",       "Liniendiagramm: "},
        {"chart18.description", "A chart displaying lines and or shapes, representing data in a "
                               +"CategoryDataset.  This plot also illustrates the use of a "
                               +"background image on the chart, and alpha-transparency on the "
                               +"plot."},

        {"chart19.title",       "Vertikales XY Balkendiagramm: "},
        {"chart19.description", "A chart showing vertical bars, based on data in an "
                               +"IntervalXYDataset."},

        {"chart20.title",       "Null Daten: "},
        {"chart20.description", "A chart with a null dataset."},

        {"chart21.title",       "Keine Daten: "},
        {"chart21.description", "A chart with a dataset containing zero series."},

        {"chart22.title",       "Diagramm in einer JScrollPane: "},
        {"chart22.description", "A chart embedded in a JScrollPane."},

        {"chart23.title",       "Einzelserien Balkendiagramm: "},
        {"chart23.description", "A single series bar chart.  This chart also illustrates the use "
                               +"of a border around a ChartPanel."},

        {"chart24.title",       "Dynamisches Diagramm: "},
        {"chart24.description", "A dynamic chart, to test the event notification mechanism."},

        {"chart25.title",       "Overlaid Diagramm: "},
        {"chart25.description", "Anzeige eines overlaid chart with high/low/open/close and moving "
                               +"average plots."},

        {"chart26.title",       "Horizontales Kombi-Diagramm: "},
        {"chart26.description", "Anzeige eines horizontally combined chart of time series and XY bar "
                               +"plots."},

        {"chart27.title",       "Vertikales Kombi-Diagramm: "},
        {"chart27.description", "Ein vertikal kominiertes Diagramm eines XY-Diagramms, TimeSeries "
                               +"und eines VerticalXYBar-plots"},

        {"chart28.title",       "Kombi- und Overlaid-Diagramm: "},
        {"chart28.description", "Ein kominiertes Diagramm eines XY-Diagramms, overlaid TimeSeries- "
                               +"und eines HighLow & TimeSeries-plots"},

        {"chart29.title",       "Kombi- und Overlaid Dynamisches Diagramm: "},
        {"chart29.description", "Anzeige eines dynamisch kombinierten und overlaid Diagramm, um den "
                               +"event Benachrichtigungs-Mechanismus zu testen."},

        {"charts.display", "Anzeige"},

        // chart titles and labels...
        {"bar.horizontal.title",  "Horizontales Balkendiagramm"},
        {"bar.horizontal.domain", "Kategorien"},
        {"bar.horizontal.range",  "Werte"},

        {"bar.horizontal-stacked.title",  "Horizontal Stacked Balkendiagramm"},
        {"bar.horizontal-stacked.domain", "Kategorien"},
        {"bar.horizontal-stacked.range",  "Werte"},

        {"bar.vertical.title",  "Vertikales Balkendiagramm"},
        {"bar.vertical.domain", "Kategorien"},
        {"bar.vertical.range",  "Werte"},

        {"bar.vertical3D.title",  "Vertikales Balkendiagramm"},
        {"bar.vertical3D.domain", "Kategorien"},
        {"bar.vertical3D.range",  "Werte"},

        {"bar.vertical-stacked.title",  "Vertikales Balkendiagramm"},
        {"bar.vertical-stacked.domain", "Kategorien"},
        {"bar.vertical-stacked.range",  "Werte"},

        {"bar.vertical-stacked3D.title",  "Vertikales Balkendiagramm"},
        {"bar.vertical-stacked3D.domain", "Kategorien"},
        {"bar.vertical-stacked3D.range",  "Werte"},

        {"pie.pie1.title", "Kreisdiagramm 1"},

        {"pie.pie2.title", "Kreisdiagramm 2"},

        {"xyplot.sample1.title",  "XY Plot"},
        {"xyplot.sample1.domain", "X Werte"},
        {"xyplot.sample1.range",  "Y Werte"},

        {"timeseries.sample1.title",     "Zeitreihen Diagramm 1"},
        {"timeseries.sample1.subtitle",  "Wert von GBP in JPY"}, // not sure if this is correct
        {"timeseries.sample1.domain",    "Datum"},
        {"timeseries.sample1.range",     "CCY pro GBP"},
        {"timeseries.sample1.copyright", "(C)opyright 2002, by Simba Management Limited"},

        {"timeseries.sample2.title",    "Zeitreihen Diagramm 2"},
        {"timeseries.sample2.domain",   "Millisekunden"},
        {"timeseries.sample2.range",    "Log Achse"},
        {"timeseries.sample2.subtitle", "Millisekunden"},

        //{"timeseries.sample3.title",    "Zeitreihen Diagramm with Moving Average"},
        {"timeseries.sample3.title",    "Zeitreihen Diagramm mit gleitendem Durchschnitt"},  // not sure if this is correct
        {"timeseries.sample3.domain",   "Datum"},
        {"timeseries.sample3.range",    "CCY pro GBP"},
        //{"timeseries.sample3.subtitle", "30 day moving average of GBP"},
        {"timeseries.sample3.subtitle", "30 Tage gleitender Durchschnitt von GBP"},  // not sure if this is correct

        {"timeseries.highlow.title",    "High/Low/Open/Close Diagramm"},
        {"timeseries.highlow.domain",   "Datum"},
        {"timeseries.highlow.range",    "Wert ($ pro Aktie)"},  // not sure if this is correct
        {"timeseries.highlow.subtitle", "IBM Aktien Wert"},     // not sure if this is correct

        {"timeseries.candlestick.title",    "CandleStick Diagramm"},
        {"timeseries.candlestick.domain",   "Datum"},
        {"timeseries.candlestick.range",    "Wert ($ pro Aktie)"},  // not sure if this is correct
        {"timeseries.candlestick.subtitle", "IBM Aktien Wert"},     // not sure if this is correct

        {"timeseries.signal.title",    "Signal Diagramm"},
        {"timeseries.signal.domain",   "Datum"},
        {"timeseries.signal.range",    "Wert ($ pro Aktie)"},  // not sure if this is correct
        {"timeseries.signal.subtitle", "IBM Aktien Wert"},     // not sure if this is correct

        {"other.wind.title",  "Wind Plot"},
        {"other.wind.domain", "X-Achse"},
        {"other.wind.range",  "Y-Achse"},

        {"other.scatter.title",  "Scatter Plot"},
        {"other.scatter.domain", "X-Achse"},
        {"other.scatter.range",  "Y-Achse"},

        {"other.line.title",  "Linien Plot"},
        {"other.line.domain", "Kategorie"},
        {"other.line.range",  "Wert"},

        {"other.xybar.title",  "Zeitreihen Balkendiagramm"},
        {"other.xybar.domain", "Datum"},
        {"other.xybar.range",  "Value"},

        {"test.null.title",  "XY Plot (null data)"},
        {"test.null.domain", "X"},
        {"test.null.range",  "Y"},

        {"test.zero.title",  "XY Plot (keine Daten)"},
        {"test.zero.domain", "X-Achse"},
        {"test.zero.range",  "Y-Achse"},

        {"test.scroll.title",    "Zeitreihen"},
        {"test.scroll.subtitle", "Wert von GBP"}, // not sure if this is correct
        {"test.scroll.domain",   "Datum"},
        {"test.scroll.range",    "Wert"},

        {"test.single.title",     "Einzelserien Balkendiagramm"},
        {"test.single.subtitle1", "Subtitel 1"},
        {"test.single.subtitle2", "Subtitel 2"},
        {"test.single.domain",    "Datum"},
        {"test.single.range",     "Wert"},

        {"test.dynamic.title",  "Dynamisches Diagramm"},
        {"test.dynamic.domain", "Wertebereich"},
        {"test.dynamic.range",  "Range"},  // to do

        {"combined.overlaid.title",     "Overlaid Diagramm"},
        //{"combined.overlaid.subtitle",  "High/Low/Open/Close plus Moving Average"},
        {"combined.overlaid.subtitle",  "High/Low/Open/Close plus gleitender Durchschnitt"}, // not sure if this is correct
        {"combined.overlaid.domain",    "Datum" },
        {"combined.overlaid.range",     "IBM"},

        {"combined.horizontal.title",     "Horizontales Kombi Diagramm"},
        {"combined.horizontal.subtitle",  "Zeitreihen und XY Balkendiagramme"},
        {"combined.horizontal.domains",   new String[] {"Datum 1", "Datum 2", "Datum 3"} },
        {"combined.horizontal.range",     "CCY pro GBP"},

        {"combined.vertical.title",     "Vertikales Kombi Diagramm"},
        {"combined.vertical.subtitle",  "Vier Diagramme in einem"},
        {"combined.vertical.domain",    "Datum"},
        {"combined.vertical.ranges",    new String[] {"CCY pro GBP", "Pfund", "IBM", "Bars"} },

        {"combined.combined-overlaid.title",     "Kombi und Overlaid Diagramm"},
        {"combined.combined-overlaid.subtitle",  "XY, Overlaid (zwei TimeSeries) und Overlaid "
                                                +"(HighLow und TimeSeries)"},
        {"combined.combined-overlaid.domain",    "Datum"},
        {"combined.combined-overlaid.ranges",    new String[] {"CCY pro GBP", "Pfund", "IBM"} },

        {"combined.dynamic.title",     "Dynamisches Kombi Diagramm"},
        {"combined.dynamic.subtitle",  "XY (series 0), XY (series 1), Overlaid (both series) "
                                      +"and XY (both series)"},                                    // to do
        {"combined.dynamic.domain",    "X" },
        {"combined.dynamic.ranges",    new String[] {"Y1", "Y2", "Y3", "Y4"} },

    };

}
