/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
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
 * DemoResources_es.java
 * ---------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors;
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Hans-Jurgen Greiner;
 *
 * $Id: DemoResources_es.java,v 1.1.1.1 2003/07/17 10:06:39 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 26-Mar-2002 : Version 1, translation by Hans-Jurgen Greiner (DG);
 *
 */

package com.jrefinery.chart.demo.resources;

import java.util.ListResourceBundle;

/**
 * A resource bundle that stores all the user interface items that might need localisation.
 */
public class DemoResources_es extends ListResourceBundle {

    /**
     * Returns the array of strings in the resource bundle.
     */
    public Object[][] getContents() {
        return contents;
    }

    /** The resources to be localised. */
    static final Object[][] contents = {

        // about frame...
        {"about.title", "Acerca..."},
        {"about.version.label", "Versión"},

        // menu labels...
        {"menu.file", "Archivo"},
        {"menu.file.mnemonic", new Character('F')},

        {"menu.file.exit", "Salida"},
        {"menu.file.exit.mnemonic", new Character('x')},

        {"menu.help", "Ayuda"},
        {"menu.help.mnemonic", new Character('H')},

        {"menu.help.about", "Acerca..."},
        {"menu.help.about.mnemonic", new Character('A')},

        // dialog messages...
        {"dialog.exit.title", "Confirme salida..."},
        {"dialog.exit.message", "Estas seguro que quieres salir?"},

        // labels for the tabs in the main window...
        {"tab.bar",      "Gráfico de barras"},
        {"tab.pie",      "Gráfico circular"},
        {"tab.xy",       "XY Gráficos"},
        {"tab.time",     "Gráfico de la serie de  tiempo"},
        {"tab.other",    "Otros gráficos"},
        {"tab.test",     "Gráficos de examen"},
        {"tab.combined", "Gráficos combinados"},

        // sample chart descriptions...
        {"chart1.title",       "Gráfico de barras horizontales: "},
        {"chart1.description", "Muestra barras horizontales, representando data desde a "
                              +"Categoría dataset (grupo data).  Preste atención que el eje "
                              +"numérico esta invertido."},

        {"chart2.title",       "Gráfico con pilas de barras horizontales: "},
        {"chart2.description", "muestra gráfico con pilas de barras horizontales,  " +
                               "representando data desde a " +
                               "Categoría dataset (grupo data)."},

        {"chart3.title",       "Gráfico con barras verticales: "},
        {"chart3.description", "Muestra barras verticales, representando data " +
                               "de una categoría dataset (grupo data)."},

        {"chart4.title",       "Gráfico de barra vertical en 3D: "},
        {"chart4.description", "muestra  barras verticales con un efecto de 3D, " +
                               "representando data desde a " +
                               "Categoría dataset (grupo data)."},

        {"chart5.title",       "Gráfico con pilas de barras verticales: "},
        {"chart5.description", "muestra gráfico con pilas de barras verticales, " +
                               "representando data desde a " +
                               "Categoría dataset (grupo data)."},

        {"chart6.title",       "Gráfico con pilas de barras en 3D: "},
        {"chart6.description", "Muestra pila de  barras verticales con un efecto de 3D, " +
                               "representando data de una Categoría dataset (grupo data)."},

        {"chart7.title",       "Gráfico circular 1: "},
        {"chart7.description", "Un gráfico circular mostrando una sección explotada."},

        {"chart8.title",       "Gráfico circular 2: "},
        {"chart8.description", "Un gráfico circular mostrando porcentajes sobre los " +
                               "niveles categóricos.  También, " +
                               "este plan tiene una imagen de fondo."},

        {"chart9.title",       "Plan XY: "},
        {"chart9.description", "un gráfico de línea usando data desde un grupo de data XY.  " +
                               "Ambos ejes son numérico."},

        {"chart10.title",       "Series de tiempo 1: "},
        {"chart10.description", "un gráfico de series de tiempo, representando data " +
                                "desde un grupo de data XY. Este gráfico también " +
                                "demuestra el uso de múltiples títulos gráficos."},

        {"chart11.title",       "Series de tiempo 2: "},
        {"chart11.description", "Un gráfico de series de tiempo, representando un grupo " +
                                "de data XY. Este ejes verticales tienen una escala " +
                                "logarítmica."},

        {"chart12.title",       "Series de tiempo 3: "},
        {"chart12.description", "Un gráfico de serie de tiempo con un movimiento promedio."},

        {"chart13.title",       "Gráfico Alto/Bajo/Abierto/Cerrado: "},
        {"chart13.description", "Un gráfico alto/bajo/abierto/cerrado basado sobre " +
                                "data en un grupo de data alto bajo."},

        {"chart14.title",       "Gráfico de cotizaciones: "},
        {"chart14.description", "Un gráfico de cotizaciones basado en un grupo e data altobajo."},

        {"chart15.title",       "Gráfico de señal: "},
        {"chart15.description", "Un gráfico de señal basado en data en un grupo de data de señal."},

        {"chart16.title",       "Plan de viento: "},
        {"chart16.description", "un plan de viento, representa la dirección del " +
                                "viento e intensidad  ( suministro a través de  " +
                                "un grupo data de viento)."},

        {"chart17.title",       "Esparcir plan: "},
        {"chart17.description", "Un plan esparcido, representando data en un grupo data XY."},

        {"chart18.title",       "Gráfico de línea: "},
        {"chart18.description", "un gráfico mostrando líneas y/o figuras, representando " +
                                "data en a categoría grupo data.  Este plan también " +
                                "ilustra el uso de a imagen de fondo en el gráfico, y " +
                                "alpha-transparency en él plan."},

        {"chart19.title",       "Gráfico de barra vertical XY: "},
        {"chart19.description", "Un gráfico mostrando barras verticales, basadas en data en un "
                               +"grupo data interval XY."},

        {"chart20.title",       "Data Nula: "},
        {"chart20.description", "Un gráfico con un grupo data nulo."},

        {"chart21.title",       "Cero Data: "},
        {"chart21.description", "Un gráfico con un grupo de data que contiene una serie de ceros."},

        {"chart22.title",       "Un gráfico en JScrollPane: "},
        {"chart22.description", "Un gráfico incrustado en un JScrollPane."},

        {"chart23.title",       "Un gráfico de barra con serie única: "},
        {"chart23.description", "un gráfico de barra con serie única.  " +
                                "Este gráfico también ilustra el uso " +
                                "de un borde alrededor de ChartPanel."},

        {"chart24.title",       "Gráfico dinámico: "},
        {"chart24.description", "Un gráfico dinámico, para examinar la notificación del " +
                                "evento mecánico."},

        {"chart25.title",       "Gráfico cubierto: "},
        {"chart25.description", "muestra un gráfico cubierto con alto/bajo/abierto/cerrado " +
                                "y moviéndose planes en promedio."},

        {"chart26.title",       "Gráfico combinado horizontalmente: "},
        {"chart26.description", "Muestra un gráfico combinado horizontalmente de la serie " +
                                "de tiempo y una barra XY planes."},

        {"chart27.title",       "Gráfico combinado verticalmente: "},
        {"chart27.description", "Muestra un gráfico combinado verticalmente de XY, " +
                                "serie de tiempo y VerticalXYBar planes."},

        {"chart28.title",       "Gráfico combinado y cubierto: "},
        {"chart28.description", "Un gráfico combinado de una XY, cubierto de series de " +
                                "tiempo y uno cubierto altobajo & planes de series de tiempo."},

        {"chart29.title",       "Gráfico dinámico combinado y cubierto: "},
        {"chart29.description", "muestra un gráfico dinámico combinado y cubierto, " +
                                "para examinar el notificación de evento mecánico."},

        {"charts.display", "Muestra"},

        // chart titles and labels...
        {"bar.horizontal.title",  "Gráfico de barra horizontal"},
        {"bar.horizontal.domain", "Categorías"},
        {"bar.horizontal.range",  "Valor"},

        {"bar.horizontal-stacked.title",  "Gráfico con pilas de barras horizontales"},
        {"bar.horizontal-stacked.domain", "Categorías"},
        {"bar.horizontal-stacked.range",  "Valor"},

        {"bar.vertical.title",  "Gráfico de barras verticales"},
        {"bar.vertical.domain", "Categorías"},
        {"bar.vertical.range",  "Valor"},

        {"bar.vertical3D.title",  "Gráfico de barra vertical en 3D"},
        {"bar.vertical3D.domain", "Categorías"},
        {"bar.vertical3D.range",  "Valor"},

        {"bar.vertical-stacked.title",  "Gráfico con pilas de barras verticales"},
        {"bar.vertical-stacked.domain", "Categorías"},
        {"bar.vertical-stacked.range",  "Valor"},

        {"bar.vertical-stacked3D.title",  "Gráfico de barras verticales en 3D"},
        {"bar.vertical-stacked3D.domain", "Categorías"},
        {"bar.vertical-stacked3D.range",  "Valor"},

        {"pie.pie1.title", "Gráfico circular 1"},

        {"pie.pie2.title", "Gráfico circular 2"},

        {"xyplot.sample1.title",  "Plan de XY"},
        {"xyplot.sample1.domain", "Valores de X"},
        {"xyplot.sample1.range",  "Valores de Y"},

        {"timeseries.sample1.title",     "Gráfico con series de tiempo 1"},
        {"timeseries.sample1.subtitle",  "Valor de GBP en JPY"},
        {"timeseries.sample1.domain",    "Fecha"},
        {"timeseries.sample1.range",     "CCY por GBP"},
        {"timeseries.sample1.copyright", "(C)opyright 2002, por Simba Management Limited"},

        {"timeseries.sample2.title",    "Gráfico con series de tiempo 2"},
        {"timeseries.sample2.domain",   "Milisegundo"},
        {"timeseries.sample2.range",    "Eje tronco"},
        {"timeseries.sample2.subtitle", "Milisegundos"},

        {"timeseries.sample3.title",    "gráfico con series de tiempo moviendo al promedio"},
        {"timeseries.sample3.domain",   "Fecha"},
        {"timeseries.sample3.range",    "CCY por GBP"},
        {"timeseries.sample3.subtitle", "30 dias moviendo de GBP"},
// GEEK
        {"timeseries.highlow.title",    "Gráfico Alto/Bajo/Abierto/Cerrado"},
        {"timeseries.highlow.domain",   "Fecha"},
        {"timeseries.highlow.range",    "Precio  ($ por porción)"},
        {"timeseries.highlow.subtitle", "Precio de la acción IBM"},

        {"timeseries.candlestick.title",    "Gráfico de cotización"},
        {"timeseries.candlestick.domain",   "Fecha"},
        {"timeseries.candlestick.range",    "Precio  ($ por porción)"},
        {"timeseries.candlestick.subtitle", "Precio de la acción IBM"},

        {"timeseries.signal.title",    "Gráfico de señal"},
        {"timeseries.signal.domain",   "Fecha"},
        {"timeseries.signal.range",    "Precio  ($ por porción"},
        {"timeseries.signal.subtitle", "Precio de la acción IBM"},

        {"other.wind.title",  "Plan de Viento"},
        {"other.wind.domain", "eje-X"},
        {"other.wind.range",  "eje-Y"},

        {"other.scatter.title",  "Plan Esparcido"},
        {"other.scatter.domain", "eje-X"},
        {"other.scatter.range",  "eje-Y"},

        {"other.line.title",  "Plan de línea"},
        {"other.line.domain", "Categoría"},
        {"other.line.range",  "Valor"},

        {"other.xybar.title",  "Gráfico con barras y series de tiempo"},
        {"other.xybar.domain", "Fecha"},
        {"other.xybar.range",  "Valor"},

        {"test.null.title",  "Plan XY (Nula data)"},
        {"test.null.domain", "eje-X"},
        {"test.null.range",  "eje-Y"},

        {"test.zero.title",  "Plan XY (Cero data)"},
        {"test.zero.domain", "eje-X"},
        {"test.zero.range",  "eje-Y"},

        {"test.scroll.title",    "Series de tiempo"},
        {"test.scroll.subtitle", "Valor of GBP"},
        {"test.scroll.domain",   "Fecha"},
        {"test.scroll.range",    "Valor"},

        {"test.single.title",     "Gráfico de barras de series únicas"},
        {"test.single.subtitle1", "Subtítulo 1"},
        {"test.single.subtitle2", "Subtítulo 2"},
        {"test.single.domain",    "Fecha"},
        {"test.single.range",     "Valor"},

        {"test.dynamic.title",  "Gráfico dinámico"},
        {"test.dynamic.domain", "Dominios"},
        {"test.dynamic.range",  "Alcance"},

        {"combined.overlaid.title",     "Gráfico cubierto"},
        {"combined.overlaid.subtitle",  "Alto/Bajo/Abierto/Cerrado mas moviendo a promedio"},
        {"combined.overlaid.domain",    "Fecha" },
        {"combined.overlaid.range",     "IBM"},

        {"combined.horizontal.title",     "Gráfico horizontal combinado"},
        {"combined.horizontal.subtitle",  "Series de tiempo y gráficos de barras XY"},
        {"combined.horizontal.domains",   new String[] {"Fecha 1", "Facha 2", "Fecha 3"} },
        {"combined.horizontal.range",     "CCY por GBP"},

        {"combined.vertical.title",     "Gráfico vertical combinado"},
        {"combined.vertical.subtitle",  "Cuatro gráficos en uno"},
        {"combined.vertical.domain",    "Fecha"},
        {"combined.vertical.ranges",    new String[] {"CCY por GBP", "Libras", "IBM", "Barras"} },

        {"combined.combined-overlaid.title",     "Gráfico combinado y cubierto"},
        {"combined.combined-overlaid.subtitle",  "XY, cubierto(dos series de tiempo) y cubierto "
                                                +"(Alto Bajo y series de tiempo)"},
        {"combined.combined-overlaid.domain",    "Fecha"},
        {"combined.combined-overlaid.ranges",    new String[] {"CCY por GBP", "Libras", "IBM"} },

        {"combined.dynamic.title",     "Gráfico dinámico combinado"},
        {"combined.dynamic.subtitle",  "XY (series 0), XY (serie 1), cubierto (ambas series)) "
                                      +"y XY (ambas series)"},
        {"combined.dynamic.domain",    "X" },
        {"combined.dynamic.ranges",    new String[] {"Y1", "Y2", "Y3", "Y4"} },

    };

}

