/*
 * @(#)StructTableModel.java  1.1  2000-09-29
 *
 * Copyright (c) 2000 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.binary;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

/**
 * Table model for structured binary data.
 *
 * @author  Werner Randelshofer, Hausmatt 10, CH-6405 Goldau, Switzerland
 * @version 1.1   2000-09-29
 * <br>1.0  2000-06-12
 */
public class StructTableModel extends AbstractTableModel {
    protected Vector data;
    protected StructParser.TypedefDeclaration typedef;
    private final char[] HEX = { '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f' };
    
    /**
     * Vector provides the data of the model.
     * Even entries are represented by the first column of the table.
     * Odd entries are represented by the second column of the table.
     */
    public StructTableModel(StructParser.TypedefDeclaration typedef, Vector data) {
        this.typedef = typedef;
        this.data = data;
    }
    
    @Override
    public int getRowCount() {
        return data.size();
    }
    @Override
    public int getColumnCount() {
        return 2;
    }
    @Override
    public Object getValueAt(int row, int column) {
        StructTableModel.Value elem = (StructTableModel.Value) data.elementAt(row);
        Object value;
        if (column == 0) {
            // value = identifierToString(((elem.index == null) ? elem.declaration : elem.declaration+elem.index).toString());
            int p = elem.qualifiedIdentifier.indexOf('.');
            String identifier = (p == -1) ? elem.qualifiedIdentifier : elem.qualifiedIdentifier.substring(p + 1);
            value = identifierToString(((elem.index == null) ? identifier : identifier+elem.index).toString());
        } else {
            value = elem.value;
        }
        return value;
    }
    
    public static String identifierToString(String s) {
        StringBuilder b = new StringBuilder();
        boolean wasUpperCase = true;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isUpperCase(s.charAt(i))) {
                if (!wasUpperCase) {
                    b.append(' ');
                }
                wasUpperCase = true;
            } else {
                wasUpperCase = false;
            }
            b.append(s.charAt(i));

        }
        return b.toString();
    }
    
    @Override
    public String getColumnName(int column) {
        return (column == 0) ? "Name" : "Value";
    }
    /**
     * Represents one typed value of the data structure represented by the StructTableModel.
     */
    public static class Value {
        public String qualifiedIdentifier;
        public Object declaration;
        public String index;
        public Object value;
        public int intValue;
        public Value() {
            
        }
        public Value(String qualfiedIdentifier, String index, Object declaration, Object value, int intValue) {
            this.qualifiedIdentifier = qualifiedIdentifier;
            this.index = index;
            this.declaration = declaration;
            this.value = value;
            this.intValue=intValue;
        }
    }
    
    @Override
    public String toString() {
        return (typedef != null) ? typedef.toString() : super.toString();
    }
}

