/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info: http://www.object-refinery.com/jfreechart/index.html
 * Project Lead: David Gilbert (david.gilbert@object-refinery.com);
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
 * DemoResources_fr.java
 * ---------------------
 * (C) Copyright 2002, by Anthony Boulestreau.
 *
 * Original Author:  Anthony Boulestreau;
 * Contributor(s):   -;
 *
 * $Id: DemoResources_fr.java,v 1.1.1.1 2003/07/17 10:06:39 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 26-Mar-2002 : Version 1 (AB);
 *
 */

package com.jrefinery.legacy.chart.demo.resources;

import java.util.ListResourceBundle;

/**
 * Localised resources for France.
 */
public class DemoResources_fr extends ListResourceBundle {

    /**
     * Returns the array of strings in the resource bundle.
     */
    public Object[][] getContents() {
        return contents;
    }

    /** The resources to be localised. */
    static final Object[][] contents = {

        // about frame...
        {"about.title", "A propos de..."},
        {"about.version.label", "Version"},

        // menu labels...
        {"menu.file", "Fichier"},
        {"menu.file.mnemonic", new Character('F')},

        {"menu.file.exit", "Sortie"},
        {"menu.file.exit.mnemonic", new Character('x')},

        {"menu.help", "Aide"},
        {"menu.help.mnemonic", new Character('H')},

        {"menu.help.about", "A propos de..."},
        {"menu.help.about.mnemonic", new Character('A')},

        // dialog messages...
        {"dialog.exit.title", "Confirmation de fermeture..."},
        {"dialog.exit.message", "Etes vous certain de vouloir sortir?"},

        // labels for the tabs in the main window...
        {"tab.bar", "Diagrammes en Barre"},
        {"tab.pie", "Diagrammes en Secteur"},
        {"tab.xy", "Diagrammes XY"},
        {"tab.time", "Diagrammes de Séries Temporelles"},
        {"tab.other", "Autres Diagrammes"},
        {"tab.test", "Diagrammes de Test"},
        {"tab.combined", "Diagrammes Combinés"},

        // sample chart descriptions...
        {"chart1.title", "Diagramme en Barre Horizontale: "},
        {"chart1.description", "Affiche des barre horizontale à partir des données "
        + "d'un CategoryDataset. Remarquez que l'axe numérique est inversé."},

        {"chart2.title", "Diagramme en Barre Empilée Horizontale: "},
        {"chart2.description", "Affiche des barres empilées horizontales à partir des données "
        +"d'un CategoryDataset."},

        {"chart3.title", "Diagramme en Barre Verticale: "},
        {"chart3.description", "Affiche des barres verticales à partir des données d'un CategoryDataset."},

        {"chart4.title", "Diagramme en Barre 3D Verticale: "},
        {"chart4.description", "Affiche des barres verticales avec un effet 3D à partir des données "
        +"d'un CategoryDataset."},

        {"chart5.title", "Diagramme en Barre Empilée Verticale: "},
        {"chart5.description", "Affiche des barres empilées verticale à partir des données "
        +"d'un CategoryDataset."},

        {"chart6.title", "Diagramme en Barre 3D Empilée Verticale: "},
        {"chart6.description", "Affiche des barres empilées verticale avec un effet 3D à partir des données "
        +"d'un CategoryDataset."},

        {"chart7.title", "Diagrammes en Secteur 1: "},
        {"chart7.description", "Un diagramme en secteur avec une section éclatée."},

        {"chart8.title", "Diagrammes en Secteur 2: "},
        {"chart8.description", "Un diagramme en secteur montrant des pourcentages sur les labels de catégories. De plus, "
        +"ce graphique a une image de fond."},

        {"chart9.title", "Tracé XY: "},
        {"chart9.description", "Un diagramme en ligne à partir de données d'un XYDataset. Les deux axes sont "
        +"numériques."},

        {"chart10.title", "Série Temporelle 1: "},
        {"chart10.description", "Un diagramme de séries temporelles à partir de données d'un XYDataset. Ce "
        +"diagramme montre de plus l'utilisation de plusieurs titres de diagramme."},

        {"chart11.title", "Série Temporelle 2: "},
        {"chart11.description", "Un diagramme de séries temporelles à partir de données d'un XYDataset. "
        +"L'axe vertical possède une échelle logarithmique."},

        {"chart12.title", "Série Temporelle 3: "},
        {"chart12.description", "Un diagramme de séries temporelles avec une moyenne mobile."},

        {"chart13.title", "Diagramme Max/Min/Ouverture/Fermeture: "},
        {"chart13.description", "Un diagramme max/min/ouverture/fermeture basé sur les données d'un HighLowDataset."},

        {"chart14.title", "Diagramme en Chandelier: "},
        {"chart14.description", "Un diagramme en Chandelier basé sur les données d'un HighLowDataset."},

        {"chart15.title", "Diagramme en Signal: "},
        {"chart15.description", "Diagramme en signal basé sur les données d'un SignalDataset."},

        {"chart16.title", "Tracé de Vents: "},
        {"chart16.description", "Un tracé de vents, représente la direction et l'intensité du vent (fourni "
        +"par l'intermédiaire d'un WindDataset)."},

        {"chart17.title", "Nuage de points: "},
        {"chart17.description", "Un nuage de points à partir des données d'un XYDataset."},

        {"chart18.title", "Diagramme en Ligne: "},
        {"chart18.description", "Un diagramme affichant des lignes ou des formes à partir des données "
        +"d'un CategoryDataset. Ce tracé montre de plus l'utilisation "
        +"d'une image de fond sur le diagramme, et de l'alpha-transparence sur le "
        +"tracé."},

        {"chart19.title", "Diagramme en Barre XY Verticale: "},
        {"chart19.description", "Un diagramme avec des barres verticales, basé sur des données "
        +"d'un IntervalXYDataset."},

        {"chart20.title", "Données Nulles: "},
        {"chart20.description", "Diagramme à partir d'un ensemble de données nulles."},

        {"chart21.title", "Données Zéros: "},
        {"chart21.description", "Diagramme à partir d'un ensemble de données contenant des séries de zéros."},

        {"chart22.title", "Diagramme dans un JScrollPane: "},
        {"chart22.description", "Un diagramme inséré dans un JScrollPane."},

        {"chart23.title", "Diagramme en Barre avec Série Unique: "},
        {"chart23.description", "Un diagramme en barre avec série unique. Ce diagramme montre de plus l'utilisation "
        +"d'une bordure autour d'un ChartPanel."},

        {"chart24.title", "Diagramme dynamique: "},
        {"chart24.description", "Un diagramme dynamique, pour tester le mécanisme de notification des événements."},

        {"chart25.title", "Diagramme Superposé: "},
        {"chart25.description", "Affiche un diagramme superposé d'un tracé max/min/ouverture/fermeture et "
        +"de moyenne mobile."},

        {"chart26.title", "Diagramme Combiné Horizontalement: "},
        {"chart26.description", "Affiche un diagramme combiné horizontalement d'un tracé de séries temporelles et "
        +"de barres XY."},

        {"chart27.title", "Diagramme Combiné Verticalement: "},
        {"chart27.description", "Affiche un diagramme combiné verticalement d'un tracé XY, de séries temporelles et "
        +"de barres XY vertical."},

        {"chart28.title", "Diagramme Combiné et Superposé: "},
        {"chart28.description", "Un diagramme combiné d'un tracé XY, d'un tracé superposé de deux séries temporelles et "
        +"d'un tracé superposé d'une série temporelle et d'un max/min/ouverture/fermeture."},

        {"chart29.title", "Diagramme Dynamique Combiné et Superposé: "},
        {"chart29.description", "Affiche un diagramme dynamique combiné et superposé, pour tester le mécanisme "
        +"de notification des événements."},

        {"charts.display", "Affiche"},

        // chart titles and labels...
        {"bar.horizontal.title", "Diagramme en Barre Horizontale"},
        {"bar.horizontal.domain", "Catégories"},
        {"bar.horizontal.range", "Valeur"},

        {"bar.horizontal-stacked.title", "Diagramme en Barre Empilée Horizontale"},
        {"bar.horizontal-stacked.domain", "Catégories"},
        {"bar.horizontal-stacked.range", "Valeur"},

        {"bar.vertical.title", "Diagramme en Barre Verticale"},
        {"bar.vertical.domain", "Catégories"},
        {"bar.vertical.range", "Valeur"},

        {"bar.vertical3D.title", "Diagramme en Barre 3D Verticale"},
        {"bar.vertical3D.domain", "Catégories"},
        {"bar.vertical3D.range", "Valeur"},

        {"bar.vertical-stacked.title", "Diagramme en Barre Empilée Verticale"},
        {"bar.vertical-stacked.domain", "Catégories"},
        {"bar.vertical-stacked.range", "Valeur"},

        {"bar.vertical-stacked3D.title", "Diagramme en Barre 3D Empilée Verticale"},
        {"bar.vertical-stacked3D.domain", "Catégories"},
        {"bar.vertical-stacked3D.range", "Valeur"},

        {"pie.pie1.title", "Diagramme en Secteur 1"},

        {"pie.pie2.title", "Diagramme en Secteur 2"},

        {"xyplot.sample1.title", "Tracé XY"},
        {"xyplot.sample1.domain", "Valeurs X"},
        {"xyplot.sample1.range", "Valeurs Y"},

        {"timeseries.sample1.title", "Diagramme de Séries Temporelles 1"},
        {"timeseries.sample1.subtitle", "Valeur du GBP pour le JPY"},
        {"timeseries.sample1.domain", "Date"},
        {"timeseries.sample1.range", "CCY par GBP"},
        {"timeseries.sample1.copyright", "(C)opyright 2002, by Simba Management Limited"},

        {"timeseries.sample2.title", "Diagramme de Séries Temporelles 2"},
        {"timeseries.sample2.domain", "Milliseconde"},
        {"timeseries.sample2.range", "Axes Logarithmique"},
        {"timeseries.sample2.subtitle", "Millisecondes"},

        {"timeseries.sample3.title", "Diagramme de Séries Temporelles avec Moyenne Mobile"},
        {"timeseries.sample3.domain", "Date"},
        {"timeseries.sample3.range", "CCY par GBP"},
        {"timeseries.sample3.subtitle", "Moyenne mobile sur 30 jour du GBP"},

        {"timeseries.highlow.title", "Diagramme Max/Min/Ouverture/Fermeture"},
        {"timeseries.highlow.domain", "Date"},
        {"timeseries.highlow.range", "Prix ($ par action)"},
        {"timeseries.highlow.subtitle", "Prix des actions IBM"},

        {"timeseries.candlestick.title", "Diagramme en Chandelier"},
        {"timeseries.candlestick.domain", "Date"},
        {"timeseries.candlestick.range", "Prix ($ par action)"},
        {"timeseries.candlestick.subtitle", "Prix des actions IBM"},

        {"timeseries.signal.title", "Diagramme en Signal"},
        {"timeseries.signal.domain", "Date"},
        {"timeseries.signal.range", "Prix ($ par action)"},
        {"timeseries.signal.subtitle", "Prix des actions IBM"},

        {"other.wind.title", "Tracé de Vents"},
        {"other.wind.domain", "Axe X"},
        {"other.wind.range", "Axe Y"},

        {"other.scatter.title", "Nuage de Points"},
        {"other.scatter.domain", "Axe X"},
        {"other.scatter.range", "Axe Y"},

        {"other.line.title", "Diagramme en Ligne"},
        {"other.line.domain", "Catégorie"},
        {"other.line.range", "Valeur"},

        {"other.xybar.title", "Diagramme en Barre de Séries Temporelles"},
        {"other.xybar.domain", "Date"},
        {"other.xybar.range", "Valeur"},

        {"test.null.title", "Tracé XY (données nulle)"},
        {"test.null.domain", "X"},
        {"test.null.range", "Y"},

        {"test.zero.title", "Tracé XY (données zéros)"},
        {"test.zero.domain", "Axe X"},
        {"test.zero.range", "Axe Y"},

        {"test.scroll.title", "Série Temporelle"},
        {"test.scroll.subtitle", "Valeur du GBP"},
        {"test.scroll.domain", "Date"},
        {"test.scroll.range", "Valeur"},

        {"test.single.title", "Diagramme en Barre avec Série Unique"},
        {"test.single.subtitle1", "Sous-titre 1"},
        {"test.single.subtitle2", "Sous-titre 2"},
        {"test.single.domain", "Date"},
        {"test.single.range", "Valeur"},

        {"test.dynamic.title", "Diagramme dynamique"},
        {"test.dynamic.domain", "Domaine"},
        {"test.dynamic.range", "Interval"},

        {"combined.overlaid.title", "Diagramme Superposé"},
        {"combined.overlaid.subtitle", "Max/Min/Ouverture/Fermeture plus Moyenne Mobile"},
        {"combined.overlaid.domain", "Date" },
        {"combined.overlaid.range", "IBM"},

        {"combined.horizontal.title", "Diagramme Combiné Horizontalement"},
        {"combined.horizontal.subtitle", "Séries Temporelles et Diagrammes en Barres XY"},
        {"combined.horizontal.domains", new String[] {"Date 1", "Date 2", "Date 3"} },
        {"combined.horizontal.range", "CCY par GBP"},

        {"combined.vertical.title", "Diagramme Combiné Verticalement"},
        {"combined.vertical.subtitle", "Quatre diagramme en un"},
        {"combined.vertical.domain", "Date"},
        {"combined.vertical.ranges", new String[] {"CCY par GBP", "Pounds", "IBM", "Barres"} },

        {"combined.combined-overlaid.title", "Diagramme Combiné et Superposé"},
        {"combined.combined-overlaid.subtitle", "XY, Superposé (2 TimeSeriess) et Superposé "
        +"(HighLow et TimeSeries)"},
        {"combined.combined-overlaid.domain", "Date"},
        {"combined.combined-overlaid.ranges", new String[] {"CCY par GBP", "Pounds", "IBM"} },

        {"combined.dynamic.title", "Diagramme Dynamique Combiné"},
        {"combined.dynamic.subtitle", "XY (séries 0), XY (séries 1), Superposé (les deux séries) "
        +"et XY (les deux séries)"},
        {"combined.dynamic.domain", "X" },
        {"combined.dynamic.ranges", new String[] {"Y1", "Y2", "Y3", "Y4"} },

    };

}
