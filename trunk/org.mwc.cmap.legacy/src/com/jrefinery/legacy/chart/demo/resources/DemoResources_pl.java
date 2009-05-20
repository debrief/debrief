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
 * ------------------
 * DemoResources.java
 * ------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 * Polish translation: Krzysztof Pa¼ (kpaz@samorzad.pw.edu.pl)
 *
 * $Id: DemoResources_pl.java,v 1.1.1.1 2003/07/17 10:06:39 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 15-Mar-2002 : Version 1 (DG);
 * 26-Mar-2002 : Changed name from JFreeChartDemoResources.java --> DemoResources.java (DG);
 *
 */
package com.jrefinery.legacy.chart.demo.resources;

import java.util.ListResourceBundle;

/**
 * A resource bundle that stores all the user interface items that might need localisation.
 */
public class DemoResources_pl extends ListResourceBundle {

    /**
     * Returns the array of strings in the resource bundle.
     */
    public Object[][] getContents() {
        return contents;
    }

    /** The resources to be localised. */
    static final Object[][] contents = {

        // about frame...
        { "about.title", "Informacja o..."},
        { "about.version.label", "Wersja"},

        // menu labels...
        { "menu.file", "Plik"},
        { "menu.file.mnemonic", new Character('P') },

        { "menu.file.exit", "Zakoñcz"},
        { "menu.file.exit.mnemonic", new Character('K') },

        { "menu.help", "Pomoc"},
        { "menu.help.mnemonic", new Character('C')},

        { "menu.help.about", "About..."},
        { "menu.help.about.mnemonic", new Character('A')},

        // dialog messages...
        { "dialog.exit.title", "Potwierd¼ zamkniêcie..."},
        { "dialog.exit.message", "Czy jeste¶ pewien, ¿e chcesz zakoñczyæ program ?"},

        // labels for the tabs in the main window...
        { "tab.bar",      "Wykresy Kolumnowe i S³upkowe"},
        { "tab.pie",      "Wykresy Ko³owe"},
        {"tab.xy",       "Wykresy XY"},
        {"tab.time",     "Wykresy Liniowe"},
        {"tab.other",    "Wykresy Inne"},
        {"tab.test",     "Wykresy Testowe"},
        {"tab.combined", "Wykresy Niestandardowe"},

        // sample chart descriptions...
        {"chart1.title",       "S³upkowy grupowany: "},
        {"chart1.description", "Wy¶wietla poziome s³upki, porównuje zgrupowane warto¶ci  "
                              +"dla ró¿nych kategorii.  Uwaga: skala na osi poziomej jest odwrócona."},

        {"chart2.title",       "S³upkowy skumulowany: "},
        {"chart2.description", "Wy¶wietla poziome s³upki, porównuje wk³ad poszczególnych warto¶ci "
                              +"do sumy dla ró¿nych kategorii."},

        {"chart3.title",       "Kolumnowy grupowany: "},
        {"chart3.description", "Wy¶wietla pionowe kolumny, porównuje zgrupowane warto¶ci dla ró¿nych kategorii."},

        {"chart4.title",       "Kolumnowy grupowany z efektem 3-W: "},
        {"chart4.description", "Wy¶wietla pionowe kolumny z efektem 3-W,  "
                              +"porównuje zgrupowane warto¶ci dla ró¿nych kategorii"},

        {"chart5.title",       "Kolumnowy skumulowany: "},
        {"chart5.description", "Wy¶wietla pionowe kolumny, "
                              +"porównuje skumulowane warto¶ci dla ró¿nych kategorii."},

        {"chart6.title",       "Kolumnowy skumulowany z efektem 3-W: "},
        {"chart6.description", "Wy¶wietla pionowe kolumny z efektem 3-W,  "
                              +"porównuje skumulowane warto¶ci dla ró¿nych kategorii."},

        {"chart7.title",       "Ko³owy wysuniêty: "},
        {"chart7.description", "Wy¶wietla wk³ad poszczególnych warto¶ci do sumy ca³kowitej, podkre¶laj±c jedn± z warto¶ci poprzez wysuniêcie."},

        {"chart8.title",       "Ko³owy tradycyjny: "},
        {"chart8.description", "Wy¶wietla procentowy wk³ad poszczególnych warto¶ci do sumy ca³kowitej, "
                              +"ponadto wykres ma przyk³adowy obrazek w tle."},

        {"chart9.title",       "XY Punktowy: "},
        {"chart9.description", "Wykres punktowy, z punktami danych po³±czonymi "
                              +"wyg³adzonymi liniami bez znaczników danych."},

        {"chart10.title",       "Liniowy 1: "},
        {"chart10.description", "Wykres liniowy - wy¶wietla trend w czasie lub dla ró¿nych kategorii danych XY. "
                               +"Ponadto demonstruje u¿ycie wielu etykiet/nazw na jednym wykresie."},

        {"chart11.title",       "Liniowy 2: "},
        {"chart11.description", "Wykres liniowy - wy¶wietla trend w czasie lub dla ró¿nych kategorii danych XY. "
                               +"O¶ pionowa jest wyskalowana logarytmicznie."},

        {"chart12.title",       "Liniowy 3: "},
        {"chart12.description", "Wykres liniowy - wy¶wietla trend w czasie lub dla ró¿nych kategorii danych XY ze wskazaniem zmian warto¶ci u¶rednionej ."},

        {"chart13.title",       "Gie³dowy - Liniowy: Max/Min/Otwarcie/Zamkniêcie "},
        {"chart13.description", "Wykres gie³dowy typu Max/Min/Otwarcie/Zamkniêcie oparty o dane HighLowDataset(serie warto¶ci podawane w odpowiedniej kolejno¶ci)."},

        {"chart14.title",       "Gie³dowy - Candlestick: Max/Min/Otwarcie/Zamkniêcie: "},
        {"chart14.description", "Wykres gie³dowy typu Candlestick (Max/Min/Otwarcie/Zamkniêcie) oparty o dane HighLowDataset(serie warto¶ci podawane w odpowiedniej kolejno¶ci)."},

        {"chart15.title",       "Sygna³owy: "},
        {"chart15.description", "Wykres sygna³owy oparty o dane z SignalDataset."},

        {"chart16.title",       "Wiatrowy: "},
        {"chart16.description", "Ilustracja graficzna wiatru, przedstawiaj±ca jego kierunek i si³ê "
                               +"(reprezentowan± w WindDataset)."},

        {"chart17.title",       "Rozproszony punktowy: "},
        {"chart17.description", "Wykres punktowy, rozproszony przedstawiaj±cy dane w uk³adzie XY z XYDataset."},

        {"chart18.title",       "Liniowy: "},
        {"chart18.description", "Wykres wy¶wielta linie i/lub kszta³ty, przedstawiaj±ce dane z CategoryDataset. "
                               +"Ponadto ilustruje u¿ycie obrazka w tle wykresu oraz "
                               +"przezroczysto¶ci alpha "
                               +"na rysunku."},

        {"chart19.title",       "Pionowy XY kolumnowy: "},
        {"chart19.description", "Wykres prezentuje pionowe s³upki oparte na "
                               +"IntervalXYDataset."},

        {"chart20.title",       "Puste dane: "},
        {"chart20.description", "Wykres dla braku danych (null dataset)."},

        {"chart21.title",       "Dane zero: "},
        {"chart21.description", "Wykres dla serii zer w danych."},

        {"chart22.title",       "Liniowy z JScrollPane: "},
        {"chart22.description", "Wykres liniowy osadzony w komponencie JScrollPane pozwalaj±cym na przewijanie obszaru wykresu wewn±trz okna gdy jest ono za ma³e."},

        {"chart23.title",       "Kolumnowy dla jednej serii: "},
        {"chart23.description", "Wykres kolumnowy dla jednej serii danych. "
                               +"Demonstruje przy okazji ¿ycie ramki w ChartPanel."},

        {"chart24.title",       "Wykres dynamiczy: "},
        {"chart24.description", "Dynamiczny (rysowany na bie¿±co) wykres do testowania mechanizmu zdarzeñ (event notification mechanism)."},

        {"chart25.title",       "Nak³adany gie³dowy: Max/Min/Otwarcie/Zamkniêcie: "},
        {"chart25.description", "Wyswietla wykres nak³adany gie³dowy: Max/Min/Otwarcie/Zamkniêcie z "
                               +"ilustracj± przebiegu ¶redniej."},

        {"chart26.title",       "Poziomy - kombinowany: "},
        {"chart26.description", "Wy¶wietla 3 ró¿ne poziome wykresy liniowe /czasowe i XY kolumnowy "
                               +"."},

        {"chart27.title",       "Pionowy - kombinowany: "},
        {"chart27.description", "Wy¶wietla 4 ró¿ne wykresy umo¿liwiaj±ce porównanie danych w pionie na jednym rysunku "
                               +"dla XY, liniowe /czasowe oraz kolumn pionowych XY."},

        {"chart28.title",       "Kombinowany i nak³adany: "},
        {"chart28.description", "Kombinowany wykres XY, nak³adany liniowy/TimeSeries i nak³adany "
                               +"Max/Min & liniowy."},

        {"chart29.title",       "Kombinowany i nak³adany dynamiczny: "},
        {"chart29.description", "Wy¶wietla kombinowany i nak³adany wykres dynamiczny w celu "
                               +"testowania / ilustracji mechnizmu obs³ugi zdarzeñ."},

        {"charts.display", "Poka¿"},

        // chart titles and labels...
        {"bar.horizontal.title",  "Poziomy wykres s³upkowy"},
        {"bar.horizontal.domain", "Kategorie"},
        {"bar.horizontal.range",  "Warto¶ci"},

        {"bar.horizontal-stacked.title",  "Poziomy, skumulowany wykres s³upkowy"},
        {"bar.horizontal-stacked.domain", "Kategorie"},
        {"bar.horizontal-stacked.range",  "Warto¶ci"},

        {"bar.vertical.title",  "Pionowy wykres kolumnowy"},
        {"bar.vertical.domain", "Kategorie"},
        {"bar.vertical.range",  "Warto¶ci"},

        {"bar.vertical3D.title",  "Pionowy wykres kolumnowy z efektem 3-W"},
        {"bar.vertical3D.domain", "Kategorie"},
        {"bar.vertical3D.range",  "Warto¶ci"},

        {"bar.vertical-stacked.title",  "Pionowy, skumulowany wykres kolumnowy"},
        {"bar.vertical-stacked.domain", "Kategorie"},
        {"bar.vertical-stacked.range",  "Warto¶ci"},

        {"bar.vertical-stacked3D.title",  "Pionowy, skumulowany wykres kolumnowy z efektem 3-W"},
        {"bar.vertical-stacked3D.domain", "Kategorie"},
        {"bar.vertical-stacked3D.range",  "Warto¶ci"},

        {"pie.pie1.title", "Wykres ko³owy 1 - wysuniêty"},

        {"pie.pie2.title", "Wykres ko³owy 2 - tradycyjny"},

        {"xyplot.sample1.title",  "Wykres XY Punktowy"},
        {"xyplot.sample1.domain", "X Warto¶ci"},
        {"xyplot.sample1.range",  "Y Warto¶ci"},

        {"timeseries.sample1.title",     "Wykres liniowy przebiegu kursu w czasie - 1"},
        {"timeseries.sample1.subtitle",  "Warto¶ci PLN in JPY"},
        {"timeseries.sample1.domain",    "Data"},
        {"timeseries.sample1.range",     "CCY na z³otówkê"},
        {"timeseries.sample1.copyright", "(C)opyright 2002, by Krzysztof Pa¼, PW"},

        {"timeseries.sample2.title",    "Liniowy 2"},
        {"timeseries.sample2.domain",   "Millisekundy"},
        {"timeseries.sample2.range",    "O¶ logarytmiczna"},
        {"timeseries.sample2.subtitle", "Millisekundy"},

        {"timeseries.sample3.title",    "Liniowy z ruchomym trendem u¶rednionym"},
        {"timeseries.sample3.domain",   "Data"},
        {"timeseries.sample3.range",    "CCY na PLN"},
        {"timeseries.sample3.subtitle", "30 dniowy ¶redni przebieg kursu PLN"},

        {"timeseries.highlow.title",    "Gie³dowy wykres Max/Min/Otwarcie/Zamkniêcie "},
        {"timeseries.highlow.domain",   "Data"},
        {"timeseries.highlow.range",    "Cena (PLN za udzia³)"},
        {"timeseries.highlow.subtitle", "Warto¶æ akcji TPSA"},

        {"timeseries.candlestick.title",    "Gie³dowy CandleStick"},
        {"timeseries.candlestick.domain",   "Data"},
        {"timeseries.candlestick.range",    "Cena (PLN za udzia³)"},
        {"timeseries.candlestick.subtitle", "Warto¶æ akcji JTT"},

        {"timeseries.signal.title",    "Wykres sygna³owy"},
        {"timeseries.signal.domain",   "Data"},
        {"timeseries.signal.range",    "Cena (PLN za udzia³)"},
        {"timeseries.signal.subtitle", "Warto¶æ akcji OPTIMUS S.A."},

        {"other.wind.title",  "Wykres wiatru"},
        {"other.wind.domain", "O¶ X"},
        {"other.wind.range",  "O¶ Y"},

        {"other.scatter.title",  "Rozrzucony punktowy"},
        {"other.scatter.domain", "O¶ X"},
        {"other.scatter.range",  "O¶ Y"},

        {"other.line.title",  "Liniowy"},
        {"other.line.domain", "Kategoria"},
        {"other.line.range",  "Warto¶æ"},

        {"other.xybar.title",  "Liniowy kolumnowy"},
        {"other.xybar.domain", "Data"},
        {"other.xybar.range",  "Warto¶æ"},

        {"test.null.title",  "Wykres XY (null data)"},
        {"test.null.domain", "X"},
        {"test.null.range",  "Y"},

        {"test.zero.title",  "Wykres XY (zero data)"},
        {"test.zero.domain", "O¶ X"},
        {"test.zero.range",  "O¶ Y"},

        {"test.scroll.title",    "Liniowy / Time Series"},
        {"test.scroll.subtitle", "Warto¶æ PLN"},
        {"test.scroll.domain",   "Data"},
        {"test.scroll.range",    "Warto¶æ"},

        {"test.single.title",     "Pojedyncza seria"},
        {"test.single.subtitle1", "Podtytu³ 1"},
        {"test.single.subtitle2", "Podtytu³ 2"},
        {"test.single.domain",    "Data"},
        {"test.single.range",     "Warto¶æ"},

        {"test.dynamic.title",  "Wykres Dynamiczny"},
        {"test.dynamic.domain", "Domena"},
        {"test.dynamic.range",  "Zasiêg"},

        {"combined.overlaid.title",     "Wykres Nak³adany"},
        {"combined.overlaid.subtitle",  "Max/Min/Otwarcie/Zamkniêcie z ilustracj± przebiegu ¶redniej."},
        {"combined.overlaid.domain",    "Data" },
        {"combined.overlaid.range",     "OPTIMUS S.A."},

        {"combined.horizontal.title",     "Wykres poziomo kombinowany"},
        {"combined.horizontal.subtitle",  "Linowy / Time Series s³upkowy XY "},
        {"combined.horizontal.domains",   new String[] {"Dane 1", "Dane 2", "Dane 3"} },
        {"combined.horizontal.range",     "CCY na PLN"},

        {"combined.vertical.title",     "Wykres pionowo kombinowany"},
        {"combined.vertical.subtitle",  "Cztery wykresy na jednym"},
        {"combined.vertical.domain",    "Data"},
        {"combined.vertical.ranges",    new String[] {"CCY na PLN", "Z³otówki", "KGHM", "S³upki"} },

        {"combined.combined-overlaid.title",     "Wykres kombinowany i nak³adany"},
        {"combined.combined-overlaid.subtitle",  "XY, mnak³adany (dwie TimeSeries) i nak³adany "
                                                +"(Max/Min i TimeSeries)"},
        {"combined.combined-overlaid.domain",    "Data"},
        {"combined.combined-overlaid.ranges",    new String[] {"CCY na PLN", "Z³otówki", "TPSA"} },

        {"combined.dynamic.title",     "Wykres poziomo kombinowany - dynamiczny"},
        {"combined.dynamic.subtitle",  "XY (seria 0), XY (seria 1), nak³±dany (obie serie) "
                                      +"oraz XY (obie serie)"},
        {"combined.dynamic.domain",    "X" },
        {"combined.dynamic.ranges",    new String[] {"Y1", "Y2", "Y3", "Y4"} },

    };

}
