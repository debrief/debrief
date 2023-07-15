/*
 * @(#)FileMultiplexer.java  1.0  2011-02-20
 * 
 * Copyright (c) 2011 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.imgseq;

import org.monte.media.Buffer;
import org.monte.media.Multiplexer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static org.monte.media.BufferFlag.*;

/**
 * Multiplexes samples into individual files.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-02-20 Created.
 */
@Deprecated
public class FileMultiplexer implements Multiplexer {

    private File dir;
    private String baseName;
    private String extension;
    private long position = 0;
    private int minDigits = 4;

    public FileMultiplexer(File dir, String baseName, String extension) {
        this.dir = dir;
        this.baseName = baseName;
        this.extension = extension;
    }

    @Override
    public void write(int track, Buffer buf) throws IOException {
        if (buf.isFlag(DISCARD)) {
            return;
        }

        File file = new File(dir, baseName + numToString(position + 1) + extension);

        if (buf.data instanceof byte[]) {
            FileOutputStream out = new FileOutputStream(file);
            try {
                out.write((byte[]) buf.data, buf.offset, buf.length);
            } finally {
                out.close();
            }
        } else if (buf.data instanceof File) {
            FileInputStream in = new FileInputStream((File) buf.data);
            try {
                FileOutputStream out = new FileOutputStream(file);
                try {
                    byte[] b = new byte[2048];
                    int len;
                    while ((len = in.read(b)) != -1) {
                        out.write(b, 0, len);
                    }
                } finally {
                    out.close();
                }
            } finally {
                in.close();
            }
        } else {
            throw new IllegalArgumentException("Can't process buffer data:" + buf.data);
        }

        position++;
    }

    private String numToString(long num) {
        StringBuilder b = new StringBuilder();
        b.append(Long.toString(num));
        while (b.length() < minDigits) {
            b.insert(0, '0');
        }
        return b.toString();
    }

    @Override
    public void close() throws IOException {
        //
    }
}
