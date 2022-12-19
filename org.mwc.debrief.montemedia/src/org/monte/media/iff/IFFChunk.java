/*
 * @(#)IFFChunk.java  1.1  2006-07-20
 *
 * Copyright (c) 1999-2006 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.iff;

import java.util.*;
/**
 * IFF Chunks form the building blocks of an IFF file.
 * This class is made for reading purposes only. See MutableIFFChunk
 * for writing purposes.
 *
 * @author  Werner Randelshofer, Hausmatt 10, CH-6405 Goldau, Switzerland
 * @version 1.1 2006-07-20 Reworked for Java 1.5.
 * <br>1.0  1999-10-19
 */
public class IFFChunk {
    private int id;
    private int type;
    private long size;
    private long scan;
    private byte[] data;
    private Hashtable<IFFChunk,IFFChunk> propertyChunks;
    private Vector<IFFChunk> collectionChunks;
    
    public IFFChunk(int type, int id) {
        this.id = id;
        this.type = type;
        this.size = -1;
        this.scan = -1;
    }
    public IFFChunk(int type, int id, long size, long scan) {
        this.id = id;
        this.type = type;
        this.size = size;
        this.scan = scan;
    }
    @SuppressWarnings("unchecked")
    public IFFChunk(int type, int id, long size, long scan, IFFChunk propGroup) {
        this.id = id;
        this.type = type;
        this.size = size;
        this.scan = scan;
        if (propGroup != null) {
            if (propGroup.propertyChunks != null) {
                propertyChunks = (Hashtable<IFFChunk,IFFChunk>)propGroup.propertyChunks.clone(); }
            if (propGroup.collectionChunks != null) {
                collectionChunks = (Vector<IFFChunk>)propGroup.collectionChunks.clone(); }
        }
    }
    
    /**
     * @return  ID of chunk.
     */
    public int getID() {
        return id; }
    
    /**
     * @return  Type of chunk.
     */
    public int getType() {
        return type; }
    
    /**
     * @return  Size of chunk.
     */
    public long getSize() {
        return size; }
    
    /**
     * @return  Scan position of chunk within the file.
     */
    public long getScan() {
        return scan; }
    
    public void putPropertyChunk(IFFChunk chunk) {
        if (propertyChunks == null) {
            propertyChunks = new Hashtable<IFFChunk,IFFChunk>(); }
        propertyChunks.put(chunk,chunk);
    }
    public IFFChunk getPropertyChunk(int id) {
        if (propertyChunks == null) {
            return null; }
        IFFChunk chunk = new IFFChunk(type, id);
        return (IFFChunk)propertyChunks.get(chunk);
    }
    public Enumeration propertyChunks() {
        if (propertyChunks == null) {
            propertyChunks = new Hashtable<IFFChunk,IFFChunk>(); }
        return propertyChunks.keys();
    }
    public void addCollectionChunk(IFFChunk chunk) {
        if (collectionChunks == null) {
            collectionChunks = new Vector<IFFChunk>(); }
        collectionChunks.addElement(chunk);
    }
    public IFFChunk[] getCollectionChunks(int id) {
        if (collectionChunks == null) {
            return new IFFChunk[0]; }
        Enumeration enm = collectionChunks.elements();
        int i = 0;
        while ( enm.hasMoreElements() ) {
            IFFChunk chunk = (IFFChunk)enm.nextElement();
            if (chunk.id == id) {
                i++; }
        }
        IFFChunk[] array = new IFFChunk[i];
        i = 0;
        enm = collectionChunks.elements();
        while ( enm.hasMoreElements() ) {
            IFFChunk chunk = (IFFChunk)enm.nextElement();
            if (chunk.id == id) {
                array[i++] = chunk; }
        }
        return array;
    }
    public Enumeration collectionChunks() {
        if (collectionChunks == null) {
            collectionChunks = new Vector<IFFChunk>(); }
        return collectionChunks.elements();
    }
    
    /**
     * Sets the data.
     * Note: The array will not be cloned.
     */
    public void setData(byte[] data) {
        this.data = data; }
    /**
     * Gets the data.
     * Note: The array will not be cloned.
     */
    public byte[] getData() {
        return data; }
    
    @Override
    public boolean equals(Object another) {
        if (another instanceof IFFChunk) {
            IFFChunk that = (IFFChunk) another;
            return (that.id == this.id && that.type == this.type);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return id; }
    
    @Override
    public String toString() {
        return super.toString()+"{"+IFFParser.idToString(getType())+","+IFFParser.idToString(getID())+"}";
    }
}
