package edu.nps.moves.dis7;

import java.util.*;
import java.io.*;
import edu.nps.moves.disenum.*;
import edu.nps.moves.disutil.*;


/**
 * Does not work, and causes failure in anything it is embedded in. Section 6.2.83
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All rights reserved.
 * This work is licensed under the BSD open source license, available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class StandardVariableSpecification extends Object implements Serializable
{
   /** Number of static variable records */
   protected int  numberOfStandardVariableRecords;

   /** variable length list of standard variables, The class type and length here are WRONG and will cause the incorrect serialization of any class in whihc it is embedded. */
   protected List< SimulationManagementPduHeader > standardVariables = new ArrayList< SimulationManagementPduHeader >(); 

/** Constructor */
 public StandardVariableSpecification()
 {
 }

public int getMarshalledSize()
{
   int marshalSize = 0; 

   marshalSize = marshalSize + 2;  // numberOfStandardVariableRecords
   for(int idx=0; idx < standardVariables.size(); idx++)
   {
        SimulationManagementPduHeader listElement = standardVariables.get(idx);
        marshalSize = marshalSize + listElement.getMarshalledSize();
   }

   return marshalSize;
}


public int getNumberOfStandardVariableRecords()
{ return (int)standardVariables.size();
}

/** Note that setting this value will not change the marshalled value. The list whose length this describes is used for that purpose.
 * The getnumberOfStandardVariableRecords method will also be based on the actual list length rather than this value. 
 * The method is simply here for java bean completeness.
 */
public void setNumberOfStandardVariableRecords(int pNumberOfStandardVariableRecords)
{ numberOfStandardVariableRecords = pNumberOfStandardVariableRecords;
}

public void setStandardVariables(List<SimulationManagementPduHeader> pStandardVariables)
{ standardVariables = pStandardVariables;
}

public List<SimulationManagementPduHeader> getStandardVariables()
{ return standardVariables; }


public void marshal(DataOutputStream dos)
{
    try 
    {
       dos.writeShort( (short)standardVariables.size());

       for(int idx = 0; idx < standardVariables.size(); idx++)
       {
            SimulationManagementPduHeader aSimulationManagementPduHeader = standardVariables.get(idx);
            aSimulationManagementPduHeader.marshal(dos);
       } // end of list marshalling

    } // end try 
    catch(Exception e)
    { 
      System.out.println(e);}
    } // end of marshal method

public void unmarshal(DataInputStream dis)
{
    try 
    {
       numberOfStandardVariableRecords = (int)dis.readUnsignedShort();
       for(int idx = 0; idx < numberOfStandardVariableRecords; idx++)
       {
           SimulationManagementPduHeader anX = new SimulationManagementPduHeader();
           anX.unmarshal(dis);
           standardVariables.add(anX);
       }

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
       buff.putShort( (short)standardVariables.size());

       for(int idx = 0; idx < standardVariables.size(); idx++)
       {
            SimulationManagementPduHeader aSimulationManagementPduHeader = (SimulationManagementPduHeader)standardVariables.get(idx);
            aSimulationManagementPduHeader.marshal(buff);
       } // end of list marshalling

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
       numberOfStandardVariableRecords = (int)(buff.getShort() & 0xFFFF);
       for(int idx = 0; idx < numberOfStandardVariableRecords; idx++)
       {
            SimulationManagementPduHeader anX = new SimulationManagementPduHeader();
            anX.unmarshal(buff);
            standardVariables.add(anX);
       }

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

    if(!(obj instanceof StandardVariableSpecification))
        return false;

     final StandardVariableSpecification rhs = (StandardVariableSpecification)obj;

     if( ! (numberOfStandardVariableRecords == rhs.numberOfStandardVariableRecords)) ivarsEqual = false;

     for(int idx = 0; idx < standardVariables.size(); idx++)
     {
        if( ! ( standardVariables.get(idx).equals(rhs.standardVariables.get(idx)))) ivarsEqual = false;
     }


    return ivarsEqual;
 }
} // end of class
