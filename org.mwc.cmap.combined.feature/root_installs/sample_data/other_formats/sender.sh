#*******************************************************************************
# Debrief - the Open Source Maritime Analysis Application
# http://debrief.info
#  
# (C) 2000-2020, Deep Blue C Technology Ltd
#  
# This library is free software; you can redistribute it and/or
# modify it under the terms of the Eclipse Public License v1.0
# (http://www.eclipse.org/legal/epl-v10.html)
#  
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
#*******************************************************************************
# run the PDU generator
# $1 the control file, containing ip, port, network mode, time-step, num participants, num messages
# e.g.  239.1.2.3 62040 multicast 1000 12 15
java -Djava.net.preferIPv4Stack=true -cp ../../plugins/org.mwc.debrief.dis_1.0.14.jar org.mwc.debrief.dis.diagnostics.PduGenerator $1