@rem ***************************************************************************
@rem Debrief - the Open Source Maritime Analysis Application
@rem http://debrief.info
@rem  
@rem (C) 2000-2020, Deep Blue C Technology Ltd
@rem  
@rem This library is free software; you can redistribute it and/or
@rem modify it under the terms of the Eclipse Public License v1.0
@rem (http://www.eclipse.org/legal/epl-v10.html)
@rem  
@rem This library is distributed in the hope that it will be useful,
@rem but WITHOUT ANY WARRANTY; without even the implied warranty of
@rem MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
@rem ***************************************************************************
:: run the PDU generator, passing to it the control file (as parameter one)
:: $1 the control file, containing ip, port, network mode, time-step, num participants, num messages
:: e.g.  239.1.2.3 62040 multicast 1000 12 15
:: 
java -cp ../../plugins/org.mwc.debrief.dis_1.0.14.jar org.mwc.debrief.dis.diagnostics.PduGenerator %1