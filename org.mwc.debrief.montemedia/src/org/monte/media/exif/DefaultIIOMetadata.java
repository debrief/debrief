/*
 * @(#)DefaultIIOMetadata.java  1.0  2010-09-07
 * 
 * Copyright (c) 2010 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */

package org.monte.media.exif;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

/**
 * DefaultIIOMetadata.
 *
 * @author Werner Randelshofer
 * @version 1.0 2010-09-07 Created.
 */
public class DefaultIIOMetadata extends IIOMetadata {
    private final IIOMetadataNode root;
    private final String formatName;
    public DefaultIIOMetadata(String formatName, IIOMetadataNode root) {
        super(true,// standardMetadataFormatSupported
                          formatName,
                          "javax.imageio.metadata.IIOMetadataNode",
                          null,
                          null);
        this.formatName=formatName;
        this.root=root;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public Node getAsTree(String formatName) {
        return root;
    }

    @Override
    public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
