package edu.nps.moves.dis7;

import java.util.*;
import java.io.*;
import edu.nps.moves.disenum.*;
import edu.nps.moves.disutil.*;


/**
 * The unique designation of each aggrgate in an exercise is specified by an aggregate identifier record. The aggregate ID is not an entity and shall not be treated as such. Section 6.2.3.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All rights reserved.
 * This work is licensed under the BSD open source license, available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class AggregateIdentifier extends Object implements Serializable
{
   /** Simulation address, ie site and application, the first two fields of the entity ID */
   protected SimulationAddress  simulationAddress = new SimulationAddress(); 

   /** the aggregate ID, an object identifier */
   protected int  aggregateID;


/** Constructor */
 public AggregateIdentifier()
 {
 }

public int getMarshalledSize()
{
   int marshalSize = 0; 

   marshalSize = marshalSize + simulationAddress.getMarshalledSize();  // simulationAddress
   marshalSize = marshalSize + 2;  // aggregateID

   return marshalSize;
}


public void setSimulationAddress(SimulationAddress pSimulationAddress)
{ simulationAddress = pSimulationAddress;
}

public SimulationAddress getSimulationAddress()
{ return simulationAddress; 
}

public void setAggregateID(int pAggregateID)
{ aggregateID = pAggregateID;
}

public int getAggregateID()
{ return aggregateID; 
}


public void marshal(DataOutputStream dos)
{
    try 
    {
       simulationAddress.marshal(dos);
       dos.writeShort( (short)aggregateID);
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
       aggregateID = (int)dis.readUnsignedShort();
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
       buff.putShort( (short)aggregateID);
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
       aggregateID = (int)(buff.getShort() & 0xFFFF);
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

    if(!(obj instanceof AggregateIdentifier))
        return false;

     final AggregateIdentifier rhs = (AggregateIdentifier)obj;

     if( ! (simulationAddress.equals( rhs.simulationAddress) )) ivarsEqual = false;
     if( ! (aggregateID == rhs.aggregateID)) ivarsEqual = false;

    return ivarsEqual;
 }
} // end of class
