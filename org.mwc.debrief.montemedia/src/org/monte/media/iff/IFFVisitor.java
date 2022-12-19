/*
 * @(#)IFFVisitor.java  1.0  1999-10-19
 *
 * Copyright (c) 1999 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.iff;

import org.monte.media.AbortException;
import org.monte.media.ParseException;

/**
IFFVisitor is notified each time the IFFParser visits
a data chunk and when a group is entered or leaved.

@version  1.0  1999-10-19
*/
public interface IFFVisitor
  {
  public void enterGroup(IFFChunk group)
  throws ParseException, AbortException;

  public void leaveGroup(IFFChunk group)
  throws ParseException, AbortException;

  public void visitChunk(IFFChunk group, IFFChunk chunk)
  throws ParseException, AbortException;
  }
