package MWC.Utilities.ReaderWriter;

import MWC.GUI.Layers.NeedsToKnowAboutLayers;

/** interface for REP line importers that want/need to know about the
 * parent layers object
 *
 */
public interface ExtensibleLineImporter extends PlainLineImporter,
    NeedsToKnowAboutLayers
{

}
