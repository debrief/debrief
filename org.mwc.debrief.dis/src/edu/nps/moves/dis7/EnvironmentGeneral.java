package edu.nps.moves.dis7;

import java.util.*;
import java.io.*;
import edu.nps.moves.disenum.*;
import edu.nps.moves.disutil.*;


/**
 *  Information about a geometry, a state associated with a geometry, a bounding volume, or an associated entity ID. NOTE: this class requires hand coding. 6.2.31
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All rights reserved.
 * This work is licensed under the BSD open source license, available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class EnvironmentGeneral extends Object implements Serializable
{
   /** Record type */
   protected long  environmentType;

   /** length, in bits */
   protected short  length;

   /** Identify the sequentially numbered record index */
   protected short  index;

   /** padding */
   protected short  padding1;

   /** Geometry or state record */
   protected short  geometry;

   /** padding to bring the total size up to a 64 bit boundry */
   protected short  padding2;


/** Constructor */
 public EnvironmentGeneral()
 {
 }

public int getMarshalledSize()
{
   int marshalSize = 0; 

   marshalSize = marshalSize + 4;  // environmentType
   marshalSize = marshalSize + 1;  // length
   marshalSize = marshalSize + 1;  // index
   marshalSize = marshalSize + 1;  // padding1
   marshalSize = marshalSize + 1;  // geometry
   marshalSize = marshalSize + 1;  // padding2

   return marshalSize;
}


public void setEnvironmentType(long pEnvironmentType)
{ environmentType = pEnvironmentType;
}

public long getEnvironmentType()
{ return environmentType; 
}

public void setLength(short pLength)
{ length = pLength;
}

public short getLength()
{ return length; 
}

public void setIndex(short pIndex)
{ index = pIndex;
}

public short getIndex()
{ return index; 
}

public void setPadding1(short pPadding1)
{ padding1 = pPadding1;
}

public short getPadding1()
{ return padding1; 
}

public void setGeometry(short pGeometry)
{ geometry = pGeometry;
}

public short getGeometry()
{ return geometry; 
}

public void setPadding2(short pPadding2)
{ padding2 = pPadding2;
}

public short getPadding2()
{ return padding2; 
}


public void marshal(DataOutputStream dos)
{
    try 
    {
       dos.writeInt( (int)environmentType);
       dos.writeByte( (byte)length);
       dos.writeByte( (byte)index);
       dos.writeByte( (byte)padding1);
       dos.writeByte( (byte)geometry);
       dos.writeByte( (byte)padding2);
    } // end try 
    catch(Exception e)
    { 
      System.out.println(e);}
    } // end of marshal method

public void unmarshal(DataInputStream dis)
{
    try 
    {
       environmentType = dis.readInt();
       length = (short)dis.readUnsignedByte();
       index = (short)dis.readUnsignedByte();
       padding1 = (short)dis.readUnsignedByte();
       geometry = (short)dis.readUnsignedByte();
       padding2 = (short)dis.readUnsignedByte();
    } // end try 
   catch(Exception e)
    { 
      System.out.println(e); 
    }
 } // end of unmarshal method 


/**
 * Packs a Pdu into the ByteBuffer.
 * @throws java.nio.BufferOverflowException if buff is too small
 * @throws java.nio.ReadOnlyBufferException if buff is read only
 * @see java.nio.ByteBuffer
 * @param buff The ByteBuffer at the position to begin writing
 * @since ??
 */
public void marshal(java.nio.ByteBuffer buff)
{
       buff.putInt( (int)environmentType);
       buff.put( (byte)length);
       buff.put( (byte)index);
       buff.put( (byte)padding1);
       buff.put( (byte)geometry);
       buff.put( (byte)padding2);
    } // end of marshal method

/**
 * Unpacks a Pdu from the underlying data.
 * @throws java.nio.BufferUnderflowException if buff is too small
 * @see java.nio.ByteBuffer
 * @param buff The ByteBuffer at the position to begin reading
 * @since ??
 */
public void unmarshal(java.nio.ByteBuffer buff)
{
       environmentType = buff.getInt();
       length = (short)(buff.get() & 0xFF);
       index = (short)(buff.get() & 0xFF);
       padding1 = (short)(buff.get() & 0xFF);
       geometry = (short)(buff.get() & 0xFF);
       padding2 = (short)(buff.get() & 0xFF);
 } // end of unmarshal method 


 /*
  * The equals method doesn't always work--mostly it works only on classes that consist only of primitives. Be careful.
  */
@Override
 public boolean equals(Object obj)
 {

    if(this == obj){
      return true;
    }

    if(obj == null){
       return false;
    }

    if(getClass() != obj.getClass())
        return false;

    return equalsImpl(obj);
 }

 /**
  * Compare all fields that contribute to the state, ignoring
 transient and static fields, for <code>this</code> and the supplied object
  * @param obj the object to compare to
  * @return true if the objects are equal, false otherwise.
  */
 public boolean equalsImpl(Object obj)
 {
     boolean ivarsEqual = true;

    if(!(obj instanceof EnvironmentGeneral))
        return false;

     final EnvironmentGeneral rhs = (EnvironmentGeneral)obj;

     if( ! (environmentType == rhs.environmentType)) ivarsEqual = false;
     if( ! (length == rhs.length)) ivarsEqual = false;
     if( ! (index == rhs.index)) ivarsEqual = false;
     if( ! (padding1 == rhs.padding1)) ivarsEqual = false;
     if( ! (geometry == rhs.geometry)) ivarsEqual = false;
     if( ! (padding2 == rhs.padding2)) ivarsEqual = false;

    return ivarsEqual;
 }
} // end of class
