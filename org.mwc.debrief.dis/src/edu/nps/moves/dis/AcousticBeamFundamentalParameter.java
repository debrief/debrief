package edu.nps.moves.dis;

import java.util.*;
import java.io.*;
import edu.nps.moves.disenum.*;
import edu.nps.moves.disutil.*;


/**
 * Used in UaPdu
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All rights reserved.
 * This work is licensed under the BSD open source license, available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class AcousticBeamFundamentalParameter extends Object implements Serializable
{
   /** parameter index */
   protected int  activeEmissionParameterIndex;

   /** scan pattern */
   protected int  scanPattern;

   /** beam center azimuth */
   protected float  beamCenterAzimuth;

   /** azimuthal beamwidth */
   protected float  azimuthalBeamwidth;

   /** beam center */
   protected float  beamCenterDE;

   /** DE beamwidth (vertical beamwidth) */
   protected float  deBeamwidth;


/** Constructor */
 public AcousticBeamFundamentalParameter()
 {
 }

public int getMarshalledSize()
{
   int marshalSize = 0; 

   marshalSize = marshalSize + 2;  // activeEmissionParameterIndex
   marshalSize = marshalSize + 2;  // scanPattern
   marshalSize = marshalSize + 4;  // beamCenterAzimuth
   marshalSize = marshalSize + 4;  // azimuthalBeamwidth
   marshalSize = marshalSize + 4;  // beamCenterDE
   marshalSize = marshalSize + 4;  // deBeamwidth

   return marshalSize;
}


public void setActiveEmissionParameterIndex(int pActiveEmissionParameterIndex)
{ activeEmissionParameterIndex = pActiveEmissionParameterIndex;
}

public int getActiveEmissionParameterIndex()
{ return activeEmissionParameterIndex; 
}

public void setScanPattern(int pScanPattern)
{ scanPattern = pScanPattern;
}

public int getScanPattern()
{ return scanPattern; 
}

public void setBeamCenterAzimuth(float pBeamCenterAzimuth)
{ beamCenterAzimuth = pBeamCenterAzimuth;
}

public float getBeamCenterAzimuth()
{ return beamCenterAzimuth; 
}

public void setAzimuthalBeamwidth(float pAzimuthalBeamwidth)
{ azimuthalBeamwidth = pAzimuthalBeamwidth;
}

public float getAzimuthalBeamwidth()
{ return azimuthalBeamwidth; 
}

public void setBeamCenterDE(float pBeamCenterDE)
{ beamCenterDE = pBeamCenterDE;
}

public float getBeamCenterDE()
{ return beamCenterDE; 
}

public void setDeBeamwidth(float pDeBeamwidth)
{ deBeamwidth = pDeBeamwidth;
}

public float getDeBeamwidth()
{ return deBeamwidth; 
}


public void marshal(DataOutputStream dos)
{
    try 
    {
       dos.writeShort( (short)activeEmissionParameterIndex);
       dos.writeShort( (short)scanPattern);
       dos.writeFloat( (float)beamCenterAzimuth);
       dos.writeFloat( (float)azimuthalBeamwidth);
       dos.writeFloat( (float)beamCenterDE);
       dos.writeFloat( (float)deBeamwidth);
    } // end try 
    catch(Exception e)
    { 
      System.out.println(e);}
    } // end of marshal method

public void unmarshal(DataInputStream dis)
{
    try 
    {
       activeEmissionParameterIndex = (int)dis.readUnsignedShort();
       scanPattern = (int)dis.readUnsignedShort();
       beamCenterAzimuth = dis.readFloat();
       azimuthalBeamwidth = dis.readFloat();
       beamCenterDE = dis.readFloat();
       deBeamwidth = dis.readFloat();
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
       buff.putShort( (short)activeEmissionParameterIndex);
       buff.putShort( (short)scanPattern);
       buff.putFloat( (float)beamCenterAzimuth);
       buff.putFloat( (float)azimuthalBeamwidth);
       buff.putFloat( (float)beamCenterDE);
       buff.putFloat( (float)deBeamwidth);
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
       activeEmissionParameterIndex = (int)(buff.getShort() & 0xFFFF);
       scanPattern = (int)(buff.getShort() & 0xFFFF);
       beamCenterAzimuth = buff.getFloat();
       azimuthalBeamwidth = buff.getFloat();
       beamCenterDE = buff.getFloat();
       deBeamwidth = buff.getFloat();
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

    if(!(obj instanceof AcousticBeamFundamentalParameter))
        return false;

     final AcousticBeamFundamentalParameter rhs = (AcousticBeamFundamentalParameter)obj;

     if( ! (activeEmissionParameterIndex == rhs.activeEmissionParameterIndex)) ivarsEqual = false;
     if( ! (scanPattern == rhs.scanPattern)) ivarsEqual = false;
     if( ! (beamCenterAzimuth == rhs.beamCenterAzimuth)) ivarsEqual = false;
     if( ! (azimuthalBeamwidth == rhs.azimuthalBeamwidth)) ivarsEqual = false;
     if( ! (beamCenterDE == rhs.beamCenterDE)) ivarsEqual = false;
     if( ! (deBeamwidth == rhs.deBeamwidth)) ivarsEqual = false;

    return ivarsEqual;
 }
} // end of class
