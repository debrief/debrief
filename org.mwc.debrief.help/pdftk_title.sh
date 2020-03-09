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
echo "exec pdftk"
cd ../org.mwc.cmap.combined.feature/root_installs
pdftk A=../../org.mwc.debrief.help/front_covers/UserGuide_FlattenedForViewing.pdf B=DebriefNG.pdf cat A1 B2-end output out.pdf
mv out.pdf DebriefNG.pdf
#pdftk A=../../org.mwc.debrief.help/front_covers/Tutorial_FlattenedForViewing.pdf B=DebriefNGTutorial.pdf cat A1 B2-end output out.pdf
#mv out.pdf DebriefNGTutorial.pdf
#pdftk A=../../org.mwc.debrief.help/front_covers/TMATutorial_FlattenedForViewing.pdf B=DebriefNG_TMA_Tutorial.pdf cat A1 B2-end output out.pdf
#mv out.pdf DebriefNG_TMA_Tutorial.pdf
