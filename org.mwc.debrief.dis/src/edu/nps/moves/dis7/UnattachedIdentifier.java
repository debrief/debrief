package edu.nps.moves.dis7;

import java.util.*;
import java.io.*;
import edu.nps.moves.disenum.*;
import edu.nps.moves.disutil.*;


/**
 * The unique designation of one or more unattached radios in an event or exercise Section 6.2.91
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All rights reserved.
 * This work is licensed under the BSD open source license, available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class UnattachedIdentifier extends Object implements Serializable
{
   /** See 6.2.79 */
   protected SimulationAddress  simulationAddress = new SimulationAddress(); 

   /** Reference number */
   protected int  referenceNumber;


/** Constructor */
 public UnattachedIdentifier()
 {
 }

public int getMarshalledSize()
{
   int marshalSize = 0; 

   marshalSize = marshalSize + simulationAddress.getMarshalledSize();  // simulationAddress
   marshalSize = marshalSize + 2;  // referenceNumber

   return marshalSize;
}


public void setSimulationAddress(SimulationAddress pSimulationAddress)
{ simulationAddress = pSimulationAddress;
}

public SimulationAddress getSimulationAddress()
{ return simulationAddress; 
}

public void setReferenceNumber(int pReferenceNumber)
{ referenceNumber = pReferenceNumber;
}

public int getReferenceNumber()
{ return referenceNumber; 
}


public void marshal(DataOutputStream dos)
{
    try 
    {
       simulationAddress.marshal(dos);
       dos.writeShort( (short)referenceNumber);
    } // end try 
    catch(Exception e)
    { 
      System.out.println(e);}
    } // end of marshal method

public void unmarshal(DataInputStream dis)
{
    try 
    {
       simulationAddress.unmarshal(dis);
       referenceNumber = (int)dis.readUnsignedShort();
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
       simulationAddress.marshal(buff);
       buff.putShort( (short)referenceNumber);
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
       simulationAddress.unmarshal(buff);
       referenceNumber = (int)(buff.getShort() & 0xFFFF);
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

    if(!(obj instanceof UnattachedIdentifier))
        return false;

     final UnattachedIdentifier rhs = (UnattachedIdentifier)obj;

     if( ! (simulationAddress.equals( rhs.simulationAddress) )) ivarsEqual = false;
     if( ! (referenceNumber == rhs.referenceNumber)) ivarsEqual = false;

    return ivarsEqual;
 }
} // end of class
