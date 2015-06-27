cd ../org.mwc.cmap.combined.feature/root_installs
pdftk A=../../org.mwc.debrief.help/front_covers/UserGuide_FlattenedForViewing.pdf B=DebriefNG.pdf cat A1 B2-end output out.pdf
mv out.pdf DebriefNG.pdf
pdftk A=../../org.mwc.debrief.help/front_covers/Tutorial_FlattenedForViewing.pdf B=DebriefNGTutorial.pdf cat A1 B2-end output out.pdf
mv out.pdf DebriefNGTutorial.pdf
pdftk A=../../org.mwc.debrief.help/front_covers/Tutorial_FlattenedForViewing.pdf B=DebriefNG_TMA_Tutorial.pdf cat A1 B2-end output out.pdf
mv out.pdf DebriefNG_TMA_Tutorial.pdf
