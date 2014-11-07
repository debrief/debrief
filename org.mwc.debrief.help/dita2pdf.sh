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
