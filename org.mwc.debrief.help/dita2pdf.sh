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
export DITA_HOME=../contribs/DITA-OT1.8.5
export CLASSPATH=$CLASSPATH:../contribs/SaxonHE9-6-0-1J/saxon9he.jar
export CLASSPATH=$CLASSPATH:$DITA_HOME/lib/commons-codec-1.4.jar
export CLASSPATH=$CLASSPATH:$DITA_HOME/lib
export CLASSPATH=$CLASSPATH:$DITA_HOME/lib/dost-configuration.jar
export CLASSPATH=$CLASSPATH:$DITA_HOME/lib/dost.jar
export CLASSPATH=$CLASSPATH:$DITA_HOME/lib/resolver.jar
export CLASSPATH=$CLASSPATH:$DITA_HOME/lib/xercesImpl.jar
export CLASSPATH=$CLASSPATH:$DITA_HOME/lib/xml-apis.jar
#export CLASSPATH=$CLASSPATH:$DITA_HOME/plugins/org.dita.pdf2/lib/fo.jar
echo $CLASSPATH
export ANT_OPTS="-Xmx1024M -Xms256M -XX:PermSize=128M -XX:MaxPermSize=256M"
ant -f cheat_transform.xml
./pdftk_title.sh
